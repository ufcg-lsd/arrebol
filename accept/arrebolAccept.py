from arrebol_facade import Arrebol

class AcceptTests:
    def __init__(self, arrebolUrl):
        self.url = arrebolUrl
        self.facade = Arrebol(self.url)

    def run(self):
        pass
