from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class JenkinsLogRecorderPage(PageObject):

    url = "http://{}:8080/log".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return "log [jenkins]" in self.browser.title.lower()

    def get_log_recorders(self):
        return self.q(css='[id="logRecorders"] > tbody > tr > td > a').text
