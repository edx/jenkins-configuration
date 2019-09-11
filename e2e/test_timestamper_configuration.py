from __future__ import absolute_import
import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from .pages.timestamper_config_subpage import TimestamperConfigSubPage


class TestTimestamperConfig(WebAppTest):


    def test_timestamper_config(self):
        """
        Verify a couple of the configuration options of the timestamper plugin
        from the Jenkins configuration console
        """
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/timestamper_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        timestamper_config = yaml.safe_load(yaml_contents)
        config_page = TimestamperConfigSubPage(self.browser)
        config_page.visit()
        assert timestamper_config['ENABLED_ON_PIPELINES'] == config_page.enabled_on_all_pipelines()
        assert timestamper_config['ELAPSED_TIME_FORMAT'] == config_page.get_elapsed_time_format()
