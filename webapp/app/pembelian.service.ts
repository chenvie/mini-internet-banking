import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class PembelianService {

  constructor(private http: HttpClient) { }

  async buyPulsa(data: any) {
    const url = 'http://localhost/api/pulsa/create.php'; // kode null masih valid
    const res = await this.http.post(url, data, httpOptions).toPromise();
    return <any>res;
  }
}
