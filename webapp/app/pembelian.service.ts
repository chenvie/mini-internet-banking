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

  async buyPulsa(data: any) {
    data.kode_rahasia = md5(data.kode_rahasia);
    const url = 'http://localhost/api/pulsa/create.php'; // kode null masih valid
    const res = await this.http.post(url, data, httpOptions).toPromise();
    return <any>res;
  }
}
