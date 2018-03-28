package com.tompee.convoy.interactor.user

open class DataException : Exception()

class EmptyFirstNameException : DataException()

class EmptyLastNameException : DataException()

class EmptyDisplayNameException : DataException()