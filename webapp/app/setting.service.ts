import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import md5 from 'md5';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class SettingService {

  constructor(
    private http: HttpClient
  ) { }

  /**
   * Ganti password user
   *
   * @param {Object} passUser data penggantian password
   * @param {string} passUser.id_nasabah
   * @param {string} passUser.passwordl password lama
   * @param {string} passUser.passwordb1 password baru
   * @param {string} passUser.passwordb2 password retype
   *
   * @returns {Observable<any>} Hasil request dari API dalam bentuk Observable
   */
  changePassword(passUser: {
    id_nasabah: string,
    passwordl: string,
    passwordb1: string,
    passwordb2: string
  }): Observable<any> {
    passUser.passwordl = md5(passUser.passwordl);
    passUser.passwordb1 = md5(passUser.passwordb1);
    passUser.passwordb2 = md5(passUser.passwordb2);
    const url = 'http://localhost/api/nasabah/update_password.php';
    return this.http.post(url, passUser, httpOptions);
  }

  /**
   * Ganti kode rahasia user
   *
   * @param {Object} kodeUser data penggantian kode rahasia
   * @param {string} kodeUser.id_nasabah
   * @param {string} kodeUser.kode_rahasiaL kode rahasa lama
   * @param {string} kodeUser.krb1 kode rahasa baru
   * @param {string} kodeUser.krb2 kode rahasa retype
   *
   * @returns {Observable<any>} Hasil request dari API dalam bentuk Observable
   */
  changeKode(kodeUser: {
    id_nasabah: string,
    kode_rahasiaL: string,
    krb1: string,
    krb2: string
  }): Observable<any> {
    kodeUser.kode_rahasiaL = md5(kodeUser.kode_rahasiaL);
    kodeUser.krb1 = md5(kodeUser.krb1);
    kodeUser.krb2 = md5(kodeUser.krb2);
    const url = 'http://localhost/api/nasabah/update_kode_rahasia.php';
    return this.http.post(url, kodeUser, httpOptions);
  }
}
