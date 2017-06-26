import jenkins.*;
import jenkins.model.*;
import hudson.model.*;
@GrabResolver(name='gradle', root='https://repo.gradle.org/gradle/libs-releases-local/')
@Grapes([
    // slf4j is a runtime depenedency of the gradle tooling api
    @Grab(group='org.slf4j', module='slf4j-api', version='1.7.21'),
    @Grab(group='org.gradle', module='gradle-tooling-api', version='2.10')
])
import org.gradle.tooling.GradleConnector

plugins = Jenkins.instance.pluginManager.plugins

// Determine if the plugins installed in Jenkins match the expected versions.
// if not, resolve the specifed plugins dependencies, pin them, and then
// restart Jenkins
def isInstalled = plugins.any { plugin ->
    plugin.getShortName() == 'ghprb' && plugin.getVersion() == "1.34.0"
}
if (isInstalled) {
    println("BOOTSTRAP: GHPRB present, moving on...:")
}
else {
    println("BOOTSTRAP: GHPRB not present, installing (w/ dependencies and rebooting!")
    def path = new File('/var/lib/jenkins/plugin-resolver')
    def conn = GradleConnector.newConnector().forProjectDirectory(path).connect()
    conn.newBuild().forTasks('clean').run()
    conn.newBuild().forTasks('install').run()
    conn.close()
    Jenkins.instance.restart()
}
