import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
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
  angForm: FormGroup;

  constructor(
    private login: LoginService,
    private fb: FormBuilder,
    private transfer: TransferService,
    private route: Router,
    private validator: InputValidatorService,
    private logger: NGXLogger
  ) { this.createForm(); }

  createForm() {
    this.angForm = this.fb.group({
      no_rek_tujuan: ['', Validators.required],
      nominal: ['', Validators.required],
      keterangan: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }

    this.page = 1;
    this.dataTrf.id_nasabah = this.login.userData.id_nasabah;
    this.dataTrf.username = this.login.userData.username;
  }

  async validateNorek() {
    if (this.dataTrf.no_rek_tujuan === null || this.dataTrf.no_rek_tujuan === '') {
      const log = 'transfer: username ' + this.login.userData.username + ' rekening value null';
      this.logger.error(log);
      return;
    }
    if (this.dataTrf.nominal === null || this.dataTrf.nominal === '') {
      const log = 'transfer: username ' + this.login.userData.username + ' nominal value null';
      this.logger.error(log);
      return;
    }
    const res = await this.validator.validateNorek(this.dataTrf);
    this.isNorekValid = res.check === 'True' ? true : false;
    if (this.isNorekValid) {
      this.page = 2;
      const log = 'transfer: username ' + this.login.userData.username + ' transfer to ' + this.dataTrf.no_rek_tujuan + ' rekening valid';
      this.logger.info(log);
    } else {
      const log = 'transfer: username ' + this.login.userData.username + ' transfer to ' + this.dataTrf.no_rek_tujuan + ' rekening invalid';
      this.logger.error(log);
    }
  }

  async doTransfer() {
    const res = await this.transfer.doTransfer(this.dataTrf);
    this.isSuccess = res.transfer;
    this.message = res.message;
    this.page = 3;
    if (this.isSuccess) {
      this.status = 'Berhasil';
      const log = 'transfer: username ' + this.login.userData.username + ' transfer to ' + this.dataTrf.no_rek_tujuan + ' success';
      this.logger.info(log);
    } else {
      const log = 'transfer: username ' + this.login.userData.username + ' transfer to ' +
        this.dataTrf.no_rek_tujuan + ' failed. Message: ' + this.message;
      this.logger.error(log);
    }
  }
}
