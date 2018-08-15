import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  isLoggedIn = false;
  // loginToken: any;

  constructor() { }

  login(): boolean {
    // TODO login from API
    // change return type to customer data
    this.isLoggedIn = true;
    return this.isLoggedIn;
  }

}
