from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class GitConfigurationSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(GitConfigurationSubPage, self).__init__(*args, **kwargs)
        self.name = "hudson-plugins-git-GitSCM"

    def get_global_git_user_name(self):
        return self.value_of_first_element_named('_.globalConfigName')

