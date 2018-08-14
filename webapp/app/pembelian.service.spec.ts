import { TestBed, inject } from '@angular/core/testing';

import { PembelianService } from './pembelian.service';

describe('PembelianService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PembelianService]
    });
  });

  it('should be created', inject([PembelianService], (service: PembelianService) => {
    expect(service).toBeTruthy();
  }));
});
