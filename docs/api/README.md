## API Overview

## Available endpoints
Major resource endpoints supported by the Arrebol API are:

| resource      | description                       |
|:--------------|:----------------------------------|
| `/queues`    | manages queue creation and status
| `/jobs`      | manages job execution and tracking
| `/workers`   | manages the addition of workers |

### 1 - Queues

#### 1.1 - Create a new queue

**URL**
```http
POST /queues
```

**Request example**

*Body*
```json
{
   "name":"some_awesome_name",
   "worker_nodes": [
       	{
       	    "address": "200.100.050.0",
	    "worker_pool": 5
    	},
	{
       	    "address": "200.100.050.1",
	    "worker_pool": 10
    	}
   ],   
}
```

**Response example**
```json
{
	"id": "some_unique_id"
}
```

#### 1.2 - Retrieves a list with the current queues

**URL**

```http
GET /queues
```

**Response example**
```json
[
    {
      "id": "some_unique_id",
      "name": "awesome_name",
      "jobs": 2,
      "worker_nodes": 5,
      "worker_pool": 25
    },
    {
      "id": "awesome_queue_id",
      "name": "awesome_name_bff",
      "jobs": 10,
      "worker_nodes": 2,
      "worker_pool": 10
    },
    {
      "id": "default_queue",
      "name": "awesome_name_dot",
      "jobs": 0,
      "worker_nodes": 0,
      "worker_pool": 25      
    },
]
```

#### 1.3 - Adds a list of worker nodes to the queue pool

```http
POST /queues/{id}/workers
```

*Body*
```json
[
   	{
   	    "address": "85.110.150.0",   	    
	    "worker_pool": 5
	},
	{
   	    "address": "85.110.150.1",   	    
	    "worker_pool": 2
	},
	{
   	    "address": "85.110.150.2",   	    
	    "worker_pool": 10
	}
]
```
#### 1.4 - Retrieves the execution status of all jobs in a given queue

**URL**

```http
GET /queues/{id}/jobs
```

**Response example**
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

### 2 - Jobs

#### 2.1 - Submit a new job for execution
**URL**

```http
POST /queues/{id}/jobs
```

**Request example**

*Body*

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
#### 2.2 - Retrieves the execution status of a given job

**URL**
```http
GET /queues/{id}/jobs/{job_id}/status
```

**Response example**:
```json
{
  "id": "e7dbd27e-8747-488a-8124-75ad907e005d",  
  "state": "RUNNING",  
  "tasks": [
    {
      "id": "db14414e-5e0f-487a-970e-68396f97e33d",
      "state": "FINISHED",            
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
## Responses

Many API endpoints return the JSON representation of the resources created or edited. However, if an invalid request is submitted, or some other error occurs, Arrebol must returns a JSON response in the following format:

```javascript
{
  "message" : string  
}
```

The `message` attribute contains a message commonly used to indicate errors.


## Status Codes

Arrebol returns the following status codes in its API:

| Status Code | Description |
| :--- | :--- |
| 200 | `OK` |
| 201 | `CREATED` |
| 400 | `BAD REQUEST` |
| 404 | `NOT FOUND` |
| 500 | `INTERNAL SERVER ERROR` |
