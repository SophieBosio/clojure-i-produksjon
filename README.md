# En bitteliten web app i Clojure!


Dette er et lite repo ment å vise fram hvor lite kode man trenger for å bygge en liten web app i Clojure, og hvor lett det er å fortsette å bygge den ut når man først har kommet i gang.

Demoen er ment å starte fra første commit på `demo-0`-branchen, deretter `demo-1`, osv.

## Kjøre appen

Du trenger Clojure: [Installer Clojure](https://clojure.org/guides/install_clojure)

Så kan du fyre opp et REPL og starte med:
```clojure
(user/go!)
```

Det starter en server og åpner browseren din på port 8088, så da kan du se den vakre sida di på http://localhost:8088/.

## Databaser

For moroskyld, ville jeg se hvor lett det var å bytte ut hele databasen og hvor lite annen kode jeg trengte å endre.

Derfor ligger det tre varianter av database-laget her og et interface i `db.interface` som kan switche på databasens type:
- `db.atom`, et enkelt `atom`, altså en muterbar variabel
- `db.sql`, en in-memory SQLite database
- `db.datomic`, en in-memory Datomic database, som jeg måtte knekke litt for å få den til å bli normalisert så den hadde samme interface mot resten av koden som SQL-varianten

Du kan bytte databasen som er i bruk ved å endre `:db` som vi legger på requesten i `server/middleware`.

## Størrelse

Med bare `atom`-databasen, er hele appen og uten `style`, er appen bare 258 linjer kode.
Med styling, er det 388, og med alle database-variantene, har vi 724 linjer totalt.

## Videreutvikle

Her er det mye potensiale for å legge til funksjonalitet og kanskje lande på noen gode abstraksjoner.

Fork gjerne repoet og ta det videre. Noen forslag:
- Endre status på boka
- Legge til bøker
- Fjerne bøker
- Legge til en beskrivelse på hver bok
- Se alle bøker skrevet av en forfatter
