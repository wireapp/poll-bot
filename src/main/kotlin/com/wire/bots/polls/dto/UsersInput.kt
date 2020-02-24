package com.wire.bots.polls.dto

data class UsersInput(val input: String) {
    //TODO modify this in the future - because we do not want to print decrypted users text to the log
    override fun toString(): String = input
}
