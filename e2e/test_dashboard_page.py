import unittest
import yaml
import os
from bok_choy.web_app_test import WebAppTest
from pages.dashboard_page import JenkinsDashboardPage

class TestDashboard(WebAppTest):
    
    def setUp(self):
        super(TestDashboard, self).setUp()
        config_path = os.getenv('CONFIG_PATH')
        try:
            yaml_contents = open(
                    "{}/seed_config.yml".format(config_path), 'r'
                    ).read()
        except IOError:
            pass
        self.seed_config = yaml.load(yaml_contents)
        self.dashboard_page = JenkinsDashboardPage(self.browser)

    def test_main_config(self):
        """
        Test the main dashboard on Jenkins
        """
        dashboard_page = self.dashboard_page.visit()
        assert "job_" + self.seed_config['NAME'] in dashboard_page.get_jobs_list()
