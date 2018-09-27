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

  /**
   * Validasi input kosong
   *
   * @param val value yang akan dicek
   *
   * @returns {boolean} Jika null/undefined/' ' return true, jika tidak false
   */
  static isNull(...val: string[]): boolean {
    for (const v of val) {
      if (v === null) { return true; }
      if (v === undefined) { return true; }
      if (v === '') { return true; }
    }
    return false;
  }

  /**
   * Mendeteksi kombinasi tanggal lahir pada input
   *
   * @param {string} sequence - input yang akan dilihat
   * @param {string} tanggal - tanggal yang akan dicocokkan
   *
   * @returns {boolean} Jika tidak terdapat kombinasi tanggal pada input return true, selain itu false
   */
  static validateTanggalPermutation(sequence: string, tanggal: string): boolean {
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

  constructor(
    private http: HttpClient,
    private logger: NGXLogger,
    private login: LoginService
  ) { }

  /**
   * Validasi input untuk password
   *
   * @param {string} pass - password
   *
   * @returns {boolean} Jika password valid return true, jika tidak false
   */
  validatePassword(pass: string): boolean {
    if (InputValidatorService.isNull(pass)) {
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
    if (!InputValidatorService.validateTanggalPermutation(pass, this.tanggal)) {
      const log = 'input: username ' + this.login.userData.username + ' password contains birth date';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  /**
   * Validasi input untuk kode rahasia
   *
   * @param {string} kode - kode rahasia
   *
   * @returns {boolean} Jika kode rahasia valid return true, jika tidak false
   */
  validateKode(kode: string): boolean {
    if (InputValidatorService.isNull(kode)) {
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
    if (!InputValidatorService.validateTanggalPermutation(kode, this.tanggal)) {
      const log = 'input: username ' + this.login.userData.username + ' secret code contains birth date';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  /**
   * Validasi input untuk umur registrasi
   *
   * @param {string} tanggal - tanggal
   *
   * @returns {boolean} Jika umur > 17 tahun return true, jika tidak false
   */
  validateTanggal(tanggal: string): boolean {
    this.tanggal = tanggal;
    if (InputValidatorService.isNull(tanggal)) {
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

  /**
   * Validasi input untuk range tanggal histori
   *
   * @param {string} dariTanggal - tanggal awal
   * @param {string} hinggaTanggal - tanggal akhir
   *
   * @returns {boolean} Jika range tanggal valid return true, jika tidak false
   */
  validateRangeTanggal(dariTanggal: string, hinggaTanggal: string): boolean {
    if (InputValidatorService.isNull(dariTanggal)) {
      const log = 'histori: username ' + this.login.userData.username + ' initial date null value';
      this.logger.error(log);
      return false;
    }
    if (InputValidatorService.isNull(hinggaTanggal)) {
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

  /**
   * Validasi input login box
   *
   * @param {string} username - username
   * @param {string} password - password
   *
   * @returns {boolean} Jika input valid return true, jika tidak false
   */
  validateLogin(username: string, password: string): boolean {
    if (InputValidatorService.isNull(username)) {
      const log = 'login: username null value';
      this.logger.error(log);
      return false;
    }
    if (InputValidatorService.isNull(password)) {
      const log = 'login: password null value';
      this.logger.error(log);
      return false;
    }
    return true;
  }

  /**
   * Validasi input untuk data pembelian
   *
   * @param {Object} dataBeli - data pembelian
   * @param {string} dataBeli.username
   * @param {string} dataBeli.no_hp_tujuan
   * @param {string} dataBeli.id_nasabah
   * @param {string} dataBeli.provider
   * @param {string} dataBeli.kode_rahasia
   * @param {string} dataBeli.nominal
   *
   * @returns {boolean} Jika data valid return true, jika tidak false
   */
  validatePembelian(dataBeli: {
    norek: string,
    no_hp_tujuan: string,
    provider: string,
    kode_rhs: string,
    nominal: string
  }): boolean {
    if (InputValidatorService.isNull(dataBeli.no_hp_tujuan)) {
      return false;
    }
    if (InputValidatorService.isNull(dataBeli.provider)) {
      return false;
    }
    if (InputValidatorService.isNull(dataBeli.nominal)) {
      return false;
    }
    return true;
  }

  /**
   * Validasi nomor rekening
   *
   * @param {Object} dataTrf - data transfer
   * @param {string} dataTrf.username
   * @param {string} dataTrf.kode_rahasia
   * @param {string} dataTrf.no_rek_tujuan
   * @param {string} dataTrf.id_nasabah
   * @param {string} dataTrf.nominal
   * @param {string} dataTrf.keterangan
   *
   * @returns {Promise} Hasil request dari API dalam bentuk Promise
   */
  async validateNorek(dataTrf: {
    norek_kirim: string,
    norek_terima: string,
  }) {
    const url = 'http://localhost:8080/ceknorek';
    const id = this.login.userData.id_nasabah;
    let log = 'id ' + id + ' send POST to ' + url + ', content: ' + JSON.stringify(dataTrf);
    this.logger.info(log);
    const res = await this.http.post(url, dataTrf, httpOptions).toPromise();
    log = 'id ' + id + ' receive from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }
}
