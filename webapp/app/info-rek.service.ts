import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class InfoRekService {

  constructor() { }

  getNorek(): string {
    // TODO ambil norek user
    return null;
  }

  getSaldo(): number {
    // TODO ambil saldo dari DB
    return null;
  }

  getTransaksi(dariTanggal: any, hinggaTanggal: any, tipe?: any): any {
    // TODO ambil transaksi dari DB
    // TODO buat class transaksi (?)s
  }
}
