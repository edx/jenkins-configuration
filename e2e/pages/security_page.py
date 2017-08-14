import os
import re
from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class SecurityConfigurationPage(PageObject):

    url = "http://{}:8080/configureSecurity".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return "configure global security" in self.browser.title.lower()

    def is_security_enabled(self):
        enabled = self.q(css='[name="_.useSecurity"]').attrs('checked')[0]
        return True if enabled == 'true' else False

    def is_gh_oauth_enabled(self):
        """
        return true if 

        use the `GitHub Web URI` field, which will only appear when
        GH OAuth is selected, rather than the GH OAuth radio button,
        which has no unique CSS identifier
        """
        return self.q(css='[name="_.githubWebUri"]').present

    def get_user_permissions(self, user):
        """
        return a list of the permissions enabled for a particular user
        """
        user_privileges = []
        user_css = '[name="[{}]"]'.format(user)
        for p in self.q(css='{} > td > input'.format(user_css)).attrs('name'):
            privilege_name = re.search(r'\[(?P<name>.*)\]', p).group('name')
            privilege_state = self.q(
                    css='{} > td > [name="{}"]'.format(user_css, p)
                    ).attrs('checked')[0]
            if privilege_state == 'true':
                user_privileges.append(privilege_name)
        return user_privileges
