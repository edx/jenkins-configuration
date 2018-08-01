// scan plugin updates

// Run this script on a Jenkins instance to scan the set of installed
// plugins for known security issues. For now, just report this to
// the console. TODO: integrate w/ JIRA to auto-ticket new security
// plugin updates.

import jenkins.model.Jenkins
import jenkins.security.UpdateSiteWarningsConfiguration 

// Return a list of Security Warnings from the Jenkins update center
// that are pertinent to the plugins installed in this Jenkins instance
public Set getSecurityWarnings(Jenkins jenkins) {
    jenkins.pluginManager.doCheckUpdatesServer()
    Set warnings = jenkins.getExtensionList(UpdateSiteWarningsConfiguration.class).get(0).getApplicableWarnings()
    return warnings
}

public static String formatWarning(warning) {
    String formattedWarning = "Vulnerability detected in plugin: ${warning.component}. "
    formattedWarning += "${warning.message}. For more information, see ${warning.url}"
    return formattedWarning
}

public static void main(String[] args) {
    Jenkins jenkins = Jenkins.getInstance()
    def warnings = getSecurityWarnings(jenkins)
    warnings.each { warning ->
        println(formatWarning(warning))
    }
}
