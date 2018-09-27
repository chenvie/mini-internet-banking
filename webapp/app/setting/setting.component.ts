import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { InputValidatorService } from '../input-validator.service';
import { SettingService } from '../setting.service';
import { LoginService } from '../login.service';
import { NGXLogger } from 'ngx-logger';

@Component({
  selector: 'app-setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css']
})
export class SettingComponent implements OnInit {

  rekening = [];
  norek: string;
  kodeLama: string;
  passLama: string;
  passBaru: string;
  kodeBaru: string;
  passRetype: string;
  kodeRetype: string;
  isPassBaruValid = false;
  isKodeBaruValid = false;
  isPassSama = false;
  isKodeSama = false;

  constructor(
    private validator: InputValidatorService,
    private setting: SettingService,
    private login: LoginService,
    private route: Router,
    private logger: NGXLogger) { }

  ngOnInit() {
    if (!this.login.isLoginValid) { this.route.navigate(['login']); }

    this.rekening = this.login.userData.rekening;
    this.norek = this.rekening[0].no_rek;
  }

  validatePassword(): boolean {
    this.isPassBaruValid = this.validator.validatePassword(this.passBaru);
    this.isPassSama = (this.passBaru === this.passRetype);
    return this.isPassBaruValid
        && this.isPassSama
        && !InputValidatorService.isNull(this.passLama);
  }

  validateKode(): boolean {
    this.isKodeBaruValid = this.validator.validateKode(this.kodeBaru);
    this.isKodeSama = (this.kodeBaru === this.kodeRetype);
    return this.isKodeBaruValid
        && this.isKodeSama
        && !InputValidatorService.isNull(this.kodeLama);
  }

  async submitKodeBaru() {
    const kodeUser = {
      no_rek: this.norek,
      kode_rahasiaL: this.kodeLama,
      krb1: this.kodeBaru,
      krb2: this.kodeRetype
    };

    if (this.validateKode()) {
      const res = await this.setting.changeKode(kodeUser);
      alert(res.message);
    } else {
      alert('gagal ubah kode rahasia');
      this.isKodeBaruValid = false;
      this.isKodeSama = false;
      const log = 'setting: username ' + this.login.userData.username + ' change kode - form invalid';
      this.logger.error(log);
    }
  }

  async submitPassBaru() {
    const passUser = {
      id_nasabah: this.login.userData.id_nasabah,
      passwordl: this.passLama,
      passwordb1: this.passBaru,
      passwordb2: this.passRetype
    };

    if (this.validatePassword()) {
      const res = await this.setting.changePassword(passUser);
      alert(res.message);
    } else {
      alert('gagal ubah password');
      this.isPassBaruValid = false;
      this.isPassSama = false;
      const log = 'setting: username ' + this.login.userData.username + ' change password - form invalid';
      this.logger.error(log);
    }
  }

  resetForm(): void {
    this.kodeLama = '';
    this.kodeBaru = '';
    this.kodeRetype = '';
    this.passLama = '';
    this.passBaru = '';
    this.passRetype = '';
  }

  onChangedSelect(val): void {
    this.norek = val;
  }
}
