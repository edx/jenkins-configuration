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
ec2CloudConfig = ec2Config.CLOUDS

// Get the ec2 plugin version
pluginVersion = Float.parseFloat(jenkins.pluginManager.getPlugin('ec2').getVersion())

List<AmazonEC2Cloud> clouds = new ArrayList<AmazonEC2Cloud>();
for (cloudConfig in ec2CloudConfig) {
    List<SlaveTemplate> templates = new ArrayList<SlaveTemplate>();
    ec2AmiConfig = cloudConfig.AMIS
    // Loop through the AMIs
    for (amiConfig in ec2AmiConfig) {
        // Create Spot Config if applicable
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

        // Create instanceType object
        try {
            instanceType = InstanceType.fromValue(amiConfig.INSTANCE_TYPE)
        } catch (IllegalArgumentException e) {
            logger.severe("${instanceType} is an invalid ec2 instance type")
            jenkins.doSafeExit(null)
            System.exit(1)
        }

        // Get desired Mode object
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

        // Get the init script from path if applicable
        initScriptPath = amiConfig.INIT_SCRIPT_PATH
        initScript = null
        if (initScriptPath) {
            try {
                initScript = new File(initScriptPath).text
            } catch (FileNotFoundException e) {
                logger.severe("No init script file found at path: ${initScriptPath}")
                jenkins.doSafeExit(null)
                System.exit(1)
            }
        }

        // Create list of ec2 tags
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
            initScript,
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

    privateKeyPath = cloudConfig.EC2_PRIVATE_KEY_PATH
    ec2PrivateKey = ''
    if (privateKeyPath) {
        try {
            ec2PrivateKey = new File(privateKeyPath).text
        } catch (FileNotFoundException e) {
            logger.severe("No ec2 private key file found at path: ${privateKeyPath}")
            jenkins.doSafeExit(null)
            System.exit(1)
        }
    }

    // Create the EC2 Cloud and populate it with the AMIs
    AmazonEC2Cloud cloud = new AmazonEC2Cloud(
        cloudConfig.NAME,
        cloudConfig.USE_INSTANCE_PROFILE_FOR_CREDS,
        cloudConfig.ACCESS_KEY_ID,
        cloudConfig.SECRET_ACCESS_KEY,
        cloudConfig.REGION,
        ec2PrivateKey,
        cloudConfig.INSTANCE_CAP,
        templates
    )
    clouds.add(cloud)
}

// Clear any existing ec2 configurations and apply the new
jenkins.clouds.clear()
jenkins.clouds.addAll(clouds)
jenkins.save()
logger.info("Successfully configured the EC2 plugin")
