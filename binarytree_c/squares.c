#include <stdio.h>
#include <stdlib.h>

int main(int argc, char *argv[]) {
    int number = atoi(argv[1]);

    int square = number*number;

    printf("%d\n", square);
    return 0;
}
