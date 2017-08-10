import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.hipchat_config_subpage import HipChatConfigSubPage

class TestHipChatConfig(WebAppTest):

    def test_hipchat_config(self):
        """
        Verify a couple of the configuration options of the hipchat plugin from
        the Jenkins configuration console
        """
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/hipchat_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        hipchat_config = yaml.load(yaml_contents)
        config_page = HipChatConfigSubPage(self.browser)
        config_page.visit()
        assert hipchat_config['API_TOKEN'] == config_page.get_api_token()
        assert hipchat_config['ROOM'] == config_page.get_room()
