from arrebol_facade import Arrebol

arrebolUrl=""

class AcceptTests(unittest.TestCase):
    def test_version(self):
        self.facade = Arrebol(arrebolUrl)
        self.assertTrue(False)

if __name__ == '__main__':
    unittest.main()
