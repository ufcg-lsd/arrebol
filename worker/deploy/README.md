# Worker Node deployment

Arrebol tasks are processed by Workers. In a typical deployment, a few Workers are deployed together in a single virtual machine, a Worker Node. As each task run in an isolated docker container, the Docker engine and other dependencies must have be installed and configured in the Worker Node. Below, we described how to configure the Worker Node.

## Requeriments

Before the configuration and installation of Worker Node dependencies, each Worker Node virtual machine should be configured to be reached via SSH (using a rsa key pair). Also, the [Ansible automation tool](https://www.ansible.com/) should be installed in the deploy coordination host.
To install Ansible run these commands:
```bash
sudo apt update
sudo apt install software-properties-common
sudo apt-add-repository --yes --update ppa:ansible/ansible
sudo apt install ansible
```

`Warning:` Deployment does not work for nodes with Ubuntu version 14 or earlier.

## Configuration

The `hosts.conf` configuration file should be edited to declare the Worker Node. See below how to edit it.

### Example
```
# Required
deployed_worker_ip=10.30.1.36

# Required
remote_user=ubuntu

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

`Warning: By default, the deployment opens port 5555 for the docker api.`

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
