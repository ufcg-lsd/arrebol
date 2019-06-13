import requests
import argparse
from fogbow_data import *
import time
import uuid
import sys
import constants

def get_ras_public_key():
    response = requests.get(ras_public_key_endpoint)
    publicKey = response.json()['publicKey']
    return publicKey

def create_token():
    response = requests.post(as_token_endpoint,
                headers={'Content-Type':'application/json'},
                json={'credentials':{'username':as_token_username, 'password':as_token_password, 'domain':as_token_domain, 'projectname':as_token_project_name},
                        'publicKey':get_ras_public_key()})
    token = response.json()['token']
    return token

def getImages(token):
    response = requests.get(ras_images,
                headers={'Fogbow-User-Token':token, 'Content-Type':'application/json'})
    return response.json()

def create_compute(token, specification):
    response = requests.post(ras_compute_endpoint,
                json=specification, 
                headers={'Fogbow-User-Token':token, 'Content-Type':'application/json'})
    compute_id = response.json()['id']
    return compute_id

def get_compute(token, compute_id):
    response = requests.get(ras_compute_endpoint + "/" + compute_id,
                headers={'Fogbow-User-Token':token})
    return response.json()

def delete_compute(token, compute_id):
    response = requests.delete(ras_compute_endpoint + "/" + compute_id,
                headers={'Fogbow-User-Token':token})

def create_public_ip(token, compute_id):
    response = requests.post(ras_public_ip_endpoint,
                headers={'Fogbow-User-Token':token, 'Content-Type':'application/json'},
                json={'provider':ras_member_id, 'cloudName':ras_cloud_name, 'computeId':compute_id})
    public_ip_id = response.json()['id']
    return public_ip_id

def get_public_ip(token, public_ip_id):
    response = requests.get(ras_public_ip_endpoint + "/" + public_ip_id, 
                headers={'Fogbow-User-Token':token})
    return response.json()

def delete_public_ip(token, public_ip_id):
    response = requests.delete(ras_public_ip_endpoint + "/" + public_ip_id,
                headers={'Fogbow-User-Token':token})

def wait_compute(token, compute_id, interval, max_tries):
    tries = 0
    compute_state = get_compute(token, compute_id)['state']
    while(tries < max_tries and compute_state != "READY" and compute_state != "FAILED"):
        time.sleep(constants.INTERVAL_CHECK_COMPUTE_STATE_SEC)
        compute_state = get_compute(token, compute_id)['state']
        tries += 1
    if(compute_state == "READY"):
        return {"result":True, "message": constants.COMPUTE_REQUEST_SUCCESSFUL_MESSAGE}
    elif(compute_state == "FAILED"):
        return {"result":False, "message": constants.COMPUTE_REQUEST_FAILED_MESSAGE}
    elif(tries == max_tries):
        return {"result":False, "message": constants.COMPUTE_REQUEST_MAX_TRIES_MESSAGE}

def wait_public_ip(token, public_ip_id, interval, max_tries):
    tries = 0
    public_ip_state = get_public_ip(token, public_ip_id)['state']
    while(tries < max_tries and public_ip_state != "READY" and public_ip_state != "FAILED"):
        time.sleep(constants.INTERVAL_CHECK_PUBLIC_IP_STATE_SEC)
        public_ip_state = get_public_ip(token, public_ip_id)['state']
        tries += 1
    if(public_ip_state == "READY"):
        return {"result":True, "message": constants.PUBLIC_IP_REQUEST_SUCCESSFUL_MESSAGE}
    elif(public_ip_state == "FAILED"):
        return {"result":False, "message": constants.PUBLIC_IP_REQUEST_FAILED_MESSAGE}
    elif(tries == max_tries):
        return {"result":False, "message": constants.PUBLIC_IP_REQUEST_MAX_TRIES_MESSAGE}

def add_resource(token, specification):
    compute_id = create_compute(token, specification)
    compute_state = get_compute(token, compute_id)['state']
    result, message = wait_compute(token, compute_id, constants.INTERVAL_CHECK_COMPUTE_STATE_SEC, 20)
    
    if result:
        public_ip_id = create_public_ip(token, compute_id)
        result, message = wait_public_ip(token, public_ip_id, constants.INTERVAL_CHECK_PUBLIC_IP_STATE_SEC, 20)
        if result:
            resource = {'compute_id':compute_id, 'public_ip':public_ip_id}
            return resource
        else:
            delete_public_ip(token, public_ip_id)
            time.sleep(1)
            delete_compute(token, compute_id)
            return message
    else:
        delete_compute(token, compute_id)
        return message



def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("-i", "--imageId",
                    help="Id of the compute image")
    ap.add_argument("-m", "--memory",
                    help="Compute memory size")
    ap.add_argument("-c", "--vCPU",
                    help="Amount of compute cpu")
    ap.add_argument("-d", "--disk",
                    help="Compute disk size")
    ap.add_argument("-n", "--name",
                    help="Compute name")
    args = vars(ap.parse_args())

    #TODO Add information about using args.
    #args['public_key'] = public_key
    my_token = create_token()

    response = add_resource(my_token, args)
    if(type(response) is not dict):
        print(response)
        sys.exit(1)
    else:
        print(response)
        my_file = open("workers.txt", "a")
        my_file.write(str(response))
        my_file.write('\n')
        my_file.close()
        sys.exit(0)

if __name__== "__main__":
    main()