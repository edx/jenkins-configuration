import java.util.logging.Logger
import net.sf.json.JSONObject;

import jenkins.*
import jenkins.model.*
import hudson.model.*
import org.kohsuke.github.GHCommitState;
import org.kohsuke.stapler.StaplerRequest;
import org.jenkinsci.plugins.ghprb.*;
import org.jenkinsci.plugins.ghprb.extensions.*;
import org.jenkinsci.plugins.ghprb.extensions.comments.*;
import org.jenkinsci.plugins.ghprb.extensions.status.*;

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml()

String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/ghprb_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/ghprb_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map ghprbConfig = yaml.load(configText)
def descriptor = Jenkins.instance.getDescriptorByType(
                    org.jenkinsci.plugins.ghprb.GhprbTrigger.DescriptorImpl.class
                 )

JSONObject json = new JSONObject();

json.put('serverAPIUrl', ghprbConfig.SERVER_API_URL)
json.put('accessToken', ghprbConfig.ACCESS_TOKEN)
json.put('adminlist', ghprbConfig.ADMIN_LIST.join(' '));
json.put('requestForTestingPhrase', ghprbConfig.REQUEST_TESTING_PHRASE);
json.put('whitelistPhrase', ghprbConfig.WHITE_LIST_PHRASE);
json.put('okToTestPhrase', ghprbConfig.OK_PHRASE);
json.put('retestPhrase', ghprbConfig.RETEST_PHRASE);
json.put('skipBuildPhrase', ghprbConfig.SKIP_PHRASE);
json.put('cron', ghprbConfig.CRON_SCHEDULE);
json.put('useComments', ghprbConfig.USE_COMMENTS);
json.put('useDetailedComments', ghprbConfig.USE_DETAILED_COMMENTS);
json.put('manageWebhooks', ghprbConfig.MANAGE_WEBHOOKS);
try {
    def commitState = ghprbConfig.UNSTABLE_AS.toUpperCase() as GHCommitState
    json.put('unstableAs', commitState)
} catch (IllegalArgumentException e) {
    logger.severe('Unable to cast UNSTABLE_AS into GHCommitState.')
    logger.severe('Make sure it is one the following values: PENDING, FAILURE, ERROR')
    jenkins.doSafeExit(null)
    System.exit(1)
}
json.put('autoCloseFailedPullRequests', ghprbConfig.AUTO_CLOSE_FAILED_PRS);
json.put('displayBuildErrorsOnDownstreamBuilds', ghprbConfig.DISPLAY_ERRORS_DOWNSTREAM);
String blackList = ghprbConfig.BACK_LIST_LABELS;
if (blackList) {
    blackList = backList.join(' ');
}
json.put('blackListLabels', blackList)
String whiteList = ghprbConfig.WHITE_LIST_LABELS;
if (whiteList) {
    whiteList = whiteList.join(' ');
}
json.put('whiteListLabels', whiteList);
// Leave the following fields blank, and only use them if you need to generate
// a new token via the GUI
json.put('username', '')
json.put('password', '')

StaplerRequest stapler = null
// Submit the configuration
descriptor.configure(stapler, json);

// Configure plugin extensions after the main configuration has been set up
List<GhprbExtension, GhprbExtensionDescriptor> extensions = descriptor.getExtensions()
// Remove any previously configured extensions, as they will create duplicates
extensions.remove(GhprbSimpleStatus.class)
extensions.remove(GhprbPublishJenkinsUrl.class)
extensions.remove(GhprbBuildLog.class)
extensions.remove(GhprbBuildResultMessage.class)

extensions.push(new GhprbSimpleStatus(ghprbConfig.SIMPLE_STATUS))
extensions.push(new GhprbPublishJenkinsUrl(ghprbConfig.PUBLISH_JENKINS_URL))
extensions.push(new GhprbBuildLog(ghprbConfig.BUILD_LOG_LINES_TO_DISPLAY))
ghprbConfig.RESULT_MESSAGES.each { resultMessage ->
    try {
        def resultCommitState = resultMessage.STATUS.toUpperCase() as GHCommitState
        extensions.push(new GhprbBuildResultMessage(resultCommitState, resultMessage.MESSAGE))
    } catch (IllegalArgumentException e) {
        logger.severe('Unable to cast RESULT_MESSAGE.STATUS into GHCommitState')
        logger.severe('Make sure it is one the following values: PENDING, FAILURE, ERROR')
        jenkins.doSafeExit(null)
        System.exit(1)
    }
}

logger.info('Successfully configured the GHPRB plugin')
