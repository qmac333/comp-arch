#include <stdio.h>
#include <stdlib.h>

int randFunction(int num) {
    if(num == 0) {
        return 5;
    }
    else {
        return 4*(num+1) + (2*randFunction(num-1)) - 2;
    }
}

int main(int argc, char *argv[]) {
    int number = atoi(argv[1]); //atoi method converts the string (array of chars) to an int

    int ans = randFunction(number);
    printf("%d\n", ans);
    return 0;
}
