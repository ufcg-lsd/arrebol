#!/bin/bash

readonly ANSIBLE_FILES_PATH_PATTERN="ansible_files_path"
readonly PRIVATE_KEY_FILE_PATH_PATTERN="ansible_ssh_private_key_file"
readonly DEPLOYED_WORKER_IP_PATTERN="deployed_worker_ip"
readonly NFS_SERVER_PATTERN="nfs_server"
readonly NFS_SERVER_DIR_PATTERN="nfs_server_dir"
readonly DEPLOY_WORKER_YML_FILE="deploy-worker.yml"

readonly MY_PATH="`dirname \"$0\"`"              
readonly MY_PATH="`( cd \"${MY_PATH}\" && pwd )`" 

check_variables() {
  for var in "$@"
  do
    if [ -z "${!var}" ]; then
      echo "Error. The field ${var} was not set."
      exit 1
    fi
  done
}

if [ -z "$MY_PATH" ] ; then
  # For some reason, the path is not accessible
  # to the script (e.g. permissions re-evaled after suid)
  exit 1
fi

HOSTS_CONF_FILE="${MY_PATH}/hosts.conf"

ANSIBLE_FILES_PATH=$(grep "${ANSIBLE_FILES_PATH_PATTERN}" "${HOSTS_CONF_FILE}" | awk -F "=" '{print $2}')
PRIVATE_KEY_FILE_PATH=$(grep "${PRIVATE_KEY_FILE_PATH_PATTERN}" "${HOSTS_CONF_FILE}" | awk -F "=" '{print $2}')
NFS_SERVER=$(grep -w "${NFS_SERVER_PATTERN}" "${HOSTS_CONF_FILE}" | awk -F "=" '{print $2}')
NFS_SERVER_DIR=$(grep -w "${NFS_SERVER_DIR_PATTERN}" "${HOSTS_CONF_FILE}" | awk -F "=" '{print $2}')

check_variables ANSIBLE_FILES_PATH PRIVATE_KEY_FILE_PATH NFS_SERVER NFS_SERVER_DIR

ANSIBLE_HOSTS_FILE="${ANSIBLE_FILES_PATH}/hosts"

# Clears the worker machine addresses
sed -i '/\[worker-machine\]/,/\[worker-machine:vars\]/{//!d}' "${ANSIBLE_HOSTS_FILE}"

# Fill worker address field of hosts file
grep "${DEPLOYED_WORKER_IP_PATTERN}" "${HOSTS_CONF_FILE}"| while read -r line ; do
    DEPLOYED_WORKER_IP=$(echo ${line} | awk -F "=" '{print $2}')
    sed -i "/\[worker-machine:vars\]/i ${DEPLOYED_WORKER_IP}" ${ANSIBLE_HOSTS_FILE}
done

# Writes the path of the private key file in Ansible hosts file
sed -i "s#.*${PRIVATE_KEY_FILE_PATH_PATTERN}=.*#${PRIVATE_KEY_FILE_PATH_PATTERN}=${PRIVATE_KEY_FILE_PATH}#" ${ANSIBLE_HOSTS_FILE}

(cd ${ANSIBLE_FILES_PATH} && ansible-playbook -vvv ${DEPLOY_WORKER_YML_FILE} -e nfs_server=${NFS_SERVER} -e nfs_server_dir=${NFS_SERVER_DIR})