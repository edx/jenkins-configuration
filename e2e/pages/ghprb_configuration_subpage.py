from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class GHPRBConfigurationSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(GHPRBConfigurationSubPage, self).__init__(*args, **kwargs)
        self.name = "org-jenkinsci-plugins-ghprb-GhprbTrigger"

    def get_admin_list(self):
        return self.q(css='[name="_.adminlist"]').text[0]

    def get_request_testing_phrase(self):
        return self.q(css='[name="_.requestForTestingPhrase"]').attrs('value')[0]

    def get_build_log_portion(self):
        return self.q(css='[name="_.logExcerptLines"').attrs('value')[0]
