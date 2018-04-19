from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class SplunkConfigSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(SplunkConfigSubPage, self).__init__(*args, **kwargs)
        self.name = "com-splunk-splunkjenkins-SplunkJenkinsInstallation"

    def get_batch_size(self):
        return self.value_of_first_element_named('_.maxEventsBatchSize')

    def get_meta_data_items(self):
        return self.q(css='[name="metadataItemSet"]')
