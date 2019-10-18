## API Overview

## Available endpoints
Major resource endpoints supported by the Arrebol API are:

| resource      | description                       |
|:--------------|:----------------------------------|
| `/jobs`      | manages job submission and tracking
| `/queues`    | manages queue creation and status

### 1 - Jobs

#### 1.1 - Submit a new job for execution
**URL**

```http
POST /jobs
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

#### 1.2 - Retrieves the execution status of a given job

**URL**
```http
GET /jobs/{id}/status
```

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

### 2 - Queues

#### 2.1 - Create a new queue

**URL**
```http
POST /queues
```

**Request example**

*Body*
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

**Response example**
```json
{
	"id": "some_unique_id"
}
```

#### 2.2 - Retrieves a list with the current queues

**URL**

```http
GET /queues
```

**Response example**
```json
[
    {
      "id": "some_unique_id",
      "jobs": 2,
      "worker_nodes": 5,
      "workers_pool": 25
    },
    {
      "id": "awesome_queue_id",
      "jobs": 10,
      "worker_nodes": 2,
      "workers_pool": 10
    },
    {
      "id": "default_queue",
      "jobs": 0,
      "worker_nodes": 0,
      "workers_pool": 25      
    },
]
```

#### 2.3 - Retrieves the execution status of all jobs in a given queue

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

#### 2.4 - Adds a list of worker nodes to the queue pool

```http
PUT /queues/{id}
```

*Body*
```json
[
   	{
   	    "address": "85.110.150.0",
   	    "private_key": "path_to_the_key",
	    "workers_pool": 5
	},
	{
   	    "address": "85.110.150.1",
   	    "private_key": "path_to_the_key",
	    "workers_pool": 2
	},
	{
   	    "address": "85.110.150.2",
   	    "private_key": "path_to_the_key",
	    "workers_pool": 10
	}
]
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
