#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct node {
    char name[80];
    struct node* left;
    struct node* right;
};

struct node* create(char* data) {
    struct node* newNode = (struct node*)malloc(sizeof(struct node));

    strcpy(newNode->name, data);
    newNode->left = NULL;
    newNode->right = NULL;

    return newNode;
}

struct node* traverse(struct node* temp, char* data) {
    if(temp != NULL) {
        if(strcmp(temp->name, data) == 0) {
            return temp;
        }
        else {
            struct node* found = traverse(temp->left, data);
            if(found == NULL) {
                found = traverse(temp->right, data);
            }
            return found;
        }
    }
    else {
        return NULL;
    }
}

void finalPrint(struct node* patientZero) {
    if(patientZero == NULL) {
        return;
    }
    printf("%s\n", patientZero->name);
    finalPrint(patientZero->left);
    finalPrint(patientZero->right);
}

void deallocateTree(struct node* root) {
    if(root == NULL) {
        return;
    }
    deallocateTree(root->left);
    deallocateTree(root->right);
    free(root);
}

int main(int argc, char *argv[]) {
    FILE* fPoint = fopen(argv[1], "r");
    if(fPoint == NULL) {
        printf("No file\n");
        return 0;
    }

    struct node* root = NULL;
    struct node* store = NULL;

    char infected[80];
    char infector[80];
    while(1) {
        fscanf(fPoint, "%s", infected);
        if(strcmp(infected, "DONE") == 0) {
            break;
        }

        fscanf(fPoint, "%s", infector);

        if(root == NULL) {
            root = create(infector);
        }
        struct node* temp = root;

        store = create(infected);
        temp = traverse(temp, infector);

        if(temp->left == NULL && temp->right == NULL) {
            temp->left = store;
        }
        else if(temp->right == NULL) {
            if(strcmp(temp->left->name, store->name) < 0) {
                temp->right = store;
            }
            else {
                temp->right = temp->left;
                temp->left = store;
            }
        }
    }

    finalPrint(root);

    deallocateTree(root);
    root = NULL;
    fclose(fPoint);

    return 0;
}
