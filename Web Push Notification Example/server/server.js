const express = require('express');
const webpush = require('web-push');
const bodyParser = require('body-parser');
const cors = require('cors');

const fakeDB = [];
const VAPID_PUBLIC = 'BOjD8D-0nmENTiOWwZrbYhLI9NHncdMTSozRkz0WFbDXHmF9n4cSnivUT5pQFqmypHYruorI2Ez390u-d3qp0Ok';
const VAPID_PRIVATE = '6Eul5-zrBpLczP3urOP89FcgRVJtEaS86TL35GPJUH0';
const app = express();

app.use(cors());
app.use(bodyParser.json());

webpush.setVapidDetails('mailto: asd@asd.com', VAPID_PUBLIC, VAPID_PRIVATE);

app.post('/subscription', (req, res) => {
    const subs = req.body;
    fakeDB.push(subs);
    console.log('new sub');
    console.log(fakeDB.toString());
});

app.post('/sendNotification', (req, res) => {
    const notificationPayload = {
        notification: {
            title: 'Notification Title',
            body: 'some messages',
            silent: true,
            actions: [
                {
                    action: 'action-one',
                    title: 'Action One'
                }
            ],
            data: {
                url: 'some-url'
            }
        }
    };
    Promise.all(fakeDB.map(sub => webpush.sendNotification(sub, JSON.stringify(notificationPayload))))
        .then(() => res.status(200).json({message: 'sent success'}))
        .catch(err => {
            console.error('Error. ', err);
            res.status(500);
        })
});

app.listen(3000, () => {
    console.log('Server started on port 3000');
});