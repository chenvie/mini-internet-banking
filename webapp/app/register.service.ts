import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, ObservableInput } from 'rxjs';
import md5 from 'md5';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http: HttpClient) { }

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
  register(userData: {
    nama_lengkap: string,
    email: string,
    password: string,
    no_ktp: string,
    tgl_lahir: string,
    alamat: string,
    kode_rahasia: string
  }): Observable<any> {
    userData.password = md5(userData.password);
    userData.kode_rahasia = md5(userData.kode_rahasia);
    const url = 'http://localhost/api/nasabah/create.php';
    return this.http.post(url, userData, httpOptions);
  }
}
