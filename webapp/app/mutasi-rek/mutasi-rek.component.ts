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
  trx: {
    kode_transaksi: string,
    no_rek: string,
    tgl_trans: string,
    tujuan: string,
    jenis: string,
    keterangan: string,
    nominal: string
  }[];
  rekening = [];
  norek: string;

  constructor(
    private info: InfoRekService,
    private login: LoginService,
    private route: Router,
    private logger: NGXLogger
  ) { }

  ngOnInit() {
    if (!this.login.isLoginValid) {
      this.route.navigate(['login']);
    } else {
      moment.locale('id');
      this.curDate = moment().format('LL');
      this.fromDate = moment().subtract(7, 'd').format('LL');
      this.rekening = this.login.userData.rekening;
      this.norek = this.rekening[0].no_rek;
      this.getMutasi(this.norek);
    }
  }

  async getMutasi(norek) {
    const res = await this.info.getMutasi(norek);
    try {
      this.trx = res.result;
      this.trx.forEach(t => {
        t.tgl_trans = moment(t.tgl_trans).format('DD/MM/YYYY');
      });
    } catch (error) {}
  }

  onChangedSelect(norek): void {
    this.getMutasi(norek);
  }

}
