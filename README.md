# 🧠 AnonGram - Back-End

AnonGram este o aplicație socială anonimă, localizată, unde utilizatorii pot posta gânduri, întrebări sau confesiuni. Acest repository conține codul sursă pentru partea de **back-end** dezvoltată cu **Java 17**, **Spring Boot**, **PostgreSQL**, și **Keycloak** pentru autentificare.

---

## 🚀 Stack Tehnologic

- ⚙️ **Spring Boot** 3.x
- 🧠 **Java 17**
- 🐘 **PostgreSQL** 15
- 🔐 **Keycloak** (autentificare și gestionare utilizatori)
- 🔄 **RESTful API**
- 🗓️ **Spring Scheduler** (pentru expirarea postărilor)
- 🐳 **Docker** (pentru rulare locală și deployment)

---

## 🧩 Funcționalități principale

- 🔐 Înregistrare, autentificare și refresh token cu Keycloak
- 📝 Creare și ștergere postări anonime
- 📍 Posibilitate de adăugare locație (opțională)
- 🏷️ Adăugare taguri la postări
- 📷 Upload de imagini la postări (local, extensibil spre S3)
- 👍👎 Voturi pe postări + status vizibil pentru user-ul logat
- 💬 Comentarii pe postări
- 🧹 Ștergere automată postări expirate (scheduler configurat)
- 🧾 API structurat în stil REST cu DTO-uri clare
- 🌍 Suport pentru filtrare postări după:
    - distanță
    - taguri
    - text

---