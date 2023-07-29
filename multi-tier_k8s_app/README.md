# Multi-tier Application K8s Deployment from Dashboard

## Problem Statement

Your organization is looking to create a multi-tier application
based on PHP and MySQL. Your job is to deploy this application
using the Kubernetes dashboard. Create a user (service account)
with the name of Sandry and make sure to assign her an admin
role. WordPress and MySQL Pods should use Node3 as an NFS
storage server using static volumes. The WordPress application
must verify the MySQL service before getting it deployed. If the
MySQL service is not present, then the WordPress Pod should not
be deployed. These all should be restricted to the namespace
called cep project1 and must have 3 SVCs and 3 Pods as a max
quota. All sensitive data should be using secrets and non sensitive
data should be using configmaps

## Tasks
1. Configure and Launch Dashboard and Verify Service
2. Create a ServiceAccount for Sandry with proper RBAC for admin on the dashboard
3. Configure the NFS server for MySQL and WordPress Deployment on Node3
4. Set up the NFS Client side
5. Create and verify the PV
6. Create a secret for MySQL Deployments secret data
7. Create a configmap for WordPress Deployment to store non-sensitive information
8. Create MySQL Deployment and Service
9. Create Wordpress Deployment and Service
10. Set Resource Quotas for Services and Pods

*** All Tasks assume root login and privileges. \
If you do no have root access, use sudo ***

### Task 1: Configure and Launch Dashboard and Verify Service
1. Install dashboard pod
      ```    
      kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
      ```
2. Verify Pod, Service, and Deployment
      ```
      kubectl get pods -n kubernetes-dashboard -o wide
      kubectl get deployment -n kubernetes-dashboard -o wide
      kubectl get svc -n kubernetes-dashboard -o wide
      ```   
3. Editing the Service type of the dashboard for external access to dashboard
      ```
      kubectl edit svc -n kubernetes-dashboard kubernetes-dashboard

      # Dashboard YAML file 
      apiVersion: v1
      kind: Service
      metadata:
        annotations:
          kubectl.kubernetes.io/last-applied-configuration:
      ...
        selector:
          k8s-app: kubernetes-dashboard
        sessionAffinity: None
        type: ClusterIP # <-- Change to NodePort
      status:
        loadBalancer: {}
      ```
4. Verifying the new NodePort Service type of the dashboard
      ```
      kubectl get svc -n kubernetes-dashboard -o wide
      ```
5. Checking where the Pod is running and get node IP and NodePort of service
      ```
      kubectl get pods -n kubernetes-dashboard -o wide
      kubectl get svc -n kubernetes-dashboard -o wide   
      kubectl get nodes -o wide   

      # Example
      Pod is running on worker3
      Worker3 IP = 172.35.27.1
      Dashboard Service NodePort = 30667

      Dashboard Endpoint = http://172.35.27.1:30667
      ```
6. Open any browser and go to your dashboard endpoint address. 

** You may be notified that the link is a security risks. Click advanced and continue.**


### Task 2: Create a ServiceAccount for Sandry with proper RBAC for admin on the dashboard
1. Create a ServiceAccount for Sandry
      ```
      kubectl create sa sandry -n kubernetes-dashboard
      ```
2. Create Cluster Rolebinding for Sandry SA
      ```
      vim sandry-dashboard-crb.yaml

      # Add the following block to the file
    
      apiVersion: rbac.authorization.k8s.io/v1
      kind: ClusterRoleBinding
      metadata:
        name: sandry
      roleRef:
        apiGroup: rbac.authorization.k8s.io
        kind: ClusterRole
        name: cluster-admin
      subjects:
      - kind: ServiceAccount
      name: sandry
      namespace: kubernetes-dashboard
      ```
3. Apply the Cluster Rolebinding
      ```
      kubectl apply -f sandry-dashboard-crb.yaml
      ```
4. Genereate Bearer Token
      ```
      kubectl -n kubernetes-dashboard create token sandry
      ```
5. Now copy the token and paste it into the Enter token field on the login screen
6. Click Sign-in and you have admin access through the Sandry ServiceAccount

### Task 3: Configure the NFS server for MySQL and WordPress Deployment on Node3
1. Create a directory on Node3 to share with the client system.
      ```
      mkdir /node3-nfs
      ```
2. Update the cache and run the following command to install the NFS kernel server on Node3:
      ```
      apt update
      apt install -y nfs-kernel-server
      ```
3. Open the exports file in the /etc directory to set permissions to access the host server machine.
      ```
      vim /etc/exports

      #Add the following line to the file:

      /node3-nfs 	*(rw,sync,no_root_squash)
      ```
4. Use the exportfs command to export all shared folders you registered in /etc/exports file after making the appropriate changes.
      ```
      exportfs -rv
      ```
5. Change the owner user and group to nobody and nogroup. This option makes the folder publicly accessible.
      ```
      chown nobody:nogroup /node3-nfs/
      ```
6. Set permissions to 777 to allow everyone to read, write, and execute files in this directory.
      ```
      chmod 777 /node3-nfs/
      ```
7. Restart the NFS kernel server to apply the configuration changes.
      ```
      systemctl restart nfs-kernel-server
      ```
8. Copy the internal IP of the nfs-server to attach PV to it.
      ```
      ip a
      ```
   
### Task 4: Set up the NFS Client side
1. Install the NFS common package on client machines to enable NFS (In this example, we will install this \
on all remaining nodes in the cluster including the control-plane).
      ```
      apt install nfs-common
      ```

### Task 5: Create and verify the PVs and PVCs from MySQL and Wordpress from the dashboard
1. Click the + button in the top right corner for each pv.
      ```
      # MYSQL
      # Add the following code block to the "Create from input" section and click Upload:

      apiVersion: v1
      kind: PersistentVolume
      metadata:
        name: mysql-nfspv
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        capacity:
          storage: 10Gi
        accessModes:
          - ReadWriteMany
        nfs:
          # IP address of the NFS server
          server: 172.31.6.201
          # Exported path of your NFS server
          path: "/node3-nfs"

      # WORDPRESS
      # Add the following code block to the "Create from input" section and click Upload:

      apiVersion: v1
      kind: PersistentVolume
      metadata:
        name: wordpress-nfspv
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        capacity:
          storage: 10Gi
        accessModes:
          - ReadWriteMany
        nfs:
          # IP address of the NFS server
          server: 172.31.6.201
          # Exported path of your NFS server
          path: "/node3-nfs"
      ```
2. Verify the PVs were created in the Cluster > Persistent Volumes section of the menu.

3. Click the + button in the top right corner for each pvc.
      ```
      # MYSQL
      # Add the following code block to the "Create from input" section and click Upload:
    
      apiVersion: v1
      kind: PersistentVolumeClaim
      metadata:
        name: mysql-nfspvc
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        accessModes:
          - ReadWriteMany
        resources:
          requests:
            storage: 6Gi


      # WORDPRESS
      # Add the following code block to the "Create from input" section and click Upload:
    
      apiVersion: v1
      kind: PersistentVolumeClaim
      metadata:
        name: wordpress-nfspvc
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        accessModes:
          - ReadWriteMany
        resources:
          requests:
            storage: 6Gi
      ```
4. Verify the PVCs were created in the Config and Storage > Persistent Volume Claims section of the menu.


### Task 6: Create a secret for MySQL and Wordpress Deployments secret data

1. Convert the plain text secrests to base64
      ```
      echo "mypass123" | base64  # MYSQL_ROOT_PASSWORD
      echo "sandry" | base64 # MYSQL_USER & WORDPRESS_DB_USER
      echo "123@sandry" | base64 # MYSQL_PASSWORD & WORDPRESS_DB_PASSWORD
      ```
2. Click the + button in the top right corner.
      ```

      # Add the following code block to the "Create from input" section and click Upload:

      apiVersion: v1
      kind: Secret
      metadata:
        name: mysql-sec
        namespace: cep-project1
      data:
        root_pass: bXlwYXNzMTIzCg==
        user: c2FuZHJ5Cg==
        pass: MTIzQHNhbmRyeQo=
      ```
3. Verify the secret was created in the Config and Storage > Secrets section of the menu.

### Task 7: Create a configmap for WordPress Deployment to store non-sensitive information
1. Click the + button in the top right corner.
      ```
      # Add the following code block to the "Create from input" section and click Upload:

      apiVersion: v1
      kind: ConfigMap
      metadata:
        name: mysql-map
        namespace: cep-project1
      data:
        database: wordpress
        host: wordpress-mysql
      ```
3. Verify the secret was created in the Config and Storage > Secrets section of the menu.

### Task 8: Create MySQL Deployment and Service
1. Click the + button in the top right corner.
      ```
      # Add the following code block to the "Create from input" section and click Upload:

      apiVersion: v1
      kind: Service
      metadata:
        name: wordpress-mysql
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        ports:
        - port: 3306
        selector:
          app: wordpress
          tier: mysql
        clusterIP: None
    
      ---
      apiVersion: apps/v1
      kind: Deployment
      metadata:
        name: wordpress-mysql
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        selector:
          matchLabels:
            app: wordpress
            tier: mysql
        strategy:
          type: Recreate
        template:
          metadata:
            labels:
              app: wordpress
              tier: mysql
          spec:
            containers:
            - image: mysql:8.0
              name: mysql
              env:
              - name: MYSQL_ROOT_PASSWORD
                valueFrom:
                  secretKeyRef:
                    name: mysql-sec
                    key: root_pass
              - name: MYSQL_DATABASE
                valueFrom:
                  configMapKeyRef:
                    name: mysql-map
                    key: database
              - name: MYSQL_USER
                valueFrom:
                  secretKeyRef:
                    name: mysql-sec
                    key: user
              - name: MYSQL_PASSWORD
                valueFrom:
                  secretKeyRef:
                    name: mysql-sec
                    key: pass
              ports:
              - containerPort: 3306
                name: mysql
              volumeMounts:
              - name: mysql-nfspv
                mountPath: /var/lib/mysql
            volumes:
            - name: mysql-nfspv
              persistentVolumeClaim:
                claimName: mysql-nfspvc
      ```

### Task 9: Create Wordpress Deployment and Service
1. Click the + button in the top right corner.
      ```

      # Add the following code block to the "Create from input" section and click Upload:

      apiVersion: v1
      kind: Service
      metadata:
        name: wordpress
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        ports:
        - port: 80
        selector:
          app: wordpress
          tier: frontend
        type: LoadBalancer

      ---
      apiVersion: apps/v1
      kind: Deployment
      metadata:
        name: wordpress
        namespace: cep-project1
        labels:
          app: wordpress
      spec:
        selector:
          matchLabels:
            app: wordpress
            tier: frontend
        strategy:
          type: Recreate
        template:
          metadata:
            labels:
              app: wordpress
              tier: frontend
          spec:
            containers:
            - image: wordpress:6.2.1-apache
              name: wordpress
              env:
              - name: WORDPRESS_DB_HOST
                valueFrom:
                  configMapKeyRef:
                    name: mysql-map
                    key: host              
              - name: WORDPRESS_DB_PASSWORD
                valueFrom:
                  secretKeyRef:
                    name: mysql-sec
                    key: pass
              - name: WORDPRESS_DB_USER
                valueFrom:
                  secretKeyRef:
                    name: mysql-sec
                    key: user
              ports:
              - containerPort: 80
                name: wordpress
              volumeMounts:
              - name: wordpress-nfspv
                mountPath: /var/www/html
            volumes:
            - name: wordpress-nfspv
              persistentVolumeClaim:
                claimName: wordpress-nfspvc
      ```  

### Task 10: Set Resource Quotas for Services and Pods
1. Click the + button in the top right corner.
      ```
      # Add the following code block to the "Create from input" section and click Upload:
      
      apiVersion: v1
      kind: ResourceQuota 
      metadata:
        name: wpmysql-quotas
        namespace: cep-project1
      spec:
        hard:
          pods: "3"
          services: "3"
      ```