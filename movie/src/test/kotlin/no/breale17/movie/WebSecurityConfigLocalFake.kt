package no.breale17.movie

import no.breale17.movie.config.WebSecurityConfig
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@Order(1) // need to make this Bean been used instead of WebSecurityConfig
class WebSecurityConfigLocalFake : WebSecurityConfig() {


    override fun configure(http: HttpSecurity) {
        super.configure(http)

        http.httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.inMemoryAuthentication()
                .withUser("foo").password("{noop}123").roles("USER").and()
                .withUser("bar").password("{noop}123").roles("USER").and()
                .withUser("admin").password("{noop}admin").roles("ADMIN", "USER")
    }
}