import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { InputValidatorService } from '../input-validator.service';
import { RegisterService } from '../register.service';
import { LoginService } from '../login.service';


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
  angForm: FormGroup;

  constructor(
    private validator: InputValidatorService,
    private register: RegisterService,
    private fb : FormBuilder,
    private login: LoginService,
    private route: Router) 
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
    this.isTanggalValid = this.validator.validateTanggal(this.userData.tgl_lahir); // harus diatas, menentukan tanggal di service
    this.isPassValid = this.validator.validatePassword(this.userData.password);
    this.isKodeValid = this.validator.validateKode(this.userData.kode_rahasia);
    return this.isPassValid && this.isKodeValid && this.isTanggalValid;
  }

  submitForm() {

    if (this.validateForm()) {
      this.register.register(this.userData).subscribe((data: any) => alert(data['message'] + '\nSilahkan login pada halaman utama.'));
      this.resetForm();
      this.toggleRegForm();
    } else {
      alert('pengisian formulir salah');
    }
  }
}
