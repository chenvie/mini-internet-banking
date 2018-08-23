import { Component, OnInit } from '@angular/core';
import { InputValidatorService } from '../input-validator.service';
import { SettingService } from '../setting.service';
import { LoginService } from '../login.service';

@Component({
  selector: 'app-setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css']
})
export class SettingComponent implements OnInit {

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
    private login: LoginService) { }

  ngOnInit() {
  }

  validatePassword(): boolean {
    this.isPassBaruValid = this.validator.validatePassword(this.passBaru);
    this.isPassSama = (this.passBaru === this.passRetype) && (this.validator.validateKode(this.passRetype));
    return this.isPassBaruValid && this.isPassSama && this.passLama !== undefined;
  }

  validateKode(): boolean {
    this.isKodeBaruValid = this.validator.validateKode(this.kodeBaru);
    this.isKodeSama = (this.kodeBaru === this.kodeRetype) && (this.validator.validateKode(this.kodeRetype));
    return this.isKodeBaruValid && this.isKodeSama && this.kodeLama !== undefined;
  }

  submitKodeBaru(): void {
    if (this.validateKode()) {

      const kodeUser = {
        id_nasabah: this.login.userData.id_nasabah,
        kode_rahasiaL: this.kodeLama,
        krb1: this.kodeBaru,
        krb2: this.kodeRetype
      };

      this.setting.changeKode(kodeUser).subscribe((data: any) => alert(data['message']));
    } else {
      alert('gagal ubah kode rahasia');
      this.isKodeBaruValid = false;
      this.isKodeSama = false;
    }
  }

  submitPassBaru(): void {
    const passUser = {
      id_nasabah: this.login.userData.id_nasabah,
      passwordl: this.passLama,
      passwordb1: this.passBaru,
      passwordb2: this.passRetype
    };

    if (this.validatePassword()) {
      this.setting.changePassword(passUser).subscribe((data: any) => alert(data['message']));
    } else {
      alert('gagal ubah password');
      this.isPassBaruValid = false;
      this.isPassSama = false;
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
}
