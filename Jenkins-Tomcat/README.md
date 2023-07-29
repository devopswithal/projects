# Deploying a .war file using Jenkins Maven Job

## Tasks to complete
1. Install java
2. Install Maven
3. Install & configure jenkins
4. Install & configure tomcat 9
5. Create Maven Job
6. Build Job
7. Verify success by accessing the webpage


### Install Java
1. Update cache
      ```
      apt-get update
      ```
2. install java
      ```
      apt-get install default-jdk
      ```
3. Verify install
      ```
      java --version
      ```
### Install Maven
1. Download binary to /tmp
      ```
      wget https://dlcdn.apache.org/maven/maven-3/3.9.3/binaries/apache-maven-3.9.3-bin.tar.gz -P /tmp
      ```
2. Unzip binary into /opt directory
      ```
      tar xf /tmp/apache-maven-*.tar.gz -C /opt
      ```
3. Create link "/opt/maven"
      ```
      ln -s /opt/apache-maven-3.9.3 /opt/maven
      ```
4. Create env variables file and confirm contents
      ```
      cat <<EOF > /etc/profile.d/maven.sh
      export JAVA_HOME=/usr/lib/jvm/default-java
      export M2_HOME=/opt/maven
      export MAVEN_HOME=/opt/maven
      export PATH=/opt/maven/bin:${PATH}
      EOF
      ```
5. Verify env file contents
      ```      
      cat /etc/profile.d/maven.sh
      ```
5. Change file permissions
      ```
      chmod +x /etc/profile.d/maven.sh
      ```
6. Load env file
      ```
      source /etc/profile.d/maven.sh
      ```
5. Verify Install
      ```
      mvn --version
      ```
### Install & configure jenkins
1. Add key to key ring
      ```
      curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | tee \
      /usr/share/keyrings/jenkins-keyring.asc > /dev/null
      ```
2. Add repository 
      ``` 
      echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
      https://pkg.jenkins.io/debian-stable binary/ | tee \
      /etc/apt/sources.list.d/jenkins.list > /dev/null
      ```
3. Update cache and install jenkins
      ```
      apt-get update && apt-get install jenkins -y
      ```
4. Start jenkins
      ```
      systemctl start jenkins
      ```
5. Enable Jenkins
      ```
      systemctl enable jenkins
      ```
6. Verify jenkins is running
      ```
      systemctl status jenkins
      ```
7. Print initial admin password
      ```
      cat /var/lib/jenkins/serets/initialAdminPassword
      ```
8. Visit http://{jenkins-server-ip}:8080 and use initial password to login
9. Select how you would like to install plugins
    - Recommended vs Select Plugins
10. Create admin user 
    - Follow prompts
11. Add additional plugins (Dashboard -> Manage Jenkins -> Plugins -> Available Pluging)
    - deploy-to-container
    - maven integration
12. Configure tools (Dashboard -> Manage Jenkins -> Tools)
    - Maven: name and set MAVEN_HOME path on host
    - JDK: name and set JAVA_HOME path on host
13. Create token for webhooks (Dashboard -> Manage Jenkins -> Security -> Git plugin notifyCommit access tokens)
    - Name token
    - Generate token
14. Create new webhook in Github
    - Go to code repository -> Respository settings -> Webhooks -> New Webhook
    - Enter Payload url, and Jenkins token created above

### Install & configure tomcat 9
1. Download binary
      ```
      wget -q https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.78/bin/apache-tomcat-9.0.78.tar.gz -P /tmp
      ```
2. Unarchive tarball
      ```
      tar xvzf /tmp/apache-tomcat-9.0.78.tar.gz -C /opt
      ```
3. Create link to "/opt/tomcat"
      ```
      ln -s /opt/apache-tomcat-9.0.78 /opt/tomcat
      ```
4. Create startup and shutdown commands 
      ```
      ln -s /opt/tomcat/bin/startup.sh /usr/local/bin/tomcatup
      ln -s /opt/tomcat/bin/shutdown.sh /usr/local/bin/tomcatdown
      ```
5. Change server port from 8080 (conflicts with Jenkins) 
   - Find the <Service name="Catalina"> section, then the <Connector> subsection.
   - Replace the port value with open port (ex. port="8083")
      ```
      nano /opt/tomcat/conf/server.xml
      ```
6. Create user(s) (deployer in jenkins needs access to "manager-scripts" role in tomcat)
   - Find the <Tomcat-users> section. Create new user lines anywhere above the <Tomcat-users/> closing bracket, or 
     modify the template users and roles provided. Jenkins requires a user with "manager-scripts" role.
     - Example roles: \
       \<user username="admin" password="password1" roles="manager-gui"/> \
       \<user username="deployer" password="password2" roles="manager-script"/>
      ```
      nano /opt/tomcat/conf/tomcat-users.xml
      ```
7. Allow remote access from host internal ip and any remote ips (regexp pattern)
   - In both files, find the following section: \
   <Valve className="org.apache.catalina.valves.RemoteAddrValve" \
       allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />
   - Add the internal server ip and any other remote ip to the allow line. Separate addresses with the "|" symbol.
      ```
      nano /opt/tomcat/webapps/manager/META-INF/context.xml
      nano /opt/tomcat/webapps/host-manager/META-INF/context.xml
      ```
8. Start tomcat server
      ```
      tomcatup
      ```
9. Login to Tomcat Manager webapp.
   - Navigate to tomcat url on port 8083 (http://{tomcat-server-ip}:8083)
   - Click Manager to the right
   - Use credentials from the user who has "manager-gui" role privileges.
### Create Job

1. New Item
2. Name and select "Maven Job"
3. Add Github info in SCM Section
   - URL
   - Creditials (if necessary)
4. Add "Github webhooks" as a Build Trigger 
5. Configure Build section
   - Pom file path
   - Goals
   - MAVEN_OPTS
   - Settings
6. Set post-build action to deploy war/jar to container
   - WAR/EAR pattern (**/*.war is the default)
   - Context Path (set in controller or servelet)
   - Select Container (match tomcat version on host)
   - Enter Tomcat Manager credentials (from tomcat-users.xml)
   - Enter Tomcat URL (internal ip)
### Build Job
1. Select the job from dashboard 
2. Click build button
3. At the bottom, open the running job in a new tab (new tab makes it easier to view logs and trigger subsequent builds)
4. Click Console Output button and view logs
### Verify successful deployment
1. Navigate to the tomcat manager in your browser
2. Find the context path for the running app
3. Click the context path and your app should be running.

