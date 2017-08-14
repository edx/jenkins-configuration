from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class JobConfigHistorySubPage(PageObject):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

    def is_browser_on_page(self):
        self.scroll_to_element('[name="historyRootDir"]', timeout=10)
        return self.q(css='[name="historyRootDir"]').visible

    @property
    def nameref_id(self):
        refs = self.q(css='[name="plugin"]').attrs('id')
        for temp_ref in refs:
            css_query = css='[nameref="{}"] > td > input'.format(temp_ref)
            if self.q(css=css_query) == 'jobConfigHistory':
                return temp_ref

    def expand_advanced(self):
        css_query='[nameref="{}"] > td > div.advancedLink > span.yui-button'.format(self.nameref_id)
        self.q(css=css_query).click()

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
