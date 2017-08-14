from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class GhprbLogConfigurePage(PageObject):

    url = "http://{}:8080/log/Ghprb/configure".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return self.q(css='[class="setting-input  auto-complete  yui-ac-input"]').is_present()

    def get_log_recorder_name(self):
        return self.q(css='[name="_.name"]').attrs('value')[0]

    def get_loggers_with_level(self):
        logger_names = self.q(css='[class="setting-input  auto-complete  yui-ac-input"]').attrs('value')
        level = self.q(css='[name="level"] > [selected="true"]').text
        return zip(logger_names, level)
