from __future__ import absolute_import
from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class JenkinsDashboardPage(PageObject):

    url = "http://{}:8080".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return 'dashboard [jenkins]' in self.browser.title.lower()

    def get_jobs_list(self):
        return self.q(css='[id="projectstatus"] > tbody > tr').attrs('id')
