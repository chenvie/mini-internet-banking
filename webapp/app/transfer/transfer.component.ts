import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-transfer',
  templateUrl: './transfer.component.html',
  styleUrls: ['./transfer.component.css']
})
export class TransferComponent implements OnInit {

  @Input() page: number;
  changeLog: string[] = [];

  constructor() { }

  ngOnInit(): void {
    this.page = 1;
  }

}
