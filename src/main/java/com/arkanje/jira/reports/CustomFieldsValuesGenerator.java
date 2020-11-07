package com.arkanje.jira.reports;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.column.ColumnLayoutItem;
import com.atlassian.jira.issue.fields.layout.column.ColumnLayoutStorageException;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.web.component.IssueTableLayoutBean;
import org.apache.commons.collections.map.ListOrderedMap;
import com.atlassian.jira.issue.CustomFieldManager;
import org.ofbiz.core.entity.GenericValue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.config.ConstantsManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.*;

public class CustomFieldsValuesGenerator implements ValuesGenerator<Object>
{
private final JiraAuthenticationContext authenticationContext = ComponentAccessor.getJiraAuthenticationContext();

public Map getValues(Map params)
{
GenericValue projectGV = (GenericValue) params.get("project");

try
{
final ListOrderedMap values = new ListOrderedMap();
Collection<CustomField> customFields = ComponentAccessor.getCustomFieldManager().getCustomFieldObjects();
for (CustomField customField : customFields)
{
values.put(customField.getId(), customField.getName());
}

return values;
}
catch (Exception e)
{
throw new RuntimeException(e);
}
}
}
