package no.breale17.authentication

import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<AuthenticationApplication>(*args, "--spring.profiles.active=local")

}