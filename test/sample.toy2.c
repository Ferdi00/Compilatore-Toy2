#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

char messaggio[150];
// Function prototypes
double divisione(double, double);
double moltiplicazione(double, double);
double sottrazione(double, double);
void main();
double somma(double, double);

void main() {
    double n1;
    double n2;
    int scelta;
    bool condizione2;
    bool condizione1;
    condizione2 = true;
    condizione1 = true;
    scelta = 2;
    while (condizione1 && condizione2) {
	printf("Inserisci 2 per eseguire la somma");
	printf("\n");
	printf("Inserisci 3 per eseguire la moltiplicazione");
	printf("\n");
	printf("Inserisci 4 per eseguire la divisione");
	printf("\n");
	printf("Inserisci 5 per eseguire la sottrazione");
	printf("\n");
	printf("Inserisci 1 o 0 per chiudere il programma");

	printf("Inserisci un numero : \t");
    scanf("%d", &scelta);

    if (scelta == 2) {

	printf("Inserisci primo operando : \t");
    scanf("%lf", &n1);

	printf("Inserisci secondo operando : \t");
    scanf("%lf", &n2);
	printf("\n");
	printf("Il risultato � : \t%lf",somma(n1, n2));

	}
	else if (scelta == 5) {

	printf("Inserisci primo operando : \t");
    scanf("%lf", &n1);

	printf("Inserisci secondo operando : \t");
    scanf("%lf", &n2);
	printf("\n");
	printf("Il risultato � : \t%lf",sottrazione(n1, n2));
    }

	else if (scelta == 4) {

	printf("Inserisci primo operando : \t");
    scanf("%lf", &n1);

	printf("Inserisci secondo operando : \t");
    scanf("%lf", &n2);
	printf("\n");
	printf("Il risultato � : \t%lf",divisione(n1, n2));
    }

	else if (scelta == 3) {

	printf("Inserisci primo operando : \t");
    scanf("%lf", &n1);

	printf("Inserisci secondo operando : \t");
    scanf("%lf", &n2);
	printf("\n");
	printf("Il risultato � : \t%lf",moltiplicazione(n1, n2));
    }

	else {
    condizione1 = false;
    }
	printf("\n");
	printf("\n");
    }

 }

double sottrazione(double n1, double n2) {
    double risultato;
    risultato = n1 - n2;
    return risultato;

}

double divisione(double n1, double n2) {
    double risultato;
    risultato = n1 / n2;
    return risultato;

}

double moltiplicazione(double n1, double n2) {
    double risultato;
    risultato = n1 * n2;
    return risultato;

}

double somma(double n1, double n2) {
    double risultato;
    risultato = n1 + n2;
    return risultato;

}

