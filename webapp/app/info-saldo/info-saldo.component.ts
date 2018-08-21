import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-info-saldo',
  templateUrl: './info-saldo.component.html',
  styleUrls: ['./info-saldo.component.css']
})

export class InfoSaldoComponent implements OnInit {

  @Input() norek: string;
  @Input() saldo: string;

  constructor() { }

  ngOnInit() {
  }
}
