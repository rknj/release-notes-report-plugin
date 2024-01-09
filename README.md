[![CircleCI](https://circleci.com/gh/rknj/release-notes-report-plugin/tree/master.svg?style=svg)](https://circleci.com/gh/rknj/release-notes-report-plugin/tree/master)
![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/rknj/release-notes-report-plugin)
![License](https://img.shields.io/badge/license-MIT-green)

<a href="https://marketplace.atlassian.com/apps/1223903/release-notes-report-plugin?hosting=server&tab=overview"><img src="img/logo.png" /></a>

# Jira Report for Release Notes

This report has been created to build a Release Notes with issues grouped by version and component.
With the last version of the report you will have the following options available:

<img src="src/main/resources/images/report_config.png" alt="Report configuration" width="500"/>

## Introduction

Read this article to know more about this project: https://medium.com/jahia-techblog/building-a-release-note-add-on-for-jira-65e02d9837aa

## Installation

Please note that this report uses a custom field **Changelog Notes** and if you do not have it it will uses the summary field by default.

To install this add-on follow these instructions:
- Get the latest version of the add-on and deploy it on your Jira instance
- In the project report tab you’ll see a new entry **Release Notes By Version And Component** in the **Other** section
<img src="img/report-access.png" alt="Report configuration" width="500"/>

- After a click on this entry, you'll see the configuration panel (see above)

## Usage

### New configuration fields

New fields have been added to make the report more generic. Now you can choose the custom field to use for the changelog and add HTML templates for the textarea content.

<img src="img/new-config.png" alt="Report configuration" width="500"/>

## Tutorial

This module has been created following the Atlassian tutorial: https://developer.atlassian.com/server/jira/platform/creating-a-jira-report/

## Useful links

- Atlassian Developer Documentation: https://developer.atlassian.com/server/framework/atlassian-sdk/
- Report content type: https://developer.atlassian.com/server/jira/platform/report/
- API Javadoc: https://docs.atlassian.com/software/jira/docs/api/
- Simple configuration parameters: https://developer.atlassian.com/server/jira/platform/object-configurable-parameters/
