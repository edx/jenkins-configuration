/**
*
* Automate adding credentials into Jenkins
*
**/
import java.util.logging.Logger
import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*
import hudson.util.Secret

import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.impl.*

import org.apache.commons.fileupload.*
import org.apache.commons.fileupload.disk.*
import java.nio.file.*

@Grapes([
    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
])
import org.yaml.snakeyaml.Yaml

Logger logger = Logger.getLogger("")
Jenkins jenkins = Jenkins.getInstance()
Yaml yaml = new Yaml()

globalDomain = Domain.global()
credentialsStore =
Jenkins.instance.getExtensionList(
    'com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0].getStore()

String configPath = System.getenv("JENKINS_CONFIG_PATH")
String configText = ''
try {
    configText = new File("${configPath}/credentials.yml").text
} catch (FileNotFoundException e) {
    logger.severe("Cannot find config file path @ ${configPath}/credentials.yml")
    jenkins.doSafeExit(null)
    System.exit(1)
}

// Delete any existing credentials to avoid clutter
// and unintended duplication.
credDomains = credentialsStore.getDomains()
credDomains.each { domain ->
    allCredentials = credentialsStore.getCredentials(domain)
    allCredentials.each { credential ->
        credentialsStore.removeCredentials(domain, credential)
    }
    // Now remove the domain
    credentialsStore.removeDomain(domain)
}

List credentialConfig = yaml.load(configText)
credentialConfig.each { newCredential ->
    credentialType = newCredential.credentialType
    id = newCredential.id
    description = newCredential.description
    scopeString = newCredential.scope

    // All credentials must have a credentialType and scope
    if (!credentialType || !scopeString) {
        logger.severe("Missing data for credential. Please ensure every entry in the " +
                      "credentials.yml file has a credentialType and scope")
        jenkins.doSafeExit(null)
        System.exit(1)
    }

    switch (scopeString.toLowerCase()) {
        case 'global':
            scope = CredentialsScope.GLOBAL
            break
        case 'system':
            scope = CredentialsScope.SYSTEM
            break
        default:
            logger.severe("Invalid scope specified for credential. Scope " +
                          "should be GLOBAL or SYSTEM. Was: ${scopeString}")
            jenkins.doSafeExit(null)
            System.exit(1)
    }

    switch (credentialType) {
        case 'usernamePassword':
            username = newCredential.username
            password = newCredential.password

            // Make sure the username and password fields are not empty
            if (!username || !password) {
                logger.severe("Missing data for credential. Please ensure usernamePassword " +
                              "entries in the credentials.yml file have a " +
                              "username and password")
                jenkins.doSafeExit(null)
                System.exit(1)
            }

            cred = new UsernamePasswordCredentialsImpl(
                scope,
                id,
                description,
                username,
                password
            )
            credentialsStore.addCredentials(
                globalDomain,
                cred
            )
            break
        case 'secretText':
            text = newCredential.secretText

            // Make sure the text field is not empty
            if (!text) {
                logger.severe("Missing data for credential. Please ensure secretText " +
                              "entries in the credentials.yml file have text")
                jenkins.doSafeExit(null)
                System.exit(1)
            }

            secretText = new StringCredentialsImpl(
                scope,
                id,
                description,
                Secret.fromString(text)
            )
            credentialsStore.addCredentials(globalDomain, secretText)
            break
        case 'secretFile':
            filePath = newCredential.path
            fileName = newCredential.name

            // Make sure the path and name is not empty
            if (!filePath || !fileName) {
                logger.severe("Missing data for credential. Please ensure secretFile " +
                              "entries in the credentials.yml file have a path and name")
                jenkins.doSafeExit(null)
                System.exit(1)
            }

            // Create the full path to the secret file
            fullFilePath = "${configPath}/${filePath}"

            factory = new DiskFileItemFactory()
            fileItem = factory.createItem("", "application/octet-stream", false, fileName)
            out = fileItem.getOutputStream()
            file = new File(fullFilePath)
            try {
                Files.copy(file.toPath(), out)
            } catch (NoSuchFileException e) {
                logger.severe("No file found at: ${fullFilePath}. Please ensure the file exists " +
                              "and try again.")
                jenkins.doSafeExit(null)
                System.exit(1)
            }
            secretFile = new FileCredentialsImpl(
                scope,
                id,
                description,
                fileItem,
                "",
                ""
            )
            credentialsStore.addCredentials(globalDomain, secretFile)
            break
        case 'ssh':
            username = newCredential.username
            passphrase = newCredential.passphrase
            isJenkinsMasterSsh = newCredential.isJenkinsMasterSsh

            // Make sure username, passphrase and isJenkinsMasterSsh are not empty
            if (!username || !passphrase || isJenkinsMasterSsh == null) {
                logger.severe("Missing data for credential. Please ensure ssh " +
                              "entries in the credentials.yml file have a username, " +
                              "passphrase, and isJenkinsMasterSsh")
                jenkins.doSafeExit(null)
                System.exit(1)
            }

            if (isJenkinsMasterSsh) {
                sshKey = new BasicSSHUserPrivateKey.UsersPrivateKeySource()
            } else {
                sshPath = newCredential.path

                // Make sure there is a path to the ssh file
                if (!sshPath) {
                    logger.severe("Missing data for credential. Please ensure ssh " +
                                  "entries in the credentials.yml file " +
                                  "(with isJenkinsMasterSsh = False) have a " +
                                  "path")
                    jenkins.doSafeExit(null)
                    System.exit(1)
                }

                // Create the full path to the secret file
                fullSshPath = "${configPath}/${sshPath}"

                // Make sure the ssh file exists
                sshFileExists = Files.exists(Paths.get(fullSshPath), LinkOption.NOFOLLOW_LINKS)
                if (!sshFileExists) {
                    logger.severe("No ssh file found at: ${fullSshPath}. Please ensure the file " +
                                  "exists and try again.")
                    jenkins.doSafeExit(null)
                    System.exit(1)
                }

                sshKey = new BasicSSHUserPrivateKey.FileOnMasterPrivateKeySource(fullSshPath)
            }
            ssh = new BasicSSHUserPrivateKey(
                scope,
                id,
                username,
                sshKey,
                passphrase,
                description
            )
            credentialsStore.addCredentials(globalDomain, ssh)
            break
        case 'certificate':
            certPath = newCredential.path
            password = newCredential.password

            // Make sure the path is not empty
            if (!certPath || !password) {
                logger.severe("Missing data for credential. Please ensure certificate " +
                              "entries in the credentials.yml file have a path and password")
                jenkins.doSafeExit(null)
                System.exit(1)
            }

            // Create the full path to the secret file
            fullCertPath = "${configPath}/${certPath}"

            // Make sure the certificate file exists
            certFileExists = Files.exists(Paths.get(fullCertPath), LinkOption.NOFOLLOW_LINKS)
            if (!certFileExists) {
                logger.severe("No certificate file found at: ${fullCertPath}. Please ensure the file " +
                              "exists and try again.")
                jenkins.doSafeExit(null)
                System.exit(1)
            }

            cert = new CertificateCredentialsImpl.FileOnMasterKeyStoreSource(fullCertPath)
            certificate = new CertificateCredentialsImpl(
                scope,
                id,
                description,
                password,
                cert
            )
            credentialsStore.addCredentials(globalDomain, certificate)
            break
        default:
            logger.severe("Invalid credentialType. Must be usernamePassword, secretText, " +
                          "secretFile, ssh, or certificate. Got: ${credentialType}")
            jenkins.doSafeExit(null)
            System.exit(1)
    }
}
