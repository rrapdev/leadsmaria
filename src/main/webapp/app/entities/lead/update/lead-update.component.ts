import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ILead, Lead } from '../lead.model';
import { LeadService } from '../service/lead.service';
import { ITag } from 'app/entities/tag/tag.model';
import { TagService } from 'app/entities/tag/service/tag.service';
import { ILista } from 'app/entities/lista/lista.model';
import { ListaService } from 'app/entities/lista/service/lista.service';

@Component({
  selector: 'jhi-lead-update',
  templateUrl: './lead-update.component.html',
})
export class LeadUpdateComponent implements OnInit {
  isSaving = false;

  tagsSharedCollection: ITag[] = [];
  listasSharedCollection: ILista[] = [];

  editForm = this.fb.group({
    id: [],
    nomeLead: [],
    telefone: [],
    email: [],
    dataNascimento: [],
    dataCadastro: [],
    tags: [],
    listas: [],
  });

  constructor(
    protected leadService: LeadService,
    protected tagService: TagService,
    protected listaService: ListaService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lead }) => {
      this.updateForm(lead);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lead = this.createFromForm();
    if (lead.id !== undefined) {
      this.subscribeToSaveResponse(this.leadService.update(lead));
    } else {
      this.subscribeToSaveResponse(this.leadService.create(lead));
    }
  }

  trackTagById(_index: number, item: ITag): number {
    return item.id!;
  }

  trackListaById(_index: number, item: ILista): number {
    return item.id!;
  }

  getSelectedTag(option: ITag, selectedVals?: ITag[]): ITag {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  getSelectedLista(option: ILista, selectedVals?: ILista[]): ILista {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILead>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(lead: ILead): void {
    this.editForm.patchValue({
      id: lead.id,
      nomeLead: lead.nomeLead,
      telefone: lead.telefone,
      email: lead.email,
      dataNascimento: lead.dataNascimento,
      dataCadastro: lead.dataCadastro,
      tags: lead.tags,
      listas: lead.listas,
    });

    this.tagsSharedCollection = this.tagService.addTagToCollectionIfMissing(this.tagsSharedCollection, ...(lead.tags ?? []));
    this.listasSharedCollection = this.listaService.addListaToCollectionIfMissing(this.listasSharedCollection, ...(lead.listas ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.tagService
      .query()
      .pipe(map((res: HttpResponse<ITag[]>) => res.body ?? []))
      .pipe(map((tags: ITag[]) => this.tagService.addTagToCollectionIfMissing(tags, ...(this.editForm.get('tags')!.value ?? []))))
      .subscribe((tags: ITag[]) => (this.tagsSharedCollection = tags));

    this.listaService
      .query()
      .pipe(map((res: HttpResponse<ILista[]>) => res.body ?? []))
      .pipe(
        map((listas: ILista[]) => this.listaService.addListaToCollectionIfMissing(listas, ...(this.editForm.get('listas')!.value ?? [])))
      )
      .subscribe((listas: ILista[]) => (this.listasSharedCollection = listas));
  }

  protected createFromForm(): ILead {
    return {
      ...new Lead(),
      id: this.editForm.get(['id'])!.value,
      nomeLead: this.editForm.get(['nomeLead'])!.value,
      telefone: this.editForm.get(['telefone'])!.value,
      email: this.editForm.get(['email'])!.value,
      dataNascimento: this.editForm.get(['dataNascimento'])!.value,
      dataCadastro: this.editForm.get(['dataCadastro'])!.value,
      tags: this.editForm.get(['tags'])!.value,
      listas: this.editForm.get(['listas'])!.value,
    };
  }
}
