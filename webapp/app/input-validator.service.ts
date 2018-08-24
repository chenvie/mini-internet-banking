import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import * as moment from 'moment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class InputValidatorService {

  tanggal: any;

  constructor(
    private http: HttpClient
  ) { }

  validatePassword(pass: string): boolean {
    if (pass === undefined || pass === null) { return false; }
    if (pass.length < 8) { return false; }
    const regex = /\W/g;
    const result = pass.match(regex);
    if (result !== null) { return false; }
    if (!this.validateTanggalPermutation(pass, this.tanggal)) { return false; }
    return true;
  }

  validateKode(kode: string): boolean {
    if (kode === undefined || kode === null) { return false; }
    if (kode.length < 6) { return false; }
    const regex = /\W/g;
    const result = kode.match(regex);
    if (result !== null) { return false; }
    if (!this.validateTanggalPermutation(kode, this.tanggal)) { return false; }
    return true;
  }

  validateTanggal(tanggal: string): boolean {
    this.tanggal = tanggal;
    if (tanggal === undefined || tanggal === null) { return false; }
    if (moment().year() - moment(tanggal).year() < 17) { return false; }
    return true;
  }

  validateRangeTanggal(dariTanggal: string, hinggaTanggal: string): boolean {
    if (dariTanggal === undefined || hinggaTanggal === undefined) { return false; }
    if (dariTanggal === '' || hinggaTanggal === '') { return false; }
    if (moment(dariTanggal) > moment() || moment(hinggaTanggal) > moment()) { return false; }
    if (moment(dariTanggal) > moment(hinggaTanggal)) { return false; }
    if (moment(dariTanggal) < moment().subtract(30, 'd')) { return false; }
    return true;
  }

  validateLogin(username: string, password: string): boolean {
    return username !== null && password !== null;
  }

  validateTanggalPermutation(sequence: string, tanggal: string): boolean {
    if (sequence === moment(tanggal).format('DDMMYY')) { return false; }
    if (sequence === moment(tanggal).format('MMDDYY')) { return false; }
    if (sequence === moment(tanggal).format('DDYYMM')) { return false; }
    if (sequence === moment(tanggal).format('MMYYDD')) { return false; }
    if (sequence === moment(tanggal).format('YYDDMM')) { return false; }
    if (sequence === moment(tanggal).format('YYMMDD')) { return false; }
    if (sequence === moment(tanggal).format('DDMMYYYY')) { return false; }
    if (sequence === moment(tanggal).format('MMDDYYYY')) { return false; }
    if (sequence === moment(tanggal).format('DDYYYYMM')) { return false; }
    if (sequence === moment(tanggal).format('MMYYYYDD')) { return false; }
    if (sequence === moment(tanggal).format('YYYYDDMM')) { return false; }
    if (sequence === moment(tanggal).format('YYYYMMDD')) { return false; }
    return true;
  }

  validatePembelian(dataBeli: any): boolean {
    if (dataBeli.no_hp_tujuan === '' || dataBeli.no_hp_tujuan === null) { return false; }
    if (dataBeli.provider === null) { return false; }
    if (dataBeli.nominal === null) { return false; }
    return true;
  }

  async validateNorek(dataTrf: any) {
    const url = 'http://localhost/api/transfer/cek-no-rek.php';
    const res = await this.http.post(url, dataTrf, httpOptions).toPromise();
    return <any>res;
  }
}
