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
export class RegisterService {

  constructor(private http: HttpClient) { }

  register(userData: any): Observable<any> {
    userData.password = md5(userData.password);
    userData.kode_rahasia = md5(userData.kode_rahasia);
    const url = 'http://localhost/api/nasabah/create.php';
    return this.http.post(url, userData, httpOptions);
  }
}
