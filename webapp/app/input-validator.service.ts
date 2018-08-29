import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { LoginService } from './login.service';

import * as moment from 'moment';
import { NGXLogger } from 'ngx-logger';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})

export class InputValidatorService {

  tanggal: any;

  constructor(
    private http: HttpClient,
    private logger: NGXLogger,
    private login: LoginService
  ) { }

  validatePassword(pass: string): boolean {
    if (this.isNull(pass)) {
      const log = 'input: username ' + this.login.userData.username + ' password null value';
      this.logger.error(log);
      return false;
    }
    if (pass.length < 8) {
      const log = 'input: username ' + this.login.userData.username + ' password too short';
      this.logger.error(log);
      return false;
    }
    const regex = /\W/g;
    const result = pass.match(regex);
    if (result !== null) {
      const log = 'input: username ' + this.login.userData.username + ' password contains non-alphanumeric character';
      this.logger.error(log);
      return false;
    }
    if (!this.validateTanggalPermutation(pass, this.tanggal)) {
      const log = 'input: username ' + this.login.userData.username + ' password contains birth date';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  validateKode(kode: string): boolean {
    if (this.isNull(kode)) {
      const log = 'input: username ' + this.login.userData.username + ' secret code null value';
      this.logger.error(log);
      return false;
    }
    if (kode.length < 6) {
      const log = 'input: username ' + this.login.userData.username + ' secret code too short';
      this.logger.error(log);
      return false;
    }
    const regex = /\W/g;
    const result = kode.match(regex);
    if (result !== null) {
      const log = 'input: username ' + this.login.userData.username + ' secret code contains non-alphanumeric character';
      this.logger.error(log);
      return false;
    }
    if (!this.validateTanggalPermutation(kode, this.tanggal)) {
      const log = 'input: username ' + this.login.userData.username + ' secret code contains birth date';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  validateTanggal(tanggal: string): boolean {
    this.tanggal = tanggal;
    if (this.isNull(tanggal)) {
      const log = 'registration: username ' + this.login.userData.username + ' date null value';
      this.logger.error(log);
      return false;
    }
    if (moment().year() - moment(tanggal).year() < 17) {
      const log = 'registration: username ' + this.login.userData.username + ' age less than 17';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  validateRangeTanggal(dariTanggal: string, hinggaTanggal: string): boolean {
    if (this.isNull(dariTanggal)) {
      const log = 'histori: username ' + this.login.userData.username + ' initial date null value';
      this.logger.error(log);
      return false;
    }
    if (this.isNull(hinggaTanggal)) {
      const log = 'histori: username ' + this.login.userData.username + ' target date null value';
      this.logger.error(log);
      return false;
    }
    if (moment(dariTanggal) > moment() || moment(hinggaTanggal) > moment()) {
      const log = 'histori: username ' + this.login.userData.username + ' target date larger than current date';
      this.logger.error(log);
      return false;
    }
    if (moment(dariTanggal) > moment(hinggaTanggal)) {
      const log = 'histori: username ' + this.login.userData.username + ' target date larger than initial date';
      this.logger.error(log);
      return false;
    }
    if (moment(dariTanggal) < moment().subtract(30, 'd')) {
      const log = 'histori: username ' + this.login.userData.username + ' initial date more than 30 days behind';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  validateLogin(username: string, password: string): boolean {
    if (this.isNull(username)) {
      const log = 'login: username null value';
      this.logger.error(log);
      return false;
    }
    if (this.isNull(password)) {
      const log = 'login: password null value';
      this.logger.error(log);
      return false;
    }
    return true;
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
    if (this.isNull(dataBeli.no_hp_tujuan)) {
      const log = 'transaction: username ' + this.login.userData.username + ' target phone number null value';
      this.logger.error(log);
      return false;
    }
    if (this.isNull(dataBeli.provider)) {
      const log = 'transaction: username ' + this.login.userData.username + ' provider null value';
      this.logger.error(log);
      return false;
    }
    if (this.isNull(dataBeli.nominal)) {
      const log = 'transaction: username ' + this.login.userData.username + ' nominal null value';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  async validateNorek(dataTrf: any) {
    const url = 'http://localhost/api/transfer/cek-no-rek.php';
    const res = await this.http.post(url, dataTrf, httpOptions).toPromise();
    return <any>res;
  }

  isNull(val: string): boolean {
    if (val === null) { return true; }
    if (val === undefined) { return true; }
    if (val === '') { return true; }
    return false;
  }
}
