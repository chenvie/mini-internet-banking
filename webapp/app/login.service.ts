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

  async login(userLogin: any) {
    userLogin.password = md5(userLogin.password);
    const res = await this.getLoginValidation(userLogin);
    this.isLoginValid = res.login;
    if (!this.isLoginValid) {
      this.logger.warn('login: username', userLogin.username, 'login credentials invalid');
      return false;
    } else {
      this.logger.info('login: username', userLogin.username, 'login credentials verified');
    }
    this.userData = await this.getUserData(userLogin.username);
    for (const key of Object.keys(this.userData)) {
      const val = this.userData[key];
      if (val === '') {
        this.logger.error('login: username', userLogin.username, 'error fetching', key);
        return false;
      }
    }
    this.logger.info('login: username', this.userData.username, 'fetch user data success');
    return true;
    // only return true / false
    // call [getUserData] to get user data from DB
  }

  async getUserData(username: string) {
    const url = 'http://localhost/api/nasabah/read-one.php';
    const param = '?unm=' + username;
    const res = await this.http.get(url + param).toPromise();
    return <any[8]> res;
  }

  async getLoginValidation(userLogin: any) {
    const url = 'http://localhost/api/nasabah/login.php';
    const res = await this.http.post(url, userLogin, httpOptions).toPromise();
    return <any>res;
  }

}
