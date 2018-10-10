import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PushServiceService {

  fakeDB = [];

  constructor(
    private http: HttpClient,
  ) { }

  addSubscriptionToDB(subs: PushSubscription) {
    const url = 'http://localhost:3000/subscription'
    return this.http.post(url, subs);
  }
}
