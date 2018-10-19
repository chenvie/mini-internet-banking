# PWCat

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 6.2.5.

## How to Install
1. Clone / Download Zip of this project
2. Run `npm install` to install all dependencies of project (include pwa, http-server, etc)

## Getting started (If You Create New Project without using this source code)
1. Run `ng new {PROJECT_NAME}` to create new project
1. Run `ng add @angular/pwa --project PWCat` to add Progressive Web Apps Library
2. Run `npm install --save @angular/material @angular/cdk` to install **Angular Material Library** (used for Toolbar and Card View)
4. Run `npm install --save normalize.css` to install **Normalize Theme** for project

## Run Project (Localhost)
1. Navigate to project folder
2. Run `npm install http-server` to install http-server library
3. Run `ng build --prod` to build your project
4. Run `http-server -p 8080 -c-1 dist/{PROJECT_NAME}`

## Additional: Deploy project to Github Pages
1. Navigate to project folder
2. Run `npm i angular-cli-ghpages --save-dev` to install **Github Pages** library used for deploy project to Github Pages 
3. Create repository / use existing repository at Github
4. Commit project to github repository
5. Set github source at Settings tab of your repo, scroll down a little bit and find a `GitHub Pages` section. Make sure that `gh-pages branch` is set as a source.
6. Build your project and publish to github, run `ng build --prod --base-href "https://{GITHUB_REPOSITORY_USERNAME}.github.io/{REPOSITORY_NAME}/"`
    example: `ng build --prod --base-href "https://chenvie.github.io/PWCat"`
7. Run `npx ngh --dir dist/{PROJECT_NAME}` to run your published project at github pages
8. Open your running project at your href `https://{GITHUB_REPOSITORY_USERNAME}.github.io/{REPOSITORY_NAME}/`

## Testing Progressive Web Apps
1. Install Lighthouse Chrome Extension
    visit: https://chrome.google.com/webstore/detail/lighthouse/blipmdconlkpinefehnmjammfjpmpbjk?hl=en
2. Open Chrome Dev Tools (F12)
3. Navigate to Audits Tab
4. Scroll down a little bit and find button **Run Audits**

Note: Your PWA Score after audits must be have 2 errors if you deploy your project at localhost
1. Does not redirect **HTTP** traffics to **HTTPS** --> because you deploy your project at localhost
2. Content is not sized correctly for the viewport --> because this audit is a roundabout way of determining if your page is optimized for mobile devices, you can optimized it by using **Responsive Web Designs**

#References
https://medium.com/@nsmirnova/creating-pwa-with-angular-5-part-2-progressifying-the-application-449e3a706129
https://medium.com/@nsmirnova/creating-pwa-with-angular-5-e36ea2378b5d

