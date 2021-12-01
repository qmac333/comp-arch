.text
.align 2
.globl main
main:
    li $v0, 4
    la, $a0, prompt
    syscall

    li $v0, 5
    syscall
    move $a0, $v0

    jal randFunction

    move $a0, $v0
    li $v0, 1
    syscall

    li $v0, 10
    syscall

    randFunction:
        addi $sp, $sp, -16
        sw $ra, 0($sp)
        sw $s0, 4($sp)
        sw $s1, 8($sp)
        sw $s2, 12($sp)

        beq $a0, $zero, return5

        move $s0, $a0
        addi $a0, $a0, -1
        jal randFunction
        mul $s1, $v0, 2
        add $s2, $s0, 1
        mul $s2, $s2, 4
        add $s2, $s2, $s1
        addi $v0, $s2, -2
        j clean

        return5:
            li $v0, 5
            j clean

        clean:
            lw $ra, 0($sp)
            lw $s0, 4($sp)
            lw $s1, 8($sp)
            lw $s2, 12($sp)
            addi $sp, $sp, 16
            jr $ra

.data
prompt: .asciiz "Please enter an integer: "
