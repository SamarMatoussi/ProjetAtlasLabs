export interface UserProfile {
  id: number;
  password?: string; // 
  firstname?: string;
  lastname?: string;
  token?: string; // mieux géré en dehors de l'interface
  email: string;
  cin: string; 
  isEnabled: boolean;
  phone: string;
  role: string;
}
