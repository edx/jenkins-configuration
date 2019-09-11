from __future__ import absolute_import
from unittest import TestCase

import requests

class AccessTestCase(TestCase):

    def setUp(self):
        self.jenkins_url = 'http://localhost:8080'

    def test_cli_access(self):
        cli_status_code = requests.get("{}/cli".format(self.jenkins_url)).status_code
        assert cli_status_code == 404
