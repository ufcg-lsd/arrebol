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
    "name": "some_awesome_name",    
}
```

**Response example**
```json
{
	"id": "a3f77cca-96bb-4e7a-b746-e8960f779747"
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
        "id": "a3f77cca-96bb-4e7a-b746-e8960f779747",
        "name": "some_awesome_name",
        "waiting_jobs": 0,
        "worker_pools": 0,
        "pools_size": 0
    },    
    {
        "id": "default_queue",
        "name": "awesome_name_dot_com",
        "waiting_jobs": 100,
        "worker_pools": 5,
        "pools_size": 50
    }
]
```

#### 1.3 - Retrieves a queue by it id

**URL**

```http
GET /queues/{queue_id}
```

**Resquest example**
```http
GET /queues/a3f77cca-96bb-4e7a-b746-e8960f779747
```

**Response example**
```json
{
    "id": "a3f77cca-96bb-4e7a-b746-e8960f779747",
    "name": "awesome_name",
    "waiting_jobs": 0,
    "worker_pools": 0,
    "pools_size": 0
}
```

### 3 - Workers

#### 3.1 - Adds a new worker pool to a given queue 

```http
POST /queues/{queue_id}/workers
```

**Request example**:
```http
POST /queues/a3f77cca-96bb-4e7a-b746-e8960f779747/workers
```

*Body*
```json
{
    "address": "85.110.150.0",
    "pool_size": 5
}
```
**Response example**:

```json
{
	"id": "40aa431e-a30b-4650-ad37-fc29e632ade1"
}
```

#### 3.2 - Retrieves all workers of a given queue

```http
GET /queues/{queue_id}/workers
```
**Response example**:
```json
[
    {   
        "id": "40aa431e-a30b-4650-ad37-fc29e632ade1",
        "address": "85.110.150.0",
        "pool_size": 5
    },
    {   
        "id": "56b8e729-6bdb-4387-a33e-00bcebf43857",
        "address": "200.100.050.1",
        "pool_size": 5
    },
    {   
        "id": "efc6380e-d00b-45e2-99b7-f1167fc06ed5",
        "address": "200.100.050.2",
        "pool_size": 5
    }
]
```

#### 3.3 - Remove a worker

```http
DELETE /queues/{queue_id}/workers/{worker_id}
```

### 3 - Jobs

#### 3.1 - Submit a new job for execution
**URL**

```http
POST /queues/{queue_id}/jobs
```

**Request example**

*Body*

```json
{
    "label": "some_descriptive_label",
    "tasks": [
        {
            "id": "TaskNumber-0-36b8d41a-8611-4468-93ee-40f4140c7555",
            "spec": {
                "image": "ubuntu",
                "requirements": {
                    "DockerRequirements": "DockerMemory == 1024 && DockerCPUWeight == 512"
                }
            },
            "commands": [
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
#### 3.2 - Retrieves the execution status of a given job

**URL**
```http
GET /queues/{queue_id}/jobs/{job_id}
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
#### 3.3 - Retrieves the execution status of all jobs in a given queue

**URL**

```http
GET /queues/{queue_id}/jobs
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

#### 3.4 - Retrieves the execution status of all jobs in a given queue of a such label

**URL**

```http
GET /queues/{queue_id}/jobs?label=awesome_job
```

**Response example**
```json
[
    {
        "id": "e7dbd27e-8747-488a-8124-75ad907e005d",
        "label": "awesome_job",
        "state": "FINISHED"
    },
    {
        "id": "475b122e-447-4388a-3124-743ad4040sad",
        "label": "awesome_job",
        "state": "RUNNING"
    }
]
```

#### 3.5 - Filter the jobs of a given state

A job can assume the following states
```java
  SUBMITTED,
  QUEUED,
  RUNNING,
  FINISHED,
  FAILED
```
**URL**

```http
GET /queues/{queue_id}/jobs?state=queued
```

**Response example**
```json
[
    {
        "id": "e7dbd27e-8747-488a-8124-75ad907e005d",
        "label": "awesome_job",
        "state": "QUEUED"
    },
    {
        "id": "475b122e-447-4388a-3124-743ad4040sad",
        "label": "more_awesome_job",
        "state": "QUEUED"
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
