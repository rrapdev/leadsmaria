import dayjs from 'dayjs/esm';
import { ILead } from 'app/entities/lead/lead.model';

export interface ILista {
  id?: number;
  nomeLista?: string | null;
  dataCadastro?: dayjs.Dayjs | null;
  leads?: ILead[] | null;
}

export class Lista implements ILista {
  constructor(
    public id?: number,
    public nomeLista?: string | null,
    public dataCadastro?: dayjs.Dayjs | null,
    public leads?: ILead[] | null
  ) {}
}

export function getListaIdentifier(lista: ILista): number | undefined {
  return lista.id;
}
