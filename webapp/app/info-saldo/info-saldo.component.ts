import {Component, OnInit} from '@angular/core';
import {LoginService} from '../login.service';
import {Router} from '@angular/router';

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
  ) {
  }

  async ngOnInit() {
    if (!this.login.isLoginValid) {
      this.route.navigate(['login']);
    } else {
      await this.login.getUserData();
      this.rekening = this.login.userData.rekening;
      this.selectedRekSaldo = this.rekening[0].jml_saldo;
    }
  }

  onChangedSelect(val): void {
    this.selectedRekSaldo = val;
  }
}
