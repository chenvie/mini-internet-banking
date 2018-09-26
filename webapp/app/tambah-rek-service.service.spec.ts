import { TestBed, inject } from '@angular/core/testing';

import { TambahRekServiceService } from './tambah-rek-service.service';

describe('TambahRekServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TambahRekServiceService]
    });
  });

  it('should be created', inject([TambahRekServiceService], (service: TambahRekServiceService) => {
    expect(service).toBeTruthy();
  }));
});
