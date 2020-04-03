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
    String formattedWarning = "Vulnerability detected in plugin: ${warning.component}. "
    formattedWarning += "${warning.message}. For more information, see ${warning.url}"
    return formattedWarning
}

public static void main(String[] args) {
    def warnings = getSecurityWarnings()
    def summary = "AC:\nVERIFY critical security vulnerabilities are patched on"
    warnings.each { warning ->
      //println(formatWarning(warning))
        summary +="\n$formatWarning(warning)"
    }

   println(summary)



def JQL = "jql=project=DOS AND text ~ '{security vulnerbility found}' AND status not in (done, resolved, Canceled)"
def authString = "nadeem.shahzad@arbisoft.com:3aINhIFy5qzRHC9rrlYX0CF5"
def body_req = """{
   "fields": {
     "project" : { "key" : "DEVOPS" },
     "issuetype" : { "name" : "Bug" },
     "summary" : "security vulnerbility found",
     "description" : "${list}" }
 }"""


def jira_url = "https://arbisoft123.atlassian.net/rest/api/2/issue/"
//def jira_url_search = "https://arbisoft123.atlassian.net/rest/api/2/search/?jql=project=DEVOPS AND summary ~ 'security vulnerbility found' AND status not in (done, resolved, Canceled)"
//def jira_url_search = "https://arbisoft123.atlassian.net/rest/api/2/search/?jql=project=DEVOPS&summary ~ 'security vulnerbility found'"
def jqlSearch = "https://arbisoft123.atlassian.net/rest/api/2/search/?jql=project%3DDEVOPS%20AND%20summary%20~%20%27security%20vulnerbility%20found%27%20AND%20status%20not%20in%20(done%2C%20resolved%2C%20Canceled)&fields=id"
      


//def proc = [ "curl", "-u", "${authString}", "-X", "POST" ,"--data", "${body_req}", "-H", "Content-Type: application/json", "${jira_url}"].execute()
def proc = [ "curl", "-u", "${authString}", "-X", "GET", "-H", "Content-Type: application/json", "${jira_url_search}"].execute().text
//def stuff = new JsonSlurper().parseText(proc)
println(proc)

def stuff = new JsonSlurper().parseText(proc).total
println(stuff)
if ( stuff == 0 )
{
println("create issue")
}
}
