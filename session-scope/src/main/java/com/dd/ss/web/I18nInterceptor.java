package com.dd.ss.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Slf4j
public class I18nInterceptor extends LocaleChangeInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws ServletException {
        // 从header中取 getHeader
        String newLocale = req.getHeader(getParamName());
        // 如果header中没有取到则从parameter中取， getParameter
        newLocale = newLocale==null ? req.getParameter(getParamName()):newLocale;

        // If no local found in HttpHeader and Param, return true.
        if(newLocale == null){
            return true;
        }
        // 如果从Header中获取到国际化参数值，并且当前请求的方法在LocaleChangeInterceptor定义的方法列表中
        // 或者LocaleChangeInterceptor没有定义任何请求方法时，做如下处理:
        if (checkHttpMethod(req.getMethod())) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(req);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            try {
                localeResolver.setLocale(req, resp, parseLocaleValue(newLocale));
            } catch (IllegalArgumentException ex) {
                log.error("set local {} failed:", newLocale, ex.getMessage(), ex);
            }
        }
        return true;
    }

    /**
     * Check the given http request is defined by LocaleChangeInterceptor
     *
     * If no http request method is defined in LocaleChangeInterceptor return true
     * the given http method belongs to one of the http request method list defined by LocaleChangeInterceptor
     * return true.
     *
     * Otherwise return false.
     *
     * @param currentMethod http request method. e.g. PUT GET POST OPTION etc.
     * @return  true If no http request method is defined in LocaleChangeInterceptor return true
     * or the given http method belongs to one of the http request method list defined by LocaleChangeInterceptor,
     * otherwise return false.
     */
    private boolean checkHttpMethod(String currentMethod) {
        String[] configuredMethods = getHttpMethods();
        if (ObjectUtils.isEmpty(configuredMethods)) {
            return true;
        }
        return Arrays.stream(configuredMethods).anyMatch(o->o.equalsIgnoreCase(currentMethod));
    }
}
