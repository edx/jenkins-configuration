from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn


class TimestamperConfigSubPage(ConfigurationSubPageMixIn, PageObject):


    def __init__(self, *args, **kwargs):
        super(TimestamperConfigSubPage, self).__init__(*args, **kwargs)
        self.name = "hudson-plugins-timestamper-TimestamperConfig"

    def enabled_on_all_pipelines(self):
        enabled = self.q(css='[name="_.allPipelines"]').attrs('checked')[0]
        return enabled.lower() == 'true'

    def get_elapsed_time_format(self):
        return self.value_of_first_element_named('_.elapsedTimeFormat')
