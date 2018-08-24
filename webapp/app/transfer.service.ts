import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { LoginService } from './login.service';

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

  async doTransfer(dataTrf: any) {
    const url = 'http://localhost/api/transfer/create.php';
    const res = await this.http.post(url, dataTrf, httpOptions).toPromise();
    return <any>res;
  }
}
