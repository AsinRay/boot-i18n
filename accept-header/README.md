# I18n

## Controller I18n

spring boot对国际化的支持还是很好的，要实现国际化还简单。主要流程是通过配置spring boot的LocaleResolver解析器，当请求打到
spring boot的时候对请求的所需要的语言进解析，并保存在LocaleContextHolder中。之后就是根据当前的locale获取message。
spring boot中关于国际化消息处理的顶层接口是MessageSource，它有两个开箱即可用的实现

1. 书写国际化文件
   各文件以语言和国家代码来区分，如valid_zh_CN, valid_en_US,系统会自动匹配相应的properties文件，如果没有匹配到相关文件，
则使用默认的properties,即valid.properties.

2. 配置国际化文件的位置 application.yml
   ```yml
   spring:
    messages:
      basename: i18n/messages,i18n/valid # 多个文件用逗号分隔
   ```
    basename是指国际化文件的前半部分,如messages_en_US.properties的basename就是messages.
这一部分可以在application.yml进行设置，也可以使用在实现 WebMvcConfigurer 接口的配置类中定义，例如:

   ```java
    @Bean
    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        // 多个文件时，使用setBasenames,该方法支持可变参数，使用数组或者用逗号隔开,需要说明的是classpath:不可省略.
        messageSource.setBasenames("classpath:i18n/arena","classpath:i18n/valid");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
   ```

3. 配置localeResolver，解析当前请求的locale，LocaleResolver是个接口，它也有多种实现，当然可以根据自己的实际情况自已去实现，
默认的解析器是 AcceptHeaderLocaleResolver，通过获取请求头accept-language来获取当前的locale,浏览器的Header中的Accept-Language,
例如：
 Accept-Language: zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7
 Accept-Language: en-US,en;q=0.5
 
    依据业务需要使用 SessionLocaleResolver 或者其它的LocaleResolver。

## validator I18n

在以上的基础上，对validator的值做i18n的返回.

validator的实现原理：参数校验不通过时抛出 MethodArgumentNotValidException 异常， 因此我们只需要对controller的
MethodArgumentNotValidException 做对应的处理就可以实现。

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handler(final MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(" ; "));
        return "{\"err\":\"" + message + "\"}";
    }
}
```
以上使用的是系统默认的验证器，如果用户自己定义的验证器，则需要处理ConstraintViolationException.
验证异常就是这三种情况：
    BindException
    MethodArgumentNotValidException
    ConstraintViolationException

实现用户定义的约束可参照com.dd.anno包下相关实现。


