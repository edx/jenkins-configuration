import java.util.logging.Logger

import jenkins.*
import jenkins.model.*
import jenkins.plugins.hipchat.*;

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
    configText = new File("${configPath}/hipchat_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/hipchat_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map hipchatConfig = yaml.load(configText)
def hipChat = Jenkins.instance.getDescriptorByType(
                jenkins.plugins.hipchat.HipChatNotifier.DescriptorImpl.class
              )

hipChat.setToken(hipchatConfig.API_TOKEN)
hipChat.setRoom(hipchatConfig.ROOM)
hipChat.setV2Enabled(hipchatConfig.V2_ENABLED)

logger.info('Successfully configured the HipChat plugin')
