# Étape 1 : Construction de l'application Angular
FROM node:18 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers package.json et package-lock.json pour installer les dépendances
COPY package*.json ./

# Configurer le cache pour npm dans le dossier /app/.npm-cache
RUN npm config set cache /app/.npm-cache --global

# Mettre à jour npm et installer les dépendances avec un délai d'attente prolongé et des tentatives de reconnection
RUN npm install -g npm@10.9.0 \
    && npm install --legacy-peer-deps --force --fetch-timeout=60000 --network-timeout=60000 || \
    (sleep 5 && npm install --legacy-peer-deps --force --fetch-timeout=60000 --network-timeout=60000)

# Copier tous les fichiers sources de l'application
COPY . .

# Construire l'application Angular pour la production
RUN npm run build --prod

# Étape 2 : Créer l'image finale avec Nginx pour servir l'application
FROM nginx:alpine

# Copier les fichiers générés par la construction de l'application Angular
COPY --from=build /app/dist/ /usr/share/nginx/html

# Exposer le port 80 pour Nginx
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
