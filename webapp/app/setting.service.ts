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

  changePassword(passUser: any): Observable<any> {
    passUser.passwordl = md5(passUser.passwordl);
    passUser.passwordb1 = md5(passUser.passwordb1);
    passUser.passwordb2 = md5(passUser.passwordb2);
    const url = 'http://localhost/api/nasabah/update_password.php';
    return this.http.post(url, passUser, httpOptions);
  }

  changeKode(kodeUser: any): Observable<any> {
    kodeUser.kode_rahasiaL = md5(kodeUser.kode_rahasiaL);
    kodeUser.krb1 = md5(kodeUser.krb1);
    kodeUser.krb2 = md5(kodeUser.krb2);
    const url = 'http://localhost/api/nasabah/update_kode_rahasia.php';
    return this.http.post(url, kodeUser, httpOptions);
  }
}
