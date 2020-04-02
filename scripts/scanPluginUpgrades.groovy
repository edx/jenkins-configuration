// scan plugin updates

// Run this script on a Jenkins instance to scan the set of installed
// plugins for known security issues. For now, just report this to
// the console. TODO: integrate w/ JIRA to auto-ticket new security
// plugin updates.

import jenkins.security.UpdateSiteWarningsConfiguration


// Return a list of Security Warnings from the Jenkins update center
// that are pertinent to the plugins installed in this Jenkins instance
public Set getSecurityWarnings() {
    UpdateSiteWarningsConfiguration updates = new UpdateSiteWarningsConfiguration()
    Set warnings = updates.getApplicableWarnings()
    return warnings
}

public static String formatWarning(warning) {
    String formattedWarning = "Vulnerability detected in plugin: ${warning.component}. "
    formattedWarning += "${warning.message}. For more information, see ${warning.url}"
    return formattedWarning
}

public static void main(String[] args) {
    def warnings = getSecurityWarnings()
    warnings.each { warning ->
        println(formatWarning(warning))
    }



def response = "curl -D- -u nadeem.shahzad@arbisoft.com:3aINhIFy5qzRHC9rrlYX0CF5 -X POST --data '{"fields":{"project":{"key": "DEVOPS"},"summary": "REST ye merry gentlemen.","description": "Creating of an issue using project keys and issue type names using the REST API","issuetype": {"name": "Bug"}}}' -H "Content-Type: application/json" https://arbisoft123.atlassian.net/rest/api/2/issue/".execute().text



}
