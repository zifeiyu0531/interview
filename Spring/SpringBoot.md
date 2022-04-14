
## SpringBoot
* 自动配置`Tomcat`
  * 引入Tomcat依赖
  * 配置Tomcat
* 自动配置`SpringMVC`
  * 引入SpringMVC全套组件
  * 自动配置SpringMVC常用组件（功能）
* 自动配置web场景功能，如：字符编码问题
* 默认包结构
  * `主程序`所在包及其之下所有`子包`里面的组件都会被默认扫描，无需以前的
  * 或者使用`@ComponentScan`指定扫描路径
* 各种配置拥有`默认值`
  * 默认配置最终映射到`@ConfigurationProperties`修饰的类中，类会在容器中创建对象
#### pom.xml
* `spring-boot-dependencies`: 核心依赖，在父工程中
* `spring-boot-starter`: 启动器，springboot将功能场景封装成一个个启动器，如`spring-boot-starter-web`、`spring-boot-starter-test`
#### @SpringBootApplication
标注在某个类上说明这个类是SpringBoot的主启动类
```java
@SpringBootApplication
public class SpringbootApplication {
    public static void main(String[] args) {
        // 1. 返回IOC容器
        ConfigurableApplicationContext run = SpringApplication.run(DemoApplication.class, args);
        // 2. 查看容器里注入的所有组件
        String[] names = run.getBeanDefinitionNames();
        for (String name : names) {
            System.out.println(name);
        }
        // 3. 从容器中获取组件
        User user = run.getBean("user", User.class);
    }
}
```

## SpringBoot底层注解
#### @Configuration
* 配置类中使用`@Bean`标注方法，向容器中注册`组件`，默认`单例`
* 配置类本身也是组件
* @Configuration的`proxyBeanMethod`属性表示是否`代理`Bean方法(true/false)
* 全量配置`Full`(proxyBeanMethods = true) 手动调用Bean方法时也会向容器中`检查`是否有对象
* 轻量配置`Lite`(proxyBeanMethods = false) 手动调用Bean方法时就新`创建`一个对象
```java
@Configuration // 告诉SpringBoot这是一个配置类 == 配置文件
public class MyConfig {
    
    @Bean // 向容器中添加组件，方法名为组件id，返回类型为组件类型，返回值为组件在容器中的实例
    public User user(){
        return new User("zhangsan", 18);
    }
}
```
#### @Import
用于导入组件，向`容器`中自动创建出`组件`，默认组件id为组件的`全类名`
```java
@Import({User.class})
@Configuration
public class MyConfig{
    //……
}
```
`ImportSelector`接口，返回需要导入的组件`全类名`数组
```java
public interface ImportSelector {
    // importingClassMetadata 标注了@Import的类的所有注解信息
    String[] selectImports(AnnotationMetadata importingClassMetadata);
}
```
```java
@Import({User.class, MyImportSelector.class})
@Configuration
public class MyConfig{
    //……
}
```
`ImportBeanDefinitionRegistrar`接口，手动注册组件
```java
public interface ImportBeanDefinitionRegistrar {
    /*
    * importingClassMetadata 当前被@Import修饰的类的注解信息
    * registry BeanDefinition注册类
    * 可以调用BeanDefinitionRegistry.registerBeanDefinition手动注册组件
    */
    default void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
            BeanNameGenerator importBeanNameGenerator) {

        registerBeanDefinitions(importingClassMetadata, registry);
    }
}
```
```java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean condition = registry.containsBeanDefinition("user");
        if (condition) {
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Car.class);
            registry.registerBeanDefinition("car", rootBeanDefinition);
        }
    }
}
```
#### @Conditional
条件装配，满足Conditional指定的条件则进行组件注入
`@ConditionalOnBean`：容器中`有`指定组件则进行相应操作
`@ConditionalOnMissingBean`：容器中`没有`指定组件则进行相应操作
```java
@Configuration
@ConditionalOnBean(name = "user")// 容器中有id=user的才执行
public class MyConfig1 {

    @ConditionalOnBean(Pet.class)// 容器中有Pet组件才注册
    @Bean
    public User user(){
        return new User("zhangsan", 18);
    }
}
```
#### @ImportResource
曾经的配置文件难以迁移，允许导入`配置文件`让其中定义的组件生效
```java
@Configuration
@ImportResource("classpath:beans.xml")
public class MyConfig{
    //……
}
```
#### @ConfigurationProperties
可以将`配置文件`(application.properties)中定义的值作为组件的属性值
```properties
my.car.brand=BMW
my.car.price=100000
```
1. @Component + @ConfigurationProperties
```java
@Component
@ConfigurationProperties("my.car")
public class Car {
    private String brand;
    private int price;
    //……
}
```
2. @EnableConfigurationProperties + @ConfigurationProperties
```java
@Configuration
@EnableConfigurationProperties(Car.class)
public class MyConfig{
    //……
}

@ConfigurationProperties("my.car")
public class Car {
    private String brand;
    private int price;
    //……
}
```

## SpringBoot自动配置原理
`@SpringBootApplication`下方三个核心注解
```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {}
```
#### @SpringBootConfiguration
本质上是一个`@Configuration`，代表当前是一个配置类
#### @EnableAutoConfiguration
```java
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {}
```
**1. @AutoConfigurationPackage**
利用`Registrar`给容器中导入`一系列组件`，制定了默认的包规则
将指定的一个包(`main所在包`)下的所有`组件`导入
```java
@Import(AutoConfigurationPackages.Registrar.class) // 给容器中导入组件
public @interface AutoConfigurationPackage {}
```
Registrar
```java
/**
* {@link ImportBeanDefinitionRegistrar} to store the base package from the importing configuration.
*/
static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        // 利用metadata获取包名(Application所在包)，将包下的注解批量注册
        register(registry, new PackageImports(metadata).getPackageNames().toArray(new String[0]));
    }

    @Override
    public Set<Object> determineImports(AnnotationMetadata metadata) {
        return Collections.singleton(new PackageImports(metadata));
    }
}
```
**2. @Import(AutoConfigurationImportSelector.class)**
```java
public String[] selectImports(AnnotationMetadata annotationMetadata) {
    if (!isEnabled(annotationMetadata)) {
        return NO_IMPORTS;
    }
    // 获取所有自动配置的集合
    AutoConfigurationEntry autoConfigurationEntry = getAutoConfigurationEntry(annotationMetadata);
    return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
}
```
1. `List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes)`获取到所有需要导入到容器中的配置类名
   1. `List<String> configurations = SpringFactoriesLoader.loadFactoryNames(getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader())`
      1. 通过调用`loadSpringFactories(ClassLoader classLoader)`返回一个`Map<String, List<String>>`，根据`FactoryClass`的name获取到对应的list，这里的name为`EnableAutoConfiguration`的全类名
         1. 扫描`META-INF/spring.factories`位置下的所有文件，如`spring-boot-autoconfigure-2.6.4.jar`下就有`META-INF/spring.factories`，文件里写死了启动时需要加载的所有配置类
2. 所有场景的自动配置在启动的时候默认全部加载，但最终会按照条件装配规则(`@Conditional`)按需配置
#### @ComponentScan
指定要扫描哪些包
#### 总结
- SpringBoot会先加载所有的自动配置类，`xxxxAutoConfiguration`
- 每个自动配置类按照条件(`@Conditional`)按需生效，配置类中的值会从`xxxxProperties`中拿，`xxxxProperties`和配置文件进行了绑定
- 生效的配置类会向容器中注册很多组件，这些注册的组件就对应实现相应功能
- 如果用户在应用中手动注册了某些组件，以用户注册的优先.
   - 用户自己使用`@Bean`替换底层组件
   - 用户去看这个组件是获取的`配置文件`的什么值，就在自己项目的配置文件中去定义对应的值

## SpringBoot Web开发
#### SpringMVC自动配置
Spring MVC自动配置类：`WebMvcAutoConfiguration`
```java
@Configuration(proxyBeanMethods = false)
@Import(EnableWebMvcConfiguration.class)
@EnableConfigurationProperties({ WebMvcProperties.class, WebProperties.class })
public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer, ServletContextAware {}
```
`WebMvcProperties == "spring.mvc"`
`WebProperties == "spring.web"`

#### 静态资源访问
将静态资源放在类路径下的`/static` `/public` `/resources` `/META-INF/resources`目录，访问时使用当前项目`根路径/ + 静态资源名 `
原理：静态映射默认拦截了所有请求`/**`
- `spring.web.resources.add-mappings=false` ：禁用静态资源访问
- `spring.mvc.static-path-pattern=/xxx/**` ： 修改访问静态资源时请求的前缀
- `spring.web.resources.static-locations=[classpath:/xxx/]` ：修改静态资源存放的路径

- 一个请求进来先找`Controller`能不能处理，如果不能处理再交给`静态资源`处理器

在`WebMvcAutoConfiguration`中
```java
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // 是否禁用静态资源
    if (!this.resourceProperties.isAddMappings()) {
        logger.debug("Default resource handling disabled");
        return;
    }
    // 将/webjars/**请求映射到classpath:/META-INF/resources/webjars/
    addResourceHandler(registry, "/webjars/**", "classpath:/META-INF/resources/webjars/");
    // 获取spring.mvc.static-path-pattern，默认值是/**
    addResourceHandler(registry, this.mvcProperties.getStaticPathPattern(), (registration) -> {
        // 获取spring.web.resources.static-locations
        registration.addResourceLocations(this.resourceProperties.getStaticLocations());
        if (this.servletContext != null) {
            ServletContextResource resource = new ServletContextResource(this.servletContext, SERVLET_LOCATION);
            registration.addResourceLocations(resource);
        }
    });
}
```


## 最佳实践
- 引入相关场景依赖
- 查看有哪些自动配置（配置文件中定义`debug=true`），是否需要修改
#### Lombok
简化`java bean`开发
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```
- `@Data`生成getter setter
- `@ToString`生成toString方法
- `@AllargsConstructor`生成有参构造器
- `@NoArgsConstructor`生成无参构造器
- `@EqualsAndHashCode`重新Equals和hashCode方法
- `Slf4j`注入日志类，`"log.info()"`
#### dev tools
热部署
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```
#### Spring Initializr
idea 快速创建
#### yaml
除了默认在`resources`目录下的`application.properties`文件，还可以使用`yaml`文件，使用`@ConfigurationProperties(prefix = "xxx")`和文件内的指定前缀相绑定，`properties`文件优先级比`yaml`高
- `key: value` kv之间有空格，大小写敏感
- 使用`缩进`表示层级关系
- 字符串无需加引号，如果要加，`'\n'`会将\n作为字符串输出、`"\n"`会将\n作为换行符输出

**对象**
```yaml
# 行内写法
k: {k1: v1, k2: v2, k3: v3}
# 一般写法
k:
  k1: v1
  k2: v2
  k3: v3
```
**数组**
```yaml
# 行内写法
k: [v1, v2, v3]
# 一般写法
k:
  - v1
  - v2
  - v3
```
#### 配置提示
导入`configuration-processor`编写配置文件绑定自定义类的时候会有提示
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```