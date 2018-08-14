import { Component, OnInit } from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-mutasi-rek',
  templateUrl: './mutasi-rek.component.html',
  styleUrls: ['./mutasi-rek.component.css']
})
export class MutasiRekComponent implements OnInit {

  curDate: any;
  fromDate: any;

  constructor() { }

  ngOnInit() {
    moment.locale('id');
    this.curDate = moment().format('LL');
    this.fromDate = moment().subtract(7, 'd').format('LL');
  }

}
