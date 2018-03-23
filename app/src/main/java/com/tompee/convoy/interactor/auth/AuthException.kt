package com.tompee.convoy.interactor.auth

open class AuthException : Exception()

class EmailEmptyException : AuthException()

class InvalidEmailFormatException : AuthException()

class PasswordEmptyException : AuthException()

class PasswordTooShortException : AuthException()

class AccountDoesNotExistException : AuthException()

class InvalidCredentialsException : AuthException()