package org.jahia.modules.jira.reports;

import com.atlassian.configurable.ValuesGenerator;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.bean.I18nBean;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericValue;

import java.util.*;

public class VersionOptionalValuesGenerator implements ValuesGenerator<Object>
{
    private static final Logger log = Logger.getLogger(VersionOptionalValuesGenerator.class);

    @Override
    public Map getValues(Map params)
    {
        GenericValue projectGV = (GenericValue) params.get("project");
        ApplicationUser remoteUser = (ApplicationUser) params.get("User");

        try
        {
            VersionManager versionManager = ComponentAccessor.getVersionManager();
            I18nHelper i18n = new I18nBean(remoteUser);

            Collection<Version> unreleasedVersionsCollection = versionManager.getVersionsUnreleased(projectGV.getLong("id"), false);
            List<Version> unreleasedVersions = new ArrayList<Version>( unreleasedVersionsCollection );
            Collections.sort(unreleasedVersions, new Comparator<Version>( ){
                @Override
                public int compare(Version o1, Version o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            Map<Long, String> unreleased = ListOrderedMap.decorate(new HashMap(unreleasedVersions.size()));
            Iterator<Version> unreleasedIter = unreleasedVersions.iterator();
            if (unreleasedIter.hasNext())
            {
                unreleased.put(new Long(-2), i18n.getText("common.filters.unreleasedversions"));
                while (unreleasedIter.hasNext())
                {
                    Version version = unreleasedIter.next();
                    unreleased.put(version.getId(), "- " + version.getName());
                }
            }



            Collection<Version> releasedVersionsCollection = versionManager.getVersionsReleased(projectGV.getLong("id"), false);
            List<Version> releasedVersions = new ArrayList<Version>( releasedVersionsCollection );
            Collections.sort(releasedVersions, new Comparator<Version>( ){
                @Override
                public int compare(Version o1, Version o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

            Map<Long, String> released = ListOrderedMap.decorate(new HashMap(releasedVersions.size()));
            Iterator<Version> releasedIter = releasedVersions.iterator();
            if (releasedIter.hasNext())
            {
                released.put(new Long(-3), i18n.getText("common.filters.releasedversions"));
                while (releasedIter.hasNext())
                {
                    Version version = releasedIter.next();
                    released.put(version.getId(), "- " + version.getName());
                }
            }

            int size = unreleased.size() + released.size() + 1;
            Map<Long, String> versions = ListOrderedMap.decorate(new HashMap(size));

            //versions.put(new Long(-1), i18n.getText("timetracking.nofixversion"));
            versions.putAll(unreleased);
            versions.putAll(released);
            return versions;
        }
        catch (Exception e)
        {
            log.error("Could not retrieve versions for the project: "+   ((projectGV != null) ? projectGV.getString("id"): "Project is null."), e);
            return null;
        }
    }
}