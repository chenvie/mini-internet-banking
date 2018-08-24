import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginService } from './login.service';

@Injectable({
  providedIn: 'root'
})
export class InfoRekService {

  constructor(
    private http: HttpClient,
    private login: LoginService) {}

  async getMutasi() {
    const url = 'http://localhost/api/transaksi/read-mutasi.php';
    const id = this.login.userData.id_nasabah;
    const param = '?id=' + id;
    const res = await this.http.get(url + param).toPromise();
    return <any>res;
  }

  async getHistori(historiData: any) {
    const url = 'http://localhost/api/transaksi/read-history.php';
    const param1 = '?id=' + historiData.id;
    const param2 = '&tgl_awal=' + historiData.dariTanggal;
    const param3 = '&tgl_akhir=' + historiData.hinggaTanggal;
    const res = await this.http.get(url + param1 + param2 + param3).toPromise();
    return <any>res;
  }
}
