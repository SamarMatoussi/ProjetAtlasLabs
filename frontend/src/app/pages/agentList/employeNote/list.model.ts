export interface employe {
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
  posteId?: number; 
  status_color?: any;
}
