# Instructions d'installation

Décompressez l'archive ZIP ou TAR fournie.

## Base de données

Le fichier `db.sql` contient les instructions de création des tables sur une base de données PostgreSQL et peut être directement exécuté sur la base souhaitée.

## Web

1. Déployez le fichier WAR sur le serveur.
2. Dans le fichier `<contexte>/WEB-INF/web.xml`, configurez les quatre `context-param` utilisés pour la connexion à la base de données :
  * `jdbc-url` : URL de connexion JDBC à la base de données ;
  * `jdbc-driver` : Driver JDBC à utiliser pour la connexion à la base de données ;
  * `jdbc-username` : Nom d'utilisateur utilisé pour se connecter à la base ;
  * `jdbc-password` : Mot de passe utilisé pour la connexion à la base.
3. Rechargez le contexte.
4. Accédez à la racine du contexte dans un navigateur web.
