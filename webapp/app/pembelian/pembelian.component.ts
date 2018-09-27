import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import {PembelianService} from '../pembelian.service';
import {InputValidatorService} from '../input-validator.service';
import {LoginService} from '../login.service';

@Component({
  selector: 'app-pembelian',
  templateUrl: './pembelian.component.html',
  styleUrls: ['./pembelian.component.css']
})
export class PembelianComponent implements OnInit {

  page = 1;
  dataBeli = {
    norek: null,
    no_hp_tujuan: null,
    provider: null,
    kode_rhs: null,
    nominal: null
  };
  keterangan: string;
  status: string;
  isFormValid = false;
  angForm: FormGroup;
  rekening = [];

  constructor(
    private beli: PembelianService,
    private validator: InputValidatorService,
    private route: Router,
    private login: LoginService,
    private fb: FormBuilder,
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
    this.rekening = this.login.userData.rekening;
    this.dataBeli.norek = this.rekening[0].no_rek;
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
    this.status = res.status;
    this.page = 3;
  }

  onChangedSelect(val): void {
    this.dataBeli.norek = val;
  }
}
