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
            security_yaml_contents = open(
                    "{}/security.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        try:
            main_yaml_contents = open(
                    "{}/main_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.security_config = yaml.safe_load(security_yaml_contents)
        self.main_config = yaml.safe_load(main_yaml_contents)
        self.security_page = SecurityConfigurationPage(self.browser)

    def test_security(self):
        self.security_page.visit()
        assert self.security_page.is_gh_oauth_enabled()
        for group in self.security_config['SECURITY_GROUPS']:
            for user in group['USERS']:
                permissions = self.security_page.get_user_permissions(user)
                assert all([p in group['PERMISSIONS'] for p in permissions])
        assert self.main_config['CLI']['CLI_ENABLED'] == self.security_page.is_cli_remoting_enabled()
        assert self.security_config['DSL_SCRIPT_SECURITY_ENABLED'] == self.security_page.is_dsl_script_security_enabled()
