# Spring Boot Project with PostgreSQL DB

This is a simple Spring Boot REST application that connects to a PostgreSQL database.

## Deployment to AWS Elastic Beanstalk with Amazon RDS

This guide explains how to deploy this application to AWS Elastic Beanstalk, using Amazon RDS for the PostgreSQL database.

### Prerequisites

* An AWS account.
* AWS CLI installed and configured.
* Git installed.

### 1. Create an Amazon RDS for PostgreSQL Database

1.  **Navigate to the RDS service** in the AWS Management Console.
2.  Click on **"Create database"**.
3.  Choose **"Standard Create"** and select **"PostgreSQL"**.
4.  Choose a template (e.g., "Free tier" for testing).
5.  **Configure your database:**
    *   **DB instance identifier:** Give your database a unique name.
    *   **Master username:** Set the master username for your database.
    *   **Master password:** Create a secure password.
6.  **Configure connectivity:**
    *   Make sure the database is **publicly accessible** if you want to connect to it from your local machine for testing. **For production, it's recommended to keep it private and access it from within the same VPC.**
    *   In the **"VPC security group (firewall)"** section, either choose an existing security group or create a new one.
7.  **Create the database.** It will take a few minutes for the database to be created.

### 2. Configure the Security Group

1.  Once the database is available, go to its details page and click on the **security group** under the "Connectivity & security" tab.
2.  Select the security group, and in the bottom pane, click on the **"Inbound rules"** tab.
3.  Click **"Edit inbound rules"**.
4.  Click **"Add rule"** and configure it as follows:
    *   **Type:** `PostgreSQL`
    *   **Protocol:** `TCP`
    *   **Port range:** `5432`
    *   **Source:** `Anywhere` (0.0.0.0/0) for testing, or a specific IP address for better security.
5.  **Save the rules.**

### 3. Configure the Spring Boot Application

1.  **Update `application.properties`:**
    Open `src/main/resources/application.properties` and update the datasource properties to connect to your new RDS database.

    ```properties
    spring.datasource.url=jdbc:postgresql://<your-rds-endpoint>:5432/<your-database-name>
    spring.datasource.username=<your-master-username>
    spring.datasource.password=<your-master-password>
    spring.jpa.hibernate.ddl-auto=update
    ```

    Replace `<your-rds-endpoint>`, `<your-database-name>`, and `<your-master-username>`/`<your-master-password>` with the actual values from your RDS instance. You can find the endpoint in the "Connectivity & security" tab of your RDS database.

2.  **Set the server port:**
    Elastic Beanstalk expects the application to run on port 5000. Add the following line to your `application.properties` file:

    ```properties
    server.port=5000
    ```

### 4. Build the Application

Navigate to the `spring-boot-rest` directory and build the application using Maven:

```bash
./mvnw clean package
```

This will create a `.jar` file in the `target` directory.

### 5. Deploy to Elastic Beanstalk

1.  **Create an Elastic Beanstalk application:**
    *   Go to the Elastic Beanstalk service in the AWS console.
    *   Click **"Create Application"**.
    *   Give your application a name.
2.  **Create an environment:**
    *   Choose **"Web server environment"**.
    *   **Platform:** Select `Java`.
    *   **Platform branch:** Choose a recommended version.
    *   **Application code:** Select **"Upload your code"** and upload the `.jar` file you built in the previous step (e.g., `target/spring-boot-rest-0.0.1-SNAPSHOT.jar`).
3.  **Configure more options (optional but recommended):**
    *   You can configure scaling, health checks, and other settings here.
4.  **Create the environment.** Elastic Beanstalk will provision the necessary resources and deploy your application. This might take a few minutes.

### 6. Access Your Application

Once the environment is successfully launched, you will get a URL to access your application. You can find this URL in the environment's dashboard.

---
This `README.md` provides a comprehensive guide for deploying the application to AWS, as requested.
