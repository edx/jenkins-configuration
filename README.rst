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

Setup
~~~~~

Install python dependencies for acceptance testing (on a Jenkins instance
running within a Docker container).

.. code:: bash

    make requirements

Before building the docker container, you should download Jenkins plugins (with
dependencies). By default, the plugins specified in test_data/plugins.yml will
be installed. However, if you wish to use another file, set the environment
variable $PLUGIN_CONFIG to point to the YAML file you wish to use.

.. code:: bash

    make plugins

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
