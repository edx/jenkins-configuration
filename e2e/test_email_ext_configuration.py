import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.email_ext_subpage import EmailExtConfigurationSubPage

class TestEmailExtConfiguration(WebAppTest):
    
    def setUp(self):
        super(TestEmailExtConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/email_ext_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.email_config = yaml.safe_load(yaml_contents)
        self.config_page = EmailExtConfigurationSubPage(self.browser)

    def test_email_ext_config(self):
        self.config_page.visit()
        # test a component of the email ext cofig
        assert 'what a nice email $BUILD_NUMBER' in self.config_page.get_default_email_body()
        self.config_page.expand_advanced()
        # test an advanced component of the email ext config
        assert str(self.email_config['ADV_PROPERTIES']) == self.config_page.get_advanced_email_properties()
