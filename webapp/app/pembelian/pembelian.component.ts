import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PembelianService } from '../pembelian.service';
import { InputValidatorService } from '../input-validator.service';
import { LoginService } from '../login.service';

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

  constructor(
    private beli: PembelianService,
    private validator: InputValidatorService,
    private route: Router,
    private login: LoginService) { }

  ngOnInit() {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }
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
    // if (!this.validator.validateKode(this.dataBeli.kode_rahasia)) {
    //   this.isKodeValid = false;
    //   return;
    // }
    const res = await this.beli.buyPulsa(this.dataBeli);
    this.keterangan = res.message;
    this.status = res.pulsa;
    this.txtStatus = this.status ? 'Berhasil' : 'Gagal';
    this.page = 3;
  }

}
