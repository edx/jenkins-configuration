from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class GHPRBConfigurationSubPage(PageObject):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

    def is_browser_on_page(self):
        self.scroll_to_element('[name="_.serverAPIUrl"]', timeout=10)
        return self.q(css='.setting-input.validated[name="_.serverAPIUrl"]').visible

    @property
    def nameref_id(self):
        return self.q(css=
                '[name="org-jenkinsci-plugins-ghprb-GhprbTrigger"]'
                ).attrs('id')[0]

    def get_admin_list(self):
        return self.q(css='[name="_.adminlist"]').text[0]

    def expand_advanced(self):
        css_query='[nameref="{}"] > td > div.advancedLink > span.yui-button'.format(self.nameref_id)
        self.q(css=css_query).click()

    def get_request_testing_phrase(self):
        return self.q(css='[name="_.requestForTestingPhrase"]').text[0]

    def get_build_log_portion(self):
        return self.q(css='[name="_.logExcerptLines"').attrs('value')[0]

