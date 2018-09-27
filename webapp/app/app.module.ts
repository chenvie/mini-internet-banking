import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { LoginScreenComponent } from './login-screen/login-screen.component';
import { RegisterComponent } from './register/register.component';
import { MainScreenComponent } from './main-screen/main-screen.component';
import { InfoSaldoComponent } from './info-saldo/info-saldo.component';
import { MutasiRekComponent } from './mutasi-rek/mutasi-rek.component';
import { TransferComponent } from './transfer/transfer.component';
import { PembelianComponent } from './pembelian/pembelian.component';
import { HistoriComponent } from './histori/histori.component';
import { SettingComponent } from './setting/setting.component';
import { AppRoutingModule } from './/app-routing.module';
import { LoggerModule, NgxLoggerLevel } from 'ngx-logger';
import { TambahRekeningComponent } from './tambah-rekening/tambah-rekening.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginScreenComponent,
    RegisterComponent,
    MainScreenComponent,
    InfoSaldoComponent,
    MutasiRekComponent,
    TransferComponent,
    PembelianComponent,
    HistoriComponent,
    SettingComponent,
    TambahRekeningComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule,
    LoggerModule.forRoot({
      level: NgxLoggerLevel.DEBUG,
      serverLoggingUrl: 'http://localhost/api/logger/log.php',
      serverLogLevel: NgxLoggerLevel.DEBUG
    })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
