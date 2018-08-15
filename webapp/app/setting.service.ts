import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

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
    const url = 'http://localhost/api/nasabah/update_password.php';
    return this.http.post(url, passUser, httpOptions);
  }

  changeKode(kodeUser: any): Observable<any> {
    const url = 'http://localhost/api/nasabah/update_kode_rahasia.php';
    return this.http.post(url, kodeUser, httpOptions);
  }
}
