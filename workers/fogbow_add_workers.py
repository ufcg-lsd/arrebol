import requests
import argparse
from fogbow_data import *
import time
import uuid
from threading import Thread

if __name__== "__main__":
    main()

def get_ras_public_key():
    response = requests.get(fogbow_data.ras_public_key_endpoint)
    publicKey = response.json()['publicKey']
    return publicKey

def create_token():
    response = requests.post(fogbow_data.as_token_endpoint,
                headers={'Content-Type':'application/json'},
                json={'credentials':{'username':as_token_username, 'password':as_token_password, 'domain':as_token_domain, 'projectname':as_token_project_name},
                        'publicKey':get_ras_public_key()})
    token = response.json()['token']
    return token

def create_compute(token, specification):
    response = requests.post(fogbow_data.ras_compute_endpoint,
                json=specification, 
                headers={'Fogbow-User-Token':token, 'Content-Type':'application/json'})
    compute_id = response.json()['id']
    return compute_id

def get_compute(compute_id, token):
    response = requests.get(fogbow_data.ras_compute_endpoint + "/" + compute_id,
                headers={'Fogbow-User-Token':token})
    return response.json()

def create_public_ip(token, compute_id):
    response = requests.post(ras_public_ip_endpoint,
            headers={'Fogbow-User-Token':token, 'Content-Type':'application/json'}
            json={'provider':ras_member_id, 'cloudName':ras_cloud_name, 'computeId':compute_id})
    public_ip_id = response.json()['id']
    return public_ip_id

def get_public_ip(token, public_ip_id):
    response = requests.get(fogbow_data.ras_public_ip_endpoint + "/" + public_ip_id),
            headers={'Fogbow-User-Token':token})
    return response.json()

def get_resource(token, specification):
    compute_id = create_compute(token, specification)
    compute_state = get_compute(compute_id, token)
    while(compute_state != "READY"):
        time.sleep(5)
        compute_state = get_compute(compute_id, token)
    public_ip_id = create_public_ip(token, compute_id)
    resource_id = str(uuid.uuid4())
    resource = {'resource_id':resource_id, 'compute_id':compute_id, 'public_ip':public_ip_id}

def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("-p", "--plugin", default="fogbow",
                    help="")
    ap.add_argument("-s", "--size",
                    help="")
    ap.add_argument("-i", "--imageId",
                    help="")
    ap.add_argument("-m", "--memory",
                    help="")
    ap.add_argument("-c", "--vCPU",
                    help="")
    ap.add_argument("-d", "--disk",
                    help="")
    args = vars(ap.parse_args())
    args['public_key'] = public_key
    args['name'] = name

    plugin = args.pop('plugin', None)
    size = int(args.pop('size', None))
    my_token = create_token()

    threads = []

    for i in range(0, size):
        threads.append(Thread(target=get_resource, args=(my_token, args)).start())
    
    for t in threads:
        t.join()