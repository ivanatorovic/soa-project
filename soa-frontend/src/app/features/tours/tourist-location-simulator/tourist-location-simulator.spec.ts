import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TouristLocationSimulator } from './tourist-location-simulator';

describe('TouristLocationSimulator', () => {
  let component: TouristLocationSimulator;
  let fixture: ComponentFixture<TouristLocationSimulator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TouristLocationSimulator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TouristLocationSimulator);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
