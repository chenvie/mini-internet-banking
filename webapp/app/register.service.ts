import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import md5 from 'md5';
import { NGXLogger} from 'ngx-logger';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(
    private http: HttpClient,
    private logger: NGXLogger) { }

  /**
   * Call API untuk registrasi
   *
   * @param {Object} userData data pembelian pulsa
   * @param {string} userData.nama_lengkap
   * @param {string} userData.email
   * @param {string} userData.password
   * @param {string} userData.no_ktp
   * @param {string} userData.tgl_lahir
   * @param {string} userData.alamat
   * @param {string} userData.kode_rahasia
   *
   * @returns {Observable<any>} Hasil request dari API dalam bentuk Observable
   */
  async register(userData: {
    nama_lengkap: string,
    email: string,
    password: string,
    no_ktp: string,
    tgl_lahir: string,
    alamat: string,
    kode_rahasia: string
  }) {
    userData.password = md5(userData.password);
    userData.kode_rahasia = md5(userData.kode_rahasia);
    const url = 'http://localhost:8080/nasabah';
    let log = 'NEW USER send POST to ' + url + ', content: ' + JSON.stringify(userData);
    this.logger.info(log);
    const res = await this.http.post(url, userData, httpOptions).toPromise();
    log = 'NEW USER receive from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }
}
