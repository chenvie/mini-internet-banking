import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginService } from './login.service';
import { NGXLogger } from 'ngx-logger';

@Injectable({
  providedIn: 'root'
})
export class InfoRekService {

  constructor(
    private http: HttpClient,
    private login: LoginService,
    private logger: NGXLogger) {}

  /**
   * Ambil mutasi dari API
   *
   * @returns {Promise} Hasil request dari API dalam bentuk Promise
   */
  async getMutasi(norek) {
    const url = 'http://localhost:8080/mutasi/' + norek;
    const id = this.login.userData.id_nasabah;
    let log = 'id ' + id + ' send GET to ' + url;
    this.logger.info(log);
    const res = await this.http.get(url).toPromise();
    log = 'id ' + id + ' receive from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }

  /**
   * Ambil histori dari API
   *
   * @param {Object} historiData - Kumpulan data untuk mengambil histori
   * @param {string} historiData.id - ID nasabah
   * @param {string} historiData.dariTanggal - Tanggal awal
   * @param {string} historiData.hinggaTanggal - Tanggal akhir
   *
   * @returns {Promise} Hasil request dari API dalam bentuk Promise
   */
  async getHistori(historiData: {
    no_rek: string,
    tgl_awal: string,
    tgl_akhir: string
  }) {
    const url = 'http://localhost:8080/history/' + historiData.no_rek + '/' + historiData.tgl_awal + '/' + historiData.tgl_akhir;
    const id = this.login.userData.id_nasabah;
    let log = 'id ' + id + ' send GET to ' + url;
    this.logger.info(log);
    const res = await this.http.get(url).toPromise();
    log = 'id ' + id + ' receive from ' + url + ', content: ' + JSON.stringify(res);
    this.logger.info(log);
    return <any>res;
  }
}
