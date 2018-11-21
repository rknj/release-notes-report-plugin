package org.jahia.modules.jira.reports;

import com.atlassian.jira.bc.filter.SearchRequestService;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.datetime.DateTimeFormatter;
import com.atlassian.jira.datetime.DateTimeFormatterFactory;
import com.atlassian.jira.datetime.DateTimeStyle;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.index.IssueIndexManager;
import com.atlassian.jira.issue.search.ReaderCache;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchRequest;
import com.atlassian.jira.issue.statistics.FixForVersionStatisticsMapper;
import com.atlassian.jira.issue.statistics.StatisticsMapper;
import com.atlassian.jira.issue.statistics.util.OneDimensionalDocIssueHitCollector;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.plugin.report.impl.AbstractReport;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.FieldVisibilityManager;
import com.atlassian.jira.web.action.ProjectActionSupport;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.query.Query;
import com.atlassian.query.order.SortOrder;
import com.atlassian.util.profiling.UtilTimerStack;
import com.opensymphony.util.TextUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.search.Collector;

import java.util.HashMap;
import java.util.Map;

@Scanned
public class ProjectReportByVersionAndComponent extends AbstractReport {
    private static final Logger log = Logger.getLogger(ProjectReportByVersionAndComponent.class);

    @JiraImport
    private final SearchProvider searchProvider;
    @JiraImport
    private final SearchRequestService searchRequestService;
    @JiraImport
    private final IssueFactory issueFactory;
    @JiraImport
    private final CustomFieldManager customFieldManager;
    @JiraImport
    private final IssueIndexManager issueIndexManager;
    @JiraImport
    private final SearchService searchService;
    @JiraImport
    private final FieldVisibilityManager fieldVisibilityManager;
    @JiraImport
    private final FieldManager fieldManager;
    @JiraImport
    private final ProjectManager projectManager;
    @JiraImport
    private final VersionManager versionManager;
    @JiraImport
    private final ReaderCache readerCache;
    @JiraImport
    private final DateTimeFormatter formatter;

    public ProjectReportByVersionAndComponent(final SearchProvider searchProvider, final SearchRequestService searchRequestService,
            final IssueFactory issueFactory, final CustomFieldManager customFieldManager, final IssueIndexManager issueIndexManager,
            final SearchService searchService, final FieldVisibilityManager fieldVisibilityManager, final ReaderCache readerCache,
            final FieldManager fieldManager, final ProjectManager projectManager, final VersionManager versionManager,
            @JiraImport final DateTimeFormatterFactory dateTimeFormatterFactory) {
        this.searchProvider = searchProvider;
        this.searchRequestService = searchRequestService;
        this.issueFactory = issueFactory;
        this.customFieldManager = customFieldManager;
        this.issueIndexManager = issueIndexManager;
        this.searchService = searchService;
        this.fieldVisibilityManager = fieldVisibilityManager;
        this.readerCache = readerCache;
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.versionManager = versionManager;
        this.formatter = dateTimeFormatterFactory.formatter().withStyle(DateTimeStyle.DATE).forLoggedInUser();
    }

    /**
     * Search for the issues
     * 
     * @param request
     * @param searcher
     * @param mapper
     * @param projectId
     * @return
     * @throws SearchException
     */
    public StatsGroupByComponent searchMapIssueKeys(SearchRequest request, ApplicationUser searcher, StatisticsMapper mapper,
            Long projectId) throws SearchException {
        try {
            UtilTimerStack.push("Search Count Map");
            StatsGroupByComponent statsGroup = new StatsGroupByComponent(mapper, projectId);
            Collector hitCollector = new OneDimensionalDocIssueHitCollector(mapper.getDocumentConstant(), statsGroup,
                    issueIndexManager.getIssueSearcher().getIndexReader(), issueFactory, fieldVisibilityManager, readerCache, fieldManager,
                    projectManager);
            searchProvider.searchAndSort((request != null) ? request.getQuery() : null, searcher, hitCollector,
                    PagerFilter.getUnlimitedFilter());
            return statsGroup;
        } finally {
            UtilTimerStack.pop("Search Count Map");
        }
    }

    /**
     * Generate the report content
     * 
     * @param action
     * @param params
     * @return the HTML content
     * @throws Exception
     */
    public String generateReportHtml(ProjectActionSupport action, Map params) throws Exception {
        String versionIdString = (String) params.get("versionId");
        Version version = null;
        String releaseStatus = "Unreleased";
        if (versionIdString == null || versionIdString.equals(VersionManager.ALL_RELEASED_VERSIONS)
                || versionIdString.equals(VersionManager.ALL_UNRELEASED_VERSIONS)) {
            return "<span class='errMsg'>No version has been selected. Please " + "<a href=\"IssueNavigator.jspa?reset=Update&pid="
                    + TextUtils.htmlEncode((String) params.get("selectedProjectId")) + "\">create one</a>, and re-run this report.";
        } else if (!versionIdString.equals(VersionManager.NO_VERSIONS)) {
            final Long versionId = new Long(versionIdString);
            version = versionManager.getVersion(versionId);
            
            if (version.isReleased()) {
                releaseStatus = "Released";
            }
        }

        Query query = buildQuery(action.getSelectedProjectId(), new Long(versionIdString));
        SearchRequest req = new SearchRequest(query);

        final Map<String, Object> startingParams = new HashMap<String, Object>();
        startingParams.put("action", action);
        startingParams.put("statsGroup", searchMapIssueKeys(req, action.getLoggedInUser(),
                new FixForVersionStatisticsMapper(versionManager), action.getSelectedProjectId()));
        startingParams.put("searchRequest", req);
        startingParams.put("query", query);
        startingParams.put("projectName", action.getSelectedProject().getName());
        startingParams.put("projectId", action.getSelectedProject().getId());
        startingParams.put("version", version);
        startingParams.put("releaseStatus", releaseStatus);
        startingParams.put("versionIdString", versionIdString);
        startingParams.put("customFieldManager", customFieldManager);
        startingParams.put("fieldVisibility", fieldVisibilityManager);
        startingParams.put("searchService", searchService);
        startingParams.put("portlet", this);
        startingParams.put("formatter", formatter);
        startingParams.put("versionManager", versionManager);

        return descriptor.getHtml("view", startingParams);
    }

    /**
     * Create the JQL query
     * 
     * @param projectId
     * @param versionId
     * @return
     */
    private Query buildQuery(final Long projectId, final Long versionId) {
        final JqlQueryBuilder queryBuilder = JqlQueryBuilder.newBuilder();
        final JqlClauseBuilder builder = queryBuilder.where().project(projectId);

        if (versionId != null) {
            if (VersionManager.NO_VERSIONS.equals(versionId.toString())) {
                builder.and().fixVersionIsEmpty();
            } else {
                builder.and().fixVersion().eq(versionId);
            }
        }

        queryBuilder.orderBy().issueType(SortOrder.DESC, true).issueKey(SortOrder.ASC);

        return queryBuilder.buildQuery();
    }

}