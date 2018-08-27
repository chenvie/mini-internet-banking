import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';
import { InfoRekService } from '../info-rek.service';
import * as moment from 'moment';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-mutasi-rek',
  templateUrl: './mutasi-rek.component.html',
  styleUrls: ['./mutasi-rek.component.css']
})
export class MutasiRekComponent implements OnInit {

  curDate: any;
  fromDate: any;
  norek: string;
  trx: {
    kode_transaksi: string,
    no_rek: string,
    tgl_trans: string,
    tujuan: string,
    jenis: string,
    keterangan: string,
    nominal: string
  }[];

  constructor(
    private info: InfoRekService,
    private login: LoginService,
    private route: Router,
    private logger: NGXLogger
  ) { }

  ngOnInit() {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }

    moment.locale('id');
    this.curDate = moment().format('LL');
    this.fromDate = moment().subtract(7, 'd').format('LL');
    this.norek = this.login.userData.no_rek;
    this.getMutasi();
  }

  async getMutasi() {
    const res = await this.info.getMutasi();
    this.trx = res.records;
    const l = this.trx.length;
    this.logger.info('fetching', l, 'records from mutation');
    this.trx.forEach(t => {
      t.tgl_trans = moment(t.tgl_trans).format('DD/MM/YYYY');
    });
  }

}
