import { Activite } from "../activites/activite.model";

export interface Poste {
  id?: number;
  name: string;
  description?: string;
  activiteIds?: number[];  // Liste des IDs des activités liées
}
