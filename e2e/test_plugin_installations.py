from unittest import TestCase

from jenkinsapi.jenkins import Jenkins

class PluginTestCase(TestCase):

    def setUp(self):
        self.jenkins = Jenkins("http://localhost:8080")
        self.plugins = self.jenkins.get_plugins()

    def test_sample_plugins_installed(self):
        installed_plugin_versions = {
                value.shortName: value.version
                for _, value in self.plugins.iteritems()
                }
        expected_plugin_versions = {
            # The following plugins are installed as part of an initial
            # Jenkins boot process
            "windows-slaves": "1.0",
            "ldap": "1.11",
            "ant": "1.2",
            "matrix-auth": "1.2",
            "translation": "1.12",
            "mailer": "1.16",
            "pam-auth": "1.2",
            "antisamy-markup-formatter": "1.3",
            "maven-plugin": "2.8",
            "ssh-slaves": "1.9",
            "javadoc": "1.3",
            "subversion": "2.4.5",
            "external-monitor-job": "1.4",
            "cvs": "2.12",
            # The following plugins installed via test_data/plugins.yml
            "ec2": "1.36",
            "ghprb": "1.36.0",
            "job-dsl": "1.45",
            "github-oauth": "0.24",
            "gradle": "1.24",
            "hipchat": "0.1.9",
            "jobConfigHistory": "2.10",
            "mask-passwords": "2.8",
            # The following plugins should be installed as dependencies for
            # the plugins installed via test_data/plugins.yml
            "ace-editor": "1.0.1",
            "aws-credentials": "1.23",
            "aws-java-sdk": "1.11.37",
            "durable-task": "1.12",
            "bouncycastle-api": "2.16.0",
            "node-iterator-api": "1.5",
            "github": "1.26.0",
            "plain-credentials": "1.3",
            "credentials": "2.1.16",
            "credentials-binding": "1.13",
            "matrix-project": "1.6",
            "ssh-agent": "1.3",
            "github-api": "1.82",
            "git": "2.4.0",
            "git-client": "1.18.0",
            "jackson2-api": "2.5.4",
            "junit": "1.3",
            "jquery-detached": "1.2.1",
            "scm-api": "2.0.3",
            "script-security": "1.34",
            "ssh-credentials": "1.11",
            "mapdb-api": "1.0.1.0",
            "structs": "1.7",
            "token-macro": "1.11",
            "workflow-api": "2.8",
            "workflow-cps": "2.25",
            "workflow-durable-task-step": "2.8",
            "workflow-scm-step": "2.4",
            "workflow-step-api": "2.10",
            "workflow-support": "2.12"
        }
        for plugin, version in installed_plugin_versions.iteritems():
            print "Checking if {} at version {} is installed".format(
                    plugin, installed_plugin_versions[plugin])
            assert expected_plugin_versions[plugin] == version
