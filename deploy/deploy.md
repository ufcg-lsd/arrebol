# Deploying an Arrebol Service

For the production mode, the Arrebol is deployed in a docker container from a [arrebol docker image](https://hub.docker.com/repository/docker/ufcglsd/arrebol). Containerization facilitates deployment and has several other advantages.

## Dependencies
In ubuntu 18.04, run the `setup.sh` script to install dependencies.

  ```
  sudo bash setup.sh
  ```

## Fill configuration files

All configuration files is inside `config` directory.

* Define an password to postgres data base and write to `postgres.env`
* Define an email/password to pgadmin and write to `postgres.env`
* Fill the field `spring.datasource.password` on `application.properties` with the postgres database password.
* Update the properties of `arrebol.json`

## Start deploy stack

Run the `deploy-stack.sh` script to start deploy stack (arrebol/postgres/pgadmin).

  ```
  sudo bash deploy-stack.sh
  ```

## Check deployment

To check if deployment is function correct do some requests shown below and verify the output.

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

```bash
curl -X GET http://127.0.0.1:8080/queues/default/jobs/e77d7b5c-dc3b-4f22-83ea-b6cb48736455
```

Expected
```json
{
    "id": "e77d7b5c-dc3b-4f22-83ea-b6cb48736455",
    "label": "MyJob",
    "tasks": [
        {
            "id": "ed27dc52-af1b-4c86-88ae-b86874f5a626",
            "state": "FINISHED",
            "tasks_specs": {
                "label": "MyLabel1",
                "requirements": {
                    "image": "ubuntu:latest",
                    "DockerRequirements": "DockerMemory == 1024 && DockerCPUWeight == 1024"
                },
                "commands": [
                    {
                        "command": "echo Hello World!",
                        "state": "FINISHED",
                        "exitcode": 0
                    },
                    {
                        "command": "sleep 2",
                        "state": "FINISHED",
                        "exitcode": 0
                    },
                    {
                        "command": "sleep 2",
                        "state": "FINISHED",
                        "exitcode": 0
                    },
                    {
                        "command": "echo Goodbye World!",
                        "state": "FINISHED",
                        "exitcode": 0
                    }
                ],
                "metadata": {
                    "time": "111222333"
                }
            }
        },
        {
            "id": "3934de84-440d-4786-8051-1f8aa0bb5bbb",
            "state": "FINISHED",
            "tasks_specs": {
                "label": "MyLabel2",
                "requirements": {
                    "image": "ubuntu:latest",
                    "DockerRequirements": "DockerMemory == 1024 && DockerCPUWeight == 1024"
                },
                "commands": [
                    {
                        "command": "echo Hello World!",
                        "state": "FINISHED",
                        "exitcode": 0
                    },
                    {
                        "command": "sleep 2",
                        "state": "FINISHED",
                        "exitcode": 0
                    },
                    {
                        "command": "sleep 2",
                        "state": "FINISHED",
                        "exitcode": 0
                    },
                    {
                        "command": "echo Goodbye World!",
                        "state": "FINISHED",
                        "exitcode": 0
                    }
                ],
                "metadata": {
                    "time": "111222333"
                }
            }
        }
    ],
    "job_state": "FINISHED"
}
```

## Error Cases

### Deploy stack script
While running `deploy-stack.sh` the following error may occur:
```bash
sudo bash deploy-stack.sh
Creating service lsd_arrebol
failed to create service lsd_arrebol: Error response from daemon: network lsd_arrebol-net not found
```
To solve, just run the script again.

