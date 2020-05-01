from __future__ import absolute_import
from selenium.webdriver.common.action_chains import ActionChains

from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class JenkinsConfigurationPage(PageObject):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

    def expand_advanced(self):
        element = self.browser.find_element_by_css_selector('[class="advancedLink"] > span > span > button')
        ActionChains(self.browser).move_to_element(element).click(element)

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
        css_query = '[nameref="{}"] > td > div.advancedLink > span.yui-button button'.format(nameref_id)
        element = self.browser.find_element_by_css_selector(css_query)
        ActionChains(self.browser).move_to_element(element).click(element)

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

class ConfigurationCloudSubPageMixIn(ConfigurationSubPageMixIn):
    url = "http://{}:8080/configureClouds".format(JENKINS_HOST)

    def __init__(self, *args, **kwargs):
        super(ConfigurationCloudSubPageMixIn, self).__init__(*args, **kwargs)
