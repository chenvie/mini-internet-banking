import { Component, OnInit, Input } from '@angular/core';
import { InfoRekService } from '../info-rek.service';

@Component({
  selector: 'app-info-saldo',
  templateUrl: './info-saldo.component.html',
  styleUrls: ['./info-saldo.component.css']
})

export class InfoSaldoComponent implements OnInit {

  saldo = {
    norek: null,
    saldo: null
  };

  constructor(private info: InfoRekService) { }

  ngOnInit() {
    this.getSaldo();
  }

  getSaldo(): void {
    this.info.getSaldo(2).subscribe((data: any) => this.saldo = {
      norek: data['no rekening'],
      saldo: data['saldo']
    });
  }
}
