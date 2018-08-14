import { TestBed, inject } from '@angular/core/testing';

import { InfoRekService } from './info-rek.service';

describe('InfoRekService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [InfoRekService]
    });
  });

  it('should be created', inject([InfoRekService], (service: InfoRekService) => {
    expect(service).toBeTruthy();
  }));
});
