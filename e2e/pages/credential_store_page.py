from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class CredentialStorePage(PageObject):

    url = "http://{}:8080/credential-store/domain/_/".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return "Global credentials (unrestricted)" in self.q(css='[id="main-panel"] > h1').text

    def get_number_credentials(self):
        # Find the number of rows in the table, but subtract one for the headers
        return len(self.q(css='[class="sortable pane bigtable"] > tbody > tr')) - 1

    def get_list_of_credentials_table(self):
        # Get all text from the table's rows
        return self.q(css='[class="sortable pane bigtable"] > tbody > tr > td').text
