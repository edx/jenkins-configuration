import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.ghprb_configuration_subpage import GHPRBConfigurationSubPage

class TestGHPRBConfiguration(WebAppTest):

    def setUp(self):
        super(TestGHPRBConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/ghprb_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.ghprb_config = yaml.safe_load(yaml_contents)
        self.config_page = GHPRBConfigurationSubPage(self.browser)

    def test_ghprb_config(self):
        """
        verify a couple of the configuration options of the GHPRB plugin from
        the Jenkins configuration console
        """
        self.config_page.visit()
        # test a component of the main GHPRB cofig
        assert ' '.join(self.ghprb_config['ADMIN_LIST']) == self.config_page.get_admin_list()
        self.config_page.expand_advanced()
        # test some advanced components of the GHPRB config
        assert self.ghprb_config['REQUEST_TESTING_PHRASE'] == self.config_page.get_request_testing_phrase()
        assert ' '.join(self.ghprb_config['BLACK_LIST_LABELS']) == self.config_page.get_black_list_labels()
        # test one of the extension configurations of the GHPRB
        assert str(self.ghprb_config['BUILD_LOG_LINES_TO_DISPLAY']) == self.config_page.get_build_log_portion() == '25'
