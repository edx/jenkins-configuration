import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.git_configuration_subpage import GitConfigurationSubPage

class TestGitConfiguration(WebAppTest):
    
    def setUp(self):
        super(TestGitConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/git_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.git_config = yaml.safe_load(yaml_contents)
        self.config_page = GitConfigurationSubPage(self.browser)

    def test_git_config(self):
        self.config_page.visit()
        assert self.git_config['NAME'] == self.config_page.get_global_git_user_name()
