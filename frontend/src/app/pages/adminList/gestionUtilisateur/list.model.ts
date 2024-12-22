export interface GestionUtilisateur {
  status: string;
  id?:number;
  cin?:number;
  role: string;
  firstname?: string;
  lastname?: string;
  phone?: string;
  email?: string;
  password?:string;
  isEnabled?: boolean;
  type?: any;
  type_color?: any;
  status_color?: any;
  agenceId?: number; 

}
