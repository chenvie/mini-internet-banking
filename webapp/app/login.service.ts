import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import md5 from 'md5';
import { NGXLogger } from 'ngx-logger';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})

export class LoginService {

  userData = {
    id_nasabah: null,
    username: 'NEW',
    password: null,
    nama_lengkap: null,
    kode_rahasia: null,
    tgl_lahir: null,
    jml_saldo: null,
    no_rek: null
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
  async login(userLogin: {
    username: string,
    password: string
  }) {
    userLogin.password = md5(userLogin.password);
    const res = await this.getLoginValidation(userLogin);
    this.isLoginValid = res.login;
    if (!this.isLoginValid) {
      const log = 'login: username ' + userLogin.username + ' login credentials invalid';
      this.logger.warn(log);
      return false;
    } else {
      const log = 'login: username ' + userLogin.username + ' login credentials verified';
      this.logger.info(log);
    }
    this.userData = await this.getUserData(userLogin.username);
    for (const key of Object.keys(this.userData)) {
      const val = this.userData[key];
      if (val === '') {
        const log = 'login: username ' + userLogin.username + ' error fetching' + key;
        this.logger.error(log);
        return false;
      }
    }
    const log = 'login: username ' + this.userData.username + ' fetch user data success';
    this.logger.info(log);
    return true;
  }

  /**
   * Ambil data user dari API
   *
   * @param {string} username username
   *
   * @returns {Promise<any>} Hasil request dari API dalam bentuk Promise
   */
  async getUserData(username: string) {
    const url = 'http://localhost/api/nasabah/read-one.php';
    const param = '?unm=' + username;
    const res = await this.http.get(url + param).toPromise();
    return <any[8]> res;
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
  async getLoginValidation(userLogin: any) {
    const url = 'http://localhost/api/nasabah/login.php';
    const res = await this.http.post(url, userLogin, httpOptions).toPromise();
    return <any>res;
  }

}
