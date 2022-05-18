import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'lead',
        data: { pageTitle: 'leadsMariaApp.lead.home.title' },
        loadChildren: () => import('./lead/lead.module').then(m => m.LeadModule),
      },
      {
        path: 'lista',
        data: { pageTitle: 'leadsMariaApp.lista.home.title' },
        loadChildren: () => import('./lista/lista.module').then(m => m.ListaModule),
      },
      {
        path: 'tag',
        data: { pageTitle: 'leadsMariaApp.tag.home.title' },
        loadChildren: () => import('./tag/tag.module').then(m => m.TagModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
