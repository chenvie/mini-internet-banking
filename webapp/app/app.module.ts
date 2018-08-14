import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

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
    SettingComponent
  ],
  imports: [
    BrowserModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
