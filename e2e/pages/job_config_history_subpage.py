from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class JobConfigHistorySubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(JobConfigHistorySubPage, self).__init__(*args, **kwargs)
        self.name = "plugin"

    def get_history_root_dir(self):
        return self.q(css='[name="historyRootDir"]').attrs('value')[0]

    def get_max_history_entries(self):
        return self.q(css='[name="maxHistoryEntries"]').attrs('value')[0]

    def get_skip_duplicate_history(self):
        return self.q(css='[name="skipDuplicateHistory"]').attrs('checked')[0]

    def get_show_build_badges(self):
        query_results = self.q(css='[name="showBuildBadges"]')
        for item in query_results:
            if item.get_attribute('checked'):
                return item.get_attribute('value')
