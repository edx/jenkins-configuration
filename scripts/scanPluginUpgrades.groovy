// scan plugin updates

// Run this script on a Jenkins instance to scan the set of installed
// plugins for known security issues. For now, just report this to
// the console.

import jenkins.security.UpdateSiteWarningsConfiguration
import groovy.json.JsonSlurper

// Return a list of Security Warnings from the Jenkins update center
// that are pertinent to the plugins installed in this Jenkins instance
public Set getSecurityWarnings() {
    UpdateSiteWarningsConfiguration updates = new UpdateSiteWarningsConfiguration()
    Set warnings = updates.getApplicableWarnings()
    return warnings
}

public static String formatWarning(warning) {
    String formattedWarning = "\\n* ${warning.component}. "
    formattedWarning += "${warning.message}. For more information, see ${warning.url}"
    return formattedWarning
}

public static void main(String[] args) {
    def warnings = getSecurityWarnings()
    def description = "*AC:*\\nVERIFY critical security vulnerabilities are patched on:"
    warnings.each { warning ->
        description += formatWarning(warning)
    }
    description += """\\n*Implementation Details:*\\nhttps://2u-internal.atlassian.net/wiki/spaces/SRE/pages/19270504/Jenkins+build.testeng.edx.org"""
    def env = binding.build.environment;
    JIRA_USER = env.JIRA_USER
    JIRA_API_TOKEN = env.JIRA_API_TOKEN
    def authString="${JIRA_USER}:${JIRA_API_TOKEN}"

    def jira_base_url = "https://2u-internal.atlassian.net/rest/api/2/"
    def req = """{
        "fields": {
            "project" : { "key" : "DOS" },
            "issuetype" : { "name" : "Alert" },
            "summary" : "Build Jenkins Security Check",
            "description" : "${description}" }
    }"""

    def jqlSearch = "${jira_base_url}/search/?jql=project+%3D+DOS+AND+summary+%7E+%27Build+Jenkins+Security+Check%27+AND+status+not+in+%28Closed%2C+Canceled%2C+Done%29"
    def searchIssue = [ "curl", "-u", "${authString}", "-X", "GET", "-H", "Content-Type: application/json", "${jqlSearch}"].execute().text
    def issueCount = new JsonSlurper().parseText(searchIssue).total
    if ( issueCount == 0 ) {
        println("Creating Jira Ticket...")
        def createIssue = [ "curl", "-u", "${authString}", "-X", "POST" ,"--data", "${req}", "-H", "Content-Type: application/json", "${jira_base_url}/issue"].execute()
        println(createIssue.text)
    }

}
