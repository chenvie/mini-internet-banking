# How to Install
---
1. ### Create client
    1. Create angular project `<your_angular>`
    2. Replace angular default **src** with this **client/src**
2. ### Create server
    1. Create a folder`<your_server>`
    2. Go to folder `cd <your_server>`
    3. Initialize NPM `npm init`
    4. Install dependencies `npm i body-parser cors express web-push`
    5. Copy content of **server/** to `<your_server>`
3. ### Starting Up
    1. Install http-server `npm i http-server`
    2. Go to `<your_angular>`
    3. Build project `ng build --prod`
    4. Serve your angular using http-server `http-server dist/<your_angular>/`
    5. Go to `<your_server>`
    6. Start the node server `node server.js`
4. ### Testing the Notification
    1. Open `http://localhost:8080/`
    2. Click subscribe
    3. Click allow
    4. Send POST request to `http://localhost:3000/sendNotification`
5. ### Additional: Create VAPID Key-pair
    1. `npm i web-push`
    2. `web-push generate-vapid-keys --json`
    3. note down the private and public key