# Deploying the Arrebol Service

The Arrebol service could be deployed as a docker container from a [arrebol docker image](https://hub.docker.com/repository/docker/ufcglsd/arrebol).  Most of the required dependencies to run Arrebol are kept in the own [Arrebol docker image](https://hub.docker.com/repository/docker/ufcglsd/arrebol). The only exception is the Docker Engine. To install it, in an apt-based environmnet, please run the `setup.sh` script as follow:

  ```
  sudo bash setup.sh
  ```

## Configuration

All the configuration files are within the `deploy/config` directory. It is necesssary to edit:

* The `postgres.env`, to define a password to the database;
* The `pgadmin.env`, to define an the database admin credentials;
* Assign the previously defined database password in the `spring.datasource.password` property on `application.properties` file;
* Configure the `arrebol.json` file to tune Arrebol internals.

## Install

After the configuration, execute the `deploy/deploy-stack.sh` script to install and run Arrebol components.

  ```
  sudo bash deploy-stack.sh
  ```

## Check 

To verify whether the deploy is running correctly, submit below sample requests to the Arrebol service.


---
Request
```bash
curl http://127.0.0.1:8080/queues/default
```

Expected
```json
{"id":"default","name":"Default Queue","waiting_jobs":0,"worker_pools":1,"pools_size":5}
```
---
Request
```bash
curl -X POST \
  http://127.0.0.1:8080/queues/default/jobs \
  -H 'content-type: application/json' \
  -d '{
   "label":"MyJob",
   "tasks_specs":[
      {
         "label":"MyLabel1",
         "requirements":{
            "DockerRequirements":"DockerMemory == 1024 && DockerCPUWeight == 1024",
            "image":"ubuntu:latest"
         },
         "commands":[
            "echo Hello World!",
            "sleep 2",
            "touch /nfs/test1",
            "sleep 2",
            "echo Goodbye World!"
         ],
         "metadata":{
            "time":"111222333"
         }
      },
      {
         "label":"MyLabel2",
         "requirements":{
            "DockerRequirements":"DockerMemory == 1024 && DockerCPUWeight == 1024",
            "image":"ubuntu:latest"
         },
         "commands":[
            "echo Hello World!",
            "sleep 2",
            "touch /nfs/test2",
            "sleep 2",
            "echo Goodbye World!"
         ],
         "metadata":{
            "time":"111222333"
         }
      }
   ]
}'
```

Expected
```json
{"id":"e77d7b5c-dc3b-4f22-83ea-b6cb48736455"}
```

---
Request 
* Use the job id of previous request
* Do until you see that the Job is finished
* **After Job is `finished`, check if was created a file `test1` and `test2` into `/nfs` directory from `worker host`.**

```bash
curl -X GET http://127.0.0.1:8080/queues/default/jobs/e77d7b5c-dc3b-4f22-83ea-b6cb48736455
```

Expected
```json
{
   "id":"e77d7b5c-dc3b-4f22-83ea-b6cb48736455",
   "label":"MyJob",
   "tasks":[
      {
         "id":"4c6c20fd-b679-4b1d-8b1b-08a37fb6e3cc",
         "state":"FINISHED",
         "tasks_specs":{
            "label":"MyLabel1",
            "requirements":{
               "image":"ubuntu:latest",
               "DockerRequirements":"DockerMemory == 1024 && DockerCPUWeight == 1024"
            },
            "commands":[
               {
                  "command":"echo Hello World!",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"sleep 2",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"touch /nfs/test1",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"sleep 2",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"echo Goodbye World!",
                  "state":"FINISHED",
                  "exitcode":0
               }
            ],
            "metadata":{
               "time":"111222333"
            }
         }
      },
      {
         "id":"391017e3-ab1e-4784-a694-e33764df3cec",
         "state":"FINISHED",
         "tasks_specs":{
            "label":"MyLabel2",
            "requirements":{
               "image":"ubuntu:latest",
               "DockerRequirements":"DockerMemory == 1024 && DockerCPUWeight == 1024"
            },
            "commands":[
               {
                  "command":"echo Hello World!",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"sleep 2",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"touch /nfs/test2",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"sleep 2",
                  "state":"FINISHED",
                  "exitcode":0
               },
               {
                  "command":"echo Goodbye World!",
                  "state":"FINISHED",
                  "exitcode":0
               }
            ],
            "metadata":{
               "time":"111222333"
            }
         }
      }
   ],
   "job_state":"FINISHED"
}
```

## Hard Restart

To hard restart the Arrebol Stack, run the commands as follow:
```bash
sudo docker stack rm lsd
sudo docker volume rm lsd_postgresdata
sudo docker pull ufcglsd/arrebol:saps
sudo bash deploy-stack.sh
```

