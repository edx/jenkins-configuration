import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.credential_store_page import CredentialStorePage

class TestConfiguration(WebAppTest):
    
    def setUp(self):
        super(TestConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/credentials.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.credentials = yaml.safe_load(yaml_contents)
        self.credential_store_page = CredentialStorePage(self.browser)

    def test_credential_store(self):
        """
        Test the credential store table for the test_data credentials.
        """
        credential_store_page = self.credential_store_page.visit()
        assert len(self.credentials) == credential_store_page.get_number_credentials()
        for cred in self.credentials:
            # Make sure each credentials description exists in the table. This is the
            # only info consistently shown for all credential types.
            assert cred['description'] in credential_store_page.get_list_of_credentials_table()
