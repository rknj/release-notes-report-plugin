package org.jahia.modules.jira.reports;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.statistics.StatisticsMapper;
import com.atlassian.jira.issue.statistics.StatsGroup;
import com.atlassian.jira.project.version.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class StatsGroupByComponent extends StatsGroup {

    // Map<Key, Value> => <Version, List of issues>
    private Long selectedProjectId = 0L;

    // TODO Current Map is handled by StatsGroup (Version/Issues list) but we should change it to prevent too much filtering
    // Map<Version, Map<ProjectComponent, Collection<Issue>>> componentsMap;

    public StatsGroupByComponent(StatisticsMapper<?> mapper, Long projectId) {
        super(mapper);
        selectedProjectId = projectId;
    }

    public Collection<ProjectComponent> getComponents() {
        return ComponentAccessor.getProjectComponentManager().findAllForProject(selectedProjectId);
    }

    // TODO with Jira > 7.0 replace getComponentObjects by getComponents
    @SuppressWarnings("unchecked")
    public Collection<Issue> getIssuesByComponent(Version version, ProjectComponent component) throws Exception {
        Collection<Issue> restrictedList = new ArrayList<Issue>();
        Collection<Issue> issues = (Collection<Issue>) this.get(version);
        Iterator<Issue> it = issues.iterator();
        while (it.hasNext()) {
            Issue issue = (Issue) it.next();
            if (issue.getComponents().contains(component)) {
                restrictedList.add(issue);
            }
        }
        return restrictedList;
    }

    // TODO with Jira > 7.0 replace getComponentObjects by getComponents
    @SuppressWarnings("unchecked")
    public Collection<Issue> getIssuesWithoutComponent(Version version) {
        Collection<Issue> restrictedList = new ArrayList<Issue>();
        Collection<Issue> issues = (Collection<Issue>) this.get(version);
        Iterator<Issue> it = issues.iterator();
        while (it.hasNext()) {
            Issue issue = (Issue) it.next();
            if (issue.getComponents().isEmpty()) {
                restrictedList.add(issue);
            }
        }
        return restrictedList;
    }
}
