import java.util.logging.Logger

import jenkins.*
import jenkins.model.*

import org.jenkinsci.plugins.github.GitHubPlugin
import org.jenkinsci.plugins.github.config.GitHubServerConfig

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
    configText = new File("${configPath}/github_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/github_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

List githubConfigs = yaml.load(configText)

githubConfigs.each { config ->
    GitHubServerConfig server = new GitHubServerConfig(config.CREDENTIAL_ID)
    server.setManageHooks(config.MANAGE_HOOKS.toBoolean())
    if (config.USE_CUSTOM_API_URL.toBoolean()) {
        server.setCustomApiUrl(config.USE_CUSTOM_API_URL.toBoolean())
        server.setApiUrl(config.API_URL)
    }
    if (config.CACHE_SIZE > 0) {
        server.setClientCacheSize(config.CACHE_SIZE)
    }
    GitHubPlugin.configuration().getConfigs().add(server)
}

// Note: This script does not currently include configuration for advanced
// github plugin options
