import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.ec2_configuration_subpage import Ec2ConfigurationSubPage

class TestEc2ConfigurationSubPage(WebAppTest):

    def setUp(self):
        super(TestEc2ConfigurationSubPage, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/ec2_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
           pass
        self.ec2_config = yaml.load(yaml_contents)
        self.ec2_cloud_config = self.ec2_config['CLOUDS']
        self.config_page = Ec2ConfigurationSubPage(self.browser)

    def test_ghprb_config(self):
        """
        verify a couple of the configuration options of the EC2 plugin from
        the Jenkins configuration console
        """
        self.config_page.visit()
