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
            "matrix-auth": "1.1",
            "translation": "1.10",
            "mailer": "1.11",
            "pam-auth": "1.1",
            "antisamy-markup-formatter": "1.1",
            "maven-plugin": "2.7.1",
            "ssh-slaves": "1.9",
            "javadoc": "1.1",
            "subversion": "1.54",
            "external-monitor-job": "1.4",
            "cvs": "2.11",
            # The following plugins installed via test_data/plugins.json
            "ec2": "1.29",
            "ghprb": "1.34.0",
            "job-dsl": "1.45",
            # The following plugins should be installed as dependencies for
            # the plugins installed via test_data/plugins.json
            "node-iterator-api": "1.1",
            "github": "1.9.1",
            "plain-credentials": "1.1",
            "credentials": "1.21",
            "matrix-project": "1.6",
            "ssh-agent": "1.3",
            "github-api": "1.82",
            "git": "2.4.0",
            "git-client": "1.18.0",
            "junit": "1.2",
            "scm-api": "0.2",
            "script-security": "1.13",
            "ssh-credentials": "1.11"
        }
        for plugin, version in expected_plugin_versions.iteritems():
            assert expected_plugin_versions[plugin] == version
