import { Component, OnInit } from '@angular/core';
import { InputValidatorService } from '../input-validator.service';
import { LoginService } from '../login.service';
import { Router } from '@angular/router';
import { InfoRekService } from '../info-rek.service';
import * as moment from 'moment';

@Component({
  selector: 'app-histori',
  templateUrl: './histori.component.html',
  styleUrls: ['./histori.component.css']
})
export class HistoriComponent implements OnInit {

  page = 1;
  historiData = {
    tgl_awal: null,
    tgl_akhir: null,
    no_rek: null
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
  rekening = [];

  constructor(
    private validator: InputValidatorService,
    private login: LoginService,
    private route: Router,
    private info: InfoRekService,
  ) { }

  ngOnInit() {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }
    moment.locale('id');

    this.historiData.no_rek = this.login.userData.id_nasabah;
    this.rekening = this.login.userData.rekening;
    this.page = 1;
  }

  validateRangeTanggal(): void {
    this.isRangeValid = this.validator.validateRangeTanggal(this.historiData.tgl_awal, this.historiData.tgl_akhir);
  }

  async getHistori() {
    this.validateRangeTanggal();
    if (!this.isRangeValid) { return; }
    const res = await this.info.getHistori(this.historiData);
    this.historiData.tgl_awal = moment(this.historiData.tgl_awal).format('DD MMMM YYYY');
    this.historiData.tgl_akhir = moment(this.historiData.tgl_akhir).format('DD MMMM YYYY');
    this.trx = res.result;
    try {
      this.trx.forEach(t => {
        t.tgl_trans = moment(t.tgl_trans).format('DD/MM/YYYY');
      });
    } catch (error) {}
    this.page = 2;
  }

  onChangedSelect(val): void {
    this.historiData.no_rek = val;
  }
}
