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

    def test_ec2_config(self):
        """
        Verify a couple of the configuration options of the EC2 plugin from
        the Jenkins configuration console
        """
        self.config_page.visit()

        # Since there may be several clouds and amis, get a list
        # of the configurable settings and make sure content from
        # the yaml file exists in them.
        cloud_names = self.config_page.get_cloud_names()
        cloud_regions = self.config_page.get_cloud_regions()
        cloud_access_keys = self.config_page.get_cloud_access_keys()
        ami_descriptions = self.config_page.get_ami_descriptions()
        ami_ids = self.config_page.get_ami_ids()
        ami_zones = self.config_page.get_ami_zones()
        ssh_ports = self.config_page.get_ssh_ports()
        fs_roots = self.config_page.get_fs_roots()
        idle_termination_times = self.config_page.get_idle_termination_times()

        for cloud in self.ec2_cloud_config:
            assert cloud["NAME"] in cloud_names
            assert cloud["REGION"] in cloud_regions
            assert cloud["ACCESS_KEY_ID"] in cloud_access_keys
            for ami in cloud["AMIS"]:
                assert ami["AMI_ID"] in ami_ids
                assert ami["DESCRIPTION"] in ami_descriptions
                assert ami["AVAILABILITY_ZONE"] in ami_zones
                assert ami["SSH_PORT"] in ssh_ports
                assert ami["REMOTE_FS_ROOT"] in fs_roots
                assert ami["IDLE_TERMINATION_MINUTES"] in idle_termination_times
