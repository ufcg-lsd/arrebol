import unittest
from arrebol_facade import Arrebol

arrebolUrl="http://localhost:8080"

class AcceptTests(unittest.TestCase):

    def test_version(self):
        self.facade = Arrebol(arrebolUrl)

        #it asserts it does not error-terminate in error and version is a not
        #empty string
        response = self.facade.version()

        self.assertStatusCode(response, 201)

        #TODO: need to understand better the response method
        #the code I borrowed uses response.json()['message'] instead
        actualMessage = response.json()
        #TODO: replaces by a pattern match a.b.c?
        self.assertNotEqual(actualMessage, "")

    def assertStatusCode(self, response, expectedStatusCode):
        return response.status_code == expectedStatusCode

if __name__ == '__main__':
    unittest.main()
