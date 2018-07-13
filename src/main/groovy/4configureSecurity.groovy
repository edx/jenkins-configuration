/**
*
* Configure Jenkins Global Security settings
*
**/

import java.util.logging.Logger
import javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration
import jenkins.model.GlobalConfiguration
import jenkins.model.Jenkins
import hudson.security.ProjectMatrixAuthorizationStrategy
import hudson.security.Permission
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
    configText = new File("${configPath}/security.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/security.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
securityConfig = yaml.load(configText)
securityGroups = securityConfig.SECURITY_GROUPS

List validPermissions = [
    'hudson.model.Computer.Build',
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


// Configure Job DSL script approval
dslScriptSecurity = securityConfig.DSL_SCRIPT_SECURITY_ENABLED
globalDslConfig = GlobalConfiguration.all().get(GlobalJobDslSecurityConfiguration.class)
globalDslConfig.useScriptSecurity=dslScriptSecurity
globalDslConfig.save()

jenkins.save()
