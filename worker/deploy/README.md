# Worker Node deployment

Arrebol tasks are processed by the Workers. In a typical deployment, multiple Workers are deployed together in a single virtual machine, a `Worker Node`. As each task run in an isolated docker container, the Docker engine and other dependencies must have be installed and configured in the Worker Node. Below, we detail the configuration procedure.

## Requeriments

Before the configuration and installation of Worker Node dependencies, each Worker Node virtual machine should be configured to be reached via SSH (using a rsa key pair). Also, the [Ansible](https://www.ansible.com/) automation tool should be installed in the deploy `coordination host`. This host is a machine that commands that deploy. To this end, the `coordination host` should be able to access the Worker Node through SSH.

To install Ansible run these commands:
```bash
sudo apt update
sudo apt install software-properties-common
sudo apt-add-repository --yes --update ppa:ansible/ansible
sudo apt install -y ansible
```

`Note: The deployment requires versions newer than 14 for ubuntu.`

## Configuration

The `hosts.conf` configuration file should be edited to declare the Worker Node. See below how to edit it.

### Example
```
worker_ip_1=10.30.1.36
worker_ip_2=10.30.1.37

remote_user=ubuntu

# The NFS Server Address
nfs_server=10.11.16.136

# The NFS Server directory to mount
nfs_server_dir=/nfs

# Required (if not specified, ansible will use the host ssh keys)
ansible_ssh_private_key_file=/home/admin/.ssh/priv_key

# Default
ansible_files_path=./ansible-playbook
```

## Install

After configuring the `hosts.conf` file, execute the `install.sh` script in the coordination host to setup the Worker Node.

  ```
  sudo bash install.sh
  ```

## Check 

To verify whether the worker deployment was successful, one can submit below sample requests to the docker api.

`Note 1: By default, the deployment opens port 5555 for the docker api.`

`Note 2: If you use a cloud provider you may need to add a firewall rule.`

---
Request
```bash
curl http://<worker-address>:5555/version
```

Expected
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
