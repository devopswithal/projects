# Branching Model Development

## DESCRIPTION
Create a branching model to help your team understand the Git Feature Branch Workflow for faster and efficient integration of work

## Background of the problem statement:
M-theta Technology Solutions hired you as a DevOps Architect. It is undergoing an infrastructural change to implement DevOps to develop and deliver the products. Since M-theta is an Agile organization, they follow the Scrum methodology to develop the projects incrementally. Hence, the company wants to adopt Git as a Source Code Management (SCM) tool for faster integration of work and smooth transition into DevOps.
So, as a DevOps Architect, you have been asked to build a branching model to demonstrate the Git Feature Branch Workflow for the company’s engineering team. In the branching model, you are required to create a Production branch which will act as the main (master) branch, an Integration branch which will again have two branches inside it namely Feature 1 and Feature 2, and a Hotfix branch which will be used for fixing any issues that could come up from Integration or Production branches.

### You must use the following:
- Git: To build the branching model

### Tasks:
1. Start with the Production branch (master branch), and then create a HotFix  and Integration branch
    Initialize the repository
    ```
    git init 
    ```
    - Rename master to Production
    ```
    git branch -M master Production 
    ```
    - Add README file
    ```
    vim README.md # add branch description
    git add README.md \
    git commit README.md -m “First commit: README.md”
    ```
    - Create Hotfix and Integrations branches
    ```
    git branch Hotfix \
    git branch Integrations \
    git branch --list \
    ```
    Switch to Integration branch 
    ```
    git checkout Integration \
    ```
    Add src files to git directory the continue with the following:
    ```
    git add src \
    git commit src -m “Added original app src” \
    ```
2. Subsequently, create Feature 1 and 2 branches that integrate to the Integration branch as shown in the above figure.
   -  Create Feature- and Feature-2 branches
```
git branch Feature-1 \
git branch Feature-2 \
git branch --list \
```
3. Commit some changes in the Feature 2 branch and merge it into the Integration branch. Delete this branch once. 
merging is complete
  -  Switch to Feature-2 branch
```
git checkout Feature-2 
```
  -  Add Songs feature files to src then continue with the following:
```
git add <files> \
git commit <files> -m “Added Songs Controller, Model, & Repository”
```
  -  Switch to Integration branch
```
git checkout Integration 
```
  -  Merge Feature-2 into Integration
```
git merge Feature-2
```
  -  Delete Feature-2 branch
```
git branch -D Feature-2
```
4. Commit some changes in the Feature 1 branch and rebase it to the Integration branch.
  -  Switch to Feature-1 branch 
```
git checkout Feature-1
```
  -  Add Movies feature files to src then continue with the following:
```
git add <files> \
git commit src <files> -m “Added Movies Controller, Model, & Repository”
```
  -  Rebase Feature-1 branch to the end of Integration
```
git rebase Integration
```
5. Merge the Integration branch into Hotfix and Production branch to update these branches.
  -  Merge into Hotfix 
```
git checkout Hotfix \
git merge Integration -m “Updating Hotfix with Features-1,2” \
```
  -  Merge into Production
```
git checkout Production \
git merge Integration -m “Updating Production with Features-1,2”
```
6. Commit some changes in Feature 1 branch, and then merge it into Integration, Hotfix, and Production branch. Delete 
this branch once merging is complete.
  -  Commit changes to src
```
git add <files> \
git commit <files> -m “Updated Feature-1” \
```
  - Merge into Integration
git checkout Integration \
git merge Feature-1 \

  -  Merge into Hotfix
git checkout Hotfix \
git merge Integration \

  -  Merge into Production
git checkout Production \
git merge Integration \

  -  Delete Feature-1 Branch
git branch -D Feature-1 \

7. Commit some changes in the Hotfix branch and merge it into the Production as well as the Integration branch


  - Make changes to application.properties file
git checkout Hotfix \

  -  Commit those changes
git add <files> \
git commit <files> -m “Fixed connection bug” \

  -  Merge into Integration
git checkout Integration \

  -  Merge into
git checkout Production \
git merge Hotfix \
