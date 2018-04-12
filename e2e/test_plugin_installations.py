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
            "ec2": "1.28",
            "ghprb": "1.36.0",
            "job-dsl": "1.45",
            "github-oauth": "0.24",
            "gradle": "1.24",
            "hipchat": "0.1.9",
            "jobConfigHistory": "2.10",
            "mask-passwords": "2.8",
            "workflow-aggregator": "2.5",
            # The following plugins should be installed as dependencies for
            # the plugins installed via test_data/plugins.yml
            "node-iterator-api": "1.1",
            "github": "1.26.0",
            "plain-credentials": "1.3",
            "credentials": "2.1.8",
            "credentials-binding": "1.10",
            "matrix-project": "1.6",
            "ssh-agent": "1.3",
            "github-api": "1.82",
            "git": "2.4.0",
            "git-client": "1.19.7",
            "junit": "1.3",
            "scm-api": "2.0.3",
            "script-security": "1.27",
            "ssh-credentials": "1.12",
            "mapdb-api": "1.0.1.0",
            "structs": "1.6",
            "token-macro": "1.11",
            "ace-editor": "1.0.1",
            "authentication-tokens": "1.1",
            "branch-api": "1.11",
            "cloudbees-folder": "5.12",
            "docker-commons": "1.5",
            "docker-workflow": "1.9",
            "durable-task": "1.12",
            "git-server": "1.5",
            "handlebars": "1.1",
            "icon-shim": "1.0.3",
            "jquery-detached": "1.2.1",
            "momentjs": "1.1",
            "pipeline-build-step": "2.4",
            "pipeline-graph-analysis": "1.1",
            "pipeline-input-step": "2.5",
            "pipeline-milestone-step": "1.3",
            "pipeline-model-api": "1.0",
            "pipeline-model-declarative-agent": "1.0",
            "pipeline-model-definition": "1.0",
            "pipeline-rest-api": "2.4",
            "pipeline-stage-step": "2.2",
            "pipeline-stage-tags-metadata": "1.0",
            "pipeline-stage-view": "2.4",
            "workflow-api": "2.8",
            "workflow-basic-steps": "2.3",
            "workflow-cps-global-lib": "2.5",
            "workflow-cps": "2.24",
            "workflow-durable-task-step": "2.8",
            "workflow-job": "2.9",
            "workflow-multibranch": "2.9.2",
            "workflow-scm-step": "2.3",
            "workflow-step-api": "2.7",
            "workflow-support": "2.12"
        }
        for plugin, version in installed_plugin_versions.iteritems():
            print "Checking if {} at version {} is installed".format(
                    plugin, installed_plugin_versions[plugin])
            assert expected_plugin_versions[plugin] == version
