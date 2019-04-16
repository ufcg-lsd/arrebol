import requests
import argparse
from fogbow_data import *
import time
import uuid
import sys

compute_request_failed_message = "Failed: The compute request was failed."
public_ip_request_failed_message = "Failed: The public ip request was failed."

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

def add_resource(token, specification):
    compute_id = create_compute(token, specification)
    compute_state = get_compute(token, compute_id)['state']
    while(compute_state != "READY" and compute_state != "FAILED"):
        time.sleep(3)
        compute_state = get_compute(token, compute_id)['state']
    if(compute_state == "READY"):
        public_ip_id = create_public_ip(token, compute_id)
        public_ip_state = get_public_ip(token, public_ip_id)['state']
        while(public_ip_state != "READY" and public_ip_state != "FAILED"):
            time.sleep(3)
            public_ip_state = get_public_ip(token, public_ip_id)['state']

        if(public_ip_state == "READY"):
            resource_id = str(uuid.uuid4())
            resource = {'resource_id':resource_id, 'compute_id':compute_id, 'public_ip':public_ip_id}
            return resource
        elif(public_ip_state == "FAILED"):
            delete_public_ip(token, public_ip_id)
            time.sleep(1)
            delete_compute(token, compute_id)
            return public_ip_request_failed_message
    else:
        delete_compute(token, compute_id)
        return compute_request_failed_message


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("-i", "--imageId", default="38bbda26-a44f-4ed6-85c3-8c111e315ebf",
                    help="Id of the compute image")
    ap.add_argument("-m", "--memory", default="1024",
                    help="Compute memory size")
    ap.add_argument("-c", "--vCPU", default="2",
                    help="Amount of compute cpu")
    ap.add_argument("-d", "--disk", default="20",
                    help="Compute disk size")
    args = vars(ap.parse_args())
    args['public_key'] = public_key
    args['name'] = compute_name
    my_token = create_token()

    response = add_resource(my_token, args)
    if(type(response) is not dict):
         print(response)
         sys.exit(1)
    else:
        print(response)
        sys.exit(0)

if __name__== "__main__":
    main()