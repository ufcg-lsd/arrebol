import requests
import argparse
import fogbow_data
from fogbow_data import *

ap = argparse.ArgumentParser()
ap.add_argument("-p", "--plugin", default="fogbow",
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
args['publicKey'] = public_key
args['name'] = name

plugin = args.pop('plugin', None)

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
    order = response.json()['id']
    return order

def get_compute(computeId, token):
    response = requests.get(fogbow_data.ras_compute_endpoint + "/" + computeId,
                headers={'Fogbow-User-Token':token})
    return response.json()


print(args)

my_token = create_token()
order = create_compute(my_token, args)
print(order)

compute = get_compute(order, my_token)
print(compute)