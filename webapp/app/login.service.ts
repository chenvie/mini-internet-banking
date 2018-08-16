import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { InputValidatorService } from './input-validator.service';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})

export class LoginService {

  isLoggedIn = false;
  id_nasabah = 3; // ID sementara hardcode, login API belum ngasih id
  // loginToken: any;

  constructor(private http: HttpClient) { }

  login(userLogin: any): Observable<any> {
    const url = 'http://localhost/api/nasabah/login.php';
    return this.http.post(url, userLogin, httpOptions);
    // change return type to customer data
    // return this.isLoggedIn && true;
  }

}
