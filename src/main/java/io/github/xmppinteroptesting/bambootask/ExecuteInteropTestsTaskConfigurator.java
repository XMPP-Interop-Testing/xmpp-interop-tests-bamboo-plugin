package io.github.xmppinteroptesting.bambootask;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.agent.capability.Requirement;
import com.atlassian.bamboo.v2.build.agent.capability.RequirementImpl;
import com.atlassian.bamboo.ww2.actions.build.admin.create.UIConfigSupport;
import com.atlassian.johnson.util.StringUtils;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import static io.github.xmppinteroptesting.bambootask.ExecuteInteropTestsTaskConstants.*;

@Scanned
public class ExecuteInteropTestsTaskConfigurator extends AbstractTaskConfigurator implements TaskRequirementSupport
{
    public static final String UI_CONFIG_SUPPORT = "uiConfigSupport";
    private final I18nResolver i18nResolver;
    private final UIConfigSupport uiConfigSupport;

    public ExecuteInteropTestsTaskConfigurator(@ComponentImport I18nResolver i18nResolver, @ComponentImport UIConfigSupport uiConfigSupport)
    {
        this.i18nResolver = i18nResolver;
        this.uiConfigSupport = uiConfigSupport;
    }

    public @NotNull Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

        config.put(HOST, params.getString(HOST));
        config.put(DOMAIN, params.getString(DOMAIN));
        config.put(TIMEOUT, params.getString(TIMEOUT));
        config.put(ADMIN_ACCOUNT_USERNAME, params.getString(ADMIN_ACCOUNT_USERNAME));
        config.put(ADMIN_ACCOUNT_PASSWORD, params.getString(ADMIN_ACCOUNT_PASSWORD));
        config.put(ACCOUNT_ONE_USERNAME, params.getString(ACCOUNT_ONE_USERNAME));
        config.put(ACCOUNT_ONE_PASSWORD, params.getString(ACCOUNT_ONE_PASSWORD));
        config.put(ACCOUNT_TWO_USERNAME, params.getString(ACCOUNT_TWO_USERNAME));
        config.put(ACCOUNT_TWO_PASSWORD, params.getString(ACCOUNT_TWO_PASSWORD));
        config.put(ACCOUNT_THREE_USERNAME, params.getString(ACCOUNT_THREE_USERNAME));
        config.put(ACCOUNT_THREE_PASSWORD, params.getString(ACCOUNT_THREE_PASSWORD));
        config.put(DISABLED_TESTS, params.getString(DISABLED_TESTS));
        config.put(DISABLED_SPECIFICATIONS, params.getString(DISABLED_SPECIFICATIONS));
        config.put(ENABLED_TESTS, params.getString(ENABLED_TESTS));
        config.put(ENABLED_SPECIFICATIONS, params.getString(ENABLED_SPECIFICATIONS));
        config.put(SINTEXECUTABLE, params.getString(SINTEXECUTABLE));
        config.put(BUILDJDK, params.getString(BUILDJDK));
        return config;
    }

    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        final Set<String> mustBeNonEmpty = Set.of(HOST, DOMAIN, TIMEOUT, SINTEXECUTABLE, BUILDJDK);
        for (final String name : mustBeNonEmpty) {
            final String value = params.getString(name);
            if (StringUtils.isEmpty(value)) {
                errorCollection.addError(name, i18nResolver.getText("xmppinteroptesting.error.empty"));
            }
        }

        final String timeout = params.getString(TIMEOUT);
        if (!StringUtils.isEmpty(timeout)) {
            try {
                final int millis = Integer.parseInt(timeout);
                if (millis <= 0) {
                    errorCollection.addError(TIMEOUT, i18nResolver.getText("xmppinteroptesting.error.negative"));
                }
            } catch (NumberFormatException e) {
                errorCollection.addError(TIMEOUT, i18nResolver.getText("xmppinteroptesting.error.nan"));
            }
        }
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        context.put(HOST, "127.0.0.1");
        context.put(DOMAIN, "example.org");
        context.put(TIMEOUT, 5000);
        context.put(UI_CONFIG_SUPPORT, uiConfigSupport);
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        context.put(HOST, taskDefinition.getConfiguration().get(HOST));
        context.put(DOMAIN, taskDefinition.getConfiguration().get(DOMAIN));
        context.put(TIMEOUT, taskDefinition.getConfiguration().get(TIMEOUT));
        context.put(ADMIN_ACCOUNT_USERNAME, taskDefinition.getConfiguration().get(ADMIN_ACCOUNT_USERNAME));
        context.put(ADMIN_ACCOUNT_PASSWORD, taskDefinition.getConfiguration().get(ADMIN_ACCOUNT_PASSWORD));
        context.put(ACCOUNT_ONE_USERNAME, taskDefinition.getConfiguration().get(ACCOUNT_ONE_USERNAME));
        context.put(ACCOUNT_ONE_PASSWORD, taskDefinition.getConfiguration().get(ACCOUNT_ONE_PASSWORD));
        context.put(ACCOUNT_TWO_USERNAME, taskDefinition.getConfiguration().get(ACCOUNT_TWO_USERNAME));
        context.put(ACCOUNT_TWO_PASSWORD, taskDefinition.getConfiguration().get(ACCOUNT_TWO_PASSWORD));
        context.put(ACCOUNT_THREE_USERNAME, taskDefinition.getConfiguration().get(ACCOUNT_THREE_USERNAME));
        context.put(ACCOUNT_THREE_PASSWORD, taskDefinition.getConfiguration().get(ACCOUNT_THREE_PASSWORD));
        context.put(DISABLED_TESTS, taskDefinition.getConfiguration().get(DISABLED_TESTS));
        context.put(DISABLED_SPECIFICATIONS, taskDefinition.getConfiguration().get(DISABLED_SPECIFICATIONS));
        context.put(ENABLED_TESTS, taskDefinition.getConfiguration().get(ENABLED_TESTS));
        context.put(ENABLED_SPECIFICATIONS, taskDefinition.getConfiguration().get(ENABLED_SPECIFICATIONS));
        context.put(SINTEXECUTABLE, taskDefinition.getConfiguration().get(SINTEXECUTABLE));
        context.put(BUILDJDK, taskDefinition.getConfiguration().get(BUILDJDK));

        context.put(UI_CONFIG_SUPPORT, uiConfigSupport);
    }

    @Override
    public @NotNull Set<Requirement> calculateRequirements(@NotNull TaskDefinition taskDefinition)
    {
        final String jdkLabel = taskDefinition.getConfiguration().get(BUILDJDK);
        final String executableLabel = taskDefinition.getConfiguration().get(SINTEXECUTABLE);
        return Set.of(
            new RequirementImpl("system.builder.sintexecutable." + executableLabel, true, ".*"),
            new RequirementImpl("system.jdk." + jdkLabel, true, ".*")
        );
    }
}
