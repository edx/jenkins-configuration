from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class JenkinsConfigurationPage(PageObject):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

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


class ConfigurationSubPageMixIn(object):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

    def __init__(self, *args, **kwargs):
        super(ConfigurationSubPageMixIn, self).__init__(*args, **kwargs)

    def is_browser_on_page(self):
        css = '[name="{}"]'.format(self.name)
        return self.q(css=css).is_present()

    def expand_advanced(self):
        """ Press the "Advanced" button for the plugin section."""
        nameref_id = self.q(css='[name="{}"]'.format(self.name)).attrs('id')[0]
        css_query='[nameref="{}"] > td > div.advancedLink > span.yui-button'.format(nameref_id)
        self.q(css=css_query).click()

    def values_of_elements_named(self, el_name):
        """
        Return a list of the value attributes for elements
        whose name attribute match the name specified.
        """
        return self.q(css='[name="{}"]'.format(el_name)).attrs('value')

    def value_of_first_element_named(self, el_name):
        """
        Return the value attribute for the first element
        whose name attribute matches the name specified.
        """
        return self.q(css='[name="{}"]'.format(el_name)).first.attrs('value')[0]
