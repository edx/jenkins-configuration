from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class Ec2ConfigurationSubPage(PageObject):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

    def is_browser_on_page(self):
        self.scroll_to_element('[name="_.cloudName"]', timeout=10)
        return self.q(css='[name="_.cloudName"]').visible

    @property
    def nameref_id(self):
        return self.q(css=
                '[name="jenkins-model-GlobalCloudConfiguration"]'
                ).attrs('id')[0]

    def get_cloud_name(self):
        return self.q(css='[name="_.cloudName"]').attrs('value')[0]

    def get_cloud_region(self):
        return self.q(css='[name="_.region"]').attrs('value')[0]
