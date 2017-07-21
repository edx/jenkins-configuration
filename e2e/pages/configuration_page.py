
from bok_choy.page_object import PageObject

class JenkinsConfigurationPage(PageObject):

    url = 'http://localhost:8080/configure'

    def expand_advanced(self):
        self.q(css='#yui-gen13-button').click()   

    def is_browser_on_page(self):
        return 'configure system [jenkins]' in self.browser.title.lower()

    def get_workspace_root_dir(self):
        return self.q(css='[name="_.rawWorkspaceDir"]').attrs('value')[0]

    def get_build_record_root_dir(self):
        return self.q(css='[name="_.rawBuildsDir"]').attrs('value')[0]

    def get_system_message(self):
        return self.q(css='[name="system_message"]').text[0]

    def get_number_executors(self):
        return self.q(css='[name="_.numExecutors"]').attrs('value')[0]

    def get_labels(self):
        return self.q(css='[name="_.labelString"]').attrs('value')[0].split(' ')

    def get_mode(self):
        return self.q(css='[name="master.mode"]').attrs('value')[0]

    def get_quiet_period(self):
        return self.q(css='[name="_.quietPeriod"]').attrs('value')[0]

    def get_scm_checkout_retry_count(self):
        return self.q(css='[name="_.scmCheckoutRetryCount"]').attrs('value')[0]
