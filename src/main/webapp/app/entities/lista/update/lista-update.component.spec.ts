import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ListaService } from '../service/lista.service';
import { ILista, Lista } from '../lista.model';

import { ListaUpdateComponent } from './lista-update.component';

describe('Lista Management Update Component', () => {
  let comp: ListaUpdateComponent;
  let fixture: ComponentFixture<ListaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let listaService: ListaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ListaUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ListaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ListaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    listaService = TestBed.inject(ListaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const lista: ILista = { id: 456 };

      activatedRoute.data = of({ lista });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(lista));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lista>>();
      const lista = { id: 123 };
      jest.spyOn(listaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lista });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lista }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(listaService.update).toHaveBeenCalledWith(lista);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lista>>();
      const lista = new Lista();
      jest.spyOn(listaService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lista });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lista }));
      saveSubject.complete();

      // THEN
      expect(listaService.create).toHaveBeenCalledWith(lista);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lista>>();
      const lista = { id: 123 };
      jest.spyOn(listaService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lista });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(listaService.update).toHaveBeenCalledWith(lista);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
