package org.jahia.modules.jira.reports;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.statistics.StatisticsMapper;
import com.atlassian.jira.issue.statistics.StatsGroup;
import com.atlassian.jira.project.version.Version;

import java.util.*;

public class StatsGroupByComponent extends StatsGroup {

    private Long selectedProjectId = 0L;
    private Map<Version, Map<ProjectComponent, Collection<Issue>>> componentsMap;
    private Map<Version, Collection<Issue>> issuesWithoutComponents;

    public StatsGroupByComponent(StatisticsMapper<?> mapper, Long projectId) {
        super(mapper);
        selectedProjectId = projectId;
    }

    public void buildComponentMap(Version version) {
        componentsMap = new HashMap<Version, Map<ProjectComponent, Collection<Issue>>>();
        issuesWithoutComponents = new HashMap<Version, Collection<Issue>>();
        issuesWithoutComponents.put(version, new ArrayList<Issue>());

        Map<ProjectComponent, Collection<Issue>> map = new HashMap<ProjectComponent, Collection<Issue>>();
        Iterator<Issue> it = ((Collection<Issue>) this.get(version)).iterator();
        while (it.hasNext()) {
            Issue issue = it.next();

            if (issue.getComponents().size() > 0) {
                // Only map the first component with the issue
                Iterator<ProjectComponent> componentIterator = issue.getComponents().iterator();
                ProjectComponent component = componentIterator.next();
                if (map.containsKey(component)) {
                    map.get(component).add(issue);
                } else {
                    map.put(component, new ArrayList<Issue>(){{add(issue);}});
                }
            } else {
                issuesWithoutComponents.get(version).add(issue);
            }
        }
        componentsMap.put(version, map);
    }

    public Collection<ProjectComponent> getComponents(Version version) {
        return componentsMap.get(version).keySet();
    }

    public Collection<Issue> getIssuesByComponent(Version version, ProjectComponent component) throws Exception {
        return componentsMap.get(version).get(component);
    }

    public Collection<Issue> getIssuesWithoutComponent(Version version) {
        return issuesWithoutComponents.get(version);
    }

    public Version getVersionOfComponent(ProjectComponent component){
        // Get all the versions remaining except the selected one
        Collection<Version> versions = ComponentAccessor.getVersionManager().getVersionsUnreleased(selectedProjectId, false);

        // Get a version matching the component name
        for (Version version:versions) {
            if (version.getName().toLowerCase().contains(component.getName().toLowerCase())) {
                return version;
            }
        }

        return null;
    }
}
