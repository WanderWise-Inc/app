# Welcome to WanderWise
link to our Figma Project : https://www.figma.com/files/team/1352577939008834686
package com.github.wanderwise_inc.app
![image](https://github.com/WanderWise-Inc/app/assets/56965479/494fe0de-c2c8-4947-be26-5f204a76f564)


## Workflow Guidelines 
### General
In the spirit of not repeating the mistakes that were done throught the first 3 sprints and the during Milestone 1's submissions, this document addresses the workflow issues. Every sprint will be concluded on the thursday, this includes the milestones.


### Github
#### Pull Requests

-  Pull requests will be done on the `dev` branch. 
    > [!IMPORTANT]  
    > Every Thursday, in order to conclude the sprint, we make a PR in the `main`.
-  Before making a PR to the `dev` branch, it is required to :

    ```bash
    git fetch origin # Get the latest info about created branches to your local env.
    git checkout dev # Change your working branch to dev
    git pull origin dev # Update your local dev branch with the latest changes.
    git checkout dev/feature-branch # Change your working branch to your feature branch
    git merge dev
    git add conflicted-file1 # ... all conflcted files
    git commit -m  # followed by your comit message
    git push origin feature-branch 
    ````
#### Branching
- When creating a branch follow this:

    ```bash
    git fetch origin
    git checkout dev
    git pull 
    git branch -b feature/<feature-name> # In the case of a feature
    git branch -b bugfix/<bug-name> #In the case of a bug
    ```

- Stick to a consistent naming convention that categorizes branches by type. Common prefixes include:
    - feature/ for new features.
    - test/ for tests.
    - bugfix/ for bug fixes.
    - hotfix/ for urgent fixes.
    - release/ for release tasks.
    - doc/ for documentation updates.

    **Example** : `feature/add-search-functionality`, `bugfix/login-error`


### Meetings
- **There will be 2 mendatory 30 minutes weekly stand-up meeting** per sprint excluding the friday meeting with the coaches. The exact schedule for these meetings will be decided following the when2meet which sent on the slack and filled every friday for the new sprint.

- Meetings need to be **short**, and efficient **max 45 minutes**.  They will be moderated by the scrumaster whom will ask the following questions to each team member:
    - How is your advancement for your assigned sprint tasks going ?
    - If you need help, how can we help you ?

### Scrum

#### The scrumaster's job 
- By Friday morning :
    - Pick user stories from the product backlog userstories which will be implemented for the sprint. Picking a user story means moving it from the `Product Backlog` column to the `Sprint Backlog` column

    - Break down these user stories into sprint tasks that represent 1-3h of work. **Make sure to correctly fill in the tasks properties**

    - Assign these tasks to team members, every team member should have approximately the same total amount of estimated and should span out the whole project front-end, back-end etc...

- Throughout the sprint
    - Send the when2meet linke to schedule the meetings
    - Moderate stand-up meetings
    - Write stand-up meeting notes which are then sent to the `standup-minutes` channel.
- On Thursday, the sprint end
    - Move all user stories whose sprint tasks are all completed to the top of the `Done in Sprint#X` column, meaning the user can now perform what's written in the story.
    - Fill in the sprint review sheet.

#### Developper's job
- When you start working on a sprint task, move it to the `In Development` column.
-   > [!IMPORTANT]  
    > Once you think you are done before making a PR to `dev`, you should create a new branch say `test/feature-1` in which you will write the unit tests **make sure to have maximum coverage** for the code you have written. Refere to [the branching tutorial](#branching) and add `git merge feature/feature-1`. You can then

- You can now make a PR either from `test/feature-1`, or from `feature/feature-1` having previously merged back the tests. You can now move board's task to the `In Review`. In case of your PR being refused leave it in the column while you are addressing the problems.

- Once your PR is accepted, move it to the `Done "in Sprint #X"`column 

### Communication 
- When you are having an problem, and are looking for someone to help you. Ask a question in the `#general` slack channel. 
    > [!IMPORTANT]  
    > Every thread-starting message in the `#general` channel must be in the form of a question starting with the branch you are working on. Example message : "*Working on feature/feature-1, I am having a hard time using
 the Maps API. So far I have tried X, how can I do Y ?"*
- When answering a message, make sure to answer the message in it's thread.
