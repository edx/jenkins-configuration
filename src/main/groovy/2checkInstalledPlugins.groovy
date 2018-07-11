/**
* verify plugin installations
*
* check that each expected plugin is installed and at the expected version
*
**/

import java.util.logging.Logger
import jenkins.*
import jenkins.model.*
import hudson.model.*
@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")

logger.info("Loading plugin configuration")
Yaml yaml = new Yaml(new SafeConstructor())
String configPath = System.getenv("JENKINS_CONFIG_PATH")
List expectedPlugins = yaml.load(new File("${configPath}/plugins.yml").text)

Jenkins jenkins = Jenkins.getInstance()
List installedPlugins = jenkins.getPluginManager().plugins

expectedPlugins.each { expectedPlugin ->

    Boolean isInstalled = installedPlugins.any { installedPlugin ->
        return installedPlugin.shortName == expectedPlugin.name &&
        installedPlugin.version == expectedPlugin.version
    }
    if (!isInstalled) {
        logger.severe("Expected plugin ${expectedPlugin.name} @ version ${expectedPlugin.version} not installed!")
        logger.severe("Cancelling Jenkins start up.")
        jenkins.doSafeExit(null)
        System.exit(1)
    }
    logger.info("Plugin ${expectedPlugin.name} @ version ${expectedPlugin.version} is installed")

}
logger.info("All expected plugins are installed correctly")
