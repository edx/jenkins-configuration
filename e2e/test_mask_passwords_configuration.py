import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.mask_passwords_configuration_subpage import MaskPasswordsSubPage

class TestMaskPasswordsConfiguration(WebAppTest):

    def setUp(self):
        super(TestMaskPasswordsConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/mask_passwords_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.mask_passwords_config = yaml.safe_load(yaml_contents)
        self.config_page = MaskPasswordsSubPage(self.browser)

    def test_mask_passwords_config(self):
        """
        Verify the mask passwords plugin was configured properly.
        """
        self.config_page.visit()

        selected_masked_passwords = self.config_page.get_selected_masked_password_classes()
        for class_name in self.mask_passwords_config['MASKED_PARAMETER_CLASSES']:
            assert class_name in selected_masked_passwords

        globalVars = self.config_page.get_global_var_values()
        for name in self.mask_passwords_config['NAME_PASSWORD_PAIRS']:
            assert name['NAME'] in globalVars
