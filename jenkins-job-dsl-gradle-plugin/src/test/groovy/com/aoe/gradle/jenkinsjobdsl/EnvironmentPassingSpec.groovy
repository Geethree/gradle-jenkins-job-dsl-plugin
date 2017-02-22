package com.aoe.gradle.jenkinsjobdsl

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * @author Carsten Lenz, AOE
 */
class EnvironmentPassingSpec extends Specification {
//    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile
    File jobsDir

    def setup() {
        testProjectDir.create()
        buildFile = testProjectDir.newFile('build.gradle')
        jobsDir = testProjectDir.newFolder('src', 'jobs')
        def sample = new File(jobsDir, 'sample.groovy')
        sample << """

// will fail, if environment params do not get passed through to DSL execution
assert HAMSDI == 'bamsdi'

job("simple-job") {
    description "Job for testing"

    steps {
        shell 'echo hello world'
    }
}
"""
        buildFile << """
        plugins {
            id 'com.aoe.jenkins-job-dsl'
        }

        repositories {
            mavenLocal()
            mavenCentral()
            jcenter()
        }

        jobDsl {
            sourceDir 'src/jobs'
        }

        testDsl.environment(HAMSDI: 'bamsdi')

        """.stripIndent()
    }

    def "executing testDsl"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('testDsl')
                .withPluginClasspath()
                .build()

        then:
//        result.output.contains('')
        result.task(':testDsl').outcome == SUCCESS
    }

}