import { Component, OnInit, Input } from '@angular/core';
// import { ActivatedRoute, Router } from '@angular/router';
import { PembelianService } from '../pembelian.service';
import { InputValidatorService } from '../input-validator.service';

@Component({
  selector: 'app-pembelian',
  templateUrl: './pembelian.component.html',
  styleUrls: ['./pembelian.component.css']
})
export class PembelianComponent implements OnInit {

  page = 1;
  dataBeli = {
    username: null,
    no_hp_tujuan: null,
    id_nasabah: null,
    provider: null,
    kode_rahasia: null,
    nominal: null
  };
  keterangan: string;
  status: boolean;
  txtStatus: string;
  isFormValid = false;
  isKodeValid = false;

  @Input() username: string;
  @Input() id_nasabah: string;

  constructor(
    private beli: PembelianService,
    private validator: InputValidatorService,
    /* private route: ActivatedRoute */) { }

  ngOnInit() {
    // this.route.data.subscribe();
    this.page = 1;
    this.dataBeli.username = this.username;
    this.dataBeli.id_nasabah = this.id_nasabah;
  }

  nextPage(): void {
    if (!this.validator.validatePembelian(this.dataBeli)) {
      this.isFormValid = false;
      return;
    }
    this.isFormValid = true;
    this.page = 2;
  }

  submitPulsa(): void {
    if (!this.validator.validateKode(this.dataBeli.kode_rahasia)) {
      this.isKodeValid = false;
      return;
    }
    this.beli.buyPulsa(this.dataBeli).subscribe((data: any) => {
      this.keterangan = data['message'];
      this.status = data['transfer'];
      this.txtStatus = this.status ? 'Berhasil' : 'Gagal';
    });
    this.page = 3;
  }

}
