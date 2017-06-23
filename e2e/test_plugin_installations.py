from unittest import TestCase

from jenkinsapi.jenkins import Jenkins

class PluginTestCase(TestCase):

    def setUp(self):
        self.jenkins = Jenkins("http://localhost:8080")

    def test_sample_plugins_installed(self):
        plugins = self.jenkins.get_plugins()
        installed_plugin_versions = {
                value.shortName: value.version 
                for _, value in plugins.iteritems()
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

            # TODO: enumerate plugins installed via test_data/plugins.json

            # TODO: enumerate dependency plugins and versions for the plugins
            # installed via test_data/plugins.json
        }
        for plugin, version in expected_plugin_versions.iteritems():
            print "Checking if {} at version {} is installed".format(
                    plugin, installed_plugin_versions[plugin])
            assert expected_plugin_versions[plugin] == version
