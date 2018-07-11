/**
*
* Automates setting up the mask passwords
* plugin.
*
**/

import java.util.logging.Logger
import com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsConfig
import com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper.VarPasswordPair

import jenkins.model.*
import hudson.logging.*

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml(new SafeConstructor())

String configPath = System.getenv("JENKINS_CONFIG_PATH")
String configText = ''
try {
    configText = new File("${configPath}/mask_passwords_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/mask_passwords_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map maskPasswordsConfig = yaml.load(configText)

MaskPasswordsConfig plugin = new MaskPasswordsConfig()

// Add classes that should automatically be masked
maskPasswordsConfig.MASKED_PARAMETER_CLASSES.each { maskedClass ->
    plugin.addMaskedPasswordParameterDefinition(maskedClass)
}

// Add Global name/password pairs
maskPasswordsConfig.NAME_PASSWORD_PAIRS.each { namePassPair ->
    VarPasswordPair passwordPair = new VarPasswordPair(namePassPair.NAME, namePassPair.PASSWORD)
    plugin.addGlobalVarPasswordPair(passwordPair)
}
plugin.save(plugin)

logger.info('Successfully Configured the Mask Passwords plugin')
