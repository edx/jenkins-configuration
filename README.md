Jenkins Configuration
---------------------

A collection of groovy scripts for automating the configuration of Jenkins and
third party plugins

Tools like the Jenkins Job DSL allow you to programmatically create jobs and other
resources. However, the configuration of Jenkins itself is still a manual process and is error prone.
These scripts can be used to create a reproducible and testable Jenkins instance, complete with
plugins installed and ready to use.

## Setup

Working on this repository requires that both Docker and Gradle are installed.

## Testing

Build a Docker image with Jenkins and the scripts from this repo installed
``
    make build
``

Run the image in the background
``
    make run
``

Test that Jenkins has initialized correctly
``
    make healthcheck
``
