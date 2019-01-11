// configure the email-ext plugin
// this script should be run AFTER the mailer plugin in order to inherit system values
// set by that script

// NOTE: this does not configure the following fields within the plugin:
// > additional accounts
// > additional groovy classpath

import java.util.logging.Logger
import net.sf.json.JSONObject;

import jenkins.model.*;

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml(new SafeConstructor())

String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/email_ext_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/email_ext_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map emailConfig = yaml.load(configText)

def descriptor = jenkins.getDescriptorByType(
                    hudson.plugins.emailext.ExtendedEmailPublisherDescriptor.class
                 )

// create a mocked stapler request
// the following values will be inherited from the mailer plugin (assuming the plugin
// is installed and configured) and therefore do not need to be added to the stapler
// request
//      "ext_mailer_smtp_server"
//      "ext_mailer_default_suffix"
//      "ext_mailer_use_smtp_auth"
//      "ext_mailer_smtp_username"
//      "ext_mailer_smtp_password"
//      "ext_mailer_smtp_use_ssl"
//      "ext_mailer_smtp_port"
//      "ext_mailer_charset"
params = [
    "ext_mailer_adv_properties": emailConfig.ADV_PROPERTIES,
    "ext_mailer_default_subject": emailConfig.DEFAULT_SUBJECT,
    "ext_mailer_emergency_reroute": emailConfig.EMERGENCY_REROUTE,
    "ext_mailer_default_replyto": emailConfig.DEFAULT_REPLYTO,
    "ext_mailer_max_attachment_size": emailConfig.MAX_ATTACHMENT_SIZE,
    "ext_mailer_default_recipients": emailConfig.DEFAULT_RECIPIENTS,
    "ext_mailer_allowed_domains": emailConfig.ALLOWED_DOMAINS,
    "ext_mailer_excluded_committers": emailConfig.EXCLUDED_COMMITTERS
]
if (['text/html', 'text/plain'].contains(emailConfig.DEFAULT_CONTENT_TYPE)) {
    params["ext_mailer_default_content_type"] = emailConfig.DEFAULT_CONTENT_TYPE
} else {
    logger.severe("Invalid content type specified for DEFAULT CONTENT TYPE")
    jenkins.doSafeExit(null)
    System.exit(1)
}
if (emailConfig.USE_LIST_ID == 'true') {
    params["ext_mailer_use_list_id"] =  emailConfig.USE_LIST_ID
    params["ext_mailer_list_id"] =  emailConfig.LIST_ID
}
if (emailConfig.DEBUG_MODE == 'true') {
    params["ext_mailer_debug_mode"] =  emailConfig.DEBUG_MODE
}
if (emailConfig.REQUIRE_ADMIN_FOR_TEMPLATE_TESTING == 'true') {
    params["ext_mailer_require_admin_for_template_testing"] = emailConfig.REQUIRE_ADMIN_FOR_TEMPLATE_TESTING
}
if (emailConfig.WATCHING_ENABLED == 'true') {
    params["ext_mailer_watching_enabled"] = emailConfig.WATCHING_ENABLED
}
if (emailConfig.ALLOW_UNREGISTERED_ENABLED == 'true') {
    params["ext_mailer_allow_unregistered_enabled"] = emailConfig.ALLOW_UNREGISTERED_ENABLED
}
if (emailConfig.ADD_PRECEDENCE_BULK == 'true') {
    params["ext_mailer_add_precedence_bulk"] = emailConfig.ADD_PRECEDENCE_BULK
}

// The following values can be quite complex, and are stored in their own separate
// files
paths = [
    "ext_mailer_default_presend_script": emailConfig.DEFAULT_PRESEND_SCRIPT_PATH,
    "ext_mailer_default_postsend_script": emailConfig.DEFAULT_POSTSEND_SCRIPT_PATH,
    "ext_mailer_default_body": emailConfig.DEFAULT_BODY_PATH
]
paths.each { param_name, filePath ->
    if (!filePath || filePath == '') {
        params[param_name] = null
    } else {
        try {
            fullFilePath = "${configPath}/${filePath}"
            params[param_name] = new File(fullFilePath).text
        } catch (FileNotFoundException e) {
            logger.severe("Cannot find script file path @ ${fullFilePath}")
            jenkins.doSafeExit(null)
            System.exit(1)
        }
    }
}

def stapler = [
    getParameter: { name -> params[name] },
    hasParameter: { name -> params.keySet().contains(name) },
    getParameterValues: { name -> params[name] }
] as org.kohsuke.stapler.StaplerRequest

JSONObject json = new JSONObject()
def triggerList = [
    'AbortedTrigger',
    'AbstractScriptTrigger',
    'AlwaysTrigger',
    'BuildingTrigger',
    'FailureTrigger',
    'FirstFailureTrigger',
    'FirstUnstableTrigger',
    'FixedTrigger',
    'FixedUnhealthyTrigger',
    'ImprovementTrigger',
    'NotBuiltTrigger',
    'NthFailureTrigger',
    'PreBuildScriptTrigger',
    'PreBuildTrigger',
    'RegressionTrigger',
    'ScriptTrigger',
    'SecondFailureTrigger',
    'StatusChangedTrigger',
    'StillFailingTrigger',
    'StillUnstableTrigger',
    'SuccessTrigger',
    'UnstableTrigger',
    'XNthFailureTrigger'
]
// verify that only valid trigger classes are used to set default triggers
emailConfig.TRIGGERS.each { trigger ->
    if (! triggerList.contains(trigger)) {
        logger.severe("Invalid trigger ${trigger} specified")
        jenkins.doSafeExit(null)
        System.exit(1)
    }
}
triggerClasses = emailConfig.TRIGGERS.collect { x ->
    "hudson.plugins.emailext.plugins.trigger.$x".toString()
}
json.put('defaultTriggers', triggerClasses)

try {
    descriptor.configure(stapler, json)
    descriptor.upgradeFromMailer()
} catch (MissingMethodException e) {
    logger.severe("One of the values in the config is typed incorrectly")
    logger.severe(e.toString())
    jenkins.doSafeExit(null)
    System.exit(1)
}
logger.info('Successfully configured the email ext plugin')
