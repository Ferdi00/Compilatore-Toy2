# COMPILATORE TOY2

## Descrizione del Progetto

Il progetto **Boccia-Guarnieri ES5** è un compilatore per un linguaggio di programmazione personalizzato. È stato sviluppato utilizzando **IntelliJ IDEA** e sfrutta le librerie **JFlex** e **Java CUP** per la generazione del lexer e del parser. Il compilatore traduce il codice sorgente scritto nel linguaggio personalizzato in codice C, che può essere compilato ed eseguito.

## Funzionalità

- **Lexer**: Analizza il testo sorgente e lo suddivide in token utilizzando **JFlex**.
- **Parser**: Costruisce un albero sintattico astratto (AST) dai token generati dal lexer utilizzando **Java CUP**.
- **Visite**: Implementa diverse visite per il controllo dei tipi, la generazione del codice C e la generazione di rappresentazioni XML.
- **Output**: Genera codice C compilabile ed eseguibile.

## Documentazione

La documentazione del progetto si trova nella cartella [`docs`](docs/):

- **specifiche.pdf**: Contiene le specifiche del linguaggio e del compilatore.
- **esempio_programma.txt**: Contiene un esempio di programma scritto nel linguaggio personalizzato.

## Installazione

### Prerequisiti

- **Java JDK 17** o superiore.
- **Maven** per la gestione delle dipendenze.
- **IntelliJ IDEA** (consigliato per lo sviluppo).

### Passaggi

1. Clona il repository:
   ```bash
   git clone https://github.com/username/boccia-guarnieri_es5.git
   cd boccia-guarnieri_es5
   ```

Raccolta delle informazioni sull'area di lavoro in corso```markdown

# Boccia-Guarnieri ES5

## Descrizione del Progetto

Il progetto **Boccia-Guarnieri ES5** è un compilatore per un linguaggio di programmazione personalizzato. È stato sviluppato utilizzando **IntelliJ IDEA** e sfrutta le librerie **JFlex** e **Java CUP** per la generazione del lexer e del parser. Il compilatore traduce il codice sorgente scritto nel linguaggio personalizzato in codice C, che può essere compilato ed eseguito.

## Funzionalità

- **Lexer**: Analizza il testo sorgente e lo suddivide in token utilizzando **JFlex**.
- **Parser**: Costruisce un albero sintattico astratto (AST) dai token generati dal lexer utilizzando **Java CUP**.
- **Visite**: Implementa diverse visite per il controllo dei tipi, la generazione del codice C e la generazione di rappresentazioni XML.
- **Output**: Genera codice C compilabile ed eseguibile.

## Documentazione

La documentazione del progetto si trova nella cartella [`docs`](docs/):

- **specifiche.pdf**: Contiene le specifiche del linguaggio e del compilatore.
- **esempio_programma.txt**: Contiene un esempio di programma scritto nel linguaggio personalizzato.

## Installazione

### Prerequisiti

- **Java JDK 17** o superiore.
- **Maven** per la gestione delle dipendenze.
- **IntelliJ IDEA** (consigliato per lo sviluppo).

### Passaggi

1. Clona il repository:

   ```bash
   git clone https://github.com/username/boccia-guarnieri_es5.git
   cd boccia-guarnieri_es5
   ```

2. Assicurati di avere Maven configurato correttamente.

3. Installa le dipendenze e genera il lexer e il parser:

   ```bash
   mvn clean install
   ```

4. Apri il progetto in IntelliJ IDEA.

5. Configura il JDK 17 come SDK del progetto.

6. Esegui il compilatore utilizzando la classe `Main` in [`src/es5/Main.java`](src/es5/Main.java).

## Utilizzo

1. Scrivi un programma nel linguaggio personalizzato e salvalo in un file `.txt`.

2. Esegui il compilatore passando il file `.txt` come argomento:

   ```bash
   java -cp target/classes es5.Main path/to/your_program.txt
   ```

3. Il compilatore genererà un file `.c` nella directory `test_files/c_out/`.

4. Compila il file `.c` utilizzando un compilatore C (ad esempio, `clang`):

   ```bash
   clang -o output_executable path/to/generated_program.c
   ```

5. Esegui il programma compilato:
   ```bash
   ./output_executable
   ```

## Struttura del Progetto

- **`src/es5`**: Contiene il lexer, il parser e la classe principale.
- **`src/visitor`**: Contiene le visite per il controllo dei tipi, la generazione del codice C e altre funzionalità.
- **`srcjflexcup`**: Contiene i file di configurazione per JFlex (`lexer.flex`) e Java CUP (`parser.cup`).
- **`docs`**: Contiene la documentazione del progetto.
- **`test_files`**: Contiene file di test e output generati.

## Dipendenze e Plugin

Le dipendenze e i plugin utilizzati sono configurati nel file [`pom.xml`](pom.xml). Tra i principali:

- **JFlex**: Per la generazione del lexer.
- **Java CUP**: Per la generazione del parser.
- **Exec Maven Plugin**: Per eseguire il compilatore.

```

```
