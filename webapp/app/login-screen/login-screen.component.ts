import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';
import { InputValidatorService } from '../input-validator.service';
import { NGXLogger } from 'ngx-logger';

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
  isLoginApproved = false;

  constructor(
    private loginService: LoginService,
    private validator: InputValidatorService,
    private route: Router,
    private logger: NGXLogger) { }

  ngOnInit() {
    if (this.loginService.isLoginValid) {this.route.navigate(['/main']); }
  }

  async login() {
    if (this.validator.validateLogin(this.userLogin.username, this.userLogin.password)) { // null validation
      this.isLoginApproved = await this.loginService.login(this.userLogin);
      if (this.isLoginApproved) {
        this.route.navigate(['main']);
        this.logger.info('login: username', this.loginService.userData.username, 'has logged in.');
      } else { this.logger.warn('login: username', this.userLogin.username, 'failed to login.'); }
    }
  }
}
