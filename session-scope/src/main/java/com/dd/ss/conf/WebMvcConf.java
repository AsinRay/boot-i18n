package com.dd.ss.conf;

import com.dd.ss.web.I18nInterceptor;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class WebMvcConf implements WebMvcConfigurer {

    private static final String LANG_PARAM_NAME = "lang";


    /**
     * Spring boot Autoconfiguration中配置的默认LocaleResolver 为 AcceptHeaderLocaleResolver，该默认值的locale信息
     * 通过 http header Accept-Language 来确定，但不允许更改语言环境,也就没有办法使用 LocaleChangeInterceptor.
     *
     * 要解决这个问题，请尝试在Spring bean配置文件中声明一个SessionLocaleResolver bean，它在大多数情况下应该是合适的。
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:i18n/arena","classpath:i18n/valid");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        factoryBean.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");
        factoryBean.setValidationMessageSource(messageSource());
        return factoryBean;
    }

    @Bean
    public LocaleChangeInterceptor i18nInterceptor() {
        LocaleChangeInterceptor interceptor = new I18nInterceptor();
        interceptor.setParamName(LANG_PARAM_NAME);
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(i18nInterceptor());
    }
}
