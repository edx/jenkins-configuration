from __future__ import absolute_import, print_function
from unittest import TestCase

import six
from jenkinsapi.jenkins import Jenkins


class PluginTestCase(TestCase):

    def setUp(self):
        self.jenkins = Jenkins("http://localhost:8080")
        self.plugins = self.jenkins.get_plugins()

    def test_sample_plugins_installed(self):
        installed_plugin_versions = {
            value.shortName: value.version
            for value in self.plugins.values()
        }
        expected_plugin_versions = {
            # Plugins pinned in test_data/plugins.yml
            "ant": "1.8",
            "antisamy-markup-formatter": "1.5",
            "aws-credentials": "1.24",
            "aws-java-sdk": "1.11.457",
            "bouncycastle-api": "2.17",
            "credentials": "2.1.18",
            "cvs": "2.14",
            "ec2": "1.42",
            "email-ext": "2.62",
            "external-monitor-job": "1.4",
            "ghprb": "1.42.0",
            "github": "1.29.2",
            "github-oauth": "0.31",
            "gradle": "1.29",
            "groovy": "2.1",
            "hipchat": "0.1.9",
            "javadoc": "1.3",
            "jdk-tool": "1.2",
            "job-dsl": "1.70",
            "jobConfigHistory": "2.19",
            "junit": "1.26",
            "ldap": "1.20",
            "mailer": "1.21",
            "mask-passwords": "2.10.1",
            "matrix-auth": "1.5",
            "matrix-project": "1.13",
            "maven-plugin": "3.1.2",
            "pam-auth": "1.2",
            "pipeline-model-definition": "1.2.9",
            "saml": "1.1.0",
            "script-security": "1.51",
            "shiningpanda": "0.23",
            "slack": "2.2",
            "splunk-devops": "1.6.4",
            "ssh-credentials": "1.14",
            "ssh-slaves": "1.28.1",
            "subversion": "2.10.3",
            "timestamper": "1.9",
            "translation": "1.16",
            "windows-slaves": "1.3.1",
            "workflow-aggregator": "2.5",
            # Dependencies
            "ace-editor": "1.0.1",
            "apache-httpcomponents-client-4-api": "4.5.5-3.0",
            "authentication-tokens": "1.1",
            "branch-api": "2.0.7",
            "cloudbees-folder": "5.18",
            "credentials-binding": "1.13",
            "display-url-api": "2.0",
            "docker-commons": "1.5",
            "docker-workflow": "1.14",
            "durable-task": "1.14",
            "git": "3.4.0",
            "git-client": "2.7.0",
            "git-server": "1.7",
            "github-api": "1.90",
            "github-branch-source": "1.9",
            "handlebars": "1.1",
            "icon-shim": "2.0.3",
            "jackson2-api": "2.8.11.2",
            "jquery-detached": "1.2.1",
            "jsch": "0.1.54.2",
            "mapdb-api": "1.0.1.0",
            "momentjs": "1.1",
            "node-iterator-api": "1.5.0",
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
            "token-macro": "2.6",
            "workflow-api": "2.30",
            "workflow-basic-steps": "2.6",
            "workflow-cps": "2.46",
            "workflow-cps-global-lib": "2.9",
            "workflow-durable-task-step": "2.15",
            "workflow-job": "2.11",
            "workflow-multibranch": "2.16",
            "workflow-scm-step": "2.5",
            "workflow-step-api": "2.16",
            "workflow-support": "2.17"
        }
        for plugin, version in six.iteritems(expected_plugin_versions):
            print("Checking if {} at version {} is installed".format(
                    plugin, expected_plugin_versions[plugin]))
            assert installed_plugin_versions[plugin] == version
