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

## Deployment to AWS ECS with Amazon ECR

This section explains how to deploy the application to Amazon Elastic Container Service (ECS) by pushing a Docker image to Amazon Elastic Container Registry (ECR).

### Prerequisites

*   Docker installed.
*   AWS CLI installed and configured.

### 1. Create an ECR Repository

1.  Navigate to the **ECR service** in the AWS Management Console.
2.  Click on **"Create repository"**.
3.  Give your repository a unique name (e.g., `spring-boot-rest-app`).
4.  Click **"Create repository"**.

### 2. Create a Dockerfile

Create a `Dockerfile` in the root of your `spring-boot-rest` project with the following content:

```Dockerfile
FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-jar","/application.jar"]
```

### 3. Build and Push the Docker Image to ECR

1.  **Authenticate Docker to your ECR registry:**
    Run the following command, replacing `<aws_account_id>` and `<region>` with your details.

    ```bash
    aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <aws_account_id>.dkr.ecr.<region>.amazonaws.com
    ```

2.  **Build the Docker image:**
    Navigate to the `spring-boot-rest` directory and run:

    ```bash
    docker build -t spring-boot-rest-app .
    ```

3.  **Tag the Docker image:**
    Tag your image so you can push it to your repository.

    ```bash
    docker tag spring-boot-rest-app:latest <aws_account_id>.dkr.ecr.<region>.amazonaws.com/spring-boot-rest-app:latest
    ```

4.  **Push the Docker image to ECR:**
    ```bash
    docker push <aws_account_id>.dkr.ecr.<region>.amazonaws.com/spring-boot-rest-app:latest
    ```

### 4. Create an ECS Task Definition

1.  Go to the **ECS service** in the AWS console.
2.  Click on **"Task Definitions"** in the left menu and then **"Create new Task Definition"**.
3.  **Configure task definition:**
    *   **Task definition family:** `spring-boot-rest-task`
    *   **Launch type:** `AWS Fargate`
    *   **Operating system:** `Linux/X86_64`
    *   **Task size:** Choose appropriate CPU and memory (e.g., 1 vCPU, 2GB memory).
4.  **Define the container:**
    *   **Name:** `spring-boot-rest-container`
    *   **Image URI:** Paste the URI of the image you pushed to ECR.
    *   **Port mappings:** Add a port mapping for port `8080` (or whatever port your application runs on).
    *   **Environment variables:** Add the datasource URL, username, and password as environment variables. **It is highly recommended to use AWS Secrets Manager for sensitive data.**
        *   `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<your-rds-endpoint>:5432/<your-database-name>`
        *   `SPRING_DATASOURCE_USERNAME`: `<your-master-username>`
        *   `SPRING_DATASOURCE_PASSWORD`: `<your-master-password>`
        *   `SERVER_PORT`: `8080`
5.  Click **"Create"**.

### 5. Create an ECS Cluster

1.  In the ECS console, click on **"Clusters"** and then **"Create Cluster"**.
2.  **Configure cluster:**
    *   **Cluster name:** `my-cluster`
    *   **Networking:** Choose your VPC and subnets.
    *   **Infrastructure:** Select **"AWS Fargate"**.
3.  Click **"Create"**.

### 6. Create an ECS Service

1.  Go to your newly created cluster and click the **"Services"** tab, then **"Create"**.
2.  **Configure service:**
    *   **Launch type:** `Fargate`
    *   **Task Definition:** Select the task definition you created.
    *   **Service name:** `spring-boot-rest-service`
    *   **Desired tasks:** `1`
3.  **Networking:**
    *   Choose your VPC, subnets, and security groups.
    *   Enable **"Public IP"** if you want to access your service from the internet.
4.  **Load balancing (optional but recommended for production):**
    *   You can create an Application Load Balancer to distribute traffic to your tasks.
5.  Click **"Create Service"**.

### 7. Access Your Application

Once the service is running and the task has started, you can find the public IP of the task in the task details and access your application at `http://<public-ip>:8080`.
