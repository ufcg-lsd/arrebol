import requests

class Arrebol:
    def __init__(self, arrebolUrl):
        self.arrebolUrl = arrebolUrl

    def version(self):
        self.versionEndPoint = self.arrebolUrl + "/version"
        return requests.get(self.versionEndPoint)

    def create(self, path_to_job):
        self.createEndPoint = self.arrebolUrl + "/job"
        data = open(path_to_job)
        return requests.post(self.createEndPoint, data=data)
