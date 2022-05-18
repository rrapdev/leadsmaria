import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ILead, Lead } from '../lead.model';

import { LeadService } from './lead.service';

describe('Lead Service', () => {
  let service: LeadService;
  let httpMock: HttpTestingController;
  let elemDefault: ILead;
  let expectedResult: ILead | ILead[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LeadService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      nomeLead: 'AAAAAAA',
      telefone: 'AAAAAAA',
      email: 'AAAAAAA',
      dataNascimento: currentDate,
      dataCadastro: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          dataNascimento: currentDate.format(DATE_FORMAT),
          dataCadastro: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Lead', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          dataNascimento: currentDate.format(DATE_FORMAT),
          dataCadastro: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataNascimento: currentDate,
          dataCadastro: currentDate,
        },
        returnedFromService
      );

      service.create(new Lead()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Lead', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeLead: 'BBBBBB',
          telefone: 'BBBBBB',
          email: 'BBBBBB',
          dataNascimento: currentDate.format(DATE_FORMAT),
          dataCadastro: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataNascimento: currentDate,
          dataCadastro: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Lead', () => {
      const patchObject = Object.assign(
        {
          nomeLead: 'BBBBBB',
        },
        new Lead()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          dataNascimento: currentDate,
          dataCadastro: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Lead', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nomeLead: 'BBBBBB',
          telefone: 'BBBBBB',
          email: 'BBBBBB',
          dataNascimento: currentDate.format(DATE_FORMAT),
          dataCadastro: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          dataNascimento: currentDate,
          dataCadastro: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Lead', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addLeadToCollectionIfMissing', () => {
      it('should add a Lead to an empty array', () => {
        const lead: ILead = { id: 123 };
        expectedResult = service.addLeadToCollectionIfMissing([], lead);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(lead);
      });

      it('should not add a Lead to an array that contains it', () => {
        const lead: ILead = { id: 123 };
        const leadCollection: ILead[] = [
          {
            ...lead,
          },
          { id: 456 },
        ];
        expectedResult = service.addLeadToCollectionIfMissing(leadCollection, lead);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Lead to an array that doesn't contain it", () => {
        const lead: ILead = { id: 123 };
        const leadCollection: ILead[] = [{ id: 456 }];
        expectedResult = service.addLeadToCollectionIfMissing(leadCollection, lead);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(lead);
      });

      it('should add only unique Lead to an array', () => {
        const leadArray: ILead[] = [{ id: 123 }, { id: 456 }, { id: 63848 }];
        const leadCollection: ILead[] = [{ id: 123 }];
        expectedResult = service.addLeadToCollectionIfMissing(leadCollection, ...leadArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const lead: ILead = { id: 123 };
        const lead2: ILead = { id: 456 };
        expectedResult = service.addLeadToCollectionIfMissing([], lead, lead2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(lead);
        expect(expectedResult).toContain(lead2);
      });

      it('should accept null and undefined values', () => {
        const lead: ILead = { id: 123 };
        expectedResult = service.addLeadToCollectionIfMissing([], null, lead, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(lead);
      });

      it('should return initial array if no Lead is added', () => {
        const leadCollection: ILead[] = [{ id: 123 }];
        expectedResult = service.addLeadToCollectionIfMissing(leadCollection, undefined, null);
        expect(expectedResult).toEqual(leadCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
