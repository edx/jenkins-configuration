import os
import re
from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class SecurityConfigurationPage(PageObject):

    url = "http://{}:8080/configureSecurity".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return "configure global security" in self.browser.title.lower()

    def is_dsl_script_security_enabled(self):
        enabled = self.q(css='[name="_.useScriptSecurity"]').attrs('checked')[0]
        return True if enabled == 'true' else False

    def is_security_enabled(self):
        enabled = self.q(css='[name="_.useSecurity"]').attrs('checked')[0]
        return True if enabled == 'true' else False

    def is_cli_remoting_enabled(self):
        row_id = self.q(css='[name="jenkins-CLI"]').attrs('id')
        cli_checkbox = self.q(css='[nameref="{}"] > td > [name="_.enabled"]'.format(row_id)).attrs('checked')
        if cli_checkbox and cli_checkbox[0] == 'true':
            return True
        else:
            return False

    def is_gh_oauth_enabled(self):
        """
        return true if the `GitHub Web URI` field is present, which will only
        appear when GH OAuth is selected, rather than the GH OAuth radio button,
        which has no unique CSS identifier
        """
        return self.q(css='[name="_.githubWebUri"]').visible


    def is_saml_enabled(self):
        """
        return true if the `IdpMetadataConfiguration/checkXml` field is present,
        which will only appear when SAML is selected, rather than the SAML radio button,
        which has no unique CSS identifier
        """
        css_query = '[checkurl="/descriptorByName/org.jenkinsci.plugins.saml.IdpMetadataConfiguration/checkXml"]'
        return self.q(css=css_query).visible

    def get_user_permissions(self, user):
        """
        return a list of the permissions enabled for a particular user
        """
        user_privileges = []
        user_css = '[id="hudson-security-ProjectMatrixAuthorization"] > tbody > [name="[{}]"]'.format(user)
        for p in self.q(css='{} > td > input'.format(user_css)).attrs('name'):
            privilege_name = re.search(r'\[(?P<name>.*)\]', p).group('name')
            privilege_state = self.q(
                    css='{} > td > [name="{}"]'.format(user_css, p)
                    ).attrs('checked')[0]
            if privilege_state == 'true':
                user_privileges.append(privilege_name)
        return user_privileges

    def is_csrf_protection_enabled(self):
        enabled = self.q(css='[name="_.csrf"]').attrs('checked')[0]
        return True if enabled == 'true' else False
