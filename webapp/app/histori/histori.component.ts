import { Component, OnInit } from '@angular/core';
import { InputValidatorService } from '../input-validator.service';
import { LoginService } from '../login.service';
import { Router } from '@angular/router';
import { InfoRekService } from '../info-rek.service';
import * as moment from 'moment';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-histori',
  templateUrl: './histori.component.html',
  styleUrls: ['./histori.component.css']
})
export class HistoriComponent implements OnInit {

  page = 1;
  historiData = {
    dariTanggal: null,
    hinggaTanggal: null,
    id: null
  };
  isRangeValid = false;
  trx: {
    kode_transaksi: string,
    tgl_trans: string,
    tujuan: string,
    keterangan: string,
    nominal: string,
    status: string
  }[];

  constructor(
    private validator: InputValidatorService,
    private login: LoginService,
    private route: Router,
    private info: InfoRekService,
    private logger: NGXLogger
  ) { }

  ngOnInit() {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }
    moment.locale('id');

    this.historiData.id = this.login.userData.id_nasabah;
    this.page = 1;
  }

  validateRangeTanggal(): void {
    this.isRangeValid = this.validator.validateRangeTanggal(this.historiData.dariTanggal, this.historiData.hinggaTanggal);
  }

  async getHistori() {
    this.validateRangeTanggal();
    if (!this.isRangeValid) { return; }
    const res = await this.info.getHistori(this.historiData);
    this.historiData.dariTanggal = moment(this.historiData.dariTanggal).format('DD MMMM YYYY');
    this.historiData.hinggaTanggal = moment(this.historiData.hinggaTanggal).format('DD MMMM YYYY');
    this.trx = res.records;
    const l = this.trx.length;
    this.logger.info('fetching', l, 'records from history');
    this.trx.forEach(t => {
      t.tgl_trans = moment(t.tgl_trans).format('DD/MM/YYYY');
    });
    this.page = 2;
  }

}
