package org.jahia.modules.jira.reports;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.column.ColumnLayoutItem;
import com.atlassian.jira.issue.fields.layout.column.ColumnLayoutStorageException;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.component.IssueTableLayoutBean;
import org.apache.commons.collections.map.ListOrderedMap;
import org.ofbiz.core.entity.GenericValue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.issuetype.IssueType;
 
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.*;

public class IssueTypesValuesGenerator implements ValuesGenerator<Object>
{
    private final JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
 
    public Map getValues(Map params)
    {
        GenericValue projectGV = (GenericValue) params.get("project");
        
        try
        {
            final ListOrderedMap values = new ListOrderedMap();
            Collection<IssueType> issueTypes = ComponentAccessor.getIssueTypeSchemeManager().getIssueTypesForProject(projectGV);
            for (IssueType issueType : issueTypes)
            {
                values.put(issueType.getId(), issueType.getName());
            }
 
            return values;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}