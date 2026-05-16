import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveTour } from './active-tour';

describe('ActiveTour', () => {
  let component: ActiveTour;
  let fixture: ComponentFixture<ActiveTour>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActiveTour]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActiveTour);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
