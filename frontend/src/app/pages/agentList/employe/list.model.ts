import { Poste } from "../../adminList/poste/poste.model";

export interface employe {
  status: string;
  id?:number;
  cin?:number;
  role: string;
  firstname?: string;
  lastname?: string;
  posteId?: number; 
  phone?: string;
  email?: string;
  password?:string;
  isEnabled?: boolean;
  type?: any;
  type_color?: any;
  status_color?: any;
}
