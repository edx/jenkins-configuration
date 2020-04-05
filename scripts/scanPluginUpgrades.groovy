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
    description += """\\n*Implementation Details:*\\nhttps://openedx.atlassian.net/\
wiki/spaces/EdxOps/pages/1062895636/How+to+update+jenkins+plugins+for+https+build.testeng.edx.org\\n\
https://openedx.atlassian.net/wiki/spaces/EdxOps/pages/1062895636/How+to+update+jenkins+plugins+for+https+build.testeng.edx.org"""
    def authString = "nadeem.shahzad@arbisoft.com:3aINhIFy5qzRHC9rrlYX0CF5"


def env = binding.build.environment; 

    def authString1 = env.JIRA_USER
  

println("${authString1}")

    def req = """{
        "fields": {
            "project" : { "key" : "DOS" },
            "issuetype" : { "name" : "Bug" },
            "summary" : "Build Jenkins Security Check",
            "description" : "${description}" }
    }"""

    def jira_base_url = "https://arbisoft123.atlassian.net/rest/api/2/"
    def jqlSearch = "${jira_base_url}/search/?jql=project+%3D+DOS+AND+summary+%7E+%27Build+Jenkins+Security+Check%27+AND+status+not+in+%28Closed%2C+Canceled%2C+Done%29"
      
    def searchIssue = [ "curl", "-u", "${authString}", "-X", "GET", "-H", "Content-Type: application/json", "${jqlSearch}"].execute().text
    def issueCount = new JsonSlurper().parseText(searchIssue).total
    
    if ( issueCount == 0 ) {
        println("Creating Build Jenkins Security Check Ticket")
        def createIssue = [ "curl", "-u", "${authString}", "-X", "POST" ,"--data", "${req}", "-H", "Content-Type: application/json", "${jira_base_url}/issue"].execute()
        println(createIssue.text)
    }

}
