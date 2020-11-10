/**
* Configure the Mailer plugin
**/

import java.util.logging.Logger
import net.sf.json.JSONObject
import net.sf.json.JSONException

import jenkins.model.*
import hudson.tasks.Mailer
import org.kohsuke.stapler.StaplerRequest;

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
    configText = new File("${configPath}/mailer_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/mailer_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
Map mailerConfig = yaml.load(configText)

def descriptor = jenkins.getDescriptorByType(
                    hudson.tasks.Mailer.DescriptorImpl.class
                 )
JSONObject json = new JSONObject()
json.put('smtpHost', mailerConfig.SMTP_SERVER)
json.put('replyToAddress', mailerConfig.REPLY_TO_ADDRESS)
json.put('defaultSuffix', mailerConfig.DEFAULT_SUFFIX)

// JSONObject auth = new JSONObject()
// auth.put('smtpAuthUserName', mailerConfig.SMTP_AUTH_USERNAME)
// auth.put('smtpAuthPasswordSecret', mailerConfig.SMTP_AUTH_PASSWORD)
// json.put('useSMTPAuth', auth)

json.put('authentication', true)
json.put('username', mailerConfig.SMTP_AUTH_USERNAME)
auth.put('password', mailerConfig.SMTP_AUTH_PASSWORD)

json.put('smtpPort', mailerConfig.SMTP_PORT)
json.put('useSsl', mailerConfig.USE_SSL)
json.put('useTls', true)
json.put('charset', mailerConfig.CHAR_SET)
StaplerRequest stapler = null
try {
    descriptor.configure(stapler, json)
} catch (JSONException e) {
    logger.severe("Missing parameter in the mailer JSON object")
    logger.severe(e.toString())
    jenkins.doSafeExit(null)
    System.exit(1)
} catch (MissingMethodException e) {
    logger.severe("One of the values in ${configPath}/mailer_config.yml is typed incorrectly")
    logger.severe(e.toString())
    jenkins.doSafeExit(null)
    System.exit(1)
}
catch (NullPointerException e) {
    logger.severe("YO DAWG.zzzzz there was a null point=============")
    logger.severe(e.toString())
    logger.severe(mailerConfig.toString())
    logger.severe(json.toString())
    //logger.severe(auth.toString())
    jenkins.doSafeExit(null)
    System.exit(1)
}
logger.info('Successfully configured the mailer plugin')
