from __future__ import absolute_import
import unittest
import yaml
import os

import pytest

from bok_choy.web_app_test import WebAppTest
from .pages.people_page import PeoplePage

test_shard = os.getenv('TEST_SHARD')

class TestConfiguration(WebAppTest):
    
    def setUp(self):
        super(TestConfiguration, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/user_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            self.fail("Unable to load {}/user_config.yml".format(config_path))
        self.users = yaml.safe_load(yaml_contents)
        self.people_page = PeoplePage(self.browser)

    @pytest.mark.skipif(test_shard != 'shard_2', reason='incorrect shard value')
    def test_user_creation(self):
        """
        Test the people table to see if the correct number of users has been created
        """
        people_page = self.people_page.visit()
        # check the number of created users. 'System' and 'admin' will already
        # be created, so exclude them from the test
        assert len(self.users) == people_page.get_number_users() - 2
