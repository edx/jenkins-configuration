/**
* install groovy
*
* install various versions of groovy for use as groovy shells in jobs
**/

import java.util.logging.Logger
import jenkins.model.Jenkins
import hudson.tools.InstallSourceProperty
import hudson.tools.ToolProperty
import hudson.tools.ToolPropertyDescriptor
import hudson.util.DescribableList
import hudson.plugins.groovy.GroovyInstaller
import hudson.plugins.groovy.GroovyInstallation
import hudson.plugins.groovy.Groovy

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")
Yaml yaml = new Yaml(new SafeConstructor())
Jenkins jenkins = Jenkins.getInstance()

String configPath = System.getenv("JENKINS_CONFIG_PATH")
try {
    configText = new File("${configPath}/groovy_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/groovy_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
Map globalToolConfig = yaml.load(configText)

def desc = jenkins.getDescriptor("hudson.plugins.groovy.Groovy")
if (desc != null) {
    logger.info("Installing Groovy on this Jenkins")
    ArrayList<GroovyInstallation> installations = new ArrayList<GroovyInstallation>()
    globalToolConfig.GROOVY_INSTALLATIONS.each { groovy ->
        InstallSourceProperty isp = new InstallSourceProperty()
        GroovyInstaller autoInstaller = new hudson.plugins.groovy.GroovyInstaller(
            groovy.VERSION
        )
        isp.installers.add(autoInstaller)
        DescribableList properties = new DescribableList<ToolProperty<?>, ToolPropertyDescriptor>()
        properties.add(isp)

        // Define and add our Groovy installation to Jenkins
        GroovyInstallation installation = new hudson.plugins.groovy.GroovyInstallation(
            groovy.NAME, groovy.HOME, properties
        )
        logger.info("Attempting to install Groovy @ version ${groovy.VERSION}")
        installations.add(installation)
    }
    desc.setInstallations(installations.toArray(new GroovyInstallation[installations.size()]))
    logger.info("Successfully installed the following versions of groovy")
    logger.info(desc.getInstallations().toString())
}
