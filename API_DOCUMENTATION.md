# Documentation Backend - Dentis

Ce document détaille les Entités (Base de données) et les DTOs (Objets échangés via JSON) pour l'intégration Frontend.

## 1. Énumérations (Enums)

Ces valeurs sont passées sous forme de **String** dans les JSON.

### Sexe
*   `M` (Masculin)
*   `F` (Féminin)

### GroupeSanguin
*   `A`, `B`, `AB`, `O`

### TypeRecouvrement (Patient - Assurance)
*   `Médecin de la famille`
*   `Remboursement`
*   `Santé publique`

### TypePublication
*   `Article scientifique`
*   `Étude de cas`
*   `Lancement d'un produit ou service`
*   `Actualités/innovation`

### TypeServiceMedical
*   `Diagnostic et soins courants`
*   `Parodontologie`
*   `Radiologie et imagerie dentaire`
*   `Endodontie`
*   `Esthétique dentaire`
*   `Implantologie`

### JourSemaine (Horaires)
*   `LUNDI`, `MARDI`, `MERCREDI`, `JEUDI`, `VENDREDI`, `SAMEDI`, `DIMANCHE`

---

## 2. Entités (Modèle de Données)

### User (Classe Mère)
*   `id` (Integer)
*   `nom` (String)
*   `prenom` (String)
*   `email` (String) - Unique
*   `motDePasse` (String) - Haché (BCrypt)
*   `tel` (int)
*   `sexe` (Enum: Sexe)
*   `photo` (String - Base64 ou URL)

### Patient (hérite de User)
*   `dateNaissanceP` (LocalDate)
*   `groupeSanguinP` (Enum: GroupeSanguin)
*   `recouvrementP` (Enum: TypeRecouvrement)
*   `rendezvous` (Liste de Rendezvous)

### Dentiste (hérite de User)
*   `diplome` (String - LongText)
*   `specialite` (String)
*   `gouvernorat` (String)
*   `delegation` (String)
*   `adresse` (String)
*   `verifie` (boolean) - Est validé par l'admin
*   `services` (Liste de ServiceMedical)
*   `publications` (Liste de Publication)
*   `horaires` (Liste de Horaire)

### Admin (hérite de User)
*   `adminType` (String)

### ServiceMedical
*   `numSM` (Integer) - ID
*   `nomSM` (String)
*   `typeSM` (Enum: TypeServiceMedical)
*   `descriptionSM` (String - Text)
*   `tarifSM` (BigDecimal)
*   `image` (String - Base64)
*   `dentiste` (Relation ManyToOne)

### Publication
*   `idPub` (Integer)
*   `titrePub` (String)
*   `datePub` (Date)
*   `typePub` (Enum: TypePublication)
*   `description` (String - Text)
*   `fichierPub` (String)
*   `affichePub` (String)
*   `valide` (boolean) - Validé par admin
*   `dentiste` (Relation ManyToOne)

### Rendezvous
*   `idRv` (Integer)
*   `patient` (Relation ManyToOne)
*   `dentiste` (Relation ManyToOne)
*   `dateRv` (LocalDate)
*   `heureRv` (LocalTime)
*   `statutRv` (String) - Ex: "CONFIRME", "EN_ATTENTE", "TERMINE"
*   `descriptionRv` (String)
*   `actes` (Liste de ActeMedical)

### Horaire
*   `idHoraire` (Integer)
*   `jourSemaine` (Enum: JourSemaine)
*   `matinDebut`, `matinFin` (LocalTime)
*   `apresMidiDebut`, `apresMidiFin` (LocalTime)
*   `estFerme` (boolean)
*   `dentiste` (Relation ManyToOne)

---

## 3. DTOs (Objets JSON API)

### Authentification

#### `LoginRequestDTO` (POST /login)
```json
{
  "email": "string",
  "motDePasse": "string"
}
```

#### `LoginUserResponseDTO` (Réponse commune)
*   `id`, `nom`, `prenom`, `email`, `tel`, `photo`
*   `role` (String: "PATIENT", "DENTISTE", "ADMIN")
*   `token` (JWT String)
*   `sexe` (String: "M" ou "F")

**Spécifique Patient (`LoginPatientResponseDTO`)** :
*   `dateNaissanceP` (Date "YYYY-MM-DD")
*   `groupeSanguinP` (String - voir Enum)
*   `recouvrementP` (String - voir Enum Label)

**Spécifique Admin (`LoginAdminResponseDTO`)** :
*   `adminType` (String)

---

### Inscription (Signup)

#### `SignupPatientRequestDTO` (POST /signup/patient)
```json
{
  "nom": "string",
  "prenom": "string",
  "email": "string",
  "motDePasse": "string",
  "tel": 12345678,
  "sexe": "M", /* ou "F" */
  "dateNaissanceP": "YYYY-MM-DD",
  "groupeSanguinP": "A", /* A, B, AB, O */
  "recouvrementP": "Santé publique" /* Label exact */
}
```

#### `SignupDentisteRequestDTO` (POST /signup/dentist)
```json
{
  "nom": "string",
  "prenom": "string",
  "email": "string",
  "motDePasse": "string",
  "tel": 12345678,
  "sexe": "M",
  "diplome": "string",
  "specialite": "string",
  "gouvernorat": "string",
  "delegation": "string",
  "adresse": "string"
}
```

---

### Services Médicaux

#### `ServiceMedicalDTO`
```json
{
  "numSM": 1, /* null si création */
  "nomSM": "Consultation",
  "typeSM": "Diagnostic et soins courants", /* Label exact */
  "descriptionSM": "Description...",
  "tarifSM": 50.0,
  "image": "base64_string...",
  "dentistId": 123
}
```

---

### Publications

#### `PublicationDTO`
```json
{
  "idPub": 1,
  "titrePub": "Titre",
  "datePub": "YYYY-MM-DD",
  "typePub": "Article scientifique", /* Label exact */
  "description": "Contenu...",
  "dentistId": 123,
  "dentistName": "Nom Prenom",
  "valide": true
}
```

---

### Rendez-vous

#### `RendezvousDTO`
```json
{
  "idRv": 1,
  "patientId": 10,
  "dentistId": 20,
  "serviceId": 5, /* Optionnel */
  "dateRv": "YYYY-MM-DD",
  "heureRv": "HH:MM",
  "statutRv": "CONFIRME",
  "descriptionRv": "Motif...",
  "patientName": "Nom Patient", /* Lecture seule */
  "dentistName": "Nom Dentiste", /* Lecture seule */
  "serviceName": "Nom Service" /* Lecture seule */
}
```

---

### Recherche & Horaires

#### `DentisteSearchDTO` (Résultat de recherche)
*   `id`, `nom`, `prenom`
*   `gouvernorat`, `delegation`, `adresse`
*   `diplome`, `specialite`
*   `photo`

#### `SearchDropdownResponseDTO` (Données pour les filtres)
*   `services`: Liste des types de services (String)
*   `dentistes`: Liste de `DentisteSearchDTO`

#### `HoraireDTO`
```json
{
  "jourSemaine": "LUNDI",
  "matinDebut": "08:00",
  "matinFin": "12:00",
  "apresMidiDebut": "14:00",
  "apresMidiFin": "18:00",
  "estFerme": false
}
```
