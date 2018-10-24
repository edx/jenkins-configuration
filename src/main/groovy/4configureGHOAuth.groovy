/**
* Configure Github OAuth
*
* In order to configure Github OAuth, you must first create a id-secret
* keypair. Follow the steps described here:
* https://wiki.jenkins.io/display/JENKINS/GitHub+OAuth+Plugin#GitHubOAuthPlugin-Setup
* to create the keypair. Input these values into test_data/security.yml
**/

import java.util.logging.Logger
import jenkins.model.Jenkins
import hudson.security.SecurityRealm
import org.jenkinsci.plugins.GithubSecurityRealm
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
    configText = new File("${configPath}/github_oauth.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/github_oauth.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
oauthSettings = yaml.load(configText)

SecurityRealm github_realm = new GithubSecurityRealm(oauthSettings.GITHUB_WEB_URI,
                                                     oauthSettings.GITHUB_API_URI,
                                                     oauthSettings.CLIENT_ID,
                                                     oauthSettings.CLIENT_SECRET,
                                                     oauthSettings.SCOPES
                                                    )
if (!github_realm.equals(jenkins.getSecurityRealm())) {
    // swap in the new Github Security realm (for an update)
    jenkins.setSecurityRealm(github_realm)
    jenkins.save()
}

jenkins.save()
