import { Component, OnInit } from '@angular/core';
import { LoginService } from '../login.service';

@Component({
  selector: 'app-login-screen',
  templateUrl: './login-screen.component.html',
  styleUrls: ['./login-screen.component.css']
})
export class LoginScreenComponent implements OnInit {

  isLoggedIn = false;

  constructor(private loginService: LoginService) { }

  ngOnInit() {
  }

  login(): void {
    this.isLoggedIn = this.loginService.login();
  }

}
