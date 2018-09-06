from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class SlackConfigSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(SlackConfigSubPage, self).__init__(*args, **kwargs)
        self.name = "jenkins-plugins-slack-SlackNotifier"

    def get_room(self):
        return self.value_of_first_element_named('slackRoom')
