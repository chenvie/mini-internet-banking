import { Component, OnInit, Input } from '@angular/core';
import { InputValidatorService } from '../input-validator.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  isHidden = false;
  isPassValid = false;
  isKodeValid = false;
  isTanggalValid = false;
  nama: string;
  email: string;
  password: string;
  ktp: string;
  tglLahir: string;
  alamat: string;
  kode: string;

  constructor(private validator: InputValidatorService) { }

  ngOnInit() {
  }

  toggleRegForm(): void {
    this.isHidden = !this.isHidden;
  }

  resetForm(): void {
    this.nama = '';
    this.email = '';
    this.password = '';
    this.ktp = '';
    this.tglLahir = '';
    this.alamat = '';
    this.kode = '';
  }

  validateForm(): void {
    this.isPassValid = this.validator.validatePassword(this.password);
    this.isKodeValid = this.validator.validateKode(this.kode);
    this.isTanggalValid = this.validator.validateTanggal(this.tglLahir);
    // TODO submit form through API if all valid
  }

}
