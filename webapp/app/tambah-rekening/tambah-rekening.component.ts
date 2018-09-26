import { Component, OnInit } from '@angular/core';
import {LoginService} from '../login.service';
import {TambahRekServiceService} from '../tambah-rek-service.service';
import {Router} from '@angular/router';
import { InputValidatorService } from '../input-validator.service';

@Component({
  selector: 'app-tambah-rekening',
  templateUrl: './tambah-rekening.component.html',
  styleUrls: ['./tambah-rekening.component.css']
})
export class TambahRekeningComponent implements OnInit {

  dataTbhRek = {
    kode_rahasia: null,
    id_nasabah: null,
  };

  norekBaru: string;
  message: string;
  status: string;
  err_msg: string;

  constructor(
    private login: LoginService,
    private trs: TambahRekServiceService,
    private route: Router,
    private validator: InputValidatorService) { }

  ngOnInit() {
    if (!this.login.isLoginValid) {
      this.route.navigate(['login']);
    }

    this.dataTbhRek.id_nasabah = this.login.userData.id_nasabah;
  }

  async tambahRek() {
    if (this.validator.isNull(this.dataTbhRek.kode_rahasia)) {
      this.err_msg = 'Kode rahasia harus diisi';
      return;
    }
    this.err_msg = '';
    const res = await this.trs.tambahRek(this.dataTbhRek);
    this.status = res.status;
    this.message = res.message;
    if (this.status === 'Berhasil') {
      this.norekBaru = res.no_rek;
    }
  }
}
