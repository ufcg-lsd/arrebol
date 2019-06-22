import unittest
from arrebol_facade import Arrebol

arrebolUrl="http://localhost:8080"

class AcceptTests(unittest.TestCase):

    def assertStatusCode(self, response, expectedStatusCode):
        self.assertEquals(response.status_code, expectedStatusCode)

    def test_version(self):
        self.facade = Arrebol(arrebolUrl)

        #it asserts it does not error-terminate in error and version is a not
        #empty string
        response = self.facade.version()

        self.assertStatusCode(response, 200)

        #TODO: need to understand better the response method
        #the code I borrowed uses response.json()['message'] instead
        actualMessage = response.json()
        #TODO: replaces by a pattern match a.b.c?
        self.assertNotEqual(actualMessage, "")

    def test_create(self):
        self.facade = Arrebol(arrebolUrl)

        response = self.facade.create("jobs/simple_job.json")
        self.assertStatusCode(response, 201)
        #TODO: assert job id and other stuff

    def test_create_singletask_job(self):
        self.facade = Arrebol(arrebolUrl)

        response = self.facade.create("jobs/single_task_job.json")
        self.assertStatusCode(response, 201)

if __name__ == '__main__':
    unittest.main()
