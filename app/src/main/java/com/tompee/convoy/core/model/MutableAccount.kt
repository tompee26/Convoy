package com.tompee.convoy.core.model

data class MutableAccount(var email: String = "",
                          var isAuthenticated: Boolean = false,
                          var firstName: String = "",
                          var lastName: String = "",
                          var displayName: String = "",
                          var imageUrl: String = "")