import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.configuration_page import JenkinsConfigurationPage

class TestConfiguration(WebAppTest):
    
    def setUp(self):
        super(TestConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/main_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.main_config = yaml.safe_load(yaml_contents)['MAIN']
        self.config_page = JenkinsConfigurationPage(self.browser)

    def test_main_config(self):
        """
        Test the main configuration panel on Jenkins
        """
        config_page = self.config_page.visit()
        config_page.expand_advanced()

        # Once setting these parameters is enabled, we can
        # use the following 2 tests:
        #
        # assert self.main_config['WORKSPACE_ROOT_DIR'] == config_page.get_workspace_root_dir()
        # assert self.main_config['BUILD_RECORD_ROOT_DIR'] == config_page.get_build_record_root_dir()

        assert self.main_config['SYSTEM_MESSAGE'] == config_page.get_system_message()
        assert str(self.main_config['NUMBER_OF_EXECUTORS']) == config_page.get_number_executors()
        assert self.main_config['LABELS'] == config_page.get_labels()
        assert self.main_config['USAGE'] == config_page.get_mode()
        assert str(self.main_config['QUIET_PERIOD']) == config_page.get_quiet_period()
        assert str(self.main_config['SCM_RETRY_COUNT']) == config_page.get_scm_checkout_retry_count()
