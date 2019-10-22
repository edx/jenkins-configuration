Jenkins Configuration
=====================

A collection of groovy scripts for automating the configuration of Jenkins and
third party plugins

Tools like the Jenkins Job DSL allow you to programmatically create jobs and other
resources. However, the configuration of Jenkins itself is still a manual process and is error prone.
These scripts can be used to create a reproducible and testable Jenkins instance, complete with
plugins installed and is ready to use.

Setup
-----

System Requirements
~~~~~~~~~~~~~~~~~~~

Working on this repository requires that both Docker and Gradle are installed.

install gradle
https://gradle.org/install/

install docker
https://docs.docker.com/install/

Setup
~~~~~

Before running anything the following environment variables must be set:

    - JENKINS_VERSION -> version of the Jenkins war file to install in the
        Docker container
    - JENKINS_WAR_SOURCE -> location hosting the war file you want to download
    - CONFIG_PATH -> path to the yml config files that will be copied to the
        docker container and consumed by the groovy init scripts
    - PLUGIN_CONFIG -> path the yml config file containing the desired plugin
        version names and versions to be installed prior to Jenkins initialization
    - CONTAINER_NAME -> name of the docker container that gets created
    - TEST_SHARD -> used to specify which set of scripts will be used to
        configure a Jenkins container and which tests should be run against
        said container.

This can be done by copying local_env.sample.sh, making the modifications you
need, and running:

.. code:: source

    source local_env.sh

Install python dependencies for acceptance testing (on a Jenkins instance
running within a Docker container), as well as various Groovy helper utilities.

.. code:: bash

    make requirements

Before building the docker container, you should download Jenkins plugins (with
dependencies). By default, the plugins specified in test_data/plugins.yml will
be installed. However, if you wish to use another file, set the environment
variable $PLUGIN_CONFIG to point to the YAML file you wish to use.

.. code:: bash

    make plugins

Configuring Jenkins
-------------------

Jenkins will run Groovy code placed in $JENKINS_HOME/init.groovy.d on each boot.

Tips/Tricks
~~~~~~~~~~~

Unlike the Jenkins Script Console, Jenkins-related libraries are not auto-imported,
so make sure you import the following into your scripts:

.. code:: groovy

    import jenkins.*
    import jenkins.model.*
    import hudson.*
    import hudson.model.*

Scripts are run in lexicographical order. Use a numerical prefix for scripts that
must be run in a particular order. The following order is suggested for scripts:

    - 1<scriptName> : bootstrapping scripts, such as making helper jars available
    - 2<scriptName> : verification scripts, used to check the system before configuration
    - 3<scriptName> : main (Jenkins core) configuration scripts
    - 4<scriptName> : plugin configuration scripts
    - 5<scriptName> : configuration scripts that rely on something configured in the previous step
    - ...
    - 9<scriptName> : scripts to run at the end of the configuration process (i.e. putting into quiet mode or testing a configuration

Groovy Dependencies:
~~~~~~~~~~~~~~~~~~~~

In order to use libraries outside of the Groovy standard library, you must first run
src/main/groovy/1_add_jars_to_classpath.groovy. This will allow you to make use of
the Groovy Grape_ system in subsequent scripts for importing external libraries. For
example, if you wanted to make use of the Snake Yaml library:

.. code:: groovy

    @Grab(group='org.yaml', module='snakeyaml', version='1.17')
    import org.yaml.snakeyaml.Yaml
    import org.yaml.snakeyaml.constructor.SafeConstructor

.. _Grape: http://docs.groovy-lang.org/latest/html/documentation/grape.html

Testing
-------

Linting
~~~~~~~

Run codenarc_ to lint the groovy code in src/main/groovy and src/test/groovy

.. code:: bash

    make quality

Linting reports can be viewed in build/reports/codenarc/main.html

.. _Codenarc: http://codenarc.sourceforge.net/

Acceptance Testing
~~~~~~~~~~~~~~~~~~

Build a Docker image with Jenkins and the scripts from this repo installed

* NOTE: The Dockerfile in this repo makes use of `multi-stage builds`, a
  relatively newer Docker featue. Docker version 17.05 or higher is required.

.. code:: bash

    make build

Run the image in the background

.. code:: bash

    make run

Test that Jenkins has initialized correctly

.. code:: bash

    make healthcheck

Test the configuration of a running Jenkins instance

.. code:: bash

    make e2e

Configuring Your Jenkins Instance With Ansible:
~~~~~~~~~~~~~~~~~~

Following this repository with the default configuration files will leave you
with a sample instance sufficient for testing out groovy init scripts. If you
are interested in using this to create a more accurate representation of your
Jenkins instance, you can pair these steps with our ansible role found here:
https://github.com/edx/configuration/tree/master/playbooks/roles/jenkins_common

Step 1

*** STOP! DO SETUP FIRST ***

To use this play to create your instance, first follow the steps found above
under Setup. Once you've done that, create the container:

.. code:: bash

    make run.container

Make sure the ssh key file has the correct permissions:

.. code:: bash

    chmod 0600 ssh/jc_rsa

Next, run the ansible play targeting the container:

.. code:: bash

    ansible-playbook -i localhost:2222, <path to ansible play> -e@<path to secure file> --tags jenkins:local-dev -u root --key-file ssh/jc_rsa

Start the jenkins application:

.. code:: bash

    make run.jenkins

Plugin Versions
~~~~~~~~~~~~~~~~~~

The groovy scripts in this repository are maintained to match the current configuration of our Jenkins instance here at edx (which is running version 2.150.2 of Jenkins). Plugins are always changing, and sometimes constructors or other methods that these scripts rely on change as well. Therefore, tweaks may be necessary for this to function properly with your jenkins instance.
