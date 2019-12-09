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
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml(new SafeConstructor())

String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/ec2_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/ec2_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
Map ec2Config = yaml.load(configText)

List<AmazonEC2Cloud> clouds = new ArrayList<AmazonEC2Cloud>();
for (cloudConfig in ec2Config.CLOUDS) {
    List<SlaveTemplate> templates = new ArrayList<SlaveTemplate>();
    // Loop through the AMIs
    for (amiConfig in cloudConfig.AMIS) {
        // Create Spot Config if applicable
        String maxBid = amiConfig.SPOT_CONFIG.SPOT_MAX_BID_PRICE
        SpotConfiguration spotConfig = null
        if (maxBid) {
            boolean useBidPrice = true;
            boolean fallbackToOndemand = true;
            String spotBlockReservationDurationStr = "";
            spotConfig = new SpotConfiguration(
                useBidPrice,
                maxBid,
                fallbackToOndemand,
                spotBlockReservationDurationStr
            )
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
        String modeString = amiConfig.MODE.toLowerCase()
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
        String initScriptPath = amiConfig.INIT_SCRIPT_PATH
        String initScript = null
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

        // Create the AMI type
        amiType = new UnixData(
            amiConfig.AMI_TYPE.ROOT_COMMAND_PREFIX,
            amiConfig.AMI_TYPE.SLAVE_COMMAND_PREFIX,
            amiConfig.AMI_TYPE.SLAVE_COMMAND_SUFFIX,
            amiConfig.AMI_TYPE.REMOTE_SSH_PORT
        )

        // Create the AMI
        SlaveTemplate template = new SlaveTemplate(
            amiConfig.AMI_ID,
            amiConfig.AVAILABILITY_ZONE,
            spotConfig,
            amiConfig.SECURITY_GROUPS,
            amiConfig.REMOTE_FS_ROOT,
            instanceType,
            amiConfig.EBS_OPTIMIZED,
            amiConfig.LABEL_STRING,
            mode,
            amiConfig.DESCRIPTION,
            initScript,
            amiConfig.TEMP_DIR,
            amiConfig.USER_DATA,
            amiConfig.NUM_EXECUTORS,
            amiConfig.REMOTE_ADMIN,
            amiType,
            amiConfig.JVM_OPTIONS,
            amiConfig.STOP_ON_TERMINATE,
            amiConfig.SUBNET_ID,
            tags,
            amiConfig.IDLE_TERMINATION_MINUTES,
            amiConfig.USE_PRIVATE_DNS_NAME,
            amiConfig.INSTANCE_CAP,
            amiConfig.IAM_INSTANCE_PROFILE,
            amiConfig.DELETE_ROOT_ON_TERMINATION,
            amiConfig.USE_EPHEMERAL_DEVICES,
            amiConfig.USE_DEDICATED_TENANCY,
            amiConfig.LAUNCH_TIMEOUT,
            amiConfig.ASSOCIATE_PUBLIC_IP,
            amiConfig.CUSTOM_DEVICE_MAPPING,
            amiConfig.USE_EXTERNAL_SSH_PROCESS,
            amiConfig.CONNECT_WITH_PUBLIC_IP,
            amiConfig.MONITORING,
            amiConfig.T2_UNLIMITED
        )
        templates.add(template);
    }

    String privateKeyPath = cloudConfig.EC2_PRIVATE_KEY_PATH
    String ec2PrivateKey = ''
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
        cloudConfig.CREDENTIAL_ID,
        cloudConfig.REGION,
        ec2PrivateKey,
        cloudConfig.INSTANCE_CAP,
        templates,
        cloudConfig.ROLE_ARN,
        cloudConfig.ROLE_SESSION_NAME
    )
    clouds.add(cloud)
}

// Clear any existing ec2 configurations and apply the new
jenkins.clouds.clear()
jenkins.clouds.addAll(clouds)
jenkins.save()
logger.info("Successfully configured the EC2 plugin")
