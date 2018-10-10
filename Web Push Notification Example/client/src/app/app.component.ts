import { Component } from '@angular/core';
import {SwPush} from '@angular/service-worker';
import {PushServiceService} from './push-service.service';

@Component({
  selector: 'app-root',
  // templateUrl: './app.component.html',
  template: '<button class="button button-primary" (click)="subscribeToNotifications()">Subscribe</button>',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angular-push-notification';

  readonly VAPID_PUBLIC = 'BOjD8D-0nmENTiOWwZrbYhLI9NHncdMTSozRkz0WFbDXHmF9n4cSnivUT5pQFqmypHYruorI2Ez390u-d3qp0Ok';

  constructor(
    private swPush: SwPush,
    private pushService: PushServiceService,
  ) {}

  subscribeToNotifications() {
    this.swPush.requestSubscription({
      serverPublicKey: this.VAPID_PUBLIC,
    })
      .then(sub => this.pushService.addSubscriptionToDB(sub).subscribe())
      .catch(err => console.error('Could not subscribe to notifications', err));
    // console.log(this.fakeDB);
  }
}
