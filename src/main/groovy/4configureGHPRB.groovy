import java.util.logging.Logger
import net.sf.json.JSONObject;
import javax.servlet.http.HttpServletRequestWrapper
import java.lang.reflect.Field

import jenkins.*
import jenkins.model.*
import hudson.model.*
import hudson.util.Secret
import org.kohsuke.github.GHCommitState;
import org.kohsuke.stapler.*;
import org.jenkinsci.plugins.ghprb.*;
import org.jenkinsci.plugins.ghprb.extensions.*;
import org.jenkinsci.plugins.ghprb.extensions.comments.*;
import org.jenkinsci.plugins.ghprb.extensions.status.*;

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17'),
    @Grab(group='org.mockito', module='mockito-all', version='1.10.19')
])
import org.yaml.snakeyaml.Yaml
import org.mockito.Mockito

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

json.put('requestForTestingPhrase', ghprbConfig.REQUEST_TESTING_PHRASE);
json.put('whitelistPhrase', ghprbConfig.WHITE_LIST_PHRASE);
json.put('okToTestPhrase', ghprbConfig.OK_PHRASE);
json.put('retestPhrase', ghprbConfig.RETEST_PHRASE);
json.put('skipBuildPhrase', ghprbConfig.SKIP_PHRASE);
json.put('cron', ghprbConfig.CRON_SCHEDULE);
json.put('useComments', ghprbConfig.USE_COMMENTS);
json.put('useDetailedComments', ghprbConfig.USE_DETAILED_COMMENTS);
json.put('manageWebhooks', ghprbConfig.MANAGE_WEBHOOKS);
List adminList = ghprbConfig.ADMIN_LIST;
if (adminList) {
    json.put('adminlist', adminList.join(' '));
} else {
    json.put('adminlist', '');
}
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
json.put("githubAuth", null);

List blackList = ghprbConfig.BLACK_LIST_LABELS;
if (blackList) {
    json.put('blackListLabels', blackList.join(' '));
} else {
    json.put('blackListLabels', '');
}

List whiteList = ghprbConfig.WHITE_LIST_LABELS;
if (whiteList) {
    json.put('whiteListLabels', whiteList.join(' '));
} else {
    json.put('whiteListLabels', '');
}

StaplerRequest stapler =  new RequestImpl(
    new Stapler(),
    Mockito.mock(HttpServletRequestWrapper.class),
    new ArrayList<AncestorImpl>(),
    new TokenList("")
)
descriptor.configure(stapler, json);

Field githubAuth = descriptor.class.getDeclaredField("githubAuth")
githubAuth.setAccessible(true)
githubAuthArray = new ArrayList<GhprbGitHubAuth>()
Secret sharedSecret = new Secret(ghprbConfig.SHARED_SECRET)
githubAuthArray.add(new GhprbGitHubAuth(
    ghprbConfig.SERVER_API_URL,
    null,
    ghprbConfig.CREDENTIALS_ID,
    null,
    null,
    sharedSecret)
)
githubAuth.set(descriptor, githubAuthArray)
descriptor.save()

// Configure plugin extensions after the main configuration has been set up
List<GhprbExtension, GhprbExtensionDescriptor> extensions = descriptor.getExtensions()
// Remove any previously configured extensions, as they will create duplicates
extensions.remove(GhprbSimpleStatus.class)
extensions.remove(GhprbPublishJenkinsUrl.class)
extensions.remove(GhprbBuildLog.class)
extensions.remove(GhprbBuildResultMessage.class)

// Only add GHPRB extensions if they have non empty/zero values in
// github_config.yml.
if (!ghprbConfig.SIMPLE_STATUS.isEmpty()) {
    extensions.push(new GhprbSimpleStatus(ghprbConfig.SIMPLE_STATUS))
}
if (!ghprbConfig.PUBLISH_JENKINS_URL.isEmpty()) {
    extensions.push(new GhprbPublishJenkinsUrl(ghprbConfig.PUBLISH_JENKINS_URL))
}
if (ghprbConfig.BUILD_LOG_LINES_TO_DISPLAY > 0) {
    extensions.push(new GhprbBuildLog(ghprbConfig.BUILD_LOG_LINES_TO_DISPLAY))
}
if (ghprbConfig.RESULT_MESSAGES.isEmpty()) {
    ArrayList<GhprbBuildResultMessage> buildResultMessages = new ArrayList<GhprbBuildResultMessage>()
    ghprbConfig.RESULT_MESSAGES.each { resultMessage ->
        try {
            def resultCommitState = resultMessage.STATUS.toUpperCase() as GHCommitState
            buildResultMessages << new GhprbBuildResultMessage(resultCommitState, resultMessage.MESSAGE)
        } catch (IllegalArgumentException e) {
            logger.severe('Unable to cast RESULT_MESSAGE.STATUS into GHCommitState')
            logger.severe('Make sure it is one the following values: PENDING, FAILURE, ERROR')
            jenkins.doSafeExit(null)
            System.exit(1)
        }
    }
    extensions.push(new GhprbBuildStatus(buildResultMessages))
}

logger.info('Successfully configured the GHPRB plugin')
