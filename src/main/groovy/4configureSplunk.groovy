/**
* Conifgure the Splunk Plugin
**/

import java.util.logging.Logger

import com.splunk.splunkjenkins.SplunkJenkinsInstallation
import com.splunk.splunkjenkins.model.MetaDataConfigItem

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml

Logger logger = Logger.getLogger("")
String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/splunk_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/splunk_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
Yaml yaml = new Yaml()
Map splunkConfig = yaml.load(configText)

SplunkJenkinsInstallation config = SplunkJenkinsInstallation.get();

config.setEnabled(splunkConfig.SPLUNK_APP_ENABLED)
config.setHost(splunkConfig.SPLUNK_HOSTNAME)
config.setPort(new Integer(splunkConfig.SPLUNK_HOST_PORT))
config.setUseSSL(splunkConfig.USE_SSL)
config.setToken(splunkConfig.SPLUNK_TOKEN)
config.setRawEventEnabled(splunkConfig.RAW_EVENTS_ENABLED)

String scriptType = splunkConfig.SCRIPT_TYPE.toLowerCase()
String scriptPath = "${configPath}/${splunkConfig.SCRIPT_PATH}"
switch (scriptType) {
    case 'path':
        config.setScriptPath(scriptPath)
        break
    case 'inline':
        String scriptContents = new File(scriptPath).text
        config.setScriptContent(scriptContents)
        break
    default:
        logger.severe('Invalid script type')
        jenkins.doSafeExit(null)
        System.exit(1)
}

config.setMaxEventsBatchSize(new Long(splunkConfig.MAX_EVENT_BATCH_SIZE))
config.setSplunkAppUrl(splunkConfig.SPLUNK_APP_URL)
config.setRetriesOnError(splunkConfig.RETRIES_ON_ERROR)
config.setIgnoredJobs(splunkConfig.IGNORED_JOBS_PATTERN)
config.setMetadataHost(splunkConfig.MASTER_HOSTNAME)
config.setMetadataSource(splunkConfig.EVENT_SOURCE)

List<String> dataSources = [
    "Build Event",
    "Build Report",
    "Console Log",
    "Jenkins Config",
    "Log File",
    "Queue Information",
    "Slave Information",
    "Default"
]

List<String> dataConfigItems = [ 'Index', 'Source Type', 'Disabled' ]

Set<MetaDataConfigItem> metaDataItems = new HashSet<MetaDataConfigItem>()
for (metadataConfig in splunkConfig.METADATA) {
    if (!dataSources.contains(metadataConfig.DATA_SOURCE)) {
        logger.severe("Invalid data source: ${Config.DATA_SOURCE}")
        jenkins.doSafeExit(null)
        System.exit(1)
    }
    if (!dataConfigItems.contains(metadataConfig.CONFIG_ITEM)) {
        logger.severe("Invalid data config item: ${Config.CONFIG_ITEM}")
        jenkins.doSafeExit(null)
        System.exit(1)
    }
    MetaDataConfigItem metaDataItem = new MetaDataConfigItem(
        metadataConfig.DATA_SOURCE,
        metadataConfig.CONFIG_ITEM,
        metadataConfig.VALUE
    )
    metaDataItems.add(metaDataItem)
}
config.setMetadataItemSet(metaDataItems)


config.updateCache();

if (config.isValid()) {
    logger.info('Splunk config valid.')
    config.save();
    SplunkJenkinsInstallation.markComplete(true);
} else {
    logger.error('Splunk config invalid. Aborting initialization')
    jenkins.doSafeExit(null)
    System.exit(1)
}

logger.info('Successfully configured the Splunk plugin')
