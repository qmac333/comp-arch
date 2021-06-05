.text
.align 2
.globl main
main:
    la $s3, done_with_program # $s3 stores the address of "DONE\n" to be used later

    la $a0, root_node
    jal create
    move $s0, $v0 # $s0 contains the address of the beginning of the struct

input_loop:
    li $v0, 4
    la, $a0, patient_prompt
    syscall

    li $v0, 8
    la $a0, patient_buffer
    li $a1, 16
    syscall
    la $s1, patient_buffer

    #li $v0, 4
    #la, $a0, ($s1)      //This is code to print out a string from a register which holds the address of that string
    #syscall

    move $a0, $s1       # moves the inputted string into $a0
    move $a1, $s3       # moves the string "DONE\n" into $a1
    jal strcmp
    beqz $v0, end_input

    li $v0, 4
    la, $a0, infector_prompt
    syscall

    li $v0, 8
    la $a0, infector_buffer
    li $a1, 16
    syscall
    la $s2, infector_buffer

    move $a0, $s1
    move $a1, $s2
    move $a2, $s0
    jal tree_insert

    j input_loop

end_input:
    move $a0, $s0
    jal tree_print

    j exit

# $a0 = name
create:
    addi $sp, $sp, -8
    sw $ra, 0($sp)
    sw $s0, 4($sp)

    move $a1, $a0 # moves the address of "Bluedevil\n" into $a1 for the strcpy function

    li $a0, 24
    li $v0, 9
    syscall
    move $s0, $v0

    move $a0, $s0
    jal strcpy
    sw $zero, 16($s0)
    sw $zero, 20($s0)

    move $v0, $s0

    lw $ra, 0($sp)
    lw $s0, 4($sp)
    addi $sp, $sp, 8

    jr $ra

# $a0 = patient, $a1 = infector, $a2 = root (Bluedevil)
tree_insert:
    addi $sp, $sp, -28
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $s2, 12($sp)
    sw $s3, 16($sp)
    sw $s4, 20($sp)
    sw $s5, 24($sp)

    move $s0, $a0
    move $s1, $a1
    move $s2, $a2

    move $a0, $s0
    jal create
    move $s3, $v0    # saved the new struct (node) in $s3

    move $s4, $s2    # copies the root struct address into $s4
    move $a0, $s4
    move $a1, $s1
    jal traverse
    move $s5, $v0

    beqz $s5, exit

    move $a0, $s3
    move $a1, $s5
    jal add_node

    #move $a0, $s2
    #jal tree_print

    lw $ra, 0($sp)
    lw $s0, 4($sp)
    lw $s1, 8($sp)
    lw $s2, 12($sp)
    lw $s3, 16($sp)
    lw $s4, 20($sp)
    lw $s5, 24($sp)
    addi $sp, $sp, 28
    jr $ra

# a0 = root , jal recurse
# saving etc.
# offset 0($a0)

# $a0 contains the address of a copy of the root struct
# $a1 contains the address of the infector buffer
traverse:
    addi $sp, $sp, -28
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $s2, 12($sp)
    sw $s3, 16($sp)
    sw $s4, 20($sp)
    sw $s5, 24($sp)

    move $s0, $a0   # $s0 has the root
    move $s1, $a1   # $s1 has the infector
    move $s5, $a0   # $s5 has the root

    beqz $s0, reached_null  # base case to see if we have reached a null node

    # lines 147-152 are base case to see if we found the target node
    move $a0, $s0
    move $a1, $s1
    jal strcmp
    move $s2, $v0

    beqz $s2, found_equal

    move $a1, $s1     # $a1 has the infector
    lw $s0, 16($s0)   # $s0 = root->left
    move $a0, $s0
    jal traverse
    move $s3, $v0

    # if we get null after searching left, go right
    beqz $s3, go_right

    move $v0, $s3
    j clean

    reached_null:
        move $v0, $zero
        j clean

    found_equal:
        move $v0, $s0
        j clean

    go_right:
        move $s0, $s5
        move $a1, $s1
        lw $s0, 20($s0)   # $s0 = root->right
        move $a0, $s0
        jal traverse
        move $s3, $v0
        j clean

    clean:
        lw $ra, 0($sp)
        lw $s0, 4($sp)
        lw $s1, 8($sp)
        lw $s2, 12($sp)
        lw $s3, 16($sp)
        lw $s4, 20($sp)
        lw $s5, 24($sp)
        addi $sp, $sp, 28
        jr $ra

# $a0 has the node to be added, $a1 has the node to be added to
add_node:
    addi $sp, $sp, -32
    sw $ra, 0($sp)
    sw $s0, 4($sp)
    sw $s1, 8($sp)
    sw $s2, 12($sp)
    sw $s3, 16($sp)
    sw $s4, 20($sp)
    sw $s5, 24($sp)
    sw $s6, 28($sp)

    move $s0, $a0
    move $s1, $a1

    lw $s2, 16($s1)
    beqz $s2, add_left
    lw $s2, 20($s1)
    beqz $s2, alphabetical_add

    add_left:
        sw $s0, 16($s1)
        j end_add

    # $s1 has the node to be added to, $s0 has the node to be added
    alphabetical_add:
        lw $s6, 16($s1)
        move $a0, $s6
        move $a1, $s0
        jal strcmp
        move $s3, $v0

        bltz $s3, add_right

        lw $s4, 16($s1)
        lw $s5, 20($s1)
        move $s5, $s4
        sw $s5, 20($s1)
        j add_left

    add_right:
        sw $s0, 20($s1)
        j end_add

    end_add:
        lw $ra, 0($sp)
        lw $s0, 4($sp)
        lw $s1, 8($sp)
        lw $s2, 12($sp)
        lw $s3, 16($sp)
        lw $s4, 20($sp)
        lw $s5, 24($sp)
        lw $s6, 28($sp)
        addi $sp, $sp, 32

        jr $ra

# $a0 is the root node of the tree
tree_print:
    addi $sp, $sp, -8
    sw $ra, 0($sp)
    sw $s0, 4($sp)

    move $s0, $a0

    beqz $s0, end_printing

    move $a0, $s0
    li $v0, 4
    syscall

    lw $a0, 16($s0)
    jal tree_print

    lw $a0, 20($s0)
    jal tree_print

    end_printing:
        lw $ra, 0($sp)
        lw $s0, 4($sp)
        addi $sp, $sp, 8

        jr $ra

# $a0 = address of dest, $a1 = address of src
strcpy:
	lb $t0, 0($a1)
	beq $t0, $zero, done_copying
	sb $t0, 0($a0)
	addi $a0, $a0, 1
	addi $a1, $a1, 1
	j strcpy

	done_copying:
	jr $ra

# $a0, $a1 = strings to compare
# $v0 = result of strcmp($a0, $a1)
strcmp:
	lb $t0, 0($a0)
	lb $t1, 0($a1)

	bne $t0, $t1, done_with_strcmp_loop
	addi $a0, $a0, 1
	addi $a1, $a1, 1
	bnez $t0, strcmp
	li $v0, 0
	jr $ra

	done_with_strcmp_loop:
	sub $v0, $t0, $t1
	jr $ra

exit:
    li $v0, 10
    syscall

.data
patient_prompt: .asciiz "Please enter patient: "
infector_prompt: .asciiz "Please enter infector: "
patient_buffer: .space 16
infector_buffer: .space 16
root_node: .asciiz "BlueDevil\n"
done_with_program: .asciiz "DONE\n"
