import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddKeyPoint } from './add-key-point';

describe('AddKeyPoint', () => {
  let component: AddKeyPoint;
  let fixture: ComponentFixture<AddKeyPoint>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddKeyPoint]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddKeyPoint);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
