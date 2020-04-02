// scan plugin updates

// Run this script on a Jenkins instance to scan the set of installed
// plugins for known security issues. For now, just report this to
// the console. TODO: integrate w/ JIRA to auto-ticket new security
// plugin updates.

import jenkins.security.UpdateSiteWarningsConfiguration
import groovy.json.StreamingJsonBuilder

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


def authString = "nadeem.shahzad@arbisoft.com:3aINhIFy5qzRHC9rrlYX0CF5"
def body_req = '''{
   "fields": {
     "project" : { "key" : "DEVOPS" },
     "issuetype" : { "name" : "Bug" },
     "summary" : "TEST",
     "description" : "TEST"}
 }'''


def url = "https://arbisoft123.atlassian.net/rest/api/2/issue/"

      


// def response = "curl -D- -u \"${authString}\" -X POST --data '${body_req}' -H \"Content-Type: application/json\" ${url}".execute()




		
def connection = new URL("https://arbisoft123.atlassian.net/rest/api/2/issue/").openConnection() as HttpURLConnection
connection.setRequestMethod( "POST" )
connection.setRequestProperty( "Authorization", "Basic ${authString}" )
connection.doOutput = true
connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
connection.getOutputStream().write(body_req.getBytes("UTF-8"))
connection.connect()

//this is used only to see what Jira responses 
def postRC = connection.getResponseCode();
println(postRC);
if(postRC.equals(200)) { 
println(connection.getInputStream().getText());
}



}
