/*
The MIT License

Copyright (c) 2015, Kohsuke Kawaguchi

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

/**
* shutdown jenkins CLI
*
* as advised in https://github.com/jenkinsci-cert/SECURITY-218, this script
* will shutdown the Jenkins CLI components, which are a security concern for
* the version of Jenkins we are currently running.
*
**/

import java.util.logging.Logger
import jenkins.*;
import jenkins.model.*;
import hudson.model.*;
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
    configText = new File("${configPath}/main_config.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/main_config.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}
Boolean cliEnabled = (Boolean) yaml.load(configText).CLI.CLI_ENABLED

if (cliEnabled) {
    logger.info("Leaving the Jenkins CLI subsystem intact")
    System.exit(0)
}

// disabled CLI access over TCP listener (separate port)
def p = AgentProtocol.all()
p.each { x ->
  if (x.name.contains("CLI")) {
    p.remove(x)
  }
}

// disable CLI access over /cli URL
def removal = { lst ->
    lst.each { x ->
        if (x.getClass().name.contains("CLIAction")) {
            lst.remove(x)
        }
    }
}

logger.info("Removing the Jenkins CLI subsystem")
removal(jenkins.getExtensionList(RootAction.class))
removal(jenkins.actions)
