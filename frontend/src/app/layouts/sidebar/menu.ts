import { MenuItem } from './menu.model';

export const MENUADMIN: MenuItem[] = [
    {
        id: 2,
        label: 'MENUITEMS.DASHBOARDS.TEXT',
        icon: 'bx-home-circle',
        subItems: []
    },
    {
        id: 58,
        label: "Gestion des Agents",
        icon: "bx bx-id-card",
        link: "/adminList/gestionAgent",
        parentId: 57
    },
    {
        id: 59,
        label: 'Gestion des Agences',
        icon: 'bx bx-building-house',
        link: '/adminList/listAgence',
        parentId: 57
    },
    {
        id: 60,
        label: 'Gestion des Activités',
        icon: 'bx bx-edit',
        link: '/adminList/activites',
        parentId: 57
    },
    {
        id: 62, // Updated ID
        label: 'Gestion des Postes',
        icon: 'bx bx-edit',
        link: '/adminList/poste',
        parentId: 126 
    },
    {
        id: 65,
        label: 'Évaluation employés',
        icon: 'bx bx-checkbox-checked',
        link: '/adminList/EvaluerEmployés',
        parentId: 57
    },
    {
        id: 63,
        label: 'Suivre les évaluations',
        icon: 'bx bx-archive-in',
        link: '/adminList/UploadsComponent',
        parentId: 57
    },
    

];


export const MENUAGENT: MenuItem[] = [
   
    {
        id: 2,
        label: 'MENUITEMS.DASHBOARDS.TEXT',
        icon: 'bx-home-circle',
        subItems: [
           
        ]
    },
            {
                id: 64,
                label: 'Gestion employe',
                icon: 'bx bx-id-card',
                link: '/agentList/employe',
                parentId: 57
            },
            {
                id: 65,
                label: 'Évaluation employés',
                icon: 'bx bx-checkbox-checked',
                link: '/agentList/Notation',
                parentId: 57
            },
           
          
];

export const MENUEMPLOYE: MenuItem[] = [
   
    {
        id: 2,
        label: 'MENUITEMS.DASHBOARDS.TEXT',
        icon: 'bx-home-circle',
        subItems: [
           
        ]
    },
    {
        id: 65,
        label: 'Évaluation employés',
        icon: 'bx bx-checkbox-checked',
        link: '/employe/notes',
        parentId: 57
    },
       
    
           
];