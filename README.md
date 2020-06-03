# Jira Report for Release Notes

This report has been created to build a Release Notes with issues grouped by version and component.
It is available from the reports tab on a Jira project.
Please note that this report is based on the custom field "Changelog Notes" but if you do not have it it will display the summary.

With the last version of the report (2.1.0) you have to select a version and the issue types you want to see in the report.

## Commands
- atlas-run: to run a local instance of Jira
- atlas-package: to build the add-on
- atlas-mvn: Maven command to use with Atlassian SDK (ex: for release:prepare and release:perform)

## Tutorial

This module has been created following the Atlassian tutorial: https://developer.atlassian.com/server/jira/platform/creating-a-jira-report/

## Useful links

https://developer.atlassian.com/server/jira/platform/report/

https://docs.atlassian.com/software/jira/docs/api/6.4.12/

https://developer.atlassian.com/server/framework/atlassian-sdk/packaging-and-releasing-your-plugin/

https://developer.atlassian.com/server/jira/platform/object-configurable-parameters/
