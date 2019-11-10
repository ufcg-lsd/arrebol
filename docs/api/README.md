## API Overview
--------------
### 1 - Queues
#### 1.1 - Create a new queue

| Method | URI |
| :--- | :--- |
| `POST` | `/api/queues` |

**Request body**
```json
{
    "name": "awesome_name",    
}
```

**Response example**
```json
{
	"id": "a3f77cca-96bb-4e7a-b746-e8960f779747"
}
```
#### 1.2 - Retrieves a list with the current queues

| Method | URI |
| :--- | :--- |
| `GET` | `/api/queues` |

**Response example**
```json
[
    {
        "id": "a3f77cca-96bb-4e7a-b746-e8960f779747",
        "name": "awesome_name",
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

| Method | URI |
| :--- | :--- |
| `GET` | `/api/queues/{queue_id}` |

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
#### 1.4 - Adds a new worker pool to a given queue [Deprecated]
| Method | URI |
| :--- | :--- |
| `POST` | `/api/queues/{queue_id}/workers` |

**Request Body**
```json
{
    "address": "85.110.150.0",
    "pool_size": 5
}
```
**Response example**
```json
{
	"id": "40aa431e-a30b-4650-ad37-fc29e632ade1"
}
```

#### 1.5 - Retrieves all workers of a given queue [Deprecated]
| Method | URI |
| :--- | :--- |
| `GET` | `/api/queues/{queue_id}/workers` |

**Response example**:
```json
[
    {   
        "id": "40aa431e-a30b-4650-ad37-fc29e632ade1",
        "address": "85.110.150.0",
        "pool_size": 5
    },
    {   
        "id": "efc6380e-d00b-45e2-99b7-f1167fc06ed5",
        "address": "200.100.050.2",
        "pool_size": 5
    }
]
```

#### 1.6 - Remove a worker
| Method | URI |
| :--- | :--- |
| `DELETE` | `/api/queues/{queue_id}/workers{worker_id}` |

### 2 - Jobs

#### 2.1 - Submit a new job for execution
| Method | URI |
| :--- | :--- |
| `POST` | `/api/queues/{queue_id}/jobs` |

**Request body**
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
#### 2.2 - Retrieves the execution status of a given job
| Method | URI |
| :--- | :--- |
| `GET` | `/api/queues/{queue_id}/jobs/{job_id}` |

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
#### 2.3 - Retrieves the execution status of all jobs in a given queue

| Method | URI |
| :--- | :--- |
| `GET` | `/api/queues/{queue_id}/jobs` |

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

#### 2.4 - Retrieves the execution status of all jobs in a given queue of a such label
| Method | URI |
| :--- | :--- |
| `GET` | `/api/queues/{queue_id}/jobs?label=awesome_job` |

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

#### 2.5 - Filter the jobs of a given state

A job can assume the following states
```java  
  QUEUED,
  RUNNING,
  FINISHED,
  FAILED
```
| Method | URI |
| :--- | :--- |
| `GET` | `/api/queues/{queue_id}/jobs?state=queued` |

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
