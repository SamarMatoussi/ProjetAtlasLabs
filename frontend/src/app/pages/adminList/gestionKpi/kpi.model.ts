import { Activite } from "../activites/activite.model";

// Représente une table de KPI avec ses détails
export interface TableKpi {
    id?: number; 
    nameKpi?: string;
    label: string; 
    description: string; 
    activiteId?: number; 
    activite?: Activite; 
    parametrages?: ParametrageKpi[]; 
    type?: KpiType; 
}

// Représente un paramétrage de KPI
export interface ParametrageKpi {
    id?: number; 
    name?: string; 
    description?: string; 
    appreciation?: string; 
    min?: number; 
    max?: number; 
    utilite?: string; 
    kpiId?: number; 
}
export interface KpiWithParametrage {
    kpi: TableKpi;
    parametrages: ParametrageKpi[];
    
}

// Enum pour les types de KPI
export enum KpiType {
    NUMERIQUE = 'NUMERIQUE', 
    STRING = 'STRING' 
}

// Représente le résultat de recherche pour les tables de KPI
export interface SearchResult {
    tables: TableKpi[]; // Liste des tables de KPI
    total: number; // Total des résultats
}
