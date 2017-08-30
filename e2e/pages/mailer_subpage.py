from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class MailerConfigurationSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(MailerConfigurationSubPage, self).__init__(*args, **kwargs)
        self.name = "hudson-tasks-Mailer"

    def get_smtp_server(self):
        return self.q(css='[name="_.smtpServer"]').attrs('value')[0]

    def get_smtp_port(self):
        return self.q(css='[name="_.smtpPort"]').attrs('value')[0]
