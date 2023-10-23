package com.example.electricitybackend.config.pageable;

import com.example.electricitybackend.commons.data.model.paging.Pageable;
import com.example.electricitybackend.commons.data.model.paging.PageableParamParser;
import com.example.electricitybackend.config.annotation.PageableRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class PageableHeaderArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(PageableRequest.class) != null;
    }

    @Override
    public Pageable resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest nativeWebRequest,
            WebDataBinderFactory webDataBinderFactory) {

        HttpServletRequest request = (HttpServletRequest) nativeWebRequest.getNativeRequest();

        return PageableParamParser.parser(request.getParameterMap());
    }
}
