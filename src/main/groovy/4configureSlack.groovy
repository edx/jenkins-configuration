/* Configure the Slack Notifier plugin */

import java.util.logging.Logger
import net.sf.json.JSONObject

import jenkins.model.Jenkins
import org.kohsuke.stapler.StaplerRequest

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml(new SafeConstructor())

String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/slack_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe(
        "Cannot find config file path @ ${configPath}/slack_config.yml"
    )
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map slackConfig = yaml.load(configText)

def slackParameters = [
    slackBaseUrl: slackConfig.SLACK_BASE_URL,
    slackBotUser: slackConfig.IS_SLACK_BOT,
    slackRoom: slackConfig.SLACK_ROOM,
    slackTeamDomain: slackConfig.SLACK_TEAM_DOMAIN,
]

def slack = jenkins.getExtensionList(
        jenkins.plugins.slack.SlackNotifier.DescriptorImpl.class
    )[0]

// Make use Jenkins credentials to authenticate to Slack, rather
// than a token, as advised by the Slack plugin documentation
JSONObject formData = [
        'slack': ['tokenCredentialId': slackConfig.SLACK_CREDENTIAL_ID]
    ] as JSONObject
def request = [getParameter: { p -> slackParameters[p] } ] as StaplerRequest

slack.configure(request, formData)

slack.save()
jenkins.save()
