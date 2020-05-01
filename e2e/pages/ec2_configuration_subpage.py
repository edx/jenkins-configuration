from __future__ import absolute_import
from . import JENKINS_HOST
from bok_choy.page_object import PageObject
from .configuration_page import ConfigurationCloudSubPageMixIn

class Ec2ConfigurationSubPage(ConfigurationCloudSubPageMixIn, PageObject):

    def __init__(self, *args, **kwargs):
        super(Ec2ConfigurationSubPage, self).__init__(*args, **kwargs)
        self.name = "jenkins-model-GlobalCloudConfiguration"

    def get_cloud_names(self):
        return self.values_of_elements_named('_.cloudName')

    def get_cloud_credential_id(self):
        return self.values_of_elements_named('_.credentialsId')

    def get_cloud_role_arns(self):
        return self.values_of_elements_named('_.roleArn')

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
