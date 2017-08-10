from bok_choy.page_object import PageObject

class JenkinsDashboardPage(PageObject):

    url = 'http://localhost:8080' 

    def is_browser_on_page(self):
        return 'dashboard [jenkins]' in self.browser.title.lower()

    def get_jobs_list(self):
        return self.q(css='[id="projectstatus"] > tbody > tr').attrs('id')
