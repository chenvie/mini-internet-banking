import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

import md5 from 'md5';
import { NGXLogger } from 'ngx-logger';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})

export class LoginService {

  userData = {
    id_nasabah: '',
    username: '',
    password: '',
    rekening: [],
  };
  isLoginValid = false;

  constructor(
    private http: HttpClient,
    private logger: NGXLogger
  ) { }

  /**
   * Login to webapp
   *
   * @param {Object} userLogin data login
   * @param {string} userLogin.username username
   * @param {string} userLogin.password password
   *
   * @returns {boolean} Jika berhasil login dan fetch data return true, jika tidak false
   */
  async login(userLogin: {username: string, password: string}) {
    this.userData.username = userLogin.username;
    userLogin.password = md5(userLogin.password);
    const res = await this.getLoginValidation(userLogin);
    this.userData.id_nasabah = res.id_nasabah;
    this.isLoginValid = res.status === '1';
    await this.getUserData();
    return this.isLoginValid;
  }

  /**
   * Ambil data user dari API
   *
   * @returns {Promise<any>} Hasil request dari API dalam bentuk Promise
   */
  async getUserData() {
    const url = 'http://localhost:8080/nasabah/' + this.userData.id_nasabah;
    let log = 'username ' + this.userData.username + ' send GET to ' + url;
    this.logger.info(log);
    const res = await this.http.get<any>(url).toPromise();
    log = 'username ' + this.userData.username + ' receive from ' + url + ' content: ' + JSON.stringify(res);
    this.logger.info(log);
    this.userData.rekening = res.result;
  }

  /**
   * Validasi login dari API
   *
   * @param {Object} userLogin data login
   * @param {string} userLogin.username username
   * @param {string} userLogin.password password
   *
   * @returns {Promise<any>} Hasil request dari API dalam bentuk Promise
   */
  async getLoginValidation(userLogin: {username: string, password: string}) {
    const url = 'http://localhost:8080/login';
    let log = 'username ' + this.userData.username + ' send POST to ' + url + ' content: ' + JSON.stringify(userLogin);
    this.logger.info(log);
    const res = await this.http.post(url, userLogin, httpOptions).toPromise();
    log = 'username ' + this.userData.username + ' receive from ' + url + ' content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }

}
