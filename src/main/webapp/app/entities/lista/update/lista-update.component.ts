import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ILista, Lista } from '../lista.model';
import { ListaService } from '../service/lista.service';

@Component({
  selector: 'jhi-lista-update',
  templateUrl: './lista-update.component.html',
})
export class ListaUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nomeLista: [],
    dataCadastro: [],
  });

  constructor(protected listaService: ListaService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lista }) => {
      this.updateForm(lista);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const lista = this.createFromForm();
    if (lista.id !== undefined) {
      this.subscribeToSaveResponse(this.listaService.update(lista));
    } else {
      this.subscribeToSaveResponse(this.listaService.create(lista));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILista>>): void {
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

  protected updateForm(lista: ILista): void {
    this.editForm.patchValue({
      id: lista.id,
      nomeLista: lista.nomeLista,
      dataCadastro: lista.dataCadastro,
    });
  }

  protected createFromForm(): ILista {
    return {
      ...new Lista(),
      id: this.editForm.get(['id'])!.value,
      nomeLista: this.editForm.get(['nomeLista'])!.value,
      dataCadastro: this.editForm.get(['dataCadastro'])!.value,
    };
  }
}
