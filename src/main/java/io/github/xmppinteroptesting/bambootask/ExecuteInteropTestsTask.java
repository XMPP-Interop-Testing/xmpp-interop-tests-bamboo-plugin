package io.github.xmppinteroptesting.bambootask;

import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.build.test.junit.JunitTestReportCollector;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.atlassian.johnson.util.StringUtils;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.utils.process.ExternalProcess;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static io.github.xmppinteroptesting.bambootask.ExecuteInteropTestsTaskConstants.*;

@Scanned
public class ExecuteInteropTestsTask implements TaskType
{
    private final CapabilityContext capabilityContext;
    private final ProcessService processService;
    private final TestCollationService testCollationService;

    public ExecuteInteropTestsTask(@ComponentImport CapabilityContext capabilityContext, @ComponentImport ProcessService processService, @ComponentImport TestCollationService testCollationService)
    {
        this.capabilityContext = capabilityContext;
        this.processService = processService;
        this.testCollationService = testCollationService;
    }

    @NotNull
    @Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        try {
            final ConfigurationMap config = taskContext.getConfigurationMap();
            final String host = config.get(HOST);
            final String domain = config.get(DOMAIN);
            final Long timeout = config.getAsLong(TIMEOUT);
            final String accountUsername = config.get(ADMIN_ACCOUNT_USERNAME);
            final String accountPassword = config.get(ADMIN_ACCOUNT_PASSWORD);
            final String accountOneUsername = config.get(ACCOUNT_ONE_USERNAME);
            final String accountOnePassword = config.get(ACCOUNT_ONE_PASSWORD);
            final String accountTwoUsername = config.get(ACCOUNT_TWO_USERNAME);
            final String accountTwoPassword = config.get(ACCOUNT_TWO_PASSWORD);
            final String accountThreeUsername = config.get(ACCOUNT_THREE_USERNAME);
            final String accountThreePassword = config.get(ACCOUNT_THREE_PASSWORD);
            final String disabledTests = config.get(DISABLED_TESTS);
            final String disabledSpecifications = config.get(DISABLED_SPECIFICATIONS);
            final String enabledTests = config.get(ENABLED_TESTS);
            final String enabledSpecifications = config.get(ENABLED_SPECIFICATIONS);
            final String failOnImpossibleTest = config.get(FAIL_ON_IMPOSSIBLE_TEST);
            final String sintExecutable = config.get(SINTEXECUTABLE);
            final String buildJdk = config.get(BUILDJDK);

            final String sintPath = capabilityContext.getCapabilityValue("system.builder.sintexecutable" + "." + sintExecutable);

            if (StringUtils.isBlank(sintPath) || Files.notExists(Paths.get(sintPath))) {
                taskContext.getBuildLogger().addErrorLogEntry("Unable to find Smack SINT Server Extensions JAR in location that's configured in the corresponding executable: " + sintPath);
                return TaskResultBuilder.newBuilder(taskContext).failedWithError().build();
            }

            final String buildJdkPath = capabilityContext.getCapabilityValue("system.jdk" + "." + buildJdk);
            if (StringUtils.isBlank(buildJdkPath) || Files.notExists(Paths.get(buildJdkPath))) {
                taskContext.getBuildLogger().addErrorLogEntry("Unable to JDK in configured path: " + buildJdkPath);
                return TaskResultBuilder.newBuilder(taskContext).failedWithError().build();
            }
            final Path java = Paths.get(buildJdkPath).resolve("bin").resolve("java");
            if (Files.notExists(java)) {
                taskContext.getBuildLogger().addErrorLogEntry("Unable to find java executable (using the configured JDK) in: " + java);
                return TaskResultBuilder.newBuilder(taskContext).failedWithError().build();
            }

            final List<String> command = new LinkedList<>();
            command.add(java.toString());
            command.add("-Dsinttest.host=" + host);
            command.add("-Dsinttest.service=" + domain);
            command.add("-Dsinttest.replyTimeout=" + timeout);
            if (!StringUtils.isBlank(accountUsername)) {
                command.add("-Dsinttest.adminAccountUsername=" + accountUsername);
            }
            if (!StringUtils.isBlank(accountPassword)) {
                command.add("-Dsinttest.adminAccountPassword=" + accountPassword);
            }
            if (!StringUtils.isBlank(accountOneUsername)) {
                command.add("-Dsinttest.accountOneUsername=" + accountOneUsername);
            }
            if (!StringUtils.isBlank(accountOnePassword)) {
                command.add("-Dsinttest.accountOnePassword=" + accountOnePassword);
            }
            if (!StringUtils.isBlank(accountTwoUsername)) {
                command.add("-Dsinttest.accountTwoUsername=" + accountTwoUsername);
            }
            if (!StringUtils.isBlank(accountTwoPassword)) {
                command.add("-Dsinttest.accountTwoPassword=" + accountTwoPassword);
            }
            if (!StringUtils.isBlank(accountThreeUsername)) {
                command.add("-Dsinttest.accountThreeUsername=" + accountThreeUsername);
            }
            if (!StringUtils.isBlank(accountThreePassword)) {
                command.add("-Dsinttest.accountThreePassword=" + accountThreePassword);
            }
            if (!StringUtils.isBlank(disabledTests)) {
                command.add("-Dsinttest.disabledTests=" + disabledTests);
            }
            if (!StringUtils.isBlank(disabledSpecifications)) {
                command.add("-Dsinttest.disabledSpecifications=" + disabledSpecifications);
            }
            if (!StringUtils.isBlank(enabledTests)) {
                command.add("-Dsinttest.enabledTests=" + enabledTests);
            }
            if (!StringUtils.isBlank(enabledSpecifications)) {
                command.add("-Dsinttest.enabledSpecifications=" + enabledSpecifications);
            }
            if (!StringUtils.isBlank(failOnImpossibleTest)) {
                command.add("-Dsinttest.failOnImpossibleTest=" + Boolean.parseBoolean(failOnImpossibleTest));
            }
            command.add("-Dsinttest.securityMode=disabled");
            command.add("-Dsinttest.enabledConnections=tcp");
            command.add("-Dsinttest.dnsResolver=javax");
            command.add("-Dsinttest.testPackages=org");
            command.add("-Dsinttest.testRunResultProcessors=org.igniterealtime.smack.inttest.SmackIntegrationTestFramework$JulTestRunResultProcessor,org.igniterealtime.smack.inttest.util.JUnitXmlTestRunResultProcessor");
            command.add("-Dsinttest.debugger=org.igniterealtime.smack.inttest.util.FileLoggerFactory");
            command.add("-DlogDir="+ taskContext.getWorkingDirectory().toPath().resolve("logs"));
            command.add("-jar");
            command.add(sintPath);

            final ExternalProcess process = processService.executeExternalProcess(
                taskContext,
                new ExternalProcessBuilder()
                    .command(command)
                    .workingDirectory(taskContext.getWorkingDirectory())
            );

            final Path testResults = taskContext.getWorkingDirectory().toPath().resolve("logs").resolve("test-results.xml");
            if (Files.exists(testResults)) {
                testCollationService.collateTestResults(taskContext, "logs/test-results.xml");

            }

            return TaskResultBuilder.newBuilder(taskContext)
                //.checkReturnCode(process, 0) // Smack will return 0 or 2.
                .checkTestFailures()
                .build();


        } catch (Throwable t) {
            throw new TaskException("Error executing Smack integration tests.", t);
        }
    }
}
