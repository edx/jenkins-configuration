import jenkins.model.Jenkins
import hudson.plugins.ec2.*
import com.amazonaws.services.ec2.model.InstanceType
import hudson.model.Node

Jenkins jenkins = Jenkins.getInstance()


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

AmazonEC2Cloud cloud = new AmazonEC2Cloud("AZ", true, "awskey", "aws secret key", "AZ", sshKey, "3", templates);

jenkins.clouds.add(cloud)

jenkins.save()
