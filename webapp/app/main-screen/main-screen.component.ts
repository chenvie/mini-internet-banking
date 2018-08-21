import { Component, OnInit} from '@angular/core';
import { LoginService } from '../login.service';

@Component({
  selector: 'app-main-screen',
  templateUrl: './main-screen.component.html',
  styleUrls: ['./main-screen.component.css']
})
export class MainScreenComponent implements OnInit {

  page = 1;

  userData = {
    id_nasabah: null,
    username: null,
    password: null,
    nama_lengkap: null,
    kode_rahasia: null,
    tgl_lahir: null,
    jml_saldo: null,
    no_rek: null
  };

  username: string;

  constructor(private loginService: LoginService) { }

  ngOnInit() {
    this.username = 'coba'; // for testing only
    this.getUserData();
  }

  getUserData(): void {
    this.loginService.getUserData(this.username).subscribe((data: any) => this.userData = {
      id_nasabah: data['id'],
      username: data['username'],
      password: data['password'],
      nama_lengkap: data['nama_lengkap'],
      kode_rahasia: data['kode_rahasia'],
      tgl_lahir: data['tgl_lahir'],
      jml_saldo: data['jml_saldo'],
      no_rek: data['no_rek']
    });
  }
}
