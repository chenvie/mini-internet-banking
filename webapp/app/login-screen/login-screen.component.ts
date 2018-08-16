import { Component, OnInit } from '@angular/core';
import { LoginService } from '../login.service';
import { InputValidatorService } from '../input-validator.service';

@Component({
  selector: 'app-login-screen',
  templateUrl: './login-screen.component.html',
  styleUrls: ['./login-screen.component.css']
})
export class LoginScreenComponent implements OnInit {

  userLogin = {
    username: null,
    password: null,
  };
  loginInfo = {
    isLoggedIn: 'False'
  };
  isLoginCorrect = true;

  constructor(
    private loginService: LoginService,
    private validator: InputValidatorService) { }

  ngOnInit() {
  }

  login(): void {
    if (this.validator.validateLogin(this.userLogin.username, this.userLogin.password)) {
      this.loginService.login(this.userLogin).subscribe((data: any) => this.loginInfo = {
        isLoggedIn: data['login']
      });
    }
    this.isLoginCorrect = this.loginInfo.isLoggedIn === 'True' ? true : false;
  }
}
