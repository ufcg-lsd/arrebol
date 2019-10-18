
### 1 - Jobs
#### 1.1 - Submit a new job for execution
**Endpoint**:
`POST /jobs`
**Request example**:
*Body example*
```json
{
   "label":"some_descriptive_label",
   "queue_id": "some_unique_id",
   "tasks":[
      {
         "id":"TaskNumber-0-36b8d41a-8611-4468-93ee-40f4140c7555",
         "spec":{
            "image":"ubuntu",
            "requirements":{
               "DockerRequirements":"DockerMemory == 1024 && DockerCPUWeight == 512"
            }
         },
         "commands":[
            "sleep 10",
            "sleep 20"
         ]         
      }
   ]
}
```
**Response example**:
```json
{
	"id": "e7dbd27e-8747-488a-8124-75ad907e005d"
}
```
**Status codes**:
- **201** – no error
- **400** – bad parameter
- **500** – server error

#### 1.2 - Retrieves the execution status of a given job
**Endpoint**:
`GET /jobs/{id}/status`
**Request example**:
GET /job/e7dbd27e-8747-488a-8124-75ad907e005d/status
**Response example**:
```json
{
  "id": "e7dbd27e-8747-488a-8124-75ad907e005d",
  "label": "some_descriptive_label",
  "state": "RUNNING",
  "queue_id": "some_unique_id",
  "tasks": [
    {
      "id": "db14414e-5e0f-487a-970e-68396f97e33d",
      "state": "FINISHED",
      "spec": {
        "image": "ubuntu",
        "requirements": {
          "DockerRequirements": "DockerMemory == 1024 && DockerCPUWeight == 512"
        }
      },
      "commands": [
        {
          "command": "sleep 10",
          "state": "FINISHED",
          "exit_code": 0
        },
        {
          "command": "sleep 20",
          "state": "RUNNING",
          "exit_code": -1
        }
      ]
    }
  ]
}
```
**Status codes**:
- **200** – no error
- **404** – no such job
- **500** – server error

#### 2 - Queues
#### 2.1 - Create a new queue
`POST /queues`
**Request example**:
*Body example*
```json
{
   "name":"long_jobs",
   "workers_nodes": [
       	{
       	    "address": "200.100.050.0",
       	    "private_key": ""
    	}
   ]
}
```
**Response example**:
```json
{
	"id": "some_unique_id"
}
```
**Status codes**:
- **201** – no error
- **400** – bad parameter
- **500** – server error

#### 2.2 - Retrieves a list with the current queues
**Endpoint**:
`GET /queues`
**Request example**:
GET /queues
**Response example**:
```json
[
    {
      "id": "some_unique_id",
      "jobs": 2,
      "worker_nodes": 5,
      "workers_pool": 
    },
    {
      "id": "awesome_queue_id",
      "jobs": 10,
      "worker_nodes": 2
    },
    {
      "id": "default_queue",
      "jobs": 0,
      "worker_nodes": 0
    },
]
```
**Status codes**:
- **200** – no error
- **404** – no such job
- **500** – server error

#### 2.3 - Retrieves the execution status of all jobs in a given queue
**Endpoint**:
`GET /queues/{id}/jobs`
**Request example**:
GET /queues/some_unique_id/jobs
**Response example**:
```json
[
    {
      "id": "e7dbd27e-8747-488a-8124-75ad907e005d",
      "label": "some_descriptive_label",
      "state": "FINISHED"
    },
    {
      "id": "e7dbd27e-8747-488a-8124-75ad907e005d",
      "label": "awesome_job",
      "state": "RUNNING"
    },
    {
      "id": "e7dbd27e-8747-488a-8124-75ad907e005d",
      "label": "sleep_job",
      "state": "READY"
    }
]
```
**Status codes**:
- **200** – no error
- **404** – no such job
- **500** – server error

#### 2.4 - Adds a list of worker nodes to the queue pool
`PUT /queues/{id}`
**Request example**:
PUT /queues/some_unique_id
*Body example*
```json
[
   	{
   	    "address": "85.110.150.0",
   	    "private_key": "path_to_the_key"
	},
	{
   	    "address": "85.110.150.1",
   	    "private_key": "path_to_the_key"
	},
	{
   	    "address": "85.110.150.2",
   	    "private_key": "path_to_the_key"
	}
]
```
**Status codes**:
- **201** – no error
- **400** – bad parameter
- **500** – server error
