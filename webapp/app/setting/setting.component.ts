import { Component, OnInit } from '@angular/core';
import { InputValidatorService } from '../input-validator.service';

@Component({
  selector: 'app-setting',
  templateUrl: './setting.component.html',
  styleUrls: ['./setting.component.css']
})
export class SettingComponent implements OnInit {

  passBaru: string;
  kodeBaru: string;
  isPassBaruValid = false;
  isKodeBaruValid = false;
  isPassSama = false;
  isKodeSama = false;

  constructor(private validator: InputValidatorService) { }

  ngOnInit() {
  }

  validatePassword(): void {
    this.isPassBaruValid = this.validator.validatePassword(this.passBaru);
    // TODO go to API, check old pass, check pass retype
  }

  validateKode(): void {
    this.isKodeBaruValid = this.validator.validateKode(this.kodeBaru);
    // go to API, check old pass, check kode retype
  }
}

