import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';
import { InputValidatorService } from '../input-validator.service';
import { TransferService } from '../transfer.service';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {

  page: number;
  dataTrf = {
    username: null,
    kode_rahasia: null,
    no_rek_tujuan: null,
    id_nasabah: null,
    nominal: null,
    keterangan: null
  };
  isNorekValid = true;
  isSuccess = false;
  message: string;
  status = 'Gagal';

  constructor(
    private login: LoginService,
    private transfer: TransferService,
    private route: Router,
    private validator: InputValidatorService,
    private logger: NGXLogger
  ) { }

  ngOnInit(): void {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }

    this.page = 1;
    this.dataTrf.id_nasabah = this.login.userData.id_nasabah;
    this.dataTrf.username = this.login.userData.username;
  }

  async validateNorek() {
    const res = await this.validator.validateNorek(this.dataTrf);
    this.isNorekValid = res.check === 'True' ? true : false;
    if (this.isNorekValid) {
      this.page = 2;
      this.logger.info('username', this.login.userData.username, 'transfer to', this.dataTrf.no_rek_tujuan, 'norek valid');
    } else {
      this.logger.warn('username', this.login.userData.username, 'transfer to', this.dataTrf.no_rek_tujuan, 'norek invalid');
    }
  }

  async doTransfer() {
    const res = await this.transfer.doTransfer(this.dataTrf);
    this.isSuccess = res.transfer;
    this.message = res.message;
    this.page = 3;
    if (this.isSuccess) {
      this.status = 'Berhasil';
      this.logger.info('username', this.login.userData.username, 'transfer success');
    } else {
      this.logger.warn('username', this.login.userData.username, 'transfer failed');
    }
  }
}
