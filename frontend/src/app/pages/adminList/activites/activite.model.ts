import { Poste } from "../poste/poste.model";

export interface Activite {
  id?: number;
  name: string;
  description?: string;
  posteIds?: number[]; 
}

