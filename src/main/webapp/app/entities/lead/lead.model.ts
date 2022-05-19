import dayjs from 'dayjs/esm';
import { ITag } from 'app/entities/tag/tag.model';
import { ILista } from 'app/entities/lista/lista.model';

export interface ILead {
  id?: number;
  nomeLead?: string | null;
  telefone?: string | null;
  email?: string | null;
  dataNascimento?: dayjs.Dayjs | null;
  dataCadastro?: dayjs.Dayjs | null;
  tags?: ITag[] | null;
  listas?: ILista[] | null;
}

export class Lead implements ILead {
  constructor(
    public id?: number,
    public nomeLead?: string | null,
    public telefone?: string | null,
    public email?: string | null,
    public dataNascimento?: dayjs.Dayjs | null,
    public dataCadastro?: dayjs.Dayjs | null,
    public tags?: ITag[] | null,
    public listas?: ILista[] | null
  ) {}
}

export function getLeadIdentifier(lead: ILead): number | undefined {
  return lead.id;
}
