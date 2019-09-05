/* Configure the Timestamper plugin */

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
    configText = new File("${configPath}/timestamper_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe(
        "Cannot find config file path @ ${configPath}/timestamper_config.yml"
    )
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map timestamperConfig = yaml.load(configText)
pluginConfig = jenkins.getDescriptor(
    "hudson.plugins.timestamper.TimestamperConfig"
)
pluginConfig.setSystemTimeFormat(timestamperConfig.SYSTEM_CLOCK_FORMAT)
pluginConfig.setElapsedTimeFormat(timestamperConfig.ELAPSED_TIME_FORMAT)
pluginConfig.setAllPipelines(timestamperConfig.ENABLED_ON_PIPELINES.toBoolean())

jenkins.save()
