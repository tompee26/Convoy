package com.tompee.convoy.core.model

data class MutableAccount(var email: String = "",
                          var isAuthenticated: Boolean = false,
                          var name: String = "",
                          var imageUrl: String = "")