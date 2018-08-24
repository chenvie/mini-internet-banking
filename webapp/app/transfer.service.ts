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

  async doTransfer(dataTrf: any) {
    dataTrf.kode_rahasia = md5(dataTrf.kode_rahasia);
    const url = 'http://localhost/api/transfer/create.php';
    const res = await this.http.post(url, dataTrf, httpOptions).toPromise();
    return <any>res;
  }
}
