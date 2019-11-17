package no.breale17.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class GatewayApplicationRunner

fun main(args: Array<String>) {
    SpringApplication.run(GatewayApplicationRunner::class.java, *args)
}