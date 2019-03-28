import requests

class Arrebol:
    def __init__(self, arrebolUrl):
        self.arrebolUrl = arrebolUrl

    def version(self):
        self.versionEndPoint = self.arrebolUrl + "/version"
        return requests.get(self.versionEndPoint)
