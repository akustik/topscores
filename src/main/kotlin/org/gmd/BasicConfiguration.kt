package org.gmd

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter


@Configuration
@EnableWebSecurity
open class BasicConfiguration(private val env: EnvProvider) : WebSecurityConfigurerAdapter(false) {

    companion object {
        private val TOKEN_CONFIGURATION = Regex("token:(\\w+)")
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder?) {
        val inMemoryBuilder = auth!!.inMemoryAuthentication()
        env.getEnv()
                .filter { entry -> TOKEN_CONFIGURATION.matches(entry.key) }
                .forEach { key, token ->
                    run {
                        val (account) = TOKEN_CONFIGURATION.matchEntire(key)!!.destructured
                        inMemoryBuilder.withUser(account).password(token).roles("ADMIN")
                    }
                }
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
    }
}