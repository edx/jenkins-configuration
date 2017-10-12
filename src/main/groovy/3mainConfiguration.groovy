/**
* main configuration
*
* This script will automate the configuration of the main jenkins options,
* found at <jenkinsURL>/configure. It does not configure any of the options
* offered via plugins.
*
* Values for this configuration should NEVER be placed in this script, but
* available to Jenkins as an environment variable pointing to YAML data
*
**/

import java.util.logging.Logger
import jenkins.*
import jenkins.model.*
import hudson.model.*
import hudson.tasks.Shell
import hudson.slaves.EnvironmentVariablesNodeProperty
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry
import hudson.model.Node.Mode
import hudson.markup.RawHtmlMarkupFormatter
import hudson.markup.EscapedMarkupFormatter
@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml()

String configPath = System.getenv("JENKINS_CONFIG_PATH")
String configText = ''
try {
    configText = new File("${configPath}/main_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/main_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map mainConfig = yaml.load(configText).MAIN
Map propertiesConfig = yaml.load(configText).GLOBAL_PROPERTIES
Map locationConfig = yaml.load(configText).LOCATION
Map shellConfig = yaml.load(configText).SHELL
Map formatterConfig = yaml.load(configText).FORMATTER

logger.info("Configuring basic Jenkins options")
try {
    jenkins.setSystemMessage(mainConfig.SYSTEM_MESSAGE)
    jenkins.setNumExecutors(mainConfig.NUMBER_OF_EXECUTORS)
    jenkins.setLabelString(mainConfig.LABELS.join(' '))
    jenkins.setQuietPeriod(mainConfig.QUIET_PERIOD)
    jenkins.setScmCheckoutRetryCount(mainConfig.SCM_RETRY_COUNT)
    jenkins.setDisableRememberMe(mainConfig.DISABLE_REMEMBER_ME)
    if (mainConfig.USAGE == 'NORMAL') {
        jenkins.setMode(Mode.NORMAL)
    } else if (mainConfig.USAGE == 'EXCLUSIVE') {
        jenkins.setMode(Mode.EXCLUSIVE)
    }
    else {
        logger.severe('Invalid value specified for USAGE')
        logger.severe('Exiting')
        jenkins.doSafeExit(null)
        System.exit(1)
    }
} catch (MissingMethodException e) {
    logger.severe("Invalid value specified for MAIN configuration in ${configPath}/main_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

logger.info("Adding global environment variables to Jenkins")
List<Entry> envVarList = new ArrayList<Entry>()
propertiesConfig.ENVIRONMENT_VARIABLES.each { envVar ->
                try {
                    envVarList.add(new Entry(envVar.NAME, envVar.VALUE))
                } catch (MissingMethodException e) {
                    logger.severe("Invalid value specified for environment variables in ${configPath}/main_config.yml")
                    jenkins.doSafeExit(null)
                    System.exit(1)
                }
}

jenkins.getGlobalNodeProperties().replaceBy(
    Collections.singleton(
        new EnvironmentVariablesNodeProperty(envVarList)
    )
)

logger.info("Setting up the Jenkins URL")
JenkinsLocationConfiguration location = jenkins.getExtensionList(jenkins.model.JenkinsLocationConfiguration).get(0)
try {
    location.setUrl(locationConfig.URL)
    location.setAdminAddress(locationConfig.ADMIN_EMAIL)
} catch (MissingMethodException e) {
    logger.severe("Invalid value in the LOCATION configuration of ${configPath}/main_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

logger.info("Setting the default shell used by Jenkins")
Process p = "stat ${shellConfig.EXECUTABLE}".execute()
p.waitForOrKill(1000)
if (p.exitcode != 0) {
    logger.severe("Executable ${shellConfig.EXECUTABLE} not present on system")
    jenkins.doSafeExit(null)
    System.exit(1)
}
Shell.DescriptorImpl shell = jenkins.getExtensionList(Shell.DescriptorImpl.class).get(0)
shell.setShell(shellConfig.EXECUTABLE)
shell.save()

// Configure the markup formatter for build and job descriptions
if (formatterConfig.FORMATTER_TYPE.toLowerCase() == 'rawhtml') {
    RawHtmlMarkupFormatter markupFormatter = new RawHtmlMarkupFormatter(
        formatterConfig.DISABLE_SYNTAX_HIGHLIGHTING
    )
    jenkins.setMarkupFormatter(markupFormatter)
} else if (formatterConfig.FORMATTER_TYPE.toLowerCase() == 'plain') {
    EscapedMarkupFormatter markupFormatter = new EscapedMarkupFormatter()
    jenkins.setMarkupFormatter(markupFormatter)
} else {
    logger.severe("Invalid value in the FORMATTER configuration of ${configPath}/main_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

jenkins.save()
logger.info("Finished configuring the main Jenkins options.")
