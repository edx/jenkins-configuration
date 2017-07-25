import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.log_recorder_page import JenkinsLogRecorderPage

class TestLogRecorderPage(WebAppTest):

    def setUp(self):
        super(TestLogRecorderPage, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/log_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.log_config = yaml.load(yaml_contents)
        self.log_recorder_page = JenkinsLogRecorderPage(self.browser)

    def test_log_recorder_page(self):
        """
        Ensure all the desired log recorders are shown
        on the page.
        """
        log_recorder_page = self.log_recorder_page.visit()
        logRecordersList = log_recorder_page.get_log_recorders()
        assert self.log_config[0]["LOG_RECORDER"] in logRecordersList
