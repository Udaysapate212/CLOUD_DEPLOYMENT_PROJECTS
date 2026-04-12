# Simple Web App Deployment with AWS Elastic Beanstalk

This guide explains how to deploy this simple Spring Boot web application to AWS Elastic Beanstalk.

## Prerequisites

*   An AWS Account
*   [AWS CLI](https://aws.amazon.com/cli/) installed and configured
*   [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) (version 17 or later)
*   [Apache Maven](https://maven.apache.org/download.cgi)

## 1. Build the Application

First, you need to package the application into a `.jar` file.

Navigate to the `simplewebapp` directory and run the following Maven command:

```bash
mvn clean package
```

This will create a `.jar` file in the `target` directory (e.g., `simplewebapp-0.0.1-SNAPSHOT.jar`).

## 2. Deploy to AWS Elastic Beanstalk

1.  **Sign in to the AWS Management Console.**
2.  Navigate to the **Elastic Beanstalk** service.
3.  Click **Create Application**.
4.  Enter an **Application name** (e.g., `simple-web-app`).
5.  Under **Platform**, select `Java`. For the **Platform branch**, choose a version that is compatible with your application (e.g., `Corretto 17`).
6.  For **Application code**, select **Upload your code**.
7.  Click **Upload** and select the `.jar` file you created in the previous step from your `target` directory.
8.  Click **Create application**.

Elastic Beanstalk will now create the environment and deploy your application. Once the process is complete, you can access your application using the URL provided in the Elastic Beanstalk console.
