```java
Thread.currentThread() // 获取当前线程
```
Thread相当于Runnable真实实现对象的`静态代理`

```java
// 获取系统类加载器
ClassLoader cloassLoader = ClassLoader.getSystemClassLoader();
// 获取系统类加载器的父加载器
ClassLoader parent = ClassLoader.getParent();
// 获取指定类的类加载器
ClassLoader classLoader = Class.forName("xxx.xxx.xxx").getClassLoader();
```

```java
class.getFields(); // 获取public属性
class.getDeclaredFields(); // 获取全部属性
```

```java
// 根据xml获取上下文信息
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
Object obj = context.getBean("objname")
```

```sql
insert into tablename (id) values (#{id}) #{}防止sql注入
```