package no.breale17.post

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.cloud.netflix.ribbon.RibbonClient
import org.springframework.cloud.netflix.ribbon.RibbonClients
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
@RibbonClient(name="post")
class PostsApplication {

    @Bean
    fun swaggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .build()
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfoBuilder()
                .title("API for Posts")
                .description("REST API containing all posts")
                .version("1.0")
                .build()
    }

}

fun main(args: Array<String>) {
    runApplication<PostsApplication>(*args)
}

