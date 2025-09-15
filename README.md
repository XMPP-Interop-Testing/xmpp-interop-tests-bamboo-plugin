# xmpp-interop-tests-bamboo plugin
An Atlassian Bamboo plugin that performs XMPP interoperability tests on an XMPP domain.

The checks run in a to-be-added task within the plan. The task type that will be used is provided by a custom Bamboo plugin, developed by us. The task will execute tests and output junit-esque and debugging output. The only prerequisite is that you’ve got a built server and have started it.

For more information, please visit our project website at [https://xmpp-interop-testing.github.io/](https://xmpp-interop-testing.github.io/)

## Installing the Plugin

Download the latest release of the XMPP Interop Testing plugin for Bamboo (from the GitHub 'packages' section), which should be a JAR-file.

Following the documentation provided by Atlassian, install the plugin in your Bamboo instance.

## Installing the Test Executable

The XMPP Interop Testing plugin for Bamboo that you’ve installed in the previous step provides the framework for test execution, but does not include the tests themselves. The tests are installed as a Bamboo “Executable capability”.

Download the latest release of the [Smack SINT Server Extensions project](https://github.com/XMPP-Interop-Testing/smack-sint-server-extensions/releases), which should be a JAR-file. This is the file that will be added as a Bamboo Executable capability.

Our Bamboo plugin, that you’ve installed earlier, will have made available a new “Executable capability” type, named “Smack SINT Server Extensions JAR”. Following [these guidelines in Atlassian’s documentation](https://confluence.atlassian.com/bamboo/defining-a-new-executable-capability-289277164.html), add the JAR file that you downloaded as a new Capability of the type “Executable”, that uses that Executable Type. Choose a recognizable label, which typically refers to the version number of the JAR file that you used.

## Ensure that a suitable JDK is available

The test execution requires that Java version 11 or higher is available. Ensure that your server has an appropriate JDK installed as a server capability. Refer to [Atlassian’s documentation on Defining a new JDK capability](https://confluence.atlassian.com/bamboo/defining-a-new-jdk-capability-289277157.html) for details on how to achieve this.

## Executing the tests

The Bamboo plugin that was installed earlier has made available a Task type named “XMPP Interop Testing” (in the category “Tests”).

When configuring the test, ensure that under the “Smack SINT Server Extensions JAR” option, the Test Executable is selected that was created earlier in this guide.

The Build JDK that is selected needs to be Java 11 or higher.

To interact with the server, fill out the IP address (or hostname) of the server that was started in the previous job. Also provide the name of the XMPP domain that is serviced by that server. When the server is running on local hardware, then there should not be a reason to increase the default timeout value.

The tests are executed using dedicated accounts. These accounts are created by the test framework in one of three ways:
- by using an administrative account (per [XEP-0133](https://xmpp.org/extensions/xep-0133.html))
- by explicitly providing three accounts
- using in-band registration (per [XEP-0077](https://xmpp.org/extensions/xep-0077.html))

If the first method is desired, then you should provide the credentials of an administrative user in the task configuration. Alternatively, three sets of test accounts can be provided. When none of these credentials are provided, then the last method will be used to provision test accounts.

For more information on provisioning accounts, consult the ['Test Account Provisioning' guide](https://xmpp-interop-testing.github.io/documentation/provisioning-accounts).

Finally, you can provide a comma-separated list of tests that are to be skipped (For example: `EntityCapsTest,SoftwareInfoIntegrationTest`), or specifications (not case-sensitive) that are to be skipped (For example: `XEP-0045,XEP-0060`). Similarly, to limit execution to a subset of tests, you can specify enabled tests or specifications.

For more information, please visit our project website at [https://xmpp-interop-testing.github.io/](https://xmpp-interop-testing.github.io/)
