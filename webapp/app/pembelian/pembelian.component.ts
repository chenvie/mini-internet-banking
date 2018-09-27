import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {PembelianService} from '../pembelian.service';
import {InputValidatorService} from '../input-validator.service';
import {LoginService} from '../login.service';
import {NGXLogger} from 'ngx-logger';

@Component({
  selector: 'app-pembelian',
  templateUrl: './pembelian.component.html',
  styleUrls: ['./pembelian.component.css']
})
export class PembelianComponent implements OnInit {

  page = 1;
  dataBeli = {
    username: null,
    no_hp_tujuan: null,
    id_nasabah: null,
    provider: null,
    kode_rahasia: null,
    nominal: null
  };
  keterangan: string;
  status: boolean;
  txtStatus: string;
  isFormValid = false;
  isKodeValid = false;
  angForm: FormGroup;

  constructor(
    private beli: PembelianService,
    private validator: InputValidatorService,
    private route: Router,
    private login: LoginService,
    private fb: FormBuilder,
    private logger: NGXLogger
  ) {
    this.cekForm();
  }

  cekForm() {
    this.angForm = this.fb.group({
      no_hp_tujuan: ['', Validators.required],
      provider: ['', Validators.required],
      nominal: ['', Validators.required]
    });
  }

  ngOnInit() {
    if (!this.login.isLoginValid) {
      this.route.navigate(['login']);
    }
    this.dataBeli.username = this.login.userData.username;
    this.dataBeli.id_nasabah = this.login.userData.id_nasabah;

    this.page = 1;
  }

  nextPage(): void {
    if (!this.validator.validatePembelian(this.dataBeli)) {
      this.isFormValid = false;
      return;
    }
    this.isFormValid = true;
    this.page = 2;
  }

  async submitPulsa() {
    const res = await this.beli.buyPulsa(this.dataBeli);
    this.keterangan = res.message;
    this.status = res.pulsa;
    this.txtStatus = this.status ? 'Berhasil' : 'Gagal';
    this.page = 3;
    if (this.status) {
      const log = 'transaction: username ' + this.login.userData.username + ' pulsa transaction success';
      this.logger.info(log);
    } else {
      const log = 'transaction: username ' + this.login.userData.username + ' pulsa transaction failed. Message: ' + this.keterangan;
      this.logger.error(log);
    }
  }

}
