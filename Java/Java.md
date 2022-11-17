## final、finally、finalize的区别
**final**
final修饰符有三种用法：如果一个`类`被声明为final，意味着它不能再派生出新的子类，即不能被继承，将`变量`声明为final，可以保证它们在使用中不被改变，被声明为final的变量的引用中只能读取不可修改。被声明为final的`方法`也同样只能使用，不能在子类中被重写。
**finally**
通常放在try…catch…的后面构造总是执行代码块，这就意味着程序无论正常执行还是发生异常，这里的代码只要JVM不关闭都能执行，可以将释放外部资源的代码写在finally块中。
**finalize**
Object类中定义的方法，Java中允许使用finalize()方法在垃圾收集器将对象从内存中清除出去之前做必要的`清理`工作。这个方法是由垃圾收集器在销毁对象时调用的，通过重写finalize()方法可以整理系统资源或者执行其他清理工作。

## static关键字
static是一个修饰符，用于修饰成员。由static修饰的变量，方法称为静态变量，方法。
**static关键字修饰类**
普通类是不允许声明为静态的，只有`内部类`才可以
**static关键字修饰方法**
可以直接通过类名来进行调用
**static关键字修饰变量**
被static修饰的成员变量叫做静态变量，也叫做类变量，说明这个变量是属于这个类的，而不是属于是对象，没有被static修饰的成员变量叫做实例变量，说明这个变量是属于某个具体的对象的。
**static关键字修饰代码块**
静态代码块在类第一次被载入时执行。类初始化的顺序：父类静态代码块 → 子类静态代码块 → 父类构造函数 → 子类构造函数
**注意事项：**
1. 虽然静态成员也可以使用`对象.静态成员`，但这样不好区分静态成员和非静态成员。
2. 静态方法中不可以使用this关键字。
3. 静态方法中不可以直接调用非静态方法
4. 不能将方法中的局部变量设为静态的
5. 可以使用静态区域先执行类的初始化。

## String类是final修饰的
**安全**
```java
public static void main(String[] args){
    HashSet<StringBuilder> hs = new HashSet<StringBuilder>();
    StringBuilder sb1 = new StringBuilder("aaa");
    StringBuilder sb2 = new StringBuilder("aaabbb");
    hs.add(sb1);
    hs.add(sb2);            //这时候HashSet里是{"aaa","aaabbb"}

    StringBuilder sb3=sb1;
    sb3.append("bbb");      //这时候HashSet里是{"aaabbb","aaabbb"}
    System.out.println(hs);
}
```
因为`StringBuilder`没有不可变性的保护，sb3直接在原先"aaa"的地址上改。导致sb1的值也变了。这时候，`HashSet`上就出现了两个相等的键值"aaabbb"。破坏了`HashSet`键值的唯一性。所以千万不要用可变类型做`HashMap`和`HashSet`键值。
**线程安全**
不可变对象不能被写，所以线程安全。
**效率高**
如果是字符串`常量形式的声明`首先会查看`常量池`中是否存在这个常量，如果存在就不会创建新的对象，否则在在常量池中创建该字符串并创建引用，此后不论以此种方式创建多少个相同的字符串都是指向这一个地址的引用，而不再开辟新的地址空间放入相同的数据；但是字符串对象每次`new`都会在`堆区`形成一个新的内存区域并填充相应的字符串，不论堆区是否已经存在该字符串

## String、StringBuffer、StringBuilder的区别
**String - 字符串常量**
String的值是不可变的，这就导致每次对String的操作都会生成`新的String对象 `
**StringBuffer、StringBuilder - 字符串变量**
StringBuffer和StringBuilder类的对象能够被多次的修改，并且不产生新的未使用对象。StringBuilder的方法`不是线程安全`的（不能同步访问）。由于StringBuilder相较于StringBuffer有速度优势，所以多数情况下建议使用StringBuilder类。然而在应用程序要求线程安全的情况下，则必须使用StringBuffer类。StringBuffe很多方法都是synchronized修饰的：length()、append()。

## transient关键字
将`不需要序列化`的属性前添加关键字`transient`，序列化对象的时候，这个属性就不会被序列化。

## 红黑树
* 当`冲突`(链表长度)大于等于8时，就会将冲突的Entry转换为`红黑树`进行存储。
![](pic/rbtree.png)

红黑树是一种`自平衡二叉查找树`，是一种特化的AVL树（平衡二叉树）。每一颗红黑树都是一颗`二叉排序树`。
#### 红黑树的特点
1. 每个结点要么是红的，要么是黑的。  
2. `根结点`是黑的。  
3. 每个`叶结点`（叶结点即指树尾端NIL指针或NULL结点）是黑的。  
4. 如果一个结点是红的，那么它的`俩个儿子`都是黑的。  
5. 对于任一结点而言，其到叶结点树尾端NIL指针的每一条路径都包含`相同数目的黑结点`。
#### 红黑树添加节点
* 规则：新添加的节点都为`红色`
1. 新节点位于`根节点`：将该节点直接设为`黑色`即可
2. 新节点的父节点是`黑色`时：不用动，这已经是一颗红黑树
3. 父节点和叔节点都是`红色`时：a.将父节点和叔节点设为`黑色`；b.将祖父节点设为`红色`；c.将祖父节点设为`当前节点`，并继续对新当前节点进行操作
4. 父节点是`红色`，叔节点是`黑色`时，又分如下四种情况：
当前节点是父亲的左孩子，父亲是祖父的左孩子(**Left-Left**)：a.将`祖父节点右旋`；b.交换父节点和祖父节点的颜色
当前节点是父亲的右孩子，父亲是祖父的左孩子(**Right-Left**)：a.将`父节点左旋`，并将父节点作为当前节点；b.然后再使用`Left-Left`情形
当前节点是父亲的右孩子，父亲是祖父的右孩子(**Right-Right**)：a.将`祖父节点左旋`;b.交换父节点和祖父节点的颜色
当前节点是父亲的左孩子，父亲是祖父的右孩子(**Left-Right**)：a.将`父节点右旋`，并将父节点作为当前节点；b.然后再使用`Right-Right`情形

**左旋**&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;**右旋**
<figure class="half">
    <img src="./pic/left.png">
    <img src="./pic/right.png">
</figure>
#### 红黑树删除节点
1. 删除的是`根节点`，则直接将根节点置为null;
2. 待删除节点的左右子节点`都为null`，删除时将该节点置为null;
3. 待删除节点的左右子节点`有一个有值`，则用有值的节点替换该节点即可；
4. 待删除节点的左右子节点`都不为null`，则找前驱或者后继，将前驱或者后继的值复制到该节点中，然后删除前驱或者后继（`前驱：左子树中值最大的节点，后继：右子树中值最小的节点`）；
#### 红黑树和平衡二叉树
- 平衡二叉树的左右子树的高度差绝对值不超过1，但是红黑树在某些时刻可能会`超过1`，只要符合红黑树的五个条件即可。
- 二叉树只要不平衡就会进行`旋转`，而红黑树不符合规则时，有些情况只用改变颜色不用旋转，就能达到平衡。
#### 效率对比
|      | 二叉查找树 | 平衡二叉树 | 红黑树  |
| ---- | ---------- | ---------- | ------- |
| 查找 | O(n)       | O(logn)    | O(logn) |
| 插入 | O(n)       | O(logn)    | O(logn) |
| 删除 | O(n)       | O(logn)    | O(logn) |

## HashMap
#### HashMap底层数据结构
HashMap的主干是一个`Node数组`。它实现了Map.Entry接口。Entry是HashMap的`基本组成单元`，每一个Entry包含一个`key-value`键值对。代码如下
```java
static class Entry<K,V> implements Map.Entry<K,V> {
    final K key;
    V value;
    Entry<K,V> next;    //存储指向下一个Entry的引用，单链表结构
    int hash; 
    Entry(int h, K k, V v, Entry<K,V> n) {
        value = v;
        next = n;
        key = k;
        hash = h;
    }
}

static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;

    ...
}

transient Node<K,V>[] table;

static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;  // red-black tree links
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;
    
    ...
} 
```
简单来说，HashMap由`数组 + 链表(红黑树)`组成的，数组是HashMap的主体，链表(红黑树)则是主要为了解决`哈希冲突`而存在的，在链表长度大于`8`并且表的长度大于`64`的时候会转化红黑树。如果定位到的数组位置不含链表（当前entry的next指向null）,那么查找，添加等操作很快，仅需一次寻址即可；如果定位到的数组包含链表，对于添加操作，其时间复杂度为O(n)，首先遍历链表，存在即覆盖，否则新增；对于查找操作来讲，仍需`遍历链表`，然后通过key对象的equals方法逐一比对查找。
#### HashMap扩容机制
变量**size**，它记录HashMap的底层Entry数组中`已用槽的数量`；
变量**threshold**，它是HashMap的`阈值`，（threshold = 容量*负载因子）    
变量**loadFactor**，负载因子，默认为`0.75`
* 负载因子是0.75的时候，空间利用率比较高，而且避免了相当多的Hash冲突，使得底层的链表或者是红黑树的高度比较低，提升了空间效率。

当发生`哈希冲突`并且`size大于阈值`的时候，需要进行数组扩容，扩容时，需要新建一个长度为之前数组`2倍`的新的数组，然后将当前的Entry数组中的元素全部传输过去，扩容后的新数组长度为之前的2倍。
元素在重新计算hash之后，因为n变为2倍，那么n-1的mask范围在高位多1bit(红色)，所以，元素的位置要么是在原位置，要么是在原位置再移动2次幂的位置。因此，我们在扩充HashMap的时候，不需要像JDK1.7的实现那样重新计算hash，只需要看看原来的hash值新增的那个bit是**1**还是**0**就好了，是0的话索引没变，是1的话索引变成“**原索引+oldCap**”
JDK8 是等链表整个 while 循环结束后，才给数组赋值，此时使用局部变量 `loHead` 和 `hiHead` 来保存链表的值，因为是局部变量，所以多线程的情况下，肯定是没有问题的。　　为什么有 loHead 和 hiHead 两个新老值来保存链表呢，主要是因为扩容后，链表中的元素的索引位置是可能发生变化的，代码注释中举了一个例子：　　数组大小是 8 ，在数组索引位置是 1 的地方挂着一个链表，链表有两个值，两个值的 hashcode 分别是是 9 和 33。当数组发生扩容时，新数组的大小是 16，此时 hashcode 是 33 的值计算出来的数组索引位置仍然是 1，我们称为老值(loHead)，而 hashcode 是 9 的值计算出来的数组索引位置却是 9，不是 1 了，索引位置就发生了变化，我们称为新值(hiHead)。
```java
void resize(int newCapacity) {
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // 默认原数组长度2倍
    }
    //...
    if (oldTab != null) {
    // 从索引 0 开始逐个遍历旧 table
    for (int j = 0; j < oldCap; ++j) {
        Node<K,V> e;
        if ((e = oldTab[j]) != null) {
            oldTab[j] = null;
            if (e.next == null)    // 链表只有一个元素
                newTab[e.hash & (newCap - 1)] = e;
            else if (e instanceof TreeNode)    // 红黑树，先不管
                ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
            else { // preserve order
                // 拆链表，拆成两个子链表：索引不变的元素链表和有相同偏移量的元素链表
                // 每个链表都保持原有顺序
                Node<K,V> loHead = null, loTail = null;
                Node<K,V> hiHead = null, hiTail = null;
                Node<K,V> next;
                do {
                    next = e.next;
                    if ((e.hash & oldCap) == 0) {
                        // 索引不变的元素链表
                        if (loTail == null)
                            loHead = e;
                        else    // 通过尾部去关联 next，维持了元素原有顺序
                            loTail.next = e;
                        loTail = e;
                    }
                    else {
                        // 相同偏移量的元素链表
                        if (hiTail == null)
                            hiHead = e;
                        else    // 通过尾部去关联 next，维持了元素原有顺序
                            hiTail.next = e;
                        hiTail = e;
                    }
                } while ((e = next) != null);
                if (loTail != null) {
                    loTail.next = null;
                    newTab[j] = loHead;
                }
                if (hiTail != null) {
                    hiTail.next = null;
                    newTab[j + oldCap] = hiHead;
                }
            }
        }
    }
}
}
```
#### HashMap put元素的过程
当准备添加一个key-value对时，首先通过`hash(key)`方法计算hash值，然后通过`indexFor(hash,length)`求该key-value对的存储位置，计算方法是`hash&(length-1)`，这就保证每一个key-value对都能存入HashMap中，当计算出的位置相同时，由于存入位置是一个链表，则把这个key-value对插入`链表尾`，在jdk1.8之前是插入头部的，在jdk1.8中是插入尾部的。
```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}

//...putVal
for (int binCount = 0; ; ++binCount) {
    //e是p的下一个节点
    if ((e = p.next) == null) {
        //插入链表的尾部
        p.next = newNode(hash, key, value, null);
        //如果插入后链表长度大于8则转化为红黑树
        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
            treeifyBin(tab, hash);
        break;
    }
    //如果key在链表中已经存在，则退出循环
    if (e.hash == hash &&
        ((k = e.key) == key || (key != null && key.equals(k))))
        break;
    p = e;
}
//如果key在链表中已经存在，则修改其原先的key值，并且返回老的值
if (e != null) { // existing mapping for key
    V oldValue = e.value;
    if (!onlyIfAbsent || oldValue == null)
        e.value = value;
    afterNodeAccess(e);
    return oldValue;
}
```
#### 头插法存在的问题
链表成环：多线程HashMap扩容时发生
插入的时候和平时我们追加到尾部的思路是不一致的，是链表的头结点开始循环插入，导致插入的顺序和原来链表的顺序相反的。table 是共享的，table 里面的元素也是共享的，while 循环都直接修改 table 里面的元素的 next 指向，导致指向混乱。

#### 为什么HashMap容量为2的次幂
HashMap的数组长度一定保持2的次幂，比如16的二进制表示为10000，那么length-1就是15，二进制为01111，同理扩容后的数组长度为32，二进制表示为100000，length-1为31，二进制表示为011111。从下图可以我们也能看到这样会保证`低位全为1`，而扩容后只有一位差异，也就是多出了`最左位的1`，会使得获得的数组索引index更加均匀。如果低位全部为1，那么对于hash低位部分来说，任何一位的变化都会对结果产生影响，也就是说，要得到index=21这个存储位置，hash的低位只有这一种组合。减小哈希冲突出现的可能性。
#### HashMap和HashTable的区别
HashMap只实现`Map`接口，HashTable继承`Dictionary`类，实现Map接口。
HashMap是非线程安全的，HashTable是`线程安全`的，用Synchronize修饰方法。
HashMap计算hash：key的hash值高16位不变，低16位与高16位异或作为key最终的hash值。
Hashtable计算hash：直接使用对象自身的hash。
HashMap计算index = hash & (length-1)
Hashtable计算index = (hash & 0x7FFFFFFF) % tab.length
Hashtable默认的初始大小为11，之后每次扩充，容量变为原来的`2n+1`。HashMap默认的初始化大小为16。之后每次扩充，容量变为原来的`2倍`。

## Java的四种引用
**强引用**
是指创建一个对象并把这个对象赋给一个引用变量。
```java
Object object =new Object();
String str ="hello";
```
强引用有引用变量指向时永远不会被垃圾回收，JVM宁愿抛出OutOfMemory错误也不会回收这种对象。
**软引用**
如果一个对象具有软引用，内存空间足够，垃圾回收器就不会回收它；如果`内存空间不足`了，就会回收这些对象的内存。软引用可用来实现内存敏感的`高速缓存`,比如网页缓存、图片缓存等。使用软引用能防止内存泄露，增强程序的健壮性。   
**弱引用**
弱引用也是用来描述非必需对象的，当JVM进行垃圾回收时，`无论内存是否充足`，都会回收被弱引用关联的对象。在java中，用java.lang.ref.WeakReference类来表示。
**虚引用**
虚引用和前面的软引用、弱引用不同，它并不影响对象的生命周期。在java中用java.lang.ref.PhantomReference类表示。如果一个对象与虚引用关联，则跟`没有引用`与之关联一样，在任何时候都可能被垃圾回收器回收。

## ArrayList、LinkedList、Vector
**ArrayList**
作为List的主要实现类；主要特点有`线程不安全`，但是执行效率`高效`，底层实现是`数组`结构（Collections中定义了synchronizedList(List list)将此ArrayList转化为线程安全的）默认构造方法创建一个空数组，第一次添加元素时候，扩展容量为10，之后的扩充算法：原来数组大小+原来数组的一半（也就是1.5倍）。
**LinkedList**
对于频繁的`插入、删除`操作，我们建议使用此类，因为它的执行效率高；但是`内存`消耗比ArrayList大；底层实现的`双向链表`实现。
**Vector**
List的古老实现类；`线程安全`，效率低；底层使用`数组`实现的（如今逐渐被淘汰）默认构造方法创建一个大小为10的对象数组，如果增量为0的情况下每次扩容容量是翻倍，即为原来的2倍，而当增量>0的时候，扩充为原来的大小加增量。

## equals与==的区别
`基本数据类型`byte,short,char,int,long,float,double,boolean。他们之间的比较，用双等号 == ,比较的是他们的`值`。
equals()是Object里的方法。在Object的equals中，就是使用 == 来进行比较，比较的是`内存地址`。与==不同的是，在某些Object的子类中，覆盖了equals()方法，比如String中的equals()方法比较两个字符串对象的`内容`是否相同。
```java
Integer d3 = 1;
Integer d4 = 1;
Integer d5 = 128;
Integer d6 = 128;
System.out.println(d3 == d4); 
//结果：true
System.out.println(d5 == d6); 
//结果：false
```
都是给Integer类型的参数，直接赋值后进行比较。d3和d4判断的结果相等，但d5和d6判断的结果却不相等。
因为Integer有一个**常量池**，-128~127直接的Integer数据直接缓存进入常量池。所以1在常量池，而128不在。
```java
String e = "abc";
String f = "abc";
String g = new String("abc");
String h = new String("abc");
System.out.println(e == f); 
//结果：true
System.out.println(e == g); 
//结果：false
System.out.println(g == h); 
//结果：false
```

## Java自动拆装箱
JDK5提供了一个新特性，当基本类型和引用类型`相互赋值`时，会自动调用装箱或拆箱方法，自动补全代码
自动装箱：基本数据类型默认提升它对应的包装类型：int-->Integer
自动拆箱：对应的包装类类型自动转换为基本类型：Integer-->int


## Java序列化
**实现序列化**
* 让类实现`Serializable`接口,该接口是一个标志性接口,标注该类对象是可被序列
* 然后使用一个`输出流`来构造一个对象输出流并通过`writeObject(Obejct)`方法就可以将实现对象写出
* 如果需要反序列化,则可以用一个`输入流`建立对象输入流,然后通过`readObeject`方法从流中读取对象

**作用**
* 序列化就是一种用来处理`对象流`的机制,所谓对象流也就是将对象的内容进行流化,可以对流化后的对象进行`读写`操作,也可以将流化后的对象`传输`于网络之间;
* 为了解决对象流读写操作时可能引发的问题(如果不进行序列化,可能会存在数据`乱序`的问题)
* 序列化除了能够实现对象的持久化之外，还能够用于对象的`深度克隆`

## JDBC是什么
Java数据库连接(JDBC)是Java语言中用来规范客户端程序如何来访问数据库的`应用程序接口`，提供了诸如查询和更新数据库中数据的方法。
**JDBC驱动程序：**
* JDBC-ODBC桥
这种类型的驱动把所有JDBC的调用传递给ODBC，再让后者调用数据库本地驱动代码
* 本地API驱动
这种类型的驱动通过客户端加载数据库厂商提供的本地代码库（C/C++等）来访问数据库，而在驱动程序中则包含了Java代码。
* 网络协议驱动
这种类型的驱动给客户端提供了一个网络API，客户端上的JDBC驱动程序使用套接字（Socket）来调用服务器上的中间件程序，后者在将其请求转化为所需的具体API调用。
* 本地协议驱动
这种类型的驱动使用Socket，直接在客户端和数据库间通信。

## int 和 Integer
`int`是java的`基本数据类型`。
`Integer`继承了Object类，是`对象类型`，是 int 的`包装类`。
**区别**
* 值的存储
int 存储在`栈`中
Integer 对象的`引用`存储在`栈`空间中，对象的`数据`存储在`堆`空间中。
* 初始化
int 初始化值为`0`。
Integer 初始化值为`null`。
* 传参
int 是`值传递`，栈中的数据不可变。
Integer 对象是`引用传递`，引用不可变，但是引用指向的堆空间地址中的值是可以改变的。
* 泛型支持
泛型不支持int，但是支持Integer。
* 运算
int 可以直接做运算，是类的特性。
Integer 的对象可以调用该类的方法，但是在拆箱之前不能进行运算，需要转化为基本类型int。

## 反射
JAVA反射机制是在`运行状态`中，对于任意一个类，都能够知道这个类的所有属性和方法；对于任意一个对象，都能够调用它的任意方法和属性；这种动态获取信息以及动态调用对象方法的功能称为java语言的反射机制。
在日常的第三方应用开发过程中，经常会遇到某个类的某个成员变量、方法或是属性是私有的或是只对系统应用开放，这时候就可以利用Java的反射机制通过反射来获取所需的私有成员或方法。
#### 反射的原理
**核心**:JVM在运行时才动态加载类或者调用方法以及访问属性,不需要事先(比如编译时)知道运行对象是什么
1. 当我们编写完一个Java项目之后，每个java文件都会被编译成一个`.class`文件。
2. 这些class文件在程序运行时会被`ClassLoader`加载到JVM中，当一个类被加载以后，JVM就会在内存中自动产生一个`Class对象`。
3. 通过Class对象获取`Field/Method/Construcor`

反射是什么呢？当我们的程序在运行时，需要`动态`的加载一些类这些类可能之前用不到所以不用加载到jvm，而是在运行时根据需要才加载。
原来使用new的时候，需要明确的指定类名，这个时候属于硬编码实现，而在使用反射的时候，可以只传入类名参数，就可以生成对象，降低了耦合性，使得程序更具灵活性。

## TreeMap
TreeMap存储K-V键值对，通过`红黑树`实现；天然支持排序，默认情况下通过Key值的自然顺序进行排序；
**put**
![](pic/treemap.png)
**remove**
1. 删除的是`根节点`，则直接将根节点置为null;
2. 待删除节点的左右子节点`都为null`，删除时将该节点置为null;
3. 待删除节点的左右子节点`有一个有值`，则用有值的节点替换该节点即可；
4. 待删除节点的左右子节点`都不为null`，则找前驱或者后继，将前驱或者后继的值复制到该节点中，然后删除前驱或者后继（`前驱：左子树中值最大的节点，后继：右子树中值最小的节点`）；

## ConcurrentHashMap
#### ConcurrentHashMap与Hashtable比较
1. 线程安全的实现：Hashtable采用对象锁(synchronized修饰对象方法)来保证线程安全，也就是一个Hashtable对象只有一把锁，如果线程1拿了对象A的锁进行有synchronized修饰的put方法，其他线程是无法操作对象A中有synchronized修饰的方法的(如get方法、remove方法等)，竞争激烈所以效率低下。而ConcurrentHashMap采用`CAS + synchronized`来保证并发安全性，且synchronized关键字不是用在方法上而是用在了具体的对象上，实现了更小粒度的锁。
2. 数据结构的实现：Hashtable采用的是数组 + 链表，当链表过长会影响查询效率，而ConcurrentHashMap采用数组 + 链表 + 红黑树，当链表长度超过某一个值，则将链表转成红黑树，提高查询效率。
#### init
1. 如果table==null，进入循环。
2. case1: `sizeCtl< 0` 说明其他线程抢先对table初始化或者扩容，就调用Thread.yield(); 让出一次cpu，等下次抢到cpu再循环判断。
3. case2: 以CAS操作`CASsizeCtl=-1`,表示当前线程正在初始化。下面就开始初始化。
4. 判断sizeCtl的值。 sc(sizeCtl)大于0，则 容量大小=sc，sc(sizeCtl)<=0，即如果在使用了有参数的构造函数，sc=sizeCtl=指定的容量大小,否则n=默认的容量大小16。
5. 用上面求出的容量大小new出table数组。
6. 计算阈值，sizeCtl = n - (n >>> 2) = 0.75*n。
#### put
1. 校验Key，value是否为空。如果有一个为null，那么直接报NullPointerException异常。所以可以得出concurrentHashMap中Key，value不能为空。
2. 循环尝试插入。进入循环。
   1. case1:如果没有初始化就先调用`initTable()`方法来进行初始化过程
   2. case2:根据Hash值计算插入位置`(n - 1) & hash=i`。如果`没有hash冲突`,也就是说插入位置上面没有数据，就直接`casTabAt()`方法将数据插入。
   3. case3:插入位置上有数据。数据的头节点的哈希地址为-1(即链表的头节点为ForwardingNode节点)，则表示其他线程正在对table进行扩容（transfer），就先等着,等其他线程扩容完了咱们再尝试插入。
   4. case4:上面情况都没有。就对首节点加`synchronized`锁来保证线程安全，两种情况，一种是链表形式就直接遍历到`尾端`插入，一种是`红黑树`就按照红黑树结构插入，结束加锁。
   5. 如果Hash冲突时会形成Node链表，在链表长度超过8，Node数组超过64 时会将链表结构转换为红黑树的结构。
3. break退出循环。
4. 调用`addCount()`方法统计Map已存储的键值对数量size++，检查是否需要扩容，需要扩容就扩容。
#### 扩容
Hash表的扩容，一般都包含两个步骤：
1. table数组的扩容，一般就是新建一个2倍大小的槽数组，这个过程通过由一个单线程完成，且不允许出现并发。
2. 数据迁移，就是把旧table中的各个槽中的结点重新分配到新table中*。

这一过程通常涉及到槽中key的rehash(重新Hash)，因为key映射到桶的位置与table的大小有关，新table的大小变了，key映射的位置一般也会变化。
ConcurrentHashMap在处理rehash的时候，并不会重新计算每个key的hash值，而是利用了一种很巧妙的方法。我们在上一篇说过，ConcurrentHashMap内部的table数组的大小必须为2的幂次，原因是让key均匀分布，减少冲突，这只是其中一个原因。另一个原因就是：
当table数组的大小为2的幂次时，通过key.hash & table.length-1这种方式计算出的索引i，当table扩容后（2倍），新的索引要么在原来的位置i，要么是i+n。
而且还有一个特点，扩容后key对应的索引如果发生了变化，那么其变化后的索引最高位一定是1 
#### 为什么get不需要加锁
get操作全程不需要加锁是因为Node的成员val是用`volatile`修饰的，在多线程环境下线程A修改结点的val或者新增节点的时候是对线程B可见的。

## Collection和Collections
java.util.Collection是一个`集合接口`（集合类的一个顶级接口）。它提供了对集合对象进行基本操作的通用接口方法。Collection接口在Java 类库中有很多具体的实现。Collection接口的意义是为各种具体的集合提供了最大化的统一操作方式。
Collection   
├List   
│├LinkedList   
│├ArrayList   
│└Vector   
│　└Stack   
└Set 
java.util.Collections则是`集合类的一个工具类`，其中提供了一系列`静态方法`，用于对集合中元素进行`排序`、`搜索`以及`线程安全`等各种操作。

## Exception和Error
定义一个基类`java.lang.Throwable`作为所有异常的超类，有两个子类`Error`和`Exception`，分别表示错误和异常
1. Error是程序无法处理的错误，比如OutOfMemoryError
2. Exception是程序本身可以处理的异常，这种异常分两大类运行时异常和非运行时异常。程序中应当尽可能去处理这些异常。
    **运行时异常**：RuntimeException类及其子类异常，如`NullPointerException`、`IndexOutOfBoundsException`等。程序中可以选择捕获处理，也可以不处理。
    **非运行时异常**：RuntimeException以外的异常，类型上都属于Exception类及其子类。如`IOException`、`SQLException`等以及用户自定义的Exception异常。对于这种异常，JAVA编译器强制要求我们必需对出现的这些异常进行`catch并处理`

## interface和abstract
1. 抽象类可以有`构造方法`，接口中不能有构造方法。
2. 抽象类中可以有`普通成员变量`，接口中没有普通成员变量
3. 抽象类中可以包含`非抽象的普通方法`，接口中的所有方法必须都是抽象的，不能有非抽象的普通方法。
4. 抽象类中的抽象方法的访问类型可以是`public，protected`和，但接口中的抽象方法只能是`public`类型的，并且默认即为public abstract类型。
5. 抽象类中可以包含`静态方法`，接口中不能包含静态方法
6. 抽象类和接口中都可以包含`静态成员变量`，抽象类中的静态成员变量的访问类型可以任意，但接口中定义的变量只能是`public static final`类型，并且默认即为public static final类型。
7. 一个类可以`实现多个`接口，但只能`继承一个`抽象类。

## Java多态性
多态性是指允许`不同子类型`的对象对`同一消息`作出`不同的响应`。简单的说就是用同样的对象引用调用同样的方法但是做了不同的事情。多态性分为`编译时`的多态性和`运行时`的多态性。`方法重载`（overload）实现的是`编译时`的多态性（也称为前绑定），而`方法重写`（override）实现的是`运行时`的多态性（也称为后绑定）。运行时的多态是面向对象最精髓的东西，要实现多态需要做两件事：1). 方法重写（子类继承父类并重写父类中已有的或抽象的方法）；2). 对象造型（用`父类型引用引用子类型对象`，这样同样的引用调用同样的方法就会根据子类对象的不同而表现出不同的行为）。

## Java IO与NIO
|     IO     |    NIO     |
| :--------: | :--------: |
| 面向字节流 | 面向缓冲区 |
|    阻塞    |   非阻塞   |
**面向流与面向缓冲**
Java IO和NIO之间第一个最大的区别是，IO是面向流的，NIO是面向缓冲区的。 Java IO面向流意味着每次从流中读一个或多个字节，直至读取所有字节，它们`没有被缓存`在任何地方。此外，它`不能前后移动`流中的数据。如果需要前后移动从流中读取的数据，需要先将它缓存到一个缓冲区。 Java NIO的缓冲导向方法略有不同。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后移动。这就增加了处理过程中的灵活性。但是，还需要检查是否该缓冲区中包含所有您需要处理的数据。而且，需确保当更多的数据读入缓冲区时，不要覆盖缓冲区里尚未处理的数据。
**阻塞与非阻塞IO**
Java IO的各种流是阻塞的。这意味着，当一个线程调用read() 或 write()时，该线程被阻塞，直到有一些数据被读取，或数据完全写入。该线程在此期间`不能再干任何事情`了。Java NIO的非阻塞模式，使一个线程从某`通道`发送请求读取数据，但是它仅能得到目前可用的数据，如果目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可以继续做其他的事情。 `非阻塞写`也是如此。一个线程请求写入一些数据到某通道，但不需要等待它完全写入，这个线程同时可以去做别的事情。 线程通常将非阻塞IO的空闲时间用于在其它通道上执行IO操作，所以一个单独的线程现在可以管理多个输入和输出通道（channel）。
#### 工作流程
**IO**
由于Java IO是阻塞的，所以当面对多个流的读写时需要多个线程处理。例如在网络IO中，Server端使用一个线程监听一个端口，一旦某个连接被accept，创建新的线程来处理新建立的连接。 
![](pic/io.png)
**NIO**
Java NIO 提供 Selector 实现单个线程管理多个channel的功能。 
![](pic/nio.png)

## 泛型
#### 泛型方法
```java
class GenericMethod{
 public <T> T[] sort(T[] elements){
  return elements;
 }
}
```
#### 泛型类
子类继承泛型类时或者实例化泛型类的对象时，需要指定具体的参数类型或者声明一个参数变量。
```java
class GenericClass<ID, T>{}
 
class SubGenericClass<T> extends GenericClass<Integer, T>{}
```
#### 泛型接口
子类在实现泛型接口时需要填入具体的数据类型或者填入子类声明的类型变量。
```java
interface GenericInterface<T> {
 T append(T seg);
}
```
#### 实现
泛型是通过`类型擦除`来实现的，编译器在`编译后`擦除了所有类型相关的信息，泛型信息对 Java 编译器可以见，对 Java 虚拟机不可见。所以在运行时不存在任何类型相关的信息List\<String>不能转为List\<Object>，因为泛型的类型不一样，这种转换只能在`子类与父类`之间转换，虽然Object是String的父类，但是List\<Object>和List\<String>在编译器看来，是两种完全不同的东西
Java 编译器通过如下方式实现擦除：
- 用 Object 或者界定类型替代泛型，产生的字节码中只包含了原始的类，接口和方法；
- 在恰当的位置插入`强制转换`代码来确保类型安全；
- 在继承了泛型类或接口的类中插入`桥接`方法来保留多态性。
#### 示例
定义 User 类，实现了 Comparable 接口，类型参数填入 User，实现 compareTo 方法。
```java
class User implements Comparable<User> {
 String name;
     
 public int compareTo(User other){
  return this.name.compareTo(other.name);
 }
}
```
JDK 中 Comparable 接口源码内容如下：
```java
public interface Comparable<T>{
 int compareTo(T o);
}
```
反编译出来的内容放在 Comparable.jad 文件中，文件内容如下：
```java
public interface Comparable
{
 public abstract int compareTo(Object obj);
}
```
反编译之后的内容中已经没有了类型变量 T 。compareTo 方法中的参数类型 T 也被替换成了 Object。
User.jad 文件内容如下：
```java
class User implements Comparable{
 User(){}
 public int compareTo(User user){
  return name.compareTo(user.name);
 }
 // 桥接方法
 public volatile int compareTo(Object obj){
  return compareTo((User)obj);
 }
 String name;
}
```
类型参数没有了，多了一个无参构造方法，多了一个 compareTo(Object obj) 方法，这个就是桥接方法，还可以发现参数 obj 被强转成 User 再传入 compareTo(User user) 方法


## Lambda
一个接口如果只包含`一个`抽象方法，那么它就是一个`函数式接口`
对于`函数式接口`，可以使用**lambda表达式**创建该接口的`对象`
```java
public Interface Test{
    abstract void doTest(int a);
}

Test test = (a)->{
    System.out.println("do test: " + a);
};

test.doTest(10);
```

## Lambda-Stream
**Stream**
数据的渠道,用来操作由数据源(`数组`,`集合`)所产生的元素序列.
- IO : 传输数据
- Stream流 : 操作数据,计算数据
- 数组/集合 : 存储数据

**特点**
1. Stream流本身不会存储数据
2. Stream不会修改数据源|源对象,每次回返回持有结果的新的流Stream
3. 延迟执行|惰性加载 : 当获取终止行为时候,才会执行一系列的中间操作
4. 流都是一次性的流,不能重复使用多次,一旦使用过就已经被破坏

**步骤**
1. 创建Stream
    1)Collection->stream
    2)Arrays->stream(数组)
    3)Stream.of(值列表)
2. 一系列流式的中间操作(都会返回一个持有结果的新的流)
3. 终止行为
```java
public class Class001_Stream {
    public static void main(String[] args) {
        //Collection-->stream()
        Stream<Integer> stream =  List.of(1,2,3,4,5).stream();
        System.out.println(stream);
        stream.forEach(System.out::println);
 
        //Arrays->stream(数组)
        String[] arr = {"aaa","bbb","ccc"};
        Stream<String> stream1 = Arrays.stream(arr);
        stream1.forEach(System.out::println);
 
        //Stream.of(值列表)
        Stream<Integer> stream2 = Stream.of(5,4,3,2,1);
        stream2.forEach(System.out::println);
    }
}
```
**中间操作**
1. 过滤 Stream `filter`(Predicate<? super T> predicate);
2. 去重 `distinct`() 比较equals与hashCode()
3. 截取 `limit`(long) 从第一个开始截取几个
4. 跳过 `skip`(long) 跳过前n个
5. 排序 `sorted`() --> 内部比较器 sorted(Comparator) ->外部比较器
6. 映射 `map`(Function fun) stream操作的每一个数据都所用于参数函数,映射成一个新的结果,最后返回一个持有所有映射后的新的结果的流
```java
public class Class002_Stream {
    public static void main(String[] args) {
        List<Employee> list = Arrays.asList(
                new Employee("bcd",27,9500),
                new Employee("aaa",29,10000),
                new Employee("abc",28,8000),
                new Employee("bc",28,9000),
                new Employee("bc",28,9000),
                new Employee("cde",30,12000)
        );
        //获取Stream
        Stream<Employee> stream = list.stream();
        //中间操作
        //过滤
        //stream = stream.filter(e-> e.getAge()>=28);
        //流式调用|链式调用
        //stream = stream.distinct().limit(3).skip(1);
 
        //排序
        //stream = stream.sorted();
        stream = stream.sorted((x,y)->Double.compare(y.getSalary(),x.getSalary()));  //不能通过方法引用
 
        Stream<String> names = stream.map(e->e.getName()).distinct();
 
        list.stream().map(e->e.getSalary()).distinct().filter(s->s>=10000).sorted().forEach(System.out::println);
 
        //终止行为
        //stream.forEach(System.out::println);
        names.forEach(System.out::println);
    }
}
```
**终止行为**
1. 遍历  foreach(Consumer)
2. 查找与匹配
```
allMatch-检查是否匹配所有元素
anyMatch-检查是否至少匹配一个元素
noneMatch-检查是否没有匹配所有元素
findFirst-返回第一个元素
findAny-返回当前流中的任意元素
count-返回流中元素的总个数
max-返回流中最大值
min-返回流中最小值
```
3. 规约 reduce map->reduce 加工->计算结果
4. 收集 collect()
```java
public class Class003_Stream {
    public static void main(String[] args) {
        List<Employee> list = Arrays.asList(
                new Employee("bcd",27,9500),
                new Employee("aaa",29,10000),
                new Employee("abc",28,8000),
                new Employee("bc",28,9000),
                new Employee("bc",28,9000),
                new Employee("cde",30,12000)
        );
 
        //判断每一个员工是否都>=20岁
        boolean flag = list.stream().distinct().allMatch(e->e.getAge()>=20);
        System.out.println(flag);
 
        //查找薪资最高的员工
        //Optional<T> 存储一个数据的容器类型->jdk8新增的容器类型-->帮助避免空指针异常的出现
        Optional<Employee> op = list.stream().sorted((x, y)->Double.compare(y.getSalary(),x.getSalary())).findFirst();
        System.out.println(op.get());
 
        //parallelStream() 并行流
        System.out.println(list.stream().parallel().findAny().get());
 
        System.out.println(list.stream().filter(e->e.getSalary()<=10000).count());
 
        查找薪资最高的员工
        System.out.println(list.stream().distinct().max((x,y)->Double.compare(x.getSalary(),y.getSalary())).get());;
 
        //规约
        //找到公司所有员工的薪资,求和
        System.out.println(list.stream().map(Employee::getSalary).reduce((x,y)->x+y).get());;
        //1+2+3+4+5
        Stream<Integer> stream = Stream.of(1,2,3,4,5);
        /*System.out.println(stream.reduce((x,y)->{
            System.out.println("运算过程 : x = "+x+",y = "+y);
            return x+y;
        }).get());*/
 
        System.out.println(stream.reduce(100,(x,y)->{
            System.out.println("运算过程 : x = "+x+",y = "+y);
            return x+y;
        }));;
 
        //收集collect
        System.out.println(list.stream().distinct().count());
        //static <T> Collector<T,?,Long> counting() 返回类型为 T的 Collector接受元素，用于计算输入元素的数量。
        System.out.println(list.stream().distinct().collect(Collectors.counting()));
        //平均薪资  static <T> Collector<T,?,Double> averagingDouble(ToDoubleFunction<? super T> mapper) 返回 Collector ，它生成应用于输入元素的双值函数的算术平均值。
        System.out.println(list.stream().distinct().collect(Collectors.averagingDouble(Employee::getSalary)));
 
        //static <T> Collector<T,?,List<T>> toList() 返回 Collector ，将输入元素累积到新的 List 。
        System.out.println(list.stream().filter(e->e.getAge()>=28).collect(Collectors.toList()));
        //static <T> Collector<T,?,Set<T>> toSet() 返回 Collector ，将输入元素累积到新的 Set 。
        System.out.println(list.stream().filter(e->e.getAge()>=28).collect(Collectors.toSet()));
 
        //static <T,K,U>
        //Collector<T,?,Map<K,U>> toMap(Function<? super T,? extends K> keyMapper, Function<? super T,? extends U> valueMapper) 返回 Collector ，它将元素累积到 Map其键和值是将提供的映射函数应用于输入元素的结果。
        System.out.println(list.stream().distinct().collect(Collectors.toMap(Employee::getName,Employee::getSalary)));
    }
}
```

## 元注解
元注解负责注解其他注解
**@Target:** 用于描述注解的使用范围
**@Retention:** 表示需要在什么级别保存该注解信息，用于描述注解的生命周期
**@Document:** 说明该注解将被包含在javadoc中
**@Inherited:** 说明子类可以继承父类的该注解
```java
public class Test{
    @MyAnnotation(name = "aaa", schools={"a","b"})
    public void test{

    }
}

@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface MyAnnotation{
    // 注解参数：参数类型 + 参数名();
    String name() default "";
    int age() default 0;
    String[] schools();
    // 注解参数只有一个value的时候，使用时可以省略
    String value();
}
```

## synthetic
[视频地址](https://www.bilibili.com/video/BV1dy4y1V7ck?p=8)

用于解决`外部类`和`内部类`之间相互访问时的语法正确性问题
java编译器会将外部类和内部类编译成两个class
用`synthetic`关键字标注所有存在于`字节码`但不存在于`源码`中的`构造`
构造 => Field、Method、Constructor
**Field**
```java
public class FieldDemo{

    public String hello(){
        return "hello";
    }

    class FieldDemoInner{
        // synthetic FieldDemo this$0;

        public void sayHello{
            System.out.println(hello());
        }
    }
}
```
当内部类在没有外部类实例化`对象`的情况下依旧能直接调用外部类方法
编译时自动生成外部类对象属性
**Method**
```java
public class MethodDemo{

    class MethodDemoInner{
        private String name;

        /*
        synthetic String access$000(){
            return this.name;
        }

        synthetic void access$001(String name){
            this.name = name;
        }
        */
    }

    public void getInnerName(){
        return new MethodDemoInner.name;
    }

    public String setInnerName(String name){
        new MethodDemoInner.name = name;
    }
}
```
外部类能直接访问内部类的`private`属性
编译时根据使用情况自动生成` get set `方法
**Constructor**
```java
public class ConstructorDemo{

    public ConstructorDemoInner inner = new ConstructorDemoInner();

    class ConstructorDemoInner{
        private ConstructorDemoInner(){}

        /*
        synthetic ConstructorDemoInner(){}
        */
    }
}
```
外部类能直接实例化只有`private`构造器的内部类
编译时自动生成内部类的构造器

## NBAC
[视频地址](https://www.bilibili.com/video/BV1dy4y1V7ck?p=9)

Nested Based Access Control 基于嵌套类的访问控制

* JDK11之前，在内部类中`直接访问`外部类的`private`方法，由于synthetic的存在是可以正常访问的，但在内部类中`反射调用`外部类的`private`方法时会抛`Access`异常
* 为了解决这种二义性，从`JDK11`开始引入了`NBAC`来控制外部类与内部类之间的相互访问
* 在Class类中新增了 `nesttHost` 和 `nestMembers` 来指向嵌套宿主与嵌套成员
```java
public class Outer{
    class Inner{
        
    }

    public static void main(String[] args) throws Exception {
        Class netstHost = Outer.Inner.class.getNestHost(); // Outer
        Class netstHost = Outer.class.getNestHost(); // Outer

        Class<?> nestMembers =  Outer.Inner.class.getNestMembers(); // {Outer, Inner}
        Class<?> nestMembers =  Outer.class.getNestMembers(); // {Outer, Inner}
    }
}
```
由于 `nesttHost` 和 `nestMembers` 的存在JVM可以轻易验证两个类之间的嵌套关系，同时由于外部类和内部类都持有对方的引用，因此可以访问对方的`private`构造

## SPI
SPI（Service Provider Interface，服务提供者接口）是系统为第三方专门开放的扩展规范以及动态加载扩展点的机制。
#### SPI使用
- 先编写好服务接口的实现类，即服务提供类；
- 然后在`classpath`的`META-INF/services`目录下创建一个以`接口`全限定名命名的文本文件，并在该文件中写入`实现类`的全限定名（如果有多个实现类，以换行符分隔）；
- 最后调用JDK中的`java.util.ServiceLoader`组件中的`load()`方法，就会根据上述文件来发现并加载具体的`服务实现`。
#### JDBC中的SPI
JDBC是为用户通过Java访问数据库提供的统一接口，而数据库千变万化，因此借助SPI机制可以灵活地实现数据库驱动的插件化。

在使用旧版JDBC时，我们必须首先调用类似`Class.forName("com.mysql.jdbc.Driver")`的方法，通过反射来手动加载数据库驱动。JDBC中的接口是Java的核心包，在`rt.jar`中，这个jar是由`BootstrapClassLoadre`来加载的。而`Class.forName`使用的是当前类的类加载器，当前类的类加载器是`BootstrapClassLoader`，我们知道`BootstrapClassLoader`默认是加载`rt.jar`的。明显第三方实现不在`rt.jar`。这就破坏了双亲委派模型。

但是在新版JDBC中已经不用写了，只需直接调用`DriverManager.getConnection()`方法即可获得数据库连接。新版JDBC利用SPI机制来获取并加载驱动提供类（java.sql.Driver接口的实现类），SPI是采用`ContextClassLoader`来加载第三方实现类，这样就避免了父`BootstrapClassLoader`去应该由加载子`AppClassLoader`加载的类
#### ContextClassLoader
上下文类加载器，正常情况下，线程执行到某个类的时候，只能看到这个类对应加载器所加载的类。但是你可以为当前`线程`设置一个类加载器，然后可视范围就增加多一个类加载器加载的类

jdk内部类用`引导类加载器`加载，调SPI接口的方法依赖外部JAR包用`应用类加载器`加载，父加载器访问不到子加载器的类。但是可以设置当前线程的上下文类加载器，把当前线程上下文类加载器加载的类一并纳入可视范围

父ClassLoader可以使用当前线程`Thread.current.currentThread().getContextClassLoader()`所指定的classLoader加载的类。这就改变了父ClassLoader不能使用子ClassLoader加载的类的情况，即改变了双亲委托模型。

在双亲委托模型下，类加载器是由下至上的，即下层的类加载器会委托上层进行加载。但是对于`SPI`来说，有些接口是JAVA核心库提供的，而JAVA核心库是由`启动类加载器`来加载的，而这些接口的实现却来自于不同的jar包（厂商提供），JAVA的启动类加载器是不会加载其他来源的jar包，这样传统的双亲委托模型就无法满足SPI的要求。而通过给当前线程设置`上下文类加载器`，就可以设置的上下文类加载器来实现对于接口实现类的加载。
![](pic/context.png)
- ContextClassLoader默认为`AppClassLoader`
- 子线程ContextClassLoader默认为父线程的ContextClassLoader

## 内存泄漏
1. **静态集合类**，如HashMap、LinkedList等等。如果这些容器为静态的，那么它们的生命周期与程序一致，则容器中的对象在程序结束之前将不能被释放，从而造成内存泄漏。简单而言，长生命周期的对象持有短生命周期对象的引用，尽管短生命周期的对象不再使用，但是因为长生命周期对象持有它的引用而导致不能被回收。
2. **单例模式**，和静态集合导致内存泄漏原因类似，因为单例的静态特性，它的生命周期和 JVM 的生命周期一样长，所以如果单例对象持有外部对象引用，那么这个外部对象也不会被回收，那么就会发生内存泄漏。要注意！！！！单例对象如果持有Context，那么很容易引发内存泄漏，此时需要注意传递给单例对象的Context最好是Application Context。
3. **未关闭的资源类**：如数据库连接、网络连接和IO连接等。在对数据库进行操作的过程中，首先需要建立与数据库的连接，当不再使用时，需要调用close方法来释放与数据库的连接。只有连接被关闭后，垃圾回收器才会回收对应的对象。否则，如果在访问数据库的过程中，对Connection、Statement或ResultSet不显性地关闭，将会造成大量的对象无法被回收，从而引起内存泄漏。
4. **变量不合理的作用域**。一般而言，一个变量的定义的作用范围大于其使用范围，很有可能会造成内存泄漏。另一方面，如果没有及时地把对象设置为null，很有可能导致内存泄漏的发生。

## JDK1.8新特性
1. Lamdba表达式
2. 函数式接口
   - `Function` 有输入参数，也有返回值。
    ```java
    @FunctionalInterface
    public interface Function<T, R> {
        R apply(T t);
    }
    ```
   - `Consumer` 有输入参数，没有返回值
    ```java
    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t);
    }
    ```
   - `Supplier` 没有输入参数，有返回值
    ```java
    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }
    ```
   - `Predicate` 既有输入参数也有返回值，返回类型是boolean类型
    ```java
    @FunctionalInterface
    public interface Predicate<T> {
        boolean test(T t);
    }
    ```
3. 方法引用和构造器引用
   - 方法引用
    当要传递给`Lambda`体的操作已经有实现方法，可以直接使用方法引用(实现抽象方法的列表，必须要和方法引用的方法`参数列表一致`)
    方法引用：使用操作符"`::`"将方法名和（类或者对象）分割开来。
    ```java
    public class MethodRefDemo {
        public static void main(String[] args) {
            FunctionGeneric<String> strName = s -> System.out.println(s);
            strName.fun("Lambda表达式没有使用方法引用");
            //方法引用
            FunctionGeneric<String> strName2 = System.out::println;
            strName2.fun("使用方法引用");​
        }
    }​
    ```
   - 构造器引用
    本质上：构造器引用和方法引用相似，只是使用了一个`new`方法
    使用说明：函数式接口参数列表和构造器`参数列表要一致`，该接口返回值类型也是构造器返回值类型
    格式：`ClassName::new`
    ```java
    public class MethodRefDemo {
        public static void main(String[] args) {​
            //构造器引用
            Function<String, Integer> fun1 = (num) -> new Integer(num);
            Function<String, Integer> fun2 = Integer::new;​
            //数组引用
            Function<Integer,Integer[]> fun3 = (num) ->new Integer[num];
            Function<Integer,Integer[]> fun4 = Integer[]::new;
        }
    }​
    ```
4. Stream API
5. 接口中的默认方法和静态方法
   - 默认方法
    java8允许接口中包含具体实现的方法体，该方法是默认方法，它需要使用`default`关键字修饰
   - 静态方法
    java8中允许接口中定义静态方法，使用`static`关键字修饰
    ```java    ​
    public interface DefaultMethodDemo {
        default Integer addMethod(int a ,int b){
            System.out.println("我是默认方法");
            return a+b;
        }
        static void test(){
            System.out.println("我是静态方法");
        }
    }​
    ```
6. 新时间日期API
7. OPtional
    optional类是一个容器，代表一个值`存在`或者`不存在`，原来使用`null`表示一个值存不存在，现在使用optional可以更好的表达这个概念，并且可以避免`空指针异常`。

8. 其他特性