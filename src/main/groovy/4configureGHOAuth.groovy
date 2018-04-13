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
import hudson.security.ProjectMatrixAuthorizationStrategy
import hudson.security.Permission
import org.jenkinsci.plugins.GithubSecurityRealm
@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml()

String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/security.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/security.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
securityConfig = yaml.load(configText)
securityGroups = securityConfig.SECURITY_GROUPS
oauthSettings = securityConfig.OAUTH_SETTINGS

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

List validPermissions = [   'hudson.model.Computer.Build',
                            'hudson.model.Computer.Configure',
                            'hudson.model.Computer.Connect',
                            'hudson.model.Computer.Create',
                            'hudson.model.Computer.Delete',
                            'hudson.model.Computer.Disconnect',
                            'hudson.model.Hudson.Administer',
                            'hudson.model.Hudson.ConfigureUpdateCenter',
                            'hudson.model.Hudson.Read',
                            'hudson.model.Hudson.RunScripts',
                            'hudson.model.Hudson.UploadPlugins',
                            'hudson.model.Item.Build',
                            'hudson.model.Item.Cancel',
                            'hudson.model.Item.Configure',
                            'hudson.model.Item.Create',
                            'hudson.model.Item.Delete',
                            'hudson.model.Item.Discover',
                            'hudson.model.Item.Read',
                            'hudson.model.Item.Workspace',
                            'hudson.model.Run.Delete',
                            'hudson.model.Run.Update',
                            'hudson.model.View.Configure',
                            'hudson.model.View.Create',
                            'hudson.model.View.Delete',
                            'hudson.model.View.Read'
                            ]

def strategy = new ProjectMatrixAuthorizationStrategy()
securityGroups.each { group ->
    logger.info("Adding security group: ${group.NAME}")
    group.USERS.each { user ->
        group.PERMISSIONS.each { permissionString ->
            if (validPermissions.any { it == permissionString }) {
                Permission permission = Permission.fromId(permissionString)
                strategy.add(permission, user)
            } else {
                logger.severe("Permission ${permissionString} is not supported in Jenkins")
                jenkins.doSafeExit(null)
                System.exit(1)
            }
        }
    }
}
jenkins.setAuthorizationStrategy(strategy)

// Configure Agent security settings
agentConfig = securityConfig.AGENT_SETTINGS
jenkins.setSlaveAgentPort(agentConfig.JNLP_TCP_PORT)
Set<String> protocols = new HashSet<String>();
agentConfig.PROTOCOLS.each { protocol ->
    protocols.add(protocol)
}
jenkins.setAgentProtocols(protocols)

jenkins.save()
