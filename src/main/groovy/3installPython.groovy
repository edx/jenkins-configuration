/**
* install python
*
* install various versions of python on Jenkins, so that they can be
* used by virtual environments via the Shining Panda plugin
**/

import java.util.logging.Logger

import jenkins.model.Jenkins
import jenkins.plugins.shiningpanda.tools.PythonInstallation

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
    configText = new File("${configPath}/python_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/python_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

Map pythonConfig = yaml.load(configText)

def descriptor = jenkins.getDescriptorByType(
    jenkins.plugins.shiningpanda.tools.PythonInstallation.DescriptorImpl.class
)

logger.info("Installing Python on this Jenkins")

ArrayList<PythonInstallation> installations = new ArrayList<PythonInstallation>()

pythonConfig.PYTHON_INSTALLATIONS.each { py ->
    python = new PythonInstallation(
        py.PYTHON_ALIAS, py.PYTHON_PATH, py.PYTHON_PROPERTIES
    )
    logger.info("Attempting to install the python binary @ ${py.PYTHON_PATH}")
    installations.add(python)
}

descriptor.setInstallations(
    installations.toArray(new PythonInstallation[installations.size()])
)
