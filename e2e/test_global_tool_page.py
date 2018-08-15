import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.global_tool_page import GlobalToolConfigurationPage

class TestGlobalToolConfiguration(WebAppTest):

    def setUp(self):
        super(TestGlobalToolConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            python_yaml_contents = open(
                    "{}/python_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.python_config = yaml.safe_load(python_yaml_contents)
        self.global_tool_page = GlobalToolConfigurationPage(self.browser)

    def test_python_installations(self):
        self.global_tool_page.visit()
        self.global_tool_page.expand_installations('python')
        installed_versions = self.global_tool_page.get_python_installations
        for py in self.python_config['PYTHON_INSTALLATIONS']:
            assert py['PYTHON_PATH'] in installed_versions()

