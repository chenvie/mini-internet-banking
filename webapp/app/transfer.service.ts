import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import md5 from 'md5';
import {NGXLogger} from 'ngx-logger';
import {LoginService} from './login.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class TransferService {

  constructor(
    private http: HttpClient,
    private logger: NGXLogger,
    private login: LoginService,
  ) { }

  /**
   * Call API untuk transfer
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
  async doTransfer(dataTrf: {
    norek_kirim: string,
    norek_terima: string,
    nominal: string,
    ket: string,
    kode_rhs: string,
  }) {
    dataTrf.kode_rhs = md5(dataTrf.kode_rhs);
    const url = 'http://localhost:8080/transfer/';
    const id = this.login.userData.id_nasabah;
    let log = 'id ' + id + ' send POST to ' + url + ', content: ' + JSON.stringify(dataTrf);
    this.logger.info(log);
    const res = await this.http.post(url, dataTrf, httpOptions).toPromise();
    log = 'id ' + id + ' receive from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }
}
