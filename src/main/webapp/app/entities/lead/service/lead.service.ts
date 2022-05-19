import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILead, getLeadIdentifier } from '../lead.model';

export type EntityResponseType = HttpResponse<ILead>;
export type EntityArrayResponseType = HttpResponse<ILead[]>;

@Injectable({ providedIn: 'root' })
export class LeadService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/leads');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(lead: ILead): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lead);
    return this.http
      .post<ILead>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(lead: ILead): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lead);
    return this.http
      .put<ILead>(`${this.resourceUrl}/${getLeadIdentifier(lead) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(lead: ILead): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(lead);
    return this.http
      .patch<ILead>(`${this.resourceUrl}/${getLeadIdentifier(lead) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ILead>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ILead[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLeadToCollectionIfMissing(leadCollection: ILead[], ...leadsToCheck: (ILead | null | undefined)[]): ILead[] {
    const leads: ILead[] = leadsToCheck.filter(isPresent);
    if (leads.length > 0) {
      const leadCollectionIdentifiers = leadCollection.map(leadItem => getLeadIdentifier(leadItem)!);
      const leadsToAdd = leads.filter(leadItem => {
        const leadIdentifier = getLeadIdentifier(leadItem);
        if (leadIdentifier == null || leadCollectionIdentifiers.includes(leadIdentifier)) {
          return false;
        }
        leadCollectionIdentifiers.push(leadIdentifier);
        return true;
      });
      return [...leadsToAdd, ...leadCollection];
    }
    return leadCollection;
  }

  protected convertDateFromClient(lead: ILead): ILead {
    return Object.assign({}, lead, {
      dataNascimento: lead.dataNascimento?.isValid() ? lead.dataNascimento.format(DATE_FORMAT) : undefined,
      dataCadastro: lead.dataCadastro?.isValid() ? lead.dataCadastro.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.dataNascimento = res.body.dataNascimento ? dayjs(res.body.dataNascimento) : undefined;
      res.body.dataCadastro = res.body.dataCadastro ? dayjs(res.body.dataCadastro) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((lead: ILead) => {
        lead.dataNascimento = lead.dataNascimento ? dayjs(lead.dataNascimento) : undefined;
        lead.dataCadastro = lead.dataCadastro ? dayjs(lead.dataCadastro) : undefined;
      });
    }
    return res;
  }
}
