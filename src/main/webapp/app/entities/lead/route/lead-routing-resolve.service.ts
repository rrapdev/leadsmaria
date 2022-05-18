import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILead, Lead } from '../lead.model';
import { LeadService } from '../service/lead.service';

@Injectable({ providedIn: 'root' })
export class LeadRoutingResolveService implements Resolve<ILead> {
  constructor(protected service: LeadService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILead> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((lead: HttpResponse<Lead>) => {
          if (lead.body) {
            return of(lead.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Lead());
  }
}
