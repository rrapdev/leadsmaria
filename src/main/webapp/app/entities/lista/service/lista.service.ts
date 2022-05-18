import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILista, getListaIdentifier } from '../lista.model';

export type EntityResponseType = HttpResponse<ILista>;
export type EntityArrayResponseType = HttpResponse<ILista[]>;

@Injectable({ providedIn: 'root' })
export class ListaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/listas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(lista: ILista): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lista);
    return this.http
      .post<ILista>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(lista: ILista): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lista);
    return this.http
      .put<ILista>(`${this.resourceUrl}/${getListaIdentifier(lista) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(lista: ILista): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lista);
    return this.http
      .patch<ILista>(`${this.resourceUrl}/${getListaIdentifier(lista) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILista>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILista[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addListaToCollectionIfMissing(listaCollection: ILista[], ...listasToCheck: (ILista | null | undefined)[]): ILista[] {
    const listas: ILista[] = listasToCheck.filter(isPresent);
    if (listas.length > 0) {
      const listaCollectionIdentifiers = listaCollection.map(listaItem => getListaIdentifier(listaItem)!);
      const listasToAdd = listas.filter(listaItem => {
        const listaIdentifier = getListaIdentifier(listaItem);
        if (listaIdentifier == null || listaCollectionIdentifiers.includes(listaIdentifier)) {
          return false;
        }
        listaCollectionIdentifiers.push(listaIdentifier);
        return true;
      });
      return [...listasToAdd, ...listaCollection];
    }
    return listaCollection;
  }

  protected convertDateFromClient(lista: ILista): ILista {
    return Object.assign({}, lista, {
      dataCadastro: lista.dataCadastro?.isValid() ? lista.dataCadastro.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dataCadastro = res.body.dataCadastro ? dayjs(res.body.dataCadastro) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((lista: ILista) => {
        lista.dataCadastro = lista.dataCadastro ? dayjs(lista.dataCadastro) : undefined;
      });
    }
    return res;
  }
}
