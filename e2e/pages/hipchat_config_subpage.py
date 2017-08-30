from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class HipChatConfigSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(HipChatConfigSubPage, self).__init__(*args, **kwargs)
        self.name = "jenkins-plugins-hipchat-HipChatNotifier"

    def get_api_token(self):
        return self.value_of_first_element_named('hipchat.token')

    def get_room(self):
        return self.value_of_first_element_named('hipchat.room')
