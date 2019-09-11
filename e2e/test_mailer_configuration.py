from __future__ import absolute_import
import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from .pages.mailer_subpage import MailerConfigurationSubPage

class TestMailerConfiguration(WebAppTest):
    
    def setUp(self):
        super(TestMailerConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/mailer_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.mailer_config = yaml.safe_load(yaml_contents)
        self.config_page = MailerConfigurationSubPage(self.browser)

    def test_mailer_config(self):
        self.config_page.visit()
        # test a component of the main mailer cofig
        assert self.mailer_config['SMTP_SERVER'] == self.config_page.get_smtp_server()
        self.config_page.expand_advanced()
        # test an advanced component of the mailer config
        assert str(self.mailer_config['SMTP_PORT']) == self.config_page.get_smtp_port()
