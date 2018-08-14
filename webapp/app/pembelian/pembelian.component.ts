import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-pembelian',
  templateUrl: './pembelian.component.html',
  styleUrls: ['./pembelian.component.css']
})
export class PembelianComponent implements OnInit {

  page = 1;

  constructor() { }

  ngOnInit() {
    this.page = 1;
  }

}
