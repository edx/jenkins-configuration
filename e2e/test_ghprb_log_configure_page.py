from __future__ import absolute_import
import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from .pages.ghprb_log_configure_page import GhprbLogConfigurePage

class TestGhprbLogConfigurePage(WebAppTest):

    def setUp(self):
        super(TestGhprbLogConfigurePage, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/log_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.log_config = yaml.safe_load(yaml_contents)
        self.ghprb_log_configure_page = GhprbLogConfigurePage(self.browser)

    def test_ghprb_configure_page(self):
        """
        Ensure the configuration page for our test data log (GHPRB)
        has the appropriate name and includes all of the name, log_level
        pairs from the log_config.yml file.
        """
        ghprb_log_configure_page = self.ghprb_log_configure_page.visit()
        assert self.log_config[0]["LOG_RECORDER"] == self.ghprb_log_configure_page.get_log_recorder_name()

        ghprb_loggers = self.ghprb_log_configure_page.get_loggers_with_level()
        for log in self.log_config[0]["LOGGERS"]:
            assert (log["name"], log["log_level"]) in ghprb_loggers
