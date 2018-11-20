/**
* Create Jenkins users
*
* This script is used for creating basic Jenkins users and setting basic
* information, such as email addresses. This is necessary if you choose
* to use SAML authentication, as user accounts are required on both the
* Identity Provider and Service Provider.
*
* This script must be run before you set the Security Realm for your Jenkins
* (i.e. Github Oauth, Saml). Otherwise, it will overwrite it, and set the
* Jenkins Security Realm to 'Jenkins’ own user database'
**/

import java.util.logging.Logger
import jenkins.model.Jenkins
import hudson.model.User
import hudson.security.HudsonPrivateSecurityRealm
import hudson.tasks.Mailer.UserProperty

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
    configText = new File("${configPath}/user_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/user_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

userConfigs = yaml.load(configText)

HudsonPrivateSecurityRealm realm = new HudsonPrivateSecurityRealm(false)

int userCount = 0
userConfigs.each { userData ->

    Collection<User> allUsers = User.getAll().collect { u -> u.getId() }
    if (!allUsers.contains(userData.USERNAME)) {
       realm.createAccount(userData.USERNAME, userData.PASSWORD)
    }
    User user = realm.getUser(userData.USERNAME)

    UserProperty emailAddressProperty = new UserProperty(userData.EMAIL_ADDRESS)
    user.addProperty(emailAddressProperty)
    user.save()
    userCount += 1

}

jenkins.setSecurityRealm(realm)
jenkins.save()
logger.info("Successfully created ${userCount.toString()} users")
