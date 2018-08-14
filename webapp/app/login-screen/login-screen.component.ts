import { Component, OnInit } from '@angular/core';
import { LoginService } from '../login.service';

@Component({
  selector: 'app-login-screen',
  templateUrl: './login-screen.component.html',
  styleUrls: ['./login-screen.component.css']
})
export class LoginScreenComponent implements OnInit {

  userLogin = {
    username: null,
    password: null
  };
  isLoggedIn = false;

  constructor(private loginService: LoginService) { }

  ngOnInit() {
  }

  login(): void {
    this.loginService.login(this.userLogin);
  }

}
