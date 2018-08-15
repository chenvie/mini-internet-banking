import { Component, OnInit, Input } from '@angular/core';
import { InputValidatorService } from '../input-validator.service';
import { RegisterService } from '../register.service';

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
  userData = {
    nama_lengkap: null,
    email: null,
    password: null,
    no_ktp: null,
    tgl_lahir: null,
    alamat: null,
    kode_rahasia: null,
    id_nasabah: 10 // id sementara, nunggu id generator dari backend
  };
  message: string;

  constructor(
    private validator: InputValidatorService,
    private register: RegisterService) { }

  ngOnInit() {
  }

  toggleRegForm(): void {
    this.isHidden = !this.isHidden;
  }

  resetForm(): void {
    this.userData.nama_lengkap = '';
    this.userData.email = '';
    this.userData.password = '';
    this.userData.no_ktp = '';
    this.userData.tgl_lahir = '';
    this.userData.alamat = '';
    this.userData.kode_rahasia = '';
  }

  validateForm(): boolean {
    this.isPassValid = this.validator.validatePassword(this.userData.password);
    this.isKodeValid = this.validator.validateKode(this.userData.kode_rahasia);
    this.isTanggalValid = this.validator.validateTanggal(this.userData.tgl_lahir);
    return this.isPassValid && this.isKodeValid && this.isTanggalValid;
  }

  submitForm() {
    if (this.validateForm()) {
      alert('form valid');
      this.register.register(this.userData).subscribe((data: any) => alert(data['message']));
    } else {
      alert('form invalid');
    }
  }
}
