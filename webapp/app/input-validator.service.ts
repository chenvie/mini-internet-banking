import { Injectable } from '@angular/core';
import * as moment from 'moment';

@Injectable({
  providedIn: 'root'
})
export class InputValidatorService {

  constructor() { }

  validatePassword(pass: String): boolean {
    if (pass === undefined) { return false; }
    if (pass.length < 8) { return false; }
    const regex = /\W/g;
    const result = pass.match(regex);
    if (result !== null) { return false; }
    return true;
  }

  validateKode(kode: String): boolean {
    if (kode === undefined) { return false; }
    if (kode.length < 6) { return false; }
    const regex = /\W/g;
    const result = kode.match(regex);
    if (result !== null) { return false; }
    return true;
  }

  validateTanggal(tanggal: string): boolean {
    if (tanggal === undefined) { return false; }
    if (moment().year() - moment(tanggal).year() < 17) { return false; }
    return true;
  }

  validateRangeTanggal(dariTanggal: any, hinggaTanggal: any): boolean {
    if (dariTanggal === undefined || hinggaTanggal === undefined) { return false; }
    if (dariTanggal === '' || hinggaTanggal === '') { return false; }
    if (moment(dariTanggal) > moment() || moment(hinggaTanggal) > moment()) { return false; }
    if (moment(dariTanggal) > moment(hinggaTanggal)) { return false; }
    if (moment(dariTanggal) < moment().subtract(30, 'd')) { return false; }
    return true;
  }
}
