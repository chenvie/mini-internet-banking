import { Component, OnInit} from '@angular/core';
import { LoginService } from '../login.service';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-main-screen',
  templateUrl: './main-screen.component.html',
  styleUrls: ['./main-screen.component.css']
})
export class MainScreenComponent implements OnInit {

  page = 1;

  username: string;

  constructor(
    private loginService: LoginService,
    private router: Router,
    private activatedRoute: ActivatedRoute) { }

  ngOnInit() {
  }

  route(page: number): void {
    switch (page) {
      case 1: this.router.navigate(['/'], {relativeTo: this.activatedRoute});
        break;
      case 2: this.router.navigate(['mutasi-rek']);
        break;
      case 3: this.router.navigate(['transfer']);
        break;
      case 4: this.router.navigate(['pembelian']);
        break;
      case 5: this.router.navigate(['histori']);
        break;
      case 6: this.router.navigate(['setting']);
        break;
      default:
        break;
    }
  }
}
