import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.job_config_history_subpage import JobConfigHistorySubPage

class TestJobConfigHistory(WebAppTest):

    def setUp(self):
        super(TestJobConfigHistory, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/job_config_history.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.job_config_history = yaml.load(yaml_contents)
        self.config_page = JobConfigHistorySubPage(self.browser)

    def test_job_config_history(self):
        """
        Verify the Jenkins Config History plugin has been configured
        properly.
        """
        self.config_page.visit()
        self.config_page.expand_advanced()
        assert self.job_config_history['HISTORY_ROOT_DIR'] == self.config_page.get_history_root_dir()
        assert self.job_config_history['MAX_HISTORY_ENTRIES'] == self.config_page.get_max_history_entries()
        assert str(self.job_config_history['SKIP_DUPLICATE_HISTORY']).lower() == self.config_page.get_skip_duplicate_history()
        assert self.job_config_history['SHOW_BUILD_BADGES'] == self.config_page.get_show_build_badges()
