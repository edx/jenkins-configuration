from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class EmailExtConfigurationSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(EmailExtConfigurationSubPage, self).__init__(*args, **kwargs)
        self.name = "hudson-plugins-emailext-ExtendedEmailPublisher"

    def get_default_email_body(self):
        return self.value_of_first_element_named('ext_mailer_default_body')

    def get_advanced_email_properties(self):
        return self.value_of_first_element_named('ext_mailer_adv_properties')
