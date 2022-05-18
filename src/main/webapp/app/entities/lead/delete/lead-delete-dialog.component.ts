import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILead } from '../lead.model';
import { LeadService } from '../service/lead.service';

@Component({
  templateUrl: './lead-delete-dialog.component.html',
})
export class LeadDeleteDialogComponent {
  lead?: ILead;

  constructor(protected leadService: LeadService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.leadService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
