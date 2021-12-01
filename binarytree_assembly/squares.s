.text
.align 2
.globl main
main:
    li $v0, 4
    la $a0, number_prompt
    syscall

    li $v0, 5
    syscall

    move $t0, $v0
    mul $t0, $t0, $t0

    li $v0, 1
    move $a0, $t0
    syscall

    jr $ra

.data
number_prompt: .asciiz "Please enter an integer: "
