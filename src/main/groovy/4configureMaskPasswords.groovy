/**
*
* Automates setting up the mask passwords
* plugin.
*
**/

import java.util.logging.Logger
import java.nio.file.NoSuchFileException
import com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsConfig
import com.michelin.cio.hudson.plugins.maskpasswords.MaskPasswordsBuildWrapper.VarPasswordPair

import jenkins.model.*
import hudson.logging.*

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml()

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

MaskPasswordsConfig plugin = null
try {
    // If there is an existing configuration, get it and clear it
    plugin = MaskPasswordsConfig.getInstance()
    plugin.clear()
} catch (NoSuchFileException e) {
    // If not, create a new MaskPasswordsConfig object
    plugin = new MaskPasswordsConfig()
}

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
