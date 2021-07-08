package com.lhind.tripapp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.tripapp.dto.entityDTO.*;
import com.lhind.tripapp.util.DTOModelMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import com.fasterxml.classmate.TypeResolver;
import javax.persistence.EntityManager;
import java.util.List;

@Configuration
@EnableSwagger2
public class SpringConfigurationClass implements WebMvcConfigurer  {
    private final ApplicationContext applicationContext;
    private final EntityManager entityManager;

    @Autowired
    public SpringConfigurationClass(ApplicationContext applicationContext, EntityManager entityManager) {
        this.applicationContext = applicationContext;
        this.entityManager = entityManager;
    }

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    // Krijojme object mapper te ri qe do e shtojme tek argument resolvers
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().applicationContext(this.applicationContext).build();
        argumentResolvers.add(new DTOModelMapper(objectMapper, entityManager));
    }

    @Bean
    public Docket api(TypeResolver resolver) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .additionalModels(resolver.resolve(TripUpdateDTO.class),
                                resolver.resolve(TripIdDTO.class),
                                resolver.resolve(TripCreationDTO.class),
                                resolver.resolve(TripStatusUpdateDTO.class),
                                resolver.resolve(UserCreationDTO.class));
    }
}