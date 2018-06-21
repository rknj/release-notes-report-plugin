package org.jahia.modules.jira.conditions;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.plugin.webfragment.conditions.AbstractWebCondition;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;

public class UserLoggedInIsInCoreGroupCondition extends AbstractWebCondition {

    /**
     * Only display if the user is in the Core group
     */
    @Override
    public boolean shouldDisplay(ApplicationUser user, JiraHelper helper) {
        GroupManager groupManager = ComponentAccessor.getGroupManager();
        return groupManager.isUserInGroup(user, "Core");
    }
}