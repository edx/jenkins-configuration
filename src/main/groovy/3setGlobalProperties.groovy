/**
* set global properties
*
* There are a fair amount of properties that can only be set as a runtime flag
* or via the groovy script console. This script provides unified place to load
* them
**/

import java.util.logging.Logger
import hudson.model.*
@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")
Yaml yaml = new Yaml(new SafeConstructor())

String configPath = System.getenv("JENKINS_CONFIG_PATH")
String configText = ''
try {
    configText = new File("${configPath}/properties_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/properties_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

List properties = yaml.load(configText)

properties.each { property ->
    System.setProperty(property.KEY, property.VALUE)
}
