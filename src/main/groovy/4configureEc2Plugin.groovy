/**
*
* Configure the EC2 plugin
*
**/
import java.util.logging.Logger
import jenkins.model.Jenkins
import hudson.plugins.ec2.*
import com.amazonaws.services.ec2.model.InstanceType
import hudson.model.Node

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


AmazonEC2Cloud cloud = new AmazonEC2Cloud(
    ec2CloudConfig.NAME,
    ec2CloudConfig.USE_INSTANCE_PROFILE_FOR_EC2_CREDS,
    ec2CloudConfig.ACCESS_KEY_ID,
    ec2CloudConfig.SECRET_ACCESS_KEY,
    ec2CloudConfig.REGION,
    ec2CloudConfig.EC2_KEY_PAIR_PRIVATE,
    ec2CloudConfig.INSTANCE_CAP,
    null
)

jenkins.clouds.add(cloud)

String ami = "ami-123"
String description = "foo ami";

EC2Tag tag1 = new EC2Tag("name1", "value1");
EC2Tag tag2 = new EC2Tag("name2", "value2");
List<EC2Tag> tags = new ArrayList<EC2Tag>();
tags.add(tag1);
tags.add(tag2);


SlaveTemplate template = new SlaveTemplate(ami, EC2AbstractSlave.TEST_ZONE, null, "default", "foo", InstanceType.M1Large, "ttt", Node.Mode.NORMAL, description, "bar", "bbb", "aaa", "10", "fff", null, "-Xmx1g", false, "subnet 456", tags, null, false, null, "cherry", true, false, "banana", false, "apple");
List<SlaveTemplate> templates = new ArrayList<SlaveTemplate>();
templates.add(template);

String sshKey = "ssh"

jenkins.save()
