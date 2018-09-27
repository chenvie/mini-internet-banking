import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import md5 from 'md5';
import {NGXLogger} from 'ngx-logger';
import {LoginService} from './login.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class SettingService {

  constructor(
    private http: HttpClient,
    private login: LoginService,
    private logger: NGXLogger,
  ) { }

  /**
   * Ganti password user
   *
   * @param {Object} passUser data penggantian password
   * @param {string} passUser.id_nasabah
   * @param {string} passUser.passwordl password lama
   * @param {string} passUser.passwordb1 password baru
   * @param {string} passUser.passwordb2 password retype
   *
   * @returns {Observable<any>} Hasil request dari API dalam bentuk Observable
   */
  async changePassword(passUser: {
    id_nasabah: string,
    passwordl: string,
    passwordb1: string,
    passwordb2: string
  }) {
    passUser.passwordl = md5(passUser.passwordl);
    passUser.passwordb1 = md5(passUser.passwordb1);
    passUser.passwordb2 = md5(passUser.passwordb2);
    const url = 'http://localhost:8080/update_password';
    const id = this.login.userData.id_nasabah;
    let log = 'id ' + id + ' send POST to ' + url + ', content: ' + JSON.stringify(passUser);
    this.logger.info(log);
    const res = await this.http.post(url, passUser, httpOptions).toPromise();
    log = 'id ' + id + ' receive from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }

  /**
   * Ganti kode rahasia user
   *
   * @param {Object} kodeUser data penggantian kode rahasia
   * @param {string} kodeUser.id_nasabah
   * @param {string} kodeUser.kode_rahasiaL kode rahasa lama
   * @param {string} kodeUser.krb1 kode rahasa baru
   * @param {string} kodeUser.krb2 kode rahasa retype
   *
   * @returns {Observable<any>} Hasil request dari API dalam bentuk Observable
   */
  async changeKode(kodeUser: {
    no_rek: string,
    kode_rahasiaL: string,
    krb1: string,
    krb2: string
  }) {
    kodeUser.kode_rahasiaL = md5(kodeUser.kode_rahasiaL);
    kodeUser.krb1 = md5(kodeUser.krb1);
    kodeUser.krb2 = md5(kodeUser.krb2);
    const url = 'http://localhost:8080/update_kode_rahasia';
    const id = this.login.userData.id_nasabah;
    let log = 'id ' + id + ' send POST to ' + url + ', content: ' + JSON.stringify(kodeUser);
    this.logger.info(log);
    const res = await this.http.post(url, kodeUser, httpOptions).toPromise();
    log = 'id ' + id + ' received from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }
}
