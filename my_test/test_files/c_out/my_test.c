#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

char messaggio[150];
// Function prototypes
int fibonacci(int);
 int main();

int main() {
    int numero;
    numero = 1;

	printf("Inserisci numero intero: ");
    scanf("%d", &numero);

	printf("%d",fibonacci(numero));

 }

int fibonacci(int numero) {
    int previousNumber;
    int previousPreviousNumber;
    int i;
    int currentNumber;
    currentNumber = 1;
    previousPreviousNumber = 0;
    previousNumber = 0;
    i = 1;
    while (i < numero) {
    previousPreviousNumber = previousNumber;
    previousNumber = currentNumber;
    currentNumber = previousPreviousNumber + previousNumber;
    i = i + 1;
    }
    return currentNumber;

}

