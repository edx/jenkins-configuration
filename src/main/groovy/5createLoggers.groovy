/**
*
* Automates setting up logs in Jenkins.
* Reads in a log_config.yml file, and creates the specified
* Log Recorders, along with their respective
*
**/

import java.util.logging.Logger

import jenkins.model.*
import hudson.logging.*

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml(new SafeConstructor())

String configPath = System.getenv("JENKINS_CONFIG_PATH")
String configText = ''
try {
    configText = new File("${configPath}/log_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/log_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

List loggingConfig = yaml.load(configText)
LogRecorderManager manager = jenkins.getLog()
// Clear any existing log recorders to avoid clutter
manager.logRecorders.clear()

loggingConfig.each { jenkinsLogRecorder ->
    // Create a new log recorder, then get the LogRecorder object
    // once it has been created.
    manager.doNewLogRecorder(jenkinsLogRecorder.LOG_RECORDER);
    LogRecorder logRecorderObject = manager.getLogRecorder(jenkinsLogRecorder.LOG_RECORDER)
    jenkinsLogRecorder.LOGGERS.each { jenkinsLogger ->
        // For each log, add a target to the LogRecorder with the proper
        // name and log level.
        try {
            LogRecorder.Target target = new LogRecorder.Target(jenkinsLogger.name, jenkinsLogger.log_level)
            logRecorderObject.targets.add(target)
        } catch (IllegalArgumentException e) {
            logger.severe("Invalid value specified for log_level in ${configPath}/log_config.yml")
            jenkins.doSafeExit(null)
            System.exit(1)
        }
    }
}
