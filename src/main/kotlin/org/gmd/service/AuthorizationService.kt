package org.gmd.service

open class AuthorizationService {
    
    open fun withAutorization(account: String, providedToken: String) {
        val expectedToken = System.getenv().get("token_$account")
        if(!expectedToken.equals(providedToken)) {
            throw UnsupportedOperationException("Not authorized")
        }
    }
}