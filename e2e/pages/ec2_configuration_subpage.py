from . import JENKINS_HOST
from bok_choy.page_object import PageObject

class Ec2ConfigurationSubPage(PageObject):

    url = "http://{}:8080/configure".format(JENKINS_HOST)

    def is_browser_on_page(self):
        return self.q(css='[name="_.cloudName"]').visible

    def get_cloud_names(self):
        return self.q(css='[name="_.cloudName"]').attrs('value')

    def get_cloud_regions(self):
        return self.q(css='[name="_.region"]').attrs('value')

    def get_cloud_access_keys(self):
        return self.q(css='[name="_.accessId"]').attrs('value')

    def get_ami_descriptions(self):
        return self.q(css='[name="_.description"]').attrs('value')

    def get_ami_zones(self):
        return self.q(css='[name="_.zone"]').attrs('value')

    def get_ami_ids(self):
        return self.q(css='[name="_.ami"]').attrs('value')

    def get_fs_roots(self):
        return self.q(css='[name="_.remoteFS"]').attrs('value')

    def get_ssh_ports(self):
        return self.q(css='[name="_.sshPort"]').attrs('value')

    def get_idle_termination_times(self):
        return self.q(css='[name="_.idleTerminationMinutes"]').attrs('value')
