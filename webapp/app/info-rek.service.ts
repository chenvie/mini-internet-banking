import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root'
})
export class InfoRekService {

  constructor(
    private http: HttpClient,
    private login: LoginService) { }

  getSaldo(id: number): Observable<any> {
    const url = 'http://localhost/api/nasabah/read-one-saldo.php?id=' + id;
    return this.http.get<any>(url);
  }

  getTransaksi(dariTanggal: any, hinggaTanggal: any, tipe?: any): any {
    // TODO ambil transaksi dari DB
    // TODO buat class transaksi (?)s
  }
}
