import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import md5 from 'md5';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class TambahRekServiceService {

  constructor(
    private http: HttpClient
  ) {}

  async tambahRek(dataTbhRek: { id_nasabah: string; kode_rahasia: string }) {
    dataTbhRek.kode_rahasia = md5(dataTbhRek.kode_rahasia);
    const url = 'http://localhost:8080/rekening';
    const res = await this.http.post(url, dataTbhRek, httpOptions).toPromise();
    return <any>res;
  }
}
