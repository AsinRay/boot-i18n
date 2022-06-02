# I18N

在 Spring 中需要配置的 MessageSource 现在不用配置了，Spring Boot 
会通过 org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration 自动帮我们配置一个 MessageSource 实例。

也就是在任何一个地方都可以通过如下代码来完成多语言的处理：

```java
@RestController
public class HelloController {
    @Autowired
    MessageSource messageSource;
    @GetMapping("/hello")
    public String hello() {
        return messageSource.getMessage("user.name", null, LocaleContextHolder.getLocale());
    }
}
```
在 HelloController 中我们可以直接注入 MessageSource 实例，然后调用该实例中的 getMessage 方法去获取变量的值，
第一个参数是要获取变量的 key，第二个参数是如果 value 中有占位符，可以从这里传递参数进去，第三个参数传递一个 Locale 实例即可，
这相当于当前的语言环境。

## Resolver


默认情况下，在接口调用时，通过请求头的 Accept-Language 来配置当前的环境,此外spring 也提供了session级别的可切换的resolver
来实现更加灵活的处理。

### AcceptHeaderLocaleResolver



### 自定义切换

觉得切换参数放在请求头里边好像不太方便，那么也可以自定义解析方式。
例如参数可以当成普通参数放在地址栏上，通过如下配置可以实现我们的需求。

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        registry.addInterceptor(interceptor);
    }
    @Bean
    LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }
}
```
在以上的这段配置中，我们首先提供了一个 SessionLocaleResolver 实例，这个实例会替换掉默认的 AcceptHeaderLocaleResolver，
不同于 AcceptHeaderLocaleResolver 通过请求头来判断当前的环境信息，SessionLocaleResolver 将客户端的 Locale 保存到 HttpSession 对象中，
并且可以进行修改（这意味着当前环境信息，前端给浏览器发送一次即可记住，只要 session 有效，浏览器就不必再次告诉服务端当前的环境信息）。

另外我们还配置了一个拦截器，这个拦截器会拦截请求中 key 为 lang 的参数（不配置的话是 locale），这个参数则指定了当前的环境信息。

通过在请求中添加 lang 来指定当前环境信息。这个指定只需要一次即可，也就是说，在 session 不变的情况下，下次请求可以不必带上 lang 参数，
服务端已经知道当前的环境信息了。

默认情况下，我们的配置文件放在 resources 目录下，如果想自定义，也是可以的，例如定义在 resources/i18n 目录下：

但是这种定义方式系统就不知道去哪里加载配置文件了，此时还需要 application.properties 中进行额外配置(注意这是一个相对路径)：

```properties
spring.messages.basename=i18n/messages
```
另外还有一些编码格式的配置等，内容如下：

```properties
spring.messages.cache-duration=3600
spring.messages.encoding=UTF-8
spring.messages.fallback-to-system-locale=true
```
spring.messages.cache-duration 表示 messages 文件的缓存失效时间，如果不配置则缓存一直有效。

spring.messages.fallback-to-system-locale 属性则略显神奇，网上竟然看不到一个明确的答案，后来翻了一会源码才看出端倪。
这个属性的作用在 org.springframework.context.support.AbstractResourceBasedMessageSource#getDefaultLocale 方法中生效：

```java
protected Locale getDefaultLocale() {
	if (this.defaultLocale != null) {
		return this.defaultLocale;
	}
	if (this.fallbackToSystemLocale) {
		return Locale.getDefault();
	}
	return null;
}
```
从这段代码可以看出，在找不到当前系统对应的资源文件时，如果该属性为 true，则会默认查找当前系统对应的资源文件，否则就返回 null，
返回 null 之后，最终又会调用到系统默认的 messages.properties 文件。




### RestControllerAdvice 自定义处理代码

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandlerResolver {

    @Autowired
    MessageSource messageSource;

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R handleBodyValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<String, String>();
        //得到所有的属性错误
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        //将其组成键值对的形式存入map
        for (FieldError fieldError : fieldErrors) {
            String[] str= fieldError.getField().split("\\.");
            if(str.length>1){
                errors.put(str[1], fieldError.getDefaultMessage());
            }else {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }

            String message = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
            return R.failed(message);
        }
        log.error("参数绑定异常,ex = {}", errors);
        return R.failed("haha");
    }
}
```

## 关键代码

从请求头accept-language中取locale
org.apache.catalina.connector.Request#parseLocales

```java
/**
 * Parse request locales.
 */
protected void parseLocales(){
    localesParsed=true;
    // Store the accumulated languages that have been requested in
    // a local collection, sorted by the quality value (so we can
    // add Locales in descending order).  The values will be ArrayLists
    // containing the corresponding Locales to be added
    TreeMap<Double, ArrayList<Locale>>locales=new TreeMap<>();
    Enumeration<String> values=getHeaders("accept-language");
    while(values.hasMoreElements()){
        String value=values.nextElement();
        parseLocalesHeader(value,locales);
    }
    // Process the quality values in highest->lowest order (due to
    // negating the Double value when creating the key)
    for(ArrayList<Locale> list:locales.values()){
        for(Locale locale:list){
            addLocale(locale);
            }
    }
}
```

org.springframework.context.support.AbstractMessageSource#getMessage(
org.springframework.context.MessageSourceResolvable, java.util.Locale)

```java
@Override
public final String getMessage(MessageSourceResolvable resolvable,Locale locale)throws NoSuchMessageException{
    String[]codes=resolvable.getCodes();
    if(codes!=null){
        for(String code:codes){
            String message=getMessageInternal(code,resolvable.getArguments(),locale);
            if(message!=null){
                return message;
            }
        }
    }
    String defaultMessage=getDefaultMessage(resolvable,locale);
    if(defaultMessage!=null){
        return defaultMessage;
    }
    throw new NoSuchMessageException(!ObjectUtils.isEmpty(codes)?codes[codes.length-1]:"",locale);
}
```

org.hibernate.validator.messageinterpolation.AbstractMessageInterpolator#resolveMessage

```java
private String resolveMessage(String message,Locale locale){
    String resolvedMessage=message;
    ResourceBundle userResourceBundle=userResourceBundleLocator
        .getResourceBundle(locale);

    ResourceBundle constraintContributorResourceBundle=contributorResourceBundleLocator
        .getResourceBundle(locale);

    ResourceBundle defaultResourceBundle=defaultResourceBundleLocator
        .getResourceBundle(locale);

    String userBundleResolvedMessage;
    boolean evaluatedDefaultBundleOnce=false;
    do{
        // search the user bundle recursive (step 1.1)
        userBundleResolvedMessage=interpolateBundleMessage(
        resolvedMessage,userResourceBundle,locale,true
        );

        // search the constraint contributor bundle recursive (only if the user did not define a message)
        if(!hasReplacementTakenPlace(userBundleResolvedMessage,resolvedMessage)){
            userBundleResolvedMessage=interpolateBundleMessage(
            resolvedMessage,constraintContributorResourceBundle,locale,true
            );
        }

        // exit condition - we have at least tried to validate against the default bundle and there was no
        // further replacements
        if(evaluatedDefaultBundleOnce&&!hasReplacementTakenPlace(userBundleResolvedMessage,resolvedMessage)){
            break;
        }

        // search the default bundle non recursive (step 1.2)
        resolvedMessage=interpolateBundleMessage(
            userBundleResolvedMessage,
            defaultResourceBundle,
            locale,
            false
        );
        evaluatedDefaultBundleOnce=true;
    } while(true);
    
    return resolvedMessage;
}
```