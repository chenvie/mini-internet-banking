import { Component, OnInit } from '@angular/core';
import { LoginService } from '../login.service';
import { Router } from '@angular/router';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-info-saldo',
  templateUrl: './info-saldo.component.html',
  styleUrls: ['./info-saldo.component.css']
})

export class InfoSaldoComponent implements OnInit {

  rekening = [];
  selectedRekSaldo: string;

  constructor(
    private login: LoginService,
    private route: Router,
    private logger: NGXLogger) { }

  async ngOnInit() {
    if (!this.login.isLoginValid) {
      this.route.navigate(['login']);
    } else {
      // await this.getUserData();
      this.rekening = this.login.userData.rekening;
      this.selectedRekSaldo = this.rekening[0].jml_saldo;
      // this.saldo = this.login.userData.jml_saldo;
    }
  }

  onChangedSelect(val): void {
    this.selectedRekSaldo = val;
  }

  // async getUserData() {
  // this.login.userData = await this.login.getUserData();
  //   for (const key of Object.keys(this.login.userData)) {
  //     const val = this.login.userData[key];
  //     if (val === '') {
  //       const log = 'login: username ' + this.login.userData.username + ' error fetching' + key;
  //       this.logger.error(log);
  //       return false;
  //     }
  //   }
  //   const log = 'login: username ' + this.login.userData.username + ' fetch user data success';
  //   this.logger.info(log);
  // }
}
