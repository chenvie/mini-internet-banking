import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import md5 from 'md5';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})

export class LoginService {

  userData = {
    id_nasabah: null,
    username: null,
    password: null,
    nama_lengkap: null,
    kode_rahasia: null,
    tgl_lahir: null,
    jml_saldo: null,
    no_rek: null
  };
  isLoginValid = false;

  constructor(private http: HttpClient) { }

  async login(userLogin: any) {
    userLogin.password = md5(userLogin.password);
    const res = await this.getLoginValidation(userLogin);
    this.isLoginValid = res.login;
    if (!this.isLoginValid) { return false; }
    this.userData = await this.getUserData(userLogin.username);
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
