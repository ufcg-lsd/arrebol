
## 1. Endpoints

### 1.1 Jobs

#### 1.1.1 - Submeter um novo Job para execução

`POST /jobs`

**Exemplo de requisição**:

*Body recebe um JSON como segue*
```json
{
   "label":"sleep",
   "tasks_specs":[
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

**Exemplo de resposta**:

```json
{
	"id": "e7dbd27e-8747-488a-8124-75ad907e005d"
}
```

**Status codes**:

- **201** – no error

- **400** – bad parameter

- **500** – server error

#### 1.1.2 - Pegar o status de execução de um Job

`GET /jobs/{id}`

Retorna o status de execução por uma representação intermediária do estado do Job.

**Exemplo de requisição**:

GET /job/e7dbd27e-8747-488a-8124-75ad907e005d

**Exemplo de resposta**:
```json
{
  "id": "e7dbd27e-8747-488a-8124-75ad907e005d",
  "label": "sleep",
  "job_state": "READY",
  "tasks": [
    {
      "id": "db14414e-5e0f-487a-970e-68396f97e33d",
      "state": "FINISHED",
      "task_spec": {
        "id": "TaskNumber-0-36b8d41a-8611-4468-93ee-40f4140c7555",
        "spec": {
          "image": "ubuntu",
          "requirements": {
            "DockerRequirements": "DockerMemory == 1024 && DockerCPUWeight == 512"
          }
        },
        "commands": [
          {
            "command": "mkdir test-dir && cd test-dir && touch test.file",
            "state": "FINISHED",
            "exit_code": 0
          }
        ]
      }
    }
  ]
}
```

**Status codes**:

- **200** – no error

- **404** – no such job

- **500** – server error