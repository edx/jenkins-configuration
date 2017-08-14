from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class MailerConfigurationSubPage(PageObject):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

    def is_browser_on_page(self):
        self.scroll_to_element('[name="_.smtpServer"]', timeout=10)
        return self.q(css='.setting-input.validated[name="_.smtpServer"]').visible

    @property
    def nameref_id(self):
        return self.q(css=
                '[name="hudson-tasks-Mailer"]'
                ).attrs('id')[0]

    def expand_advanced(self):
        css_query='[nameref="{}"] > td > div.advancedLink > span.yui-button'.format(self.nameref_id)
        self.q(css=css_query).click()

    def get_smtp_server(self):
        return self.q(css='[name="_.smtpServer"]').attrs('value')[0]

    def get_smtp_port(self):
        return self.q(css='[name="_.smtpPort"]').attrs('value')[0]
