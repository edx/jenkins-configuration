import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.security_page import SecurityConfigurationPage

class TestSecurityConfiguration(WebAppTest):

    def setUp(self):
        super(TestSecurityConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/security.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.security_config = yaml.load(yaml_contents)
        self.security_page = SecurityConfigurationPage(self.browser)

    def test_security(self):
        self.security_page.visit()
        assert self.security_page.is_gh_oauth_enabled()
        for group in self.security_config['SECURITY_GROUPS']:
            for user in group['USERS']:
                permissions = self.security_page.get_user_permissions(user)
                assert all([p in group['PERMISSIONS'] for p in permissions])
