import { Component, OnInit } from '@angular/core';
import { LoginService } from '../login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-info-saldo',
  templateUrl: './info-saldo.component.html',
  styleUrls: ['./info-saldo.component.css']
})

export class InfoSaldoComponent implements OnInit {

  norek: string;
  saldo: string;

  constructor(
    private login: LoginService,
    private route: Router) { }

  ngOnInit() {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }

    this.norek = this.login.userData.no_rek;
    this.saldo = this.login.userData.jml_saldo;
  }
}
