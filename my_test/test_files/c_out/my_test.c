#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

char messaggio[150];
int c = 1;

// Function prototypes
void sommac(int, int, double, char*, double*);
char* stampa(char*);
 int main();

void sommac(int a, int d, double b, char* size, double* result) {
    *result = a + b + c + d;

    if (*result > 100) {
    char valore[50];
    strcpy(valore,"grande");
    strcpy(size, valore);

	}
	else if (*result > 50) {
    char valore[50];
    strcpy(valore,"media");
    strcpy(size, valore);
    }

	else {
    char valore[50];
    strcpy(valore,"piccola");
    strcpy(size, valore);
    }

 }

char* stampa(char* messaggio) {
    int i;
    i = 0;
    while (i < 4) {
	printf("\n");
    i = i + 1;
    }
	printf(" %s ", messaggio);
    return "ok";

}

int main() {
    int a;
    double b;
    char taglia[50];
    char valore[50];
    char ans1[50];
    char ans[50];
    int x;
    double risultato;
    risultato = 0.0;
    strcpy(ans,"no");
    x = 3;
    a = 1;
    b = 2.2;
    strcpy(valore,"nok");
	sommac(a, x, b, taglia, &risultato);
	snprintf(messaggio, sizeof(messaggio), "la somma di %d e %lf incrementata di %d e' %s ", a, b, c, taglia);
	stampa(messaggio);
	strcpy(valore,messaggio);
	snprintf(messaggio, sizeof(messaggio), "ed e' pari a %lf ", risultato);
	stampa(messaggio);
	strcpy(valore,messaggio);

	printf("vuoi continuare? (si/no) - inserisci due volte la risposta\n");
    scanf("%s %s", ans, ans1);
    while (strcmp(ans, "si") == 0) {

	printf("inserisci un intero:");
    scanf("%d", &a);

	printf("inserisci un reale:");
    scanf("%lf", &b);
	sommac(a, x, b, taglia, &risultato);
	snprintf(messaggio, sizeof(messaggio), "la somma di %d e %lf incrementata di %d e' %s ", a, b, c, taglia);
	stampa(messaggio);
	strcpy(valore,messaggio);
	snprintf(messaggio, sizeof(messaggio), "ed e' pari a %lf ", risultato);
	stampa(messaggio);
	strcpy(valore,messaggio);

	printf("vuoi continuare? (si/no):\t");
    scanf("%s", ans);
    }
	printf("\n");
	printf("ciao");

 }

