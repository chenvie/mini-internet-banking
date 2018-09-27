import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginScreenComponent} from './login-screen/login-screen.component';
import {MainScreenComponent} from './main-screen/main-screen.component';
import {InfoSaldoComponent} from './info-saldo/info-saldo.component';
import {TransferComponent} from './transfer/transfer.component';
import {HistoriComponent} from './histori/histori.component';
import {MutasiRekComponent} from './mutasi-rek/mutasi-rek.component';
import {PembelianComponent} from './pembelian/pembelian.component';
import {SettingComponent} from './setting/setting.component';
import {TambahRekeningComponent} from './tambah-rekening/tambah-rekening.component';

export const routes: Routes = [
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: 'login', component: LoginScreenComponent},
  {
    path: 'main', component: MainScreenComponent, children: [
      {path: '', redirectTo: '/main/info-saldo', pathMatch: 'full'},
      {path: 'info-saldo', component: InfoSaldoComponent},
      {path: 'transfer', component: TransferComponent},
      {path: 'mutasi-rek', component: MutasiRekComponent},
      {path: 'histori', component: HistoriComponent},
      {path: 'pembelian', component: PembelianComponent},
      {path: 'setting', component: SettingComponent},
      {path: 'tambah-rek', component: TambahRekeningComponent},
    ]
  }
];

@NgModule({
  exports: [RouterModule],
  imports: [RouterModule.forRoot(routes)]
})

export class AppRoutingModule {
}
