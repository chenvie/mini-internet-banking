import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { InputValidatorService } from '../input-validator.service';
import { RegisterService } from '../register.service';
import { LoginService } from '../login.service';
import { NGXLogger } from 'ngx-logger';


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
  isFormValid = true;
  userData = {
    nama_lengkap: null,
    email: null,
    password: null,
    no_ktp: null,
    tgl_lahir: null,
    alamat: null,
    kode_rahasia: null
  };
  message: string;
  angForm: FormGroup;

  constructor(
    private validator: InputValidatorService,
    private register: RegisterService,
    private fb : FormBuilder,
    private login: LoginService,
    private route: Router,
    private logger: NGXLogger
  ) 
    { this.cekForm(); }

    cekForm(){
      this.angForm=this.fb.group({
        name: ['', Validators.required],
        email:['', Validators.required],
        pass: ['', Validators.required],
        ktp: ['', Validators.required],
        tgl: ['', Validators.required],
        alamat: ['', Validators.required],
        kode: ['', Validators.required]
      });
    }

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
    for (const key of Object.keys(this.userData)) {
      if (this.userData[key] === null || this.userData[key] === '') {
        this.logger.error('registration:', key, 'null value');
        this.isFormValid = false;
      }
    }
    if (!this.isFormValid) {
      this.isFormValid = true;
      return false;
    }
    this.isTanggalValid = this.validator.validateTanggal(this.userData.tgl_lahir); // harus diatas, menentukan tanggal di service
    this.isPassValid = this.validator.validatePassword(this.userData.password);
    this.isKodeValid = this.validator.validateKode(this.userData.kode_rahasia);
    return this.isPassValid && this.isKodeValid && this.isTanggalValid;
  }

  submitForm() {

    if (this.validateForm()) {
      this.register.register(this.userData).subscribe((data: any) => {
        alert(data['message']);
        if (data['message'] === 'Pendaftaran gagal') {
          this.logger.warn('registration: create new account failed');
        } else {
          this.logger.info('registration: create new account success'); }
      });
      this.resetForm();
      this.toggleRegForm();
    } else {
      alert('pengisian formulir salah');
      this.logger.warn('registration: create new account failed');
    }
  }
}
