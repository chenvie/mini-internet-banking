import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import md5 from 'md5';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class TransferService {

  constructor(
    private http: HttpClient
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
    username: string,
    kode_rahasia: string,
    no_rek_tujuan: string,
    id_nasabah: string,
    nominal: string,
    keterangan: string
  }) {
    dataTrf.kode_rahasia = md5(dataTrf.kode_rahasia);
    const url = 'http://localhost/api/transfer/create.php';
    const res = await this.http.post(url, dataTrf, httpOptions).toPromise();
    return <any>res;
  }
}
