freeStyleJob('tripitcalb0t') {
    displayName('tripitcalb0t')
    description('Build Dockerfiles in jessfraz/tripitcalb0t.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jessfraz/tripitcalb0t')
        sidebarLinks {
            link('https://hub.docker.com/r/jess/tripitcalb0t', 'Docker Hub: jess/tripitcalb0t', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(100)
        daysToKeep(15)
    }

    scm {
        git {
            remote {
                url('https://github.com/jessfraz/tripitcalb0t.git')
            }
branches('*/master')
            extensions {
                wipeOutWorkspace()
                cleanAfterCheckout()
            }
        }
    }

    triggers {
        cron('H H * * *')
        githubPush()
    }

    wrappers { colorizeOutput() }

    environmentVariables(DOCKER_CONTENT_TRUST: '1')
    steps {
        shell('docker build --rm --force-rm -t r.j3ss.co/tripitcalb0t:latest .')
        shell('docker tag r.j3ss.co/tripitcalb0t:latest jess/tripitcalb0t:latest')
        shell('docker push --disable-content-trust=false r.j3ss.co/tripitcalb0t:latest')
        shell('docker push --disable-content-trust=false jess/tripitcalb0t:latest')
        shell('docker rm $(docker ps --filter status=exited -q 2>/dev/null) 2> /dev/null || true')
        shell('docker rmi $(docker images --filter dangling=true -q 2>/dev/null) 2> /dev/null || true')
    }

    publishers {
        retryBuild {
            retryLimit(2)
            fixedDelay(15)
        }

        extendedEmail {
            recipientList('$DEFAULT_RECIPIENTS')
            contentType('text/plain')
            triggers {
                stillFailing {
                    attachBuildLog(true)
                }
            }
        }

        wsCleanup()
    }
}
