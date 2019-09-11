from __future__ import absolute_import
import os
import re
from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class GlobalToolConfigurationPage(PageObject):

    url = "http://{}:8080/configureTools".format(JENKINS_HOST)

    tool_names= {
        "groovy": "hudson-plugins-groovy-GroovyInstallation",
        "python": "jenkins-plugins-shiningpanda-tools-PythonInstallation"
    }

    def is_browser_on_page(self):
        return "global tool configuration" in self.browser.title.lower()

    def expand_installations(self, tool):
        """ Press the button to expand the installations for a given tool"""
        tool_name = self.tool_names[tool]
        nameref_id = self.q(css='[name="{}"]'.format(tool_name)).attrs('id')[0]
        css_query='[nameref="{}"] > td > div.advancedLink > span.yui-button'.format(nameref_id)
        self.scroll_to_element(css_query)
        self.q(css=css_query).click()

    def get_python_installations(self):
        css_query = '[checkurl="/descriptorByName/jenkins.plugins.shiningpanda.tools.PythonInstallation/checkHome"]'
        installations = self.q(css=css_query).attrs('value')
        return installations
