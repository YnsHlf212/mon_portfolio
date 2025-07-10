
## 🛡️ TP Sécurité Java – *Audit et sécurisation d'une application existante*

### 📅 Durée : 4h

### 🎓 Niveau : BTS SIO SLAM – Fin de 1ère année

---

## 🎯 Objectif

Vous êtes missionné·e par une entreprise fictive pour évaluer la sécurité d’une application de gestion de notes développée en Java.
Votre mission se déroule en **trois étapes** :

---

## 🔍 Étape 1 – Découverte de l'application (1h)

**Lancez et explorez librement l’application fournie.**

Vous devez :

* Identifier ce que l'application permet de faire.
* Comprendre sa structure : quels rôles ? quelles fonctions ? quels fichiers ?
* Arivvez-vous à vous connecter ? Avec quel(s) utilisateur(s)/rôle(s) ?
* Observer ce qui vous paraît curieux, risqué, étrange dans le comportement de l’application.

🧠 **Question de départ** :

> À votre avis, cette application est-elle sécurisée ? Justifiez.

---

## 🔎 Étape 2 – Audit de sécurité (1h30)

**Réalisez un audit technique de sécurité.**

Vous devez :

* Lire le code source (pas tout, mais les parties critiques : connexion, gestion utilisateurs, accès aux notes…)
* Identifier les failles potentielles : mauvaise gestion de l’authentification, accès non restreint, absence de chiffrement, manipulation de données, logs…
* Évaluer le **niveau de risque** de chaque problème détecté (faible / moyen / élevé)
* Référencer les failles avec le guide **OWASP Top 10** ([https://owasp.org/www-project-top-ten/](https://owasp.org/www-project-top-ten/))

🗂️ **Livrable attendu** : un **rapport d’audit de sécurité** clair, structuré, avec **au minimum les informations suivantes** :

* 📌 Liste des failles identifiées, accompagnées du nom du fichier et de la ligne (si pertinent)
* 🔥 Évaluation du niveau de criticité (faible / moyen / élevé) de chaque faille
* 🛠 Proposition de correction ou piste d’amélioration pour chaque faille
* 📚 Référence croisée avec une ou plusieurs règles ou catégories OWASP
* 🧩 Commentaire sur les conséquences potentielles d’une exploitation malveillante

---

## 🔧 Étape 3 – Corrections et rapport d’intervention (1h30)

**Vous choisissez 2 à 3 failles critiques à corriger.**

Pour chaque correction :

* Modifiez le code source de manière propre et durable.
* Testez vos modifications.
* Vérifiez que le problème est bien résolu.

🧾 **Livrable final** : un **rapport d’intervention**, contenant **au minimum les éléments suivants** :

* 🪪 Introduction : rappel du contexte, des failles corrigées, de leur niveau de criticité
* 🛠 Description technique des actions réalisées (fichiers et lignes modifiés, justification du choix technique)
* 📸 Preuves de correction (captures d’écran, extraits de code avant/après, tests réalisés)
* 🧠 Retour d’expérience (ce qui a été facile, difficile, ce que vous feriez différemment)
* 🚀 Suggestions d’améliorations futures (à court et moyen terme)

---

## 📦 Dossier à remettre

À la fin de la séance, vous devez rendre :

* Le projet modifié
* Le rapport d’audit (PDF ou `.md`)
* Le rapport d’intervention (PDF ou `.md`)

---

## 🧠 Conseils pour réussir

* Ne cherchez pas à corriger toutes les failles ! Choisissez les plus **critiques**.
* Travaillez en équipe si possible : un qui lit le code, un qui teste, un qui rédige.
* Appuyez-vous sur OWASP pour structurer vos raisonnements.
* Documentez bien vos étapes.
* Faites preuve d’esprit critique : un code qui fonctionne **n’est pas forcément un code sécurisé**.

---
