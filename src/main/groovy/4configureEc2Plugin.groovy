/**
*
* Configure the EC2 plugin
*
**/
import java.util.logging.Logger
import jenkins.model.Jenkins
import hudson.model.Node
import hudson.plugins.ec2.*
import com.amazonaws.services.ec2.model.InstanceType

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml()

String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/ec2_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/ec2_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
ec2Config = yaml.load(configText)
ec2CloudConfig = ec2Config.MAIN
ec2AmiConfig = ec2Config.AMIS

// Get the ec2 plugin version
pluginVersion = Float.parseFloat(jenkins.pluginManager.getPlugin('ec2').getVersion())

List<SlaveTemplate> templates = new ArrayList<SlaveTemplate>();
for (amiConfig in ec2AmiConfig) {
    // Create Spot Config from yaml values is applicable
    maxBid = amiConfig.SPOT_CONFIG.SPOT_MAX_BID_PRICE
    // The constructor changes based on the plugin so add support for both
    if (pluginVersion < 1.32) {
        bidType = amiConfig.SPOT_CONFIG.SPOT_INSTANCE_BID_TYPE.toLowerCase()
        validBidTypes = ['persistent', 'one-time']
        if (maxBid && bidType) {
            if (!(bidType in validBidTypes)) {
                logger.severe("Invalid value for SPOT_INSTANCE_BID_TYPE. Must be " +
                              "persistent, one-time, or null if no spot configuration " +
                              "is desired. Got: ${bidType}")
                //Create SpotConfiguration
                spotConfig = new SpotConfiguration(maxBid, bidType)
            }
            //Create SpotConfiguration
            spotConfig = new SpotConfiguration(maxBid, bidType)
        } else {
            spotConfig = null
        }
    } else {
        if (maxBid) {
            spotConfig = new SpotConfiguration(maxBid)
        } else {
            spotConfig = null
        }
    }

    // Create instanceType object from yaml value
    try {
        instanceType = InstanceType.fromValue(amiConfig.INSTANCE_TYPE)
    } catch (IllegalArgumentException e) {
        logger.severe("${instanceType} is an invalid ec2 instance type")
        jenkins.doSafeExit(null)
        System.exit(1)
    }

    // Get desired Mode object from yaml value
    modeString = amiConfig.MODE.toLowerCase()
    if (modeString == 'normal') {
        mode = Node.Mode.NORMAL
    } else if (modeString == 'exclusive') {
        mode = Node.Mode.EXCLUSIVE
    } else {
        logger.severe("Invalid value for MODE. Must be NORMAL or EXCLUSIVE " +
                      "Got: ${modeString}")
        jenkins.doSafeExit(null)
        System.exit(1)
    }

    // Create list of ec2 tags from yaml values
    List<EC2Tag> tags = new ArrayList<EC2Tag>();
    for (currentTag in amiConfig.TAGS) {
        EC2Tag tag = new EC2Tag(
            currentTag.NAME,
            currentTag.VALUE
        )
        tags.add(tag);
    }

    // Create the AMI
    SlaveTemplate template = new SlaveTemplate(
        amiConfig.AMI_ID,
        amiConfig.AVAILABILITY_ZONE,
        spotConfig,
        amiConfig.SECURITY_GROUPS,
        amiConfig.REMOTE_FS_ROOT,
        amiConfig.SSH_PORT,
        instanceType,
        amiConfig.LABEL_STRING,
        mode,
        amiConfig.DESCRIPTION,
        amiConfig.INIT_SCRIPT,
        amiConfig.TEMP_DIR,
        amiConfig.USER_DATA,
        amiConfig.NUM_EXECUTORS,
        amiConfig.REMOTE_ADMIN,
        amiConfig.ROOT_COMMAND_PREFIX,
        amiConfig.JVM_OPTIONS,
        amiConfig.STOP_ON_TERMINATE,
        amiConfig.SUBNET_ID,
        tags,
        amiConfig.IDLE_TERMINATION_MINUTES,
        amiConfig.USE_PRIVATE_DNS_NAME,
        amiConfig.INSTANCE_CAP,
        amiConfig.IAM_INSTANCE_PROFILE,
        amiConfig.USE_EPHEMERAL_DEVICES,
        amiConfig.LAUNCH_TIMEOUT
    )
    templates.add(template);
}

String fileContents = new File(ec2CloudConfig.EC2_PRIVATE_KEY_PATH).text

// Create the EC2 Cloud and populate it with the AMIs
AmazonEC2Cloud cloud = new AmazonEC2Cloud(
    ec2CloudConfig.NAME,
    ec2CloudConfig.USE_INSTANCE_PROFILE_FOR_CREDS,
    ec2CloudConfig.ACCESS_KEY_ID,
    ec2CloudConfig.SECRET_ACCESS_KEY,
    ec2CloudConfig.REGION,
    fileContents,
    ec2CloudConfig.INSTANCE_CAP,
    templates
)

// Clear any existing ec2 configurations and apply the new
jenkins.clouds.clear()
jenkins.clouds.add(cloud)
jenkins.save()
logger.info("Successfully configured the EC2 plugin")
