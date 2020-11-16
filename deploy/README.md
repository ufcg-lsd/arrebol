# Deploying the Arrebol Service

This document provides an easy way to deploy a Arrebol Service. The Arrebol has two main components, the **Arrebol Server** and the **Worker Node**. Then, before installing the Arrebol Server, please deploy a [Worker Node](../worker/deploy).

## Overview

The Arrebol Service manages the job execution in the worker nodes and monitor the job state. In short, the Arrebol uses the Worker Node resources to run the jobs.

## Infrastruture

You just need a machine for Arrebol Service.

### Requirements
* vCPU: 2
* RAM: 2GB
* FREE DISK SPACE: 10GB
* OS: Ubuntu 16 or higher 

### Security Group
* Custom TCP Rule to allow ingress in the port **8080** to access **Arrebol API**
* Custom TCP Rule to allow ingress in the port **5432** to access **Postgresql DB** (Optional)
* Custom TCP Rule to allow ingress in the port **15432** to access **Pgadmin** (Optional)

## Setup

### 1.Download the repository

Log in the arrebol host and follow the instructions.

To download the repository is need the git installed. Case it was not installed, run these commands to install:
```bash
sudo apt update
sudo apt install -y git
```
Run the below command to download the arrebol repository:
```bash
git clone -b feature/remote-worker https://github.com/ufcg-lsd/arrebol.git
```

### 2.Install dependencies

Log in the arrebol host and run the following commands to install dependencies.

  ```
  cd arrebol/deploy
  sudo bash setup.sh
  ```

### 3. Fill configuration files

Still within the `deploy` directory, go to` config` directory.
```bash
cd config
```

Now it is necessary to edit the files in the following order: **postgres.env**, **pgadmin.env**, **application.properties** and **arrebol.json**.

#### Postgres Configuration

File: postgres.env

```
POSTGRES_PASSWORD=
```

The **POSTGRES_PASSWORD** define a password to the postgres database. The postgres user is **postgres** by default. 

Considering that password is **@rrebol**. The content of the postgres.env file would be:

```
POSTGRES_PASSWORD=@arrebol
```

#### PgAdmin Configuration

File: pgadmin.env

```
PGADMIN_DEFAULT_EMAIL=
PGADMIN_DEFAULT_PASSWORD=
```

| Field                             | Description    |
|:---------------------------------:|----------------|
| **PGADMIN_DEFAULT_EMAIL**                |  This is the email address used when setting up the initial administrator account to login to pgAdmin. |
| **PGADMIN_DEFAULT_PASSWORD**              | This is the password used when setting up the initial administrator account to login to pgAdmin. |

Considering that **PGADMIN_DEFAULT_EMAIL** is **arrebol@lsd.ufcg.edu.br** and **PGADMIN_DEFAULT_PASSWORD** is **pg@dmin**. The content of the pgadmin.env file would be:

```
PGADMIN_DEFAULT_EMAIL=arrebol@lsd.ufcg.edu.br
PGADMIN_DEFAULT_PASSWORD=pg@dmin
```

#### Application properties Configuration

File: application.properties

There is one property that need to be filled in this file:
* **spring.datasource.password**

The **spring.datasource.password** is the password that arrebol service will use to connect to database. Its value is the same **POSTGRES_PASSWORD** previously defined.

Considering that **POSTGRES_PASSWORD** is **@rrebol**. The content of the application.properties file would be:

```
spring.jpa.database=POSTGRESQL
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.platform=postgres
spring.datasource.url=jdbc:postgresql://postgresql:5432/arrebol
spring.datasource.username=postgres
spring.datasource.password=@rrebol
spring.jpa.generate-ddl=true

spring.jpa.hibernate.ddl-auto=update
```

#### Arrebol Properties

Configure the arrebol.json file to tune Arrebol internals.

Each property in this file is defined by a `key` field that identifies the property and a `value` field that defines the value of that property.
In order to facilitate the deployment, it is only necessary to add the addresses to the `resourceAddresses` list.

File: arrebol.json
```json
{
  "poolType":"docker",
  "properties": [
    {
      "key":"workerPoolSize",
      "value": 5
    },
    {
      "key": "imageId",
      "value": "wesleymonte/simple-worker"
    },
    {
      "key": "resourceAddresses",
      "value": []
    }
  ]
}
```

| Field                             | Description    |
|:---------------------------------:|----------------|
| **workerPoolSize**                |  Defines the maximum number of workers that can run on the same node. |
| **imageId**                       | Sets the default docker image used to create workers. |
| **resourceAddress**               | Defines an address list of worker nodes. |

The workerPoolSize is **5** and imageId is **wesleymonte/simple-worker** by default. 

Considering that your worker nodes are **10.30.1.1** and **10.30.1.2**. The content of the **arrebol.json** file would be:

```json
{
  "poolType":"docker",
  "properties": [
    {
      "key":"workerPoolSize",
      "value": 5
    },
    {
      "key": "imageId",
      "value": "wesleymonte/simple-worker"
    },
    {
      "key": "resourceAddresses",
      "value": ["10.30.1.1", "10.30.1.2"]
    }
  ]
}
```

### 4.Install

Now go back to the **arrebol/deploy** directory and run the below commands to install the arrebol service:
```
cd ..
sudo bash deploy-stack.sh
```

Wait a few minutes, it may take a while for the services to be ready.

## Check 

To verify whether the deploy is running correctly, one can submit below sample requests to the Arrebol service.

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
