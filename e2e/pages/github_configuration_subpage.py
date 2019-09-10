from __future__ import absolute_import
from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from .configuration_page import ConfigurationSubPageMixIn

class GithubConfigurationSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(GithubConfigurationSubPage, self).__init__(*args, **kwargs)
        self.name = "github-plugin-configuration"

    def get_api_url(self):
        return self.value_of_first_element_named('_.apiUrl')

    def get_cache_size(self):
        return self.value_of_first_element_named('_.clientCacheSize')
