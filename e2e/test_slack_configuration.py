import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.slack_config_subpage import SlackConfigSubPage

class TestSlackConfig(WebAppTest):

    def test_slack_config(self):
        """
        Verify a couple of the configuration options of the slack plugin from
        the Jenkins configuration console
        """
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/slack_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        slack_config = yaml.safe_load(yaml_contents)
        config_page = SlackConfigSubPage(self.browser)
        config_page.visit()
        assert slack_config['SLACK_ROOM'] == config_page.get_room()
