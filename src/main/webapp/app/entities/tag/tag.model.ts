import { ILead } from 'app/entities/lead/lead.model';

export interface ITag {
  id?: number;
  nomeTag?: string | null;
  leads?: ILead[] | null;
}

export class Tag implements ITag {
  constructor(public id?: number, public nomeTag?: string | null, public leads?: ILead[] | null) {}
}

export function getTagIdentifier(tag: ITag): number | undefined {
  return tag.id;
}
