package org.jahia.modules.jira.reports;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.component.pico.ComponentManager;
import org.apache.commons.collections.map.ListOrderedMap;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.customfields.statistics.CustomFieldStattable;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.statistics.FixForVersionStatisticsMapper;
import com.atlassian.jira.issue.statistics.StatisticsMapper;

public class FilterStatisticsValuesGenerator implements ValuesGenerator {

    protected static final Map<String, String> systemValues;

    public static final String ALLFIXFOR = "allFixfor";
    public static final String FIXFOR = "fixfor";

    CustomFieldManager customFieldManager = ComponentManager.getInstance().getComponentInstanceOfType(CustomFieldManager.class);

    static {
        Map<String, String> systemValuesTmp = new ListOrderedMap();
        systemValuesTmp.put(ALLFIXFOR, "gadget.filterstats.field.statistictype.allfixfor");
        systemValuesTmp.put(FIXFOR, "gadget.filterstats.field.statistictype.fixfor");
        systemValues = Collections.unmodifiableMap(systemValuesTmp);
    }

    @Override
    public Map getValues(Map arg0) {
        Map allValues = new ListOrderedMap();

        allValues.putAll(systemValues);
        final List customFieldObjects = customFieldManager.getCustomFieldObjects();

        for (Iterator iterator = customFieldObjects.iterator(); iterator.hasNext();) {
            CustomField customField = (CustomField) iterator.next();
            if (customField.getCustomFieldSearcher() instanceof CustomFieldStattable) {
                allValues.put(customField.getId(), customField.getName());
            }
        }
        return allValues;
    }

    public StatisticsMapper getStatsMapper(String statsMapperKey) {
        StatisticsMapper systemMapper = getSystemMapper(statsMapperKey);
        if (systemMapper != null)
            return systemMapper;

        CustomField customField = customFieldManager.getCustomFieldObject(statsMapperKey);
        if (customField == null)
            throw new RuntimeException("No custom field with id " + statsMapperKey);
        if (customField.getCustomFieldSearcher() instanceof CustomFieldStattable) {
            final CustomFieldStattable customFieldStattable = (CustomFieldStattable) customField.getCustomFieldSearcher();
            return customFieldStattable.getStatisticsMapper(customField);
        } else {
            return null;
        }
    }

    private StatisticsMapper getSystemMapper(String statsMapperKey) {
        if (ALLFIXFOR.equals(statsMapperKey))
            return new FixForVersionStatisticsMapper(ComponentAccessor.getVersionManager(), true);
        else if (FIXFOR.equals(statsMapperKey))
            return new FixForVersionStatisticsMapper(ComponentAccessor.getVersionManager(), false);

        return null;
    }
}
