package no.breale17.user.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {

        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/users/userCount").permitAll()
                .antMatchers("/users").hasRole("ADMIN")
                .antMatchers("/users/friendrequest").hasRole("USER")
                .antMatchers("/users/basic").hasRole("USER")
                .antMatchers("/users/{id}/**")
                .access("hasRole('USER') and @userSecurity.checkId(authentication, #id)")
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                //never create a session, but use existing one if provided
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }

    @Bean
    fun userSecurity() : UserSecurity {
        return UserSecurity()
    }
}

/**
 * Custom check. Not only we need a user authenticated, but we also
 * need to make sure that a user can only access his/her data, and not the
 * one of the other users
 */
class UserSecurity{

    fun checkId(authentication: Authentication, id: String) : Boolean{

        val current = (authentication.principal as UserDetails).username

        return current == id
    }
}