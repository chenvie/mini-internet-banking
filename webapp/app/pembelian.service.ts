import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import md5 from 'md5';
import {LoginService} from './login.service';
import {NGXLogger} from 'ngx-logger';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PembelianService {

  constructor(
    private http: HttpClient,
    private logger: NGXLogger,
    private login: LoginService,
    ) { }

  /**
   * Call API untuk pembelian pulsa
   *
   * @param {Object} dataBeli data pembelian pulsa
   * @param {string} dataBeli.username
   * @param {string} dataBeli.no_hp_tujuan
   * @param {string} dataBeli.id_nasabah
   * @param {string} dataBeli.provider
   * @param {string} dataBeli.kode_rahasia
   * @param {string} dataBeli.nominal
   *
   * @returns {Promise<any>} Hasil request dari API dalam bentuk Promise
   */
  async buyPulsa(dataBeli: {
    norek: string,
    no_hp_tujuan: string,
    provider: string,
    kode_rhs: string,
    nominal: string
  }) {
    dataBeli.kode_rhs = md5(dataBeli.kode_rhs);
    const url = 'http://localhost:8080/pembelian/';
    const id = this.login.userData.id_nasabah;
    let log = 'id ' + id + ' send POST to ' + url + ', content: ' + JSON.stringify(dataBeli);
    this.logger.info(log);
    const res = await this.http.post(url, dataBeli, httpOptions).toPromise();
    log = 'id ' + id + ' receive from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }
}
