#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

char messaggio[150];
char* stringa = "globale";

// Function prototypes
void globalstampa();
 int main();

int main() {
    char stringa[50];
    strcpy(stringa,"locale");
	printf("localscope:  %s \n", stringa);
	globalstampa();

 }

void globalstampa() {
    int contatore;
    contatore = 2;
	printf("globalscope:  %s \n", stringa);

    if (true) {
    char stringa[50];
    strcpy(stringa,"stringa if");
	printf("ifscope:  %s \n", stringa);

	}    while (contatore > 0) {
    char stringa[50];
	printf("whilescope:  %s \n", stringa);
    contatore = 0;
    }

 }

