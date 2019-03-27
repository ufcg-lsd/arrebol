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

        actualMessage = response.json()['message']
        self.assertIsInstance(actualMessage, str)
        self.assertNotEqual(actualMessage, "")

    def assertStatusCode(self, response, expectedStatusCode):
        return response.status_code == expectedStatusCode

if __name__ == '__main__':
    unittest.main()
