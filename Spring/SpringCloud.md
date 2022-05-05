## 微服务技术栈
| 微服务技术条目                         | 落地技术                                                     |
| -------------------------------------- | ------------------------------------------------------------ |
| 服务开发                               | SpringBoot、Spring、SpringMVC等                              |
| 服务配置与管理                         | Netfix公司的Archaius、阿里的Diamond等                        |
| 服务注册与发现                         | Eureka、Consul、Zookeeper等                                  |
| 服务调用                               | Rest、PRC、gRPC                                              |
| 服务熔断器                             | Hystrix、Envoy等                                             |
| 负载均衡                               | Ribbon、Nginx等                                              |
| 服务接口调用(客户端调用服务的简化工具) | Fegin等                                                      |
| 消息队列                               | Kafka、RabbitMQ、ActiveMQ等                                  |
| 服务配置中心管理                       | SpringCloudConfig、Chef等                                    |
| 服务路由(API网关)                      | Zuul等                                                       |
| 服务监控                               | Zabbix、Nagios、Metrics、Specatator等                        |
| 全链路追踪                             | Zipkin、Brave、Dapper等                                      |
| 数据流操作开发包                       | SpringCloud Stream(封装与Redis，Rabbit，Kafka等发送接收消息) |
| 时间消息总栈                           | SpringCloud Bus                                              |
| 服务部署                               | Docker、OpenStack、Kubernetes等                              |

## Spring Cloud生态
- **Zuul** ： API网关
- **Eureka** ： 服务注册发现
- **Hystrix** ： 熔断、降级、限流
- **Feign** ： 通信方式，基于`HttpClient`，同步，阻塞
- **Ribbon** ：负载均衡器
SpringCloud 中文API文档：https://springcloud.cc/spring-cloud-dalston.html
SpringCloud中国社区：http://springcloud.cn/
SpringCloud中文网：https://springcloud.cc

## Eureka
#### Eureka的自我保护机制
- 默认情况下，当eureka server在一定时间内没有收到实例的心跳，便会把该实例从注册表中删除（默认是90秒），但是，如果**短时间内丢失大量的实例心跳**，便会触发eureka server的自我保护机制，比如在开发测试时，需要频繁地重启微服务实例，但是我们很少会把eureka server一起重启（因为在开发过程中不会修改eureka注册中心），当一分钟内收到的心跳数大量减少时，会触发该保护机制。可以在eureka管理界面看到Renews threshold和Renews(last min)，当后者（最后一分钟收到的心跳数）小于前者（心跳阈值）的时候，触发保护机制，会出现红色的警告：
`EMERGENCY!EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT.RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEGING EXPIRED JUST TO BE SAFE.`
- 从警告中可以看到，eureka认为虽然收不到实例的心跳，但它认为实例还是健康的，eureka会保护这些实例，不会把它们从注册表中删掉。
- 该保护机制的目的是避免网络连接故障，在发生**网络故障**时，微服务和注册中心之间无法正常通信，但服务本身是健康的，不应该注销该服务，如果eureka因网络故障而把微服务误删了，那即使网络恢复了，该微服务也不会重新注册到eureka server了，因为只有在微服务启动的时候才会发起注册请求，后面只会发送心跳和服务列表请求，这样的话，该实例虽然是运行着，但永远不会被其它服务所感知。所以，eureka server在短时间内丢失过多的客户端心跳时，会进入自我保护模式，该模式下，eureka会保护注册表中的信息，不在注销任何微服务，当网络故障恢复后，eureka会自动退出保护模式。自我保护模式可以让集群更加健壮。
#### Eureka和ZooKeeper对比
著名的CAP理论指出，一个分布式系统不可能同时满足C (一致性) 、A (可用性) 、P (容错性)，由于分区容错性P在分布式系统中是必须要保证的，因此我们只能在A和C之间进行权衡。
`Zookeeper` 保证的是 `CP` —> 满足一致性，分区容错的系统，通常性能不是特别高
`Eureka` 保证的是 `AP` —> 满足可用性，分区容错的系统，通常可能对一致性要求低一些
**Zookeeper保证的是CP**
当向注册中心查询服务列表时，我们可以容忍注册中心返回的是几分钟以前的注册信息，但不能接受服务直接down掉不可用。也就是说，服务注册功能对可用性的要求要高于一致性。但zookeeper会出现这样一种情况，当`master`节点因为网络故障与其他节点失去联系时，剩余节点会重新进行leader选举。问题在于，选举leader的时间太长，30-120s，且选举期间整个zookeeper集群是不可用的，这就导致在选举期间注册服务瘫痪。在云部署的环境下，因为网络问题使得zookeeper集群失去master节点是较大概率发生的事件，虽然服务最终能够恢复，但是，漫长的选举时间导致注册长期不可用，是不可容忍的。
**Eureka保证的是AP**
Eureka看明白了这一点，因此在设计时就优先保证`可用性`。Eureka各个节点都是平等的，几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供注册和查询服务。而Eureka的客户端在向某个Eureka注册时，如果发现连接失败，则会自动切换至其他节点，只要有一台Eureka还在，就能保住注册服务的可用性，只不过查到的信息`可能不是最新的`，除此之外，Eureka还有自我保护机制，如果在15分钟内超过85%的节点都没有正常的心跳，那么Eureka就认为客户端与注册中心出现了网络故障，此时会出现以下几种情况：
- Eureka不再从注册列表中移除因为长时间没收到心跳而应该过期的服务
- Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其他节点上 (即保证当前节点依然可用)
- 当网络稳定时，当前实例新的注册信息会被同步到其他节点中

因此，Eureka可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像zookeeper那样使整个注册服务瘫痪

## Ribbon
客户端负载均衡工具
**集中式LB**
即在服务的提供方和消费方之间使用`独立`的LB设施，如`Nginx`(反向代理服务器)，由该设施负责把访问请求通过某种策略转发至服务的提供方
**进程式LB**
将LB逻辑集成到`消费方`（客户端），消费方从服务注册中心获知有哪些地址可用，然后自己再从这些地址中选出一个合适的服务器。
Ribbon就属于进程式LB，它只是一个类库，集成于消费方进程，消费方通过它来获取到服务提供方的地址

## Feign
`@FeignClient`
前面在使用Ribbon + RestTemplate时，利用RestTemplate对Http请求的封装处理，形成了一套模板化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一个客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步的封装，由他来帮助我们定义和实现依赖服务接口的定义，在Feign的实现下，我们只需要创建一个`接口`并使用`注解`的方式来配置它 (类似以前Dao接口上标注Mapper注解，现在是一个微服务接口上面标注一个Feign注解)，即可完成对服务提供方的接口绑定，简化了使用Spring Cloud Ribbon 时，自动封装服务调用客户端的开发量。
Feign默认集成了Ribbon

## Hystrix
#### 服务雪崩
多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其他的微服务，这就是所谓的“扇出”，如果扇出的链路上某个微服务的调用响应时间过长，或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”。
#### 什么是Hystrix
Hystrix是一个应用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时，异常等，**Hystrix 能够保证在一个依赖出问题的情况下，不会导致整个体系服务失败**，避免级联故障，以提高分布式系统的弹性。
“断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控 (类似熔断保险丝) ，向调用方返回一个服务预期的，可处理的**备选响应** (FallBack) ，而不是长时间的等待或者抛出调用方法无法处理的异常，这样就可以保证了服务调用方的线程不会被长时间，不必要的占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。
#### Hystrix的功能
**1. 服务熔断**
当扇出链路的某个微服务不可用或者响应时间太长时，会进行服务的降级，进而`熔断`该节点微服务的调用，快速返回错误的响应信息。检测到该节点微服务调用响应正常后恢复调用链路。在SpringCloud框架里熔断机制通过Hystrix实现。Hystrix会监控微服务间调用的状况，当失败的调用到一定阀值，缺省是5秒内20次调用失败，就会启动熔断机制。熔断机制的注解是：`@HystrixCommand` + `@EnableCircuitBreaker`
```java
@HystrixCommand(fallbackMethod = "xxx")
@GetMapping("/user/{id}")
public User getUser(@PathVariable("id") Long id){
    //...
}
```
**2. 服务降级**
服务降级是指 当服务器压力剧增的情况下，根据实际业务情况及流量，为了保证重要或基本的服务能正常运行，可以将一些不重要或不紧急的服务或任务进行`延迟使用`或`暂停使用`。
自动降级:
1. 超时降级：主要配置好超时时间和超时重试次数和机制，并使用异步机制探测回复情况
2. 失败次数降级：主要是一些不稳定的api，当失败调用次数达到一定阀值自动降级，同样要使用异步机制探测回复情况
3. 故障降级：比如要调用的远程服务挂掉了（网络故障、DNS故障、http服务返回错误的状态码、rpc服务抛出异常），则可以直接降级。降级后的处理方案有：默认值（比如库存服务挂了，返回默认现货）、兜底数据（比如广告挂了，返回提前准备好的一些静态页面）、缓存（之前暂存的一些缓存数据）
4. 限流降级：秒杀或者抢购一些限购商品时，此时可能会因为访问量太大而导致系统崩溃，此时会使用限流来进行限制访问量，当达到限流阀值，后续请求会被降级；降级后的处理方案可以是：排队页面（将用户导流到排队页面等一会重试）、无货（直接告知用户没货了）、错误页（如活动太火爆了，稍后重试）。

实现`FallbackFactory`，重写`Object create(Throwable)`方法，注册到容器中，在`@FeignClient`中定义`fallbackFactory`属性，配置文件中配置`feign.hystrix.enable = true`开启降级
```java
@Component
public class MyServiceFallbackFacotry implements FallbackFactory {
    @Override
    public UserService create(Throwable throwable) {
        return new UserService() {
            @Override
            public User getUser(Long id){
                return new User();
            }
            //...
        }
    }
}

@Component
@FeignClient(value = "SERVICE-ID", fallbackFactory = MyServiceFallbackFacotry.class)
public interface UserService {
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") Long id);
    //...
}
```
**3. 服务限流**
**4. 监控**
`spring-cloud-starter-hystrix-dashboard` + `@EnableHystrixDashboard`
#### 服务熔断和降级的区别
**服务熔断** —> 服务端：某个服务超时或异常，引起熔断，类似于保险丝(自我熔断)
**服务降级** —> 客户端：从整体网站请求负载考虑，当某个服务熔断或者关闭之后，服务将不再被调用，此时在客户端，我们可以准备一个`FallBackFactory`，返回一个默认的值(缺省值)
- 触发原因：服务熔断一般是某个服务（下游服务）故障引起，而服务降级一般是从整体负荷考虑
- 管理目标：熔断其实是一个框架级的处理，每个微服务都需要（无层级之分），而降级一般需要对业务有层级之分（比如降级一般是从最外围服务开始）

## Zuul
Zull包含了对请求的**路由**和**过滤**两个最主要功能：
其中**路由**功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础，而**过滤**功能则负责对请求的处理过程进行干预，是实现请求校验，服务聚合等功能的基础。Zuul和Eureka进行整合，将Zuul自身注册为Eureka服务治理下的应用，同时从Eureka中获得其他服务的消息，也即以后的访问微服务都是通过Zuul跳转后获得。
注意：Zuul服务最终还是会注册进Eureka
#### Zuul的使用
`spring-cloud-starter-zuul` + `@EnableZuulProxy`
默认访问`zuul的地址/服务id/`访问指定服务
通过配置可以不暴露服务id
```yaml
zuul:
  routes:
    aaa:
      serviceId: 服务id
      path: /自定义路径/**
    bbb:
      serviceId: 服务id
      path: /自定义路径/**
  ignored-services: "*"   # 禁止默认路由规则
  prefix: /zifeiyu # 设置公共前缀
```

## SpringCloud config分布式配置中心
微服务意味着要将单体应用中的业务拆分成一个个子服务，每个服务的粒度相对较小，因此系统中会出现大量的服务，由于每个服务都需要必要的`配置信息`才能运行，所以一套集中式的，动态的配置管理设施是必不可少的。spring cloud提供了configServer来解决这个问题，我们每一个微服务自己带着一个application.yml，那上百个的配置文件修改起来，令人头疼！
#### 什么是SpringCloud config
spring cloud config 为微服务架构中的微服务提供集中化的外部支持，配置服务器为各个不同微服务应用的所有环节提供了一个中心化的外部配置。
spring cloud config 分为**服务端**和**客户端**两部分。客户端连接服务端，服务端连接远程仓库
- **服务端**也称为`分布式配置中心`，它是一个独立的微服务`应用`，用来连接配置服务器并为客户端提供获取配置信息，加密，解密信息等访问接口。
- **客户端**则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心`获取和加载`配置信息。配置服务器默认采用`git`来存储配置信息，这样就有助于对环境配置进行版本管理。并且可用通过git客户端工具来方便的管理和访问配置内容。
#### SpringCloud config的作用
- 集中式管理配置文件
- 不同环境，不同配置，动态化的配置更新，分环境部署，比如 /dev /test /prod /beta /release
- 运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息
- 当配置发生变动时，服务不需要重启，即可感知到配置的变化，并应用新的配置
将配置信息以REST接口的形式暴露
- spring cloud config 分布式配置中心与GitHub整合
#### SpringCloud config使用
**远程仓库**
在远程仓库编写配置文件`application.yml`
```yaml
spring:
  profiles:
    active: dev

---
spring:
  profiles: dev
  application:
    name: springcloud-config-dev

---
spring:
  profiles: test
  application:
    name: springcloud-config-test
```
**服务端**
`spring-cloud-config-server` + `@EnableConfigServer`
配置，连接远程仓库
```yaml
spring:
  application:
    name: springcloud-config-server
  # 连接远程仓库
  cloud:
    config:
      server:
        git:
          uri: https://github.com/zifeiyu0531/xxx.git
```
通过以下方式获取配置文件
```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```
**客户端**
`spring-cloud-starter-config`
在远程仓库编写`xxx.yml`
配置，连接服务端
```yaml
spring:
  application:
    name: springcloud-config-client
  # 连接服务端
  cloud:
    config:
      name: xxx # 需要从git上读取的资源名称
      uri: http://服务端地址
      profile: dev  # 环境
      label: master # 分支

```
通过`@Value("${xxx.xxx.xxx}")`获取远程配置信息