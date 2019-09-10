from __future__ import absolute_import
from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class PeoplePage(PageObject):

    url = "http://{}:8080/asynchPeople".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return "People" in self.q(css='[id="main-panel"] > h1').text

    def get_number_users(self):
        # Find the number of rows in the table, but subtract one for the headers
        return len(self.q(css='[class="sortable pane bigtable"] > tbody > tr')) - 1
