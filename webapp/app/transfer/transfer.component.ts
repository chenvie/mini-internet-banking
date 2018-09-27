import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from '../login.service';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { InputValidatorService } from '../input-validator.service';
import { TransferService } from '../transfer.service';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {

  page: number;
  dataTrf = {
    norek_kirim: null,
    norek_terima: null,
    nominal: null,
    ket: null,
    kode_rhs: null,
  };
  isNorekValid = true;
  message: string;
  status: string;
  rekening = [];
  angForm: FormGroup;

  constructor(
    private login: LoginService,
    private fb: FormBuilder,
    private transfer: TransferService,
    private route: Router,
    private validator: InputValidatorService,
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
    this.rekening = this.login.userData.rekening;
    this.dataTrf.norek_kirim = this.rekening[0].no_rek;
  }

  async validateNorek() {
    if (InputValidatorService.isNull(this.dataTrf.norek_terima, this.dataTrf.nominal)) {
      return;
    }
    const res = await this.validator.validateNorek(this.dataTrf);
    this.isNorekValid = res.status === 'Berhasil';
    this.page = this.isNorekValid ? 2 : 1 ;
  }

  async doTransfer() {
    const res = await this.transfer.doTransfer(this.dataTrf);
    this.status = res.status;
    this.message = res.message;
    this.page = 3;
  }

  onChangedSelect(val): void {
    this.dataTrf.norek_kirim = val;
  }
}
