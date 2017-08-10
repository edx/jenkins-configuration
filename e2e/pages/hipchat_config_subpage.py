from bok_choy.page_object import PageObject

class HipChatConfigSubPage(PageObject):

    url = 'http://localhost:8080/configure'

    def is_browser_on_page(self):
        self.scroll_to_element('[name="hipchat.server"]', timeout=10)
        return self.q(css='.setting-input[name="hipchat.server"]').visible

    def get_api_token(self):
        return self.q(css='[name="hipchat.token"]')[0].get_attribute('value')

    def get_room(self):
        return self.q(css='[name="hipchat.room"]')[0].get_attribute('value')
