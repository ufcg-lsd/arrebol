# Worker Deployment

This document describes the deploy of Workers. The deply process was design to ease the deployment of Workers over multiple hosts. Before explaining the details of the deployment process, the document first describe the main concepts used on the document.

## **Overview**

### _Worker Node_

**Worker Node** is the host where workers run. One needs to deploy at least one Worker Node for Arrebol.

### _Coordination Host_

The **coordination host** is a host from which scripts are executed to perfom the deployment. The coordination host must have access to Worker Nodes via **SSH (using a rsa key pair)** to perform its function.

![Worker Deployment](../../imgs/wd.png)

## **Infrastructure**

You will need at least two hosts: **a coordination host** and **a worker node**. At the end of the deployment, the coordination host is no longer needed.

### _Worker Node_

  #### Requirements

  The required spec of the Worker Node depends on the load submitted to Arrebol. Below, we indicate a minimum recommendation:

  - vCPU: 2
  - RAM: 2GB
  - FREE DISK SPACE: 10GB
  - OS: Ubuntu 16 or higher

  #### Network acccess rules
  
  To allow the communication between the Arrebol server and the Worker, it is necessary to allow ingress on below ports:
  
  * Ingress TCP traffic on port 5555 (Docker API)
  * Ingress TCP traffic in the port 22 (SSH)

### _Coordination Host_

  #### Requirements 
  
   Since the installation process is not a heavy task, a tiny host such as described by the below spec could handle the task.
  
  - vCPU: 1
  - RAM: 1GB
  - FREE DISK SPACE: 10GB
  - OS: Ubuntu 16 or higher

  #### Network acccess rules

    Since the uses commands the deploymeent via the coordination host, it is necessary to allow enable access to the host through below port:
    
  * Ingress TCP traffic in the port 22 (SSH)

## **Setup**

### _1.Create RSA Key Pair_

`Note: If you already have a key pair configured between the coordination host and the Worker Nodes, proceed to the Install dependencies section.`
`Note 2 The deployment uses the same private key to access all Worker Nodes` 

Log into the coordination host, and then use ssh-keygen tool to generate a key pair using the RSA algorithm.
To generate RSA keys, on the terminal, execute below commands:

```bash
mkdir -p ~/.ssh
cd ~/.ssh
ssh-keygen -t rsa -N "" -f coordination_host_key
chmod 600 coordination_host_key
```
If the commands were successful, the ssh-keygen tool had generated  two new files in the **~/.ssh** directory: 
* **coordination_host_key**: The private key. It authenticates the coordination host. Ansible uses this key to access worker nodes.
* **coordination_host_key.pub**: The public key. It authorizes the coordination host to access the worker node. Its content must be in the authorized keys of each worker node.

To allow the communicaton between the coordination and the Worker Nodes via SSH, the public key (**coordination_host_key.pub**) must be copied in the **authorized keys file** of the Worker Node. To this end, log into the Worker Node hosts and append the public key content to the `~/.ssh/authorized_keys` files.


### _2.Install dependencies_

Log in the coordination host and run the following commands to install dependencies.

```
sudo apt update
sudo apt install software-properties-common
sudo apt-add-repository --yes --update ppa:ansible/ansible
sudo apt install -y ansible git grep sed 
```

### _3.Download the repository_

Log in the coordination host and run the below commands to download the arrebol repository.

```bash
git clone -b feature/remote-worker https://github.com/ufcg-lsd/arrebol.git
```

### _4.Fill configuration file_

Go to the directory _worker/deploy_ inside _arrebol_ directory.

```bash
cd arrebol/worker/deploy
```

Then, edit the `hosts.conf` file present in this directory, as follows.\
 **All fields of the files must be filled**. See below how to edit it. 

#### Hosts configuration

File: host.conf
```
# For each worker node write a field started by `worker_ip`
# In this example, there will be 2 worker nodes.

worker_ip_1=
worker_ip_2=

remote_user=

ansible_ssh_private_key_file=
```

| Field                             | Description    |
|:---------------------------------:|----------------|
| **worker_ip**                         | The **worker node** addresses. For each ip create a field beginning with **_worker_ip_**. |
| **remote_user**                       | The user name that should be used to access the **worker nodes** via ssh.  |
| **ansible_ssh_private_key_file**      | The path to the **coordination host private key** that will be used to access worker nodes via ssh       |

Considering that this user name is **ubuntu**, your worker nodes are **10.30.1.1** and **10.30.1.2**, and the private key are in the **~/.ssh/coordination_host**. The content of the **host.conf** file would be:

```
worker_ip_1=10.30.1.1
worker_ip_2=10.30.1.2

remote_user=ubuntu

ansible_ssh_private_key_file=~/.ssh/coordination_host
```

### _5.Install_

After configuring the `hosts.conf` file, execute the `install.sh` script in the coordination host to setup the Worker Node.

  ```
  sudo bash install.sh
  ```

## **Check**


To verify whether the worker deployment was successful, one can submit below sample requests to the docker api.

---
### Request
```bash
curl http://<worker-address>:5555/version
```

### Expected
```json
{
  "Platform": {
    "Name": "Docker Engine - Community"
  },
  "Components": [
    {
      "Name": "Engine",
      "Version": "19.03.7",
      "Details": {
        "ApiVersion": "1.40",
        "Arch": "amd64",
        "BuildTime": "2020-03-04T01:21:08.000000000+00:00",
        "Experimental": "false",
        "GitCommit": "7141c199a2",
        "GoVersion": "go1.12.17",
        "KernelVersion": "4.15.0-88-generic",
        "MinAPIVersion": "1.12",
        "Os": "linux"
      }
    },
    {
      "Name": "containerd",
      "Version": "1.2.13",
      "Details": {
        "GitCommit": "7ad184331fa3e55e52b890ea95e65ba581ae3429"
      }
    },
    {
      "Name": "runc",
      "Version": "1.0.0-rc10",
      "Details": {
        "GitCommit": "dc9208a3303feef5b3839f4323d9beb36df0a9dd"
      }
    },
    {
      "Name": "docker-init",
      "Version": "0.18.0",
      "Details": {
        "GitCommit": "fec3683"
      }
    }
  ],
  "Version": "19.03.7",
  "ApiVersion": "1.40",
  "MinAPIVersion": "1.12",
  "GitCommit": "7141c199a2",
  "GoVersion": "go1.12.17",
  "Os": "linux",
  "Arch": "amd64",
  "KernelVersion": "4.15.0-88-generic",
  "BuildTime": "2020-03-04T01:21:08.000000000+00:00"
}
```
