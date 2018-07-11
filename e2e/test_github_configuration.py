import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.github_configuration_subpage import GithubConfigurationSubPage

class TestGithubConfiguration(WebAppTest):
    
    def setUp(self):
        super(TestGithubConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/github_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.github_config = yaml.safe_load(yaml_contents)
        self.config_page = GithubConfigurationSubPage(self.browser)

    def test_github_config(self):
        self.config_page.visit()
        # test a component of the main ggithub config
        assert self.github_config[0]['API_URL'] == self.config_page.get_api_url()
        self.config_page.expand_advanced()
        # test an advanced component of the github config
        assert str(self.github_config[0]['CACHE_SIZE']) == self.config_page.get_cache_size()
