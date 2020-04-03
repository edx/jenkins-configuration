// scan plugin updates

// Run this script on a Jenkins instance to scan the set of installed
// plugins for known security issues. For now, just report this to
// the console. TODO: integrate w/ JIRA to auto-ticket new security
// plugin updates.

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
    String formattedWarning = "\\n${warning.component}. "
    formattedWarning += "${warning.message}. For more information, see ${warning.url}"
    return formattedWarning
}

public static void main(String[] args) {
    def warnings = getSecurityWarnings()
    def description = "AC:\\nVERIFY critical security vulnerabilities are patched on:"
    warnings.each { warning ->
        description += formatWarning(warning)
    }
    description += """\\nImplementation Details:\\nhttps://openedx.atlassian.net/\
wiki/spaces/EdxOps/pages/1062895636/How+to+update+jenkins+plugins+for+https+build.testeng.edx.org\\n\
https://openedx.atlassian.net/wiki/spaces/EdxOps/pages/1062895636/How+to+update+jenkins+plugins+for+https+build.testeng.edx.org"""
    println(description)
    def authString = "nadeem.shahzad@arbisoft.com:3aINhIFy5qzRHC9rrlYX0CF5"
    def req = """{
        "fields": {
            "project" : { "key" : "DOS" },
            "issuetype" : { "name" : "Security" },
            "summary" : "Build Jenkins Security Check",
            "description" : "${description}" }
    }"""

    def jira_url = "https://arbisoft123.atlassian.net/rest/api/2/issue/"
    def jqlSearch = "https://arbisoft123.atlassian.net/rest/api/2/search/?jql=project%3DOS%20AND%20summary%20~%20%27Build%20Jenkins%20Security%20Check%27%20AND%20status%20not%20in%20(done%2C%20resolved%2C%20Canceled)"
      
    def searchIssue = [ "curl", "-u", "${authString}", "-X", "GET", "-H", "Content-Type: application/json", "${jqlSearch}"].execute().text

    def issueCount = new JsonSlurper().parseText(searchIssue).total
    
    if ( issueCount == 0 ) {
        println("Creating Build Jenkins Security Check Ticket")
        def createIssue = [ "curl", "-u", "${authString}", "-X", "POST" ,"--data", "${req}", "-H", "Content-Type: application/json", "${jira_url}"].execute()
        println(createIssue.text)
    }

}
