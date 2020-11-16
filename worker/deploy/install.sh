#!/bin/bash

readonly ANSIBLE_HOSTS_FILE="./ansible-playbook/hosts"
readonly PRIVATE_KEY_FILE_PATH_PATTERN="ansible_ssh_private_key_file"
readonly DEPLOYED_WORKER_IP_PATTERN="worker_ip"
readonly DEPLOY_WORKER_YML_FILE="deploy-worker.yml"

readonly MY_PATH="`dirname \"$0\"`"              
readonly MY_PATH="`( cd \"${MY_PATH}\" && pwd )`" 

if [ -z "$MY_PATH" ] ; then
  # For some reason, the path is not accessible
  # to the script (e.g. permissions re-evaled after suid)
  exit 1
fi

HOSTS_CONF_FILE="${MY_PATH}/hosts.conf"
PRIVATE_KEY_FILE_PATH=$(grep "${PRIVATE_KEY_FILE_PATH_PATTERN}" "${HOSTS_CONF_FILE}" | awk -F "=" '{print $2}')

# Clears the worker machine addresses
sed -i '/\[worker-machine\]/,/\[worker-machine:vars\]/{//!d}' "${ANSIBLE_HOSTS_FILE}"

# Fill worker address field of hosts file
grep "${DEPLOYED_WORKER_IP_PATTERN}" "${HOSTS_CONF_FILE}"| while read -r line ; do
    DEPLOYED_WORKER_IP=$(echo ${line} | awk -F "=" '{print $2}')
    sed -i "/\[worker-machine:vars\]/i ${DEPLOYED_WORKER_IP}" ${ANSIBLE_HOSTS_FILE}
done

# Writes the path of the private key file in Ansible hosts file
sed -i "s#.*${PRIVATE_KEY_FILE_PATH_PATTERN}=.*#${PRIVATE_KEY_FILE_PATH_PATTERN}=${PRIVATE_KEY_FILE_PATH}#" ${ANSIBLE_HOSTS_FILE}

(cd ${ANSIBLE_FILES_PATH} && ansible-playbook -vvv ${DEPLOY_WORKER_YML_FILE})