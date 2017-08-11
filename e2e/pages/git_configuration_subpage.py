from bok_choy.page_object import PageObject

class GitConfigurationSubPage(PageObject):

    url = 'http://localhost:8080/configure'

    def is_browser_on_page(self):
        self.scroll_to_element('[name="_.globalConfigName"]', timeout=10)
        return self.q(css='[name="_.globalConfigName"]').visible

    def get_global_git_user_name(self):
        return self.q(css='[name="_.globalConfigName"]').attrs('value')[0]
