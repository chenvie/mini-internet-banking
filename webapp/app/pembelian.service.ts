import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import md5 from 'md5';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PembelianService {

  constructor(private http: HttpClient) { }

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
    username: string,
    no_hp_tujuan: string,
    id_nasabah: string,
    provider: string,
    kode_rahasia: string,
    nominal: string
  }) {
    dataBeli.kode_rahasia = md5(dataBeli.kode_rahasia);
    const url = 'http://localhost/api/pulsa/create.php'; // kode null masih valid
    const res = await this.http.post(url, dataBeli, httpOptions).toPromise();
    return <any>res;
  }
}
