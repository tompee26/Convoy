package com.tompee.convoy.interactor.data

open class DataException : Exception()

class EmptyFirstNameException : DataException()

class EmptyLastNameException : DataException()

class EmptyDisplayNameException : DataException()