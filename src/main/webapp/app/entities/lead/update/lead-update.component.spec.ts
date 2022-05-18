import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LeadService } from '../service/lead.service';
import { ILead, Lead } from '../lead.model';
import { ITag } from 'app/entities/tag/tag.model';
import { TagService } from 'app/entities/tag/service/tag.service';
import { ILista } from 'app/entities/lista/lista.model';
import { ListaService } from 'app/entities/lista/service/lista.service';

import { LeadUpdateComponent } from './lead-update.component';

describe('Lead Management Update Component', () => {
  let comp: LeadUpdateComponent;
  let fixture: ComponentFixture<LeadUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let leadService: LeadService;
  let tagService: TagService;
  let listaService: ListaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LeadUpdateComponent],
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
      .overrideTemplate(LeadUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LeadUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    leadService = TestBed.inject(LeadService);
    tagService = TestBed.inject(TagService);
    listaService = TestBed.inject(ListaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tag query and add missing value', () => {
      const lead: ILead = { id: 456 };
      const tags: ITag[] = [{ id: 48755 }];
      lead.tags = tags;

      const tagCollection: ITag[] = [{ id: 72109 }];
      jest.spyOn(tagService, 'query').mockReturnValue(of(new HttpResponse({ body: tagCollection })));
      const additionalTags = [...tags];
      const expectedCollection: ITag[] = [...additionalTags, ...tagCollection];
      jest.spyOn(tagService, 'addTagToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lead });
      comp.ngOnInit();

      expect(tagService.query).toHaveBeenCalled();
      expect(tagService.addTagToCollectionIfMissing).toHaveBeenCalledWith(tagCollection, ...additionalTags);
      expect(comp.tagsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Lista query and add missing value', () => {
      const lead: ILead = { id: 456 };
      const listas: ILista[] = [{ id: 24633 }];
      lead.listas = listas;

      const listaCollection: ILista[] = [{ id: 29489 }];
      jest.spyOn(listaService, 'query').mockReturnValue(of(new HttpResponse({ body: listaCollection })));
      const additionalListas = [...listas];
      const expectedCollection: ILista[] = [...additionalListas, ...listaCollection];
      jest.spyOn(listaService, 'addListaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lead });
      comp.ngOnInit();

      expect(listaService.query).toHaveBeenCalled();
      expect(listaService.addListaToCollectionIfMissing).toHaveBeenCalledWith(listaCollection, ...additionalListas);
      expect(comp.listasSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const lead: ILead = { id: 456 };
      const tags: ITag = { id: 46015 };
      lead.tags = [tags];
      const listas: ILista = { id: 99041 };
      lead.listas = [listas];

      activatedRoute.data = of({ lead });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(lead));
      expect(comp.tagsSharedCollection).toContain(tags);
      expect(comp.listasSharedCollection).toContain(listas);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lead>>();
      const lead = { id: 123 };
      jest.spyOn(leadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lead });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lead }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(leadService.update).toHaveBeenCalledWith(lead);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lead>>();
      const lead = new Lead();
      jest.spyOn(leadService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lead });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: lead }));
      saveSubject.complete();

      // THEN
      expect(leadService.create).toHaveBeenCalledWith(lead);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Lead>>();
      const lead = { id: 123 };
      jest.spyOn(leadService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lead });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(leadService.update).toHaveBeenCalledWith(lead);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTagById', () => {
      it('Should return tracked Tag primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTagById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackListaById', () => {
      it('Should return tracked Lista primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackListaById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedTag', () => {
      it('Should return option if no Tag is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedTag(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Tag for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedTag(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Tag is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedTag(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });

    describe('getSelectedLista', () => {
      it('Should return option if no Lista is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedLista(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Lista for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedLista(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Lista is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedLista(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
