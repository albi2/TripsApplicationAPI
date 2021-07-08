package com.lhind.tripapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.exception.RoleNotFoundException;
import com.lhind.tripapp.model.ERole;
import com.lhind.tripapp.model.Role;
import com.lhind.tripapp.repository.RoleRepository;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Optional;


// The class that gets extended helps us more easily convert requests into classes
public class DTOModelMapper extends RequestResponseBodyMethodProcessor {

    // We use model mapper for the conversion
    private static final ModelMapper modelMapper = new ModelMapper();
    private RoleRepository roleRepository = SpringContext.getBean(RoleRepository.class);
    // Entity manager is used to check if entity with id of DTO exists already
    private EntityManager entityManager;

    public DTOModelMapper(ObjectMapper objectMapper, EntityManager entityManager) {
        super(Collections.singletonList(new MappingJackson2HttpMessageConverter(objectMapper)));
        this.entityManager = entityManager;

        Converter<String, Role> stringToRole = new Converter<String, Role>() {
            public Role convert(MappingContext<String, Role> context) {
                String str = context.getSource();
                try {
                    Optional<Role> role = roleRepository.findByName(ERole.valueOf(str));
                    if (role.isPresent()) {
                        return role.get();
                    } else throw new RoleNotFoundException("Role " + str + " could not be found!");
                } catch (IllegalArgumentException e) {
                    throw new RoleNotFoundException("Role " + str + " could not be found!");
                }
            }
        };
        modelMapper.addConverter(stringToRole);
    }

    // We have to tweak the method to make it work for
    // DTO parameters not RequestBody as by default
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DTO.class);
    }

    // By default the validation happens when parameter
    // has @Valid and @Validated interfaces
    // We override the behaviour to perform validation on all DTOs
    @Override
    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        binder.validate();
    }

    // The class takes the parameter and converts it into an entity if doesnt have @Id
    // else gets it from the database and converts DTO into it through modelMapper
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object dto = super.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        Object id = getEntityId(dto);
        if (id == null) {
            return modelMapper.map(dto, parameter.getParameterType());
        } else {
            Object persistedObject = entityManager.find(parameter.getParameterType(), id);
            if(persistedObject == null) {
                throw new EntityNotFoundException("Could not find entity of type" +
                        " " +parameter.getParameterType().getName() + " with id " + id +".");
            }
            modelMapper.map(dto, persistedObject);
            return persistedObject;
        }
    }

    // This method by default converts the request  into the class
    // provided after request Body
    // We override it to convert the request into the DTO class provided in the interface
    // the rest is done by resolve argument
    @Override
    protected Object readWithMessageConverters(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        for (Annotation ann : parameter.getParameterAnnotations()) {
            DTO dtoType = AnnotationUtils.getAnnotation(ann, DTO.class);
            if (dtoType != null) {
                return super.readWithMessageConverters(inputMessage, parameter, dtoType.value());
            }
        }
        throw new RuntimeException();
    }

    // Iterate over the fields of the DTO and find the one with @Id
    // Return the value of this ID so it can be used by resolve argument
    // to convert DTO to entity
    private Object getEntityId(@NotNull Object dto) {
        for (Field field : dto.getClass().getDeclaredFields()) {

            if (field.getAnnotation(Id.class) != null) {
                try {
                    field.setAccessible(true);
                    return field.get(dto);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

}
