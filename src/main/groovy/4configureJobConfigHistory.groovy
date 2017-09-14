/**
*
* Configure the Job Configure History Plugin
*
**/
import java.util.logging.Logger
import net.sf.json.JSONObject
import net.sf.json.JSONException

import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import hudson.plugins.jobConfigHistory.JobConfigHistory

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
    configText = new File("${configPath}/job_config_history.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/job_config_history.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

try {
    plugin = jenkins.getPlugin(JobConfigHistory.class)
} catch (MissingPropertyException e) {
    logger.severe("Cannot find Job Config History plugin on Jenkins instance")
    jenkins.doSafeExit(null)
    System.exit(1)
}

// Create a json object
json = new JSONObject()

// Now load in the yaml file and set custom values
config = yaml.load(configText)
config_field_map = [
    'historyRootDir':config.HISTORY_ROOT_DIR,
    'maxHistoryEntries':config.MAX_HISTORY_ENTRIES,
    'maxDaysToKeepEntries':config.MAX_DAYS_TO_KEEP_ENTRIES,
    'maxEntriesPerPage':config.MAX_ENTRIES_PER_PAGE,
    'skipDuplicateHistory':config.SKIP_DUPLICATE_HISTORY,
    'excludePattern':config.EXCLUDE_PATTERN,
    'saveModuleConfiguration':config.SAVE_MODULE_CONFIGURATION,
    'showBuildBadges':config.SHOW_BUILD_BADGES,
    'excludedUsers':config.EXCLUDED_USERS
]

// arrays to be used for error checking
showBuildBadgesOptions = ['never', 'always', 'userWithConfigPermission', 'adminUser']

// populate the JSON object with the contents of the yaml file
config_field_map.each { param, value ->
    if (param == 'showBuildBadges'
        && !(value in showBuildBadgesOptions)) {
        logger.severe("Invalid setting for ${param}. Must be: never, " +
                      "always, userWithConfigPermission, or adminUser. " +
                      "Was: ${value}")
        jenkins.doSafeExit(null)
        System.exit(1)
    }
        json.put(param, value)
}

// Update the plugin
try {
    plugin.configure(null, json)
} catch (JSONException e) {
    logger.severe("Missing parameter in the Job Config History JSON object")
    logger.severe(e.toString())
    jenkins.doSafeExit(null)
    System.exit(1)
} catch (MissingMethodException e) {
    logger.severe("One of the values in ${configPath}/job_config_history.yml " +
                  "is typed incorrectly")
    logger.severe(e.toString())
    jenkins.doSafeExit(null)
    System.exit(1)
}
logger.info('Successfully configured the Job Config History plugin')
