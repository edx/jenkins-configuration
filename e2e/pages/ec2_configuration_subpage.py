from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from configuration_page import ConfigurationSubPageMixIn

class Ec2ConfigurationSubPage(ConfigurationSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(Ec2ConfigurationSubPage, self).__init__(*args, **kwargs)
        self.name = "jenkins-model-GlobalCloudConfiguration"

    def get_cloud_names(self):
        return self.values_of_elements_named('_.cloudName')

    def get_cloud_regions(self):
        return self.values_of_elements_named('_.region')

    def get_cloud_access_keys(self):
        return self.values_of_elements_named('_.accessId')

    def get_ami_descriptions(self):
        return self.values_of_elements_named('_.description')

    def get_ami_zones(self):
        return self.values_of_elements_named('_.zone')

    def get_ami_ids(self):
        return self.values_of_elements_named('_.ami')

    def get_fs_roots(self):
        return self.values_of_elements_named('_.remoteFS')

    def get_ssh_ports(self):
        return self.values_of_elements_named('_.sshPort')

    def get_idle_termination_times(self):
        return self.values_of_elements_named('_.idleTerminationMinutes')
