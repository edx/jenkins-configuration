import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.splunk_config_subpage import SplunkConfigSubPage

class TestSplunkConfig(WebAppTest):

    def test_splunk_config(self):
        """
        Verify a couple of the configuration options of the splunk plugin from
        the Jenkins configuration console
        """
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/splunk_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        splunk_config = yaml.load(yaml_contents)
        config_page = SplunkConfigSubPage(self.browser)
        config_page.visit()
        assert str(splunk_config['MAX_EVENT_BATCH_SIZE']) == config_page.get_batch_size()
        assert len(splunk_config['METADATA']) == len(config_page.get_meta_data_items())
