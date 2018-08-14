import { Component, OnInit } from '@angular/core';
import { InputValidatorService } from '../input-validator.service';

@Component({
  selector: 'app-histori',
  templateUrl: './histori.component.html',
  styleUrls: ['./histori.component.css']
})
export class HistoriComponent implements OnInit {

  page = 1;
  dariTanggal: any;
  hinggaTanggal: any;
  isRangeValid = false;

  constructor(private validator: InputValidatorService) { }

  ngOnInit() {
    this.page = 1;
  }

  validateRangeTanggal(): void {
    this.isRangeValid = this.validator.validateRangeTanggal(this.dariTanggal, this.hinggaTanggal);
    if (this.isRangeValid) {
      // TODO get history from API if all valid
      alert('ok ' + this.dariTanggal + ' ' + this.hinggaTanggal);
    }
  }

}
