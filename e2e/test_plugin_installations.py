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
            # Plugins pinned in test_data/plugins.yml
            "ant": "1.2",
            "antisamy-markup-formatter": "1.3",
            "credentials": "2.1.16",
            "cvs": "2.12",
            "ec2": "1.28",
            "external-monitor-job": "1.4",
            "ghprb": "1.40.0",
            "github": "1.27.0",
            "github-oauth": "0.24",
            "gradle": "1.24",
            "hipchat": "0.1.9",
            "javadoc": "1.3",
            "job-dsl": "1.67",
            "jobConfigHistory": "2.10",
            "junit": "1.20",
            "ldap": "1.11",
            "mailer": "1.21",
            "mask-passwords": "2.8",
            "matrix-auth": "1.2",
            "matrix-project": "1.11",
            "maven-plugin": "2.8",
            "pam-auth": "1.2",
            "pipeline-model-definition": "1.2.9",
            "script-security": "1.42",
            "splunk-devops": "1.6.4",
            "ssh-credentials": "1.13",
            "ssh-slaves": "1.9",
            "subversion": "2.4.5",
            "translation": "1.12",
            "windows-slaves": "1.0",
            "workflow-aggregator": "2.5",
            # Dependencies
            "ace-editor": "1.0.1",
            "apache-httpcomponents-client-4-api": "4.5.3-2.0",
            "authentication-tokens": "1.1",
            "branch-api": "2.0.7",
            "cloudbees-folder": "5.18",
            "credentials-binding": "1.13",
            "display-url-api": "2.0",
            "docker-commons": "1.5",
            "docker-workflow": "1.14",
            "durable-task": "1.14",
            "git": "3.3.1",
            "git-client": "2.7.0",
            "github-api": "1.90",
            "git-server": "1.7",
            "handlebars": "1.1",
            "icon-shim": "1.0.3",
            "jquery-detached": "1.2.1",
            "jsch": "0.1.54.1",
            "mapdb-api": "1.0.1.0",
            "momentjs": "1.1",
            "node-iterator-api": "1.1",
            "pipeline-build-step": "2.4",
            "pipeline-graph-analysis": "1.1",
            "pipeline-input-step": "2.8",
            "pipeline-milestone-step": "1.3",
            "pipeline-model-api": "1.2.9",
            "pipeline-model-declarative-agent": "1.1.1",
            "pipeline-model-extensions": "1.2.9",
            "pipeline-rest-api": "2.4",
            "pipeline-stage-step": "2.3",
            "pipeline-stage-tags-metadata": "1.2.9",
            "pipeline-stage-view": "2.4",
            "plain-credentials": "1.4",
            "scm-api": "2.2.6",
            "structs": "1.14",
            "token-macro": "2.1",
            "workflow-api": "2.25",
            "workflow-basic-steps": "2.6",
            "workflow-cps": "2.46",
            "workflow-cps-global-lib": "2.9",
            "workflow-durable-task-step": "2.15",
            "workflow-job": "2.11",
            "workflow-multibranch": "2.16",
            "workflow-scm-step": "2.5",
            "workflow-step-api": "2.14",
            "workflow-support": "2.17"
        }
        for plugin, version in expected_plugin_versions.iteritems():
            print "Checking if {} at version {} is installed".format(
                    plugin, expected_plugin_versions[plugin])
            assert installed_plugin_versions[plugin] == version
