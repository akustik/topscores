package org.gmd

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


@Configuration
@EnableSwagger2
open class SwaggerConfiguration {
    
    @Bean
    open fun gamesApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("Games")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/games/**"))
                .build()
    }

    @Bean
    open fun scoresApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("Scores")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/scores/**"))
                .build()
    }

    @Bean
    open fun triggerApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("Triggers")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/trigger/**"))
                .build()
    }

    @Bean
    open fun healthApi(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .groupName("Health")
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/health/**"))
                .build()
    }
}