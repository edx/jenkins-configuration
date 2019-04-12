/* Configure the Slack Notifier plugin */

import java.util.logging.Logger

import jenkins.model.Jenkins

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

def slack = jenkins.getExtensionList(
        jenkins.plugins.slack.SlackNotifier.DescriptorImpl.class
    )[0]

slack.baseUrl = slackConfig.SLACK_BASE_URL
slack.teamDomain = slackConfig.SLACK_TEAM_DOMAIN
slack.tokenCredentialId = slackConfig.SLACK_CREDENTIAL_ID
slack.botUser = slackConfig.IS_SLACK_BOT
slack.room = slackConfig.SLACK_ROOM

slack.save()
jenkins.save()
