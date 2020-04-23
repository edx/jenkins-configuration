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
            "credentials": "2.1.19",
            "cvs": "2.14",
            "ec2": "1.44.1",
            "email-ext": "2.62",
            "external-monitor-job": "1.4",
            "ghprb": "1.42.0",
            "github": "1.29.2",
            "github-oauth": "0.33",
            "gradle": "1.29",
            "groovy": "2.2",
            "javadoc": "1.3",
            "jdk-tool": "1.2",
            "job-dsl": "1.72",
            "jobConfigHistory": "2.19",
            "junit": "1.28",
            "ldap": "1.20",
            "mailer": "1.21",
            "mask-passwords": "2.12.0",
            "matrix-auth": "1.5",
            "matrix-project": "1.14",
            "maven-plugin": "3.4",
            "pam-auth": "1.5.1",
            "pipeline-model-definition": "1.3.4.1",
            "saml": "1.1.0",
            "script-security": "1.66",
            "shiningpanda": "0.23",
            "slack": "2.21",
            "splunk-devops": "1.6.4",
            "ssh-credentials": "1.14",
            "ssh-slaves": "1.28.1",
            "subversion": "2.10.3",
            "timestamper": "1.9",
            "translation": "1.16",
            "windows-slaves": "1.3.1",
            "workflow-aggregator": "2.6",
            # Dependencies
            "ace-editor": "1.0.1",
            "apache-httpcomponents-client-4-api": "4.5.5-3.0",
            "authentication-tokens": "1.1",
            "branch-api": "2.0.18",
            "cloudbees-folder": "6.6",
            "credentials-binding": "1.13",
            "display-url-api": "2.0",
            "docker-commons": "1.5",
            "docker-workflow": "1.14",
            "durable-task": "1.26",
            "git": "3.4.0",
            "git-client": "2.7.3",
            "git-server": "1.7",
            "github-api": "1.90",
            "github-branch-source": "1.9",
            "handlebars": "1.1",
            "icon-shim": "2.0.3",
            "jackson2-api": "2.8.11.3",
            "jquery-detached": "1.2.1",
            "jsch": "0.1.54.2",
            "mapdb-api": "1.0.1.0",
            "momentjs": "1.1",
            "node-iterator-api": "1.5.0",
            "pipeline-build-step": "2.7",
            "pipeline-graph-analysis": "1.1",
            "pipeline-input-step": "2.8",
            "pipeline-milestone-step": "1.3.1",
            "pipeline-model-api": "1.3.4.1",
            "pipeline-model-declarative-agent": "1.1.1",
            "pipeline-model-extensions": "1.3.4.1",
            "pipeline-rest-api": "2.10",
            "pipeline-stage-step": "2.3",
            "pipeline-stage-tags-metadata": "1.3.4.1",
            "pipeline-stage-view": "2.10",
            "plain-credentials": "1.4",
            "scm-api": "2.2.8",
            "structs": "1.17",
            "token-macro": "2.6",
            "workflow-api": "2.34",
            "workflow-basic-steps": "2.11",
            "workflow-cps": "2.61.1",
            "workflow-cps-global-lib": "2.11",
            "workflow-durable-task-step": "2.22",
            "workflow-job": "2.26",
            "workflow-multibranch": "2.20",
            "workflow-scm-step": "2.6",
            "workflow-step-api": "2.19",
            "workflow-support": "2.22"
        }
        for plugin, version in six.iteritems(expected_plugin_versions):
            print("Checking if {} at version {} is installed".format(
                    plugin, expected_plugin_versions[plugin]))
            assert installed_plugin_versions[plugin] == version
