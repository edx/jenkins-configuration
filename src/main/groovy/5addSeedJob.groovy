/**
*
* Create the Seed Job on startup from an XML script.
*
**/
import java.util.logging.Logger
import jenkins.*
import jenkins.model.*
import hudson.model.*

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
    configText = new File("${configPath}/seed_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/seed_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map seedConfig = yaml.load(configText)
seedJobName = seedConfig.NAME
seedXmlFilePath = seedConfig.XML_PATH

if (!seedJobName || !seedXmlFilePath) {
    logger.severe("Missing data for seed job. Please ensure the " +
                  "seed_config.yml file has a NAME and XML_PATH")
    jenkins.doSafeExit(null)
    System.exit(1)
}

try {
    seedXml = new File("${seedXmlFilePath}")
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${seedXmlFilePath}")
    jenkins.doSafeExit(null)
    System.exit(1)
}

existingSeedJob = jenkins.getJob(seedJobName)
if (existingSeedJob) {
    logger.info("Jobs by the name: ${seedJobName} exists. Deleting.")
    existingSeedJob.delete()
}

xmlByteStream = new ByteArrayInputStream(seedXml.getBytes())
try {
    jenkins.createProjectFromXML(seedJobName, xmlByteStream)
} catch (IOException e) {
    logger.severe("Failed to create seed job from ${seedXmlFilePath}. " +
                  "Please ensure the xml is valid and try again.")
    jenkins.doSafeExit(null)
    System.exit(1)
}
logger.info("${seedJobName} created")
