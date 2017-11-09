

# [synchronized方法底层原理](http://blog.csdn.net/javazejian/article/details/72828483#理解java对象头与monitor)
 * 同步代码块
   * 原理：同步语句块的实现使用的是monitorenter 和 monitorexit 指令.
 
 * 同步对象方法
   * 方法级的同步是隐式，即无需通过字节码指令来控制的，它实现在方法调用和返回操作之中。JVM可以从方法常量池中的方法表结构
	 (method_info Structure) 中的 ACC_SYNCHRONIZED 访问标志区分一个方法是否同步方法。当方法调用时，调用指令将会 检查方法的
	 ACC_SYNCHRONIZED 访问标志是否被设置，如果设置了，执行线程将先持有monitor（虚拟机规范中用的是管程一词）， 
	 然后再执行方法，最后再方法完成(无论是正常完成还是非正常完成)时释放monitor
   * 注意到的是在Java早期版本中，synchronized属于重量级锁，效率低下，因为监视器锁（monitor）是依赖于底层的
     操作系统的Mutex Lock来实现的,而操作系统实现线程之间的切换时需要从用户态转换到核心态，这个状态之间的转换需要相对比较长的时间，
	 时间成本相对较高，这也是为什么早期的synchronized效率低的原因.

 * 同步静态方法 (同步对象方法)
 
 ```java
 //'javap -sysinfo -v -c com.heaven7.java.data.mediator.test.SynchronizedTest
public class SynchronizedTest {

    private static int sIndex = 0;
    private int id;

    public static synchronized void setIndex(int index){
        sIndex = index;
    }

    public synchronized void setId(int id){
        this.id = id;
    }

    public void setId2(int id){
        synchronized (this) {
            this.id = id;
        }
    }

}

 ```
 
 javap 反编译
 ```java
 Classfile /E:/study/github/data-mediator/data-mediator/out/test/classes/com/heaven7/java/data/mediator/test/SynchronizedTest.class
  Last modified 2017-11-3; size 826 bytes
  MD5 checksum 854a22ec484caf6bb43a63590229b9a2
  Compiled from "SynchronizedTest.java"
public class com.heaven7.java.data.mediator.test.SynchronizedTest
  minor version: 0
  major version: 51
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #5.#28         // java/lang/Object."<init>":()V
   #2 = Fieldref           #4.#29         // com/heaven7/java/data/mediator/test/SynchronizedTest.sIndex:I
   #3 = Fieldref           #4.#30         // com/heaven7/java/data/mediator/test/SynchronizedTest.id:I
   #4 = Class              #31            // com/heaven7/java/data/mediator/test/SynchronizedTest
   #5 = Class              #32            // java/lang/Object
   #6 = Utf8               sIndex
   #7 = Utf8               I
   #8 = Utf8               id
   #9 = Utf8               <init>
  #10 = Utf8               ()V
  #11 = Utf8               Code
  #12 = Utf8               LineNumberTable
  #13 = Utf8               LocalVariableTable
  #14 = Utf8               this
  #15 = Utf8               Lcom/heaven7/java/data/mediator/test/SynchronizedTest;
  #16 = Utf8               setIndex
  #17 = Utf8               (I)V
  #18 = Utf8               index
  #19 = Utf8               setId
  #20 = Utf8               setId2
  #21 = Utf8               StackMapTable
  #22 = Class              #31            // com/heaven7/java/data/mediator/test/SynchronizedTest
  #23 = Class              #32            // java/lang/Object
  #24 = Class              #33            // java/lang/Throwable
  #25 = Utf8               <clinit>
  #26 = Utf8               SourceFile
  #27 = Utf8               SynchronizedTest.java
  #28 = NameAndType        #9:#10         // "<init>":()V
  #29 = NameAndType        #6:#7          // sIndex:I
  #30 = NameAndType        #8:#7          // id:I
  #31 = Utf8               com/heaven7/java/data/mediator/test/SynchronizedTest
  #32 = Utf8               java/lang/Object
  #33 = Utf8               java/lang/Throwable
{
  public com.heaven7.java.data.mediator.test.SynchronizedTest();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 8: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/heaven7/java/data/mediator/test/SynchronizedTest;

  public static synchronized void setIndex(int);
    descriptor: (I)V
    flags: ACC_PUBLIC, ACC_STATIC, ACC_SYNCHRONIZED
    Code:
      stack=1, locals=1, args_size=1
         0: iload_0
         1: putstatic     #2                  // Field sIndex:I
         4: return
      LineNumberTable:
        line 14: 0
        line 15: 4
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0 index   I

  public synchronized void setId(int);
    descriptor: (I)V
    flags: ACC_PUBLIC, ACC_SYNCHRONIZED
    Code:
      stack=2, locals=2, args_size=2
         0: aload_0
         1: iload_1
         2: putfield      #3                  // Field id:I
         5: return
      LineNumberTable:
        line 18: 0
        line 19: 5
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       6     0  this   Lcom/heaven7/java/data/mediator/test/SynchronizedTest;
            0       6     1    id   I

  public void setId2(int);
    descriptor: (I)V
    flags: ACC_PUBLIC
    Code:
      stack=2, locals=4, args_size=2
         0: aload_0
         1: dup
         2: astore_2
         3: monitorenter
         4: aload_0
         5: iload_1
         6: putfield      #3                  // Field id:I
         9: aload_2
        10: monitorexit
        11: goto          19
        14: astore_3
        15: aload_2
        16: monitorexit
        17: aload_3
        18: athrow
        19: return
      Exception table:
         from    to  target type
             4    11    14   any
            14    17    14   any
      LineNumberTable:
        line 22: 0
        line 23: 4
        line 24: 9
        line 25: 19
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      20     0  this   Lcom/heaven7/java/data/mediator/test/SynchronizedTest;
            0      20     1    id   I
      StackMapTable: number_of_entries = 2
        frame_type = 255 /* full_frame */
          offset_delta = 14
          locals = [ class com/heaven7/java/data/mediator/test/SynchronizedTest, int, class java/lang/Object ]
          stack = [ class java/lang/Throwable ]
        frame_type = 250 /* chop */
          offset_delta = 4

  static {};
    descriptor: ()V
    flags: ACC_STATIC
    Code:
      stack=1, locals=0, args_size=0
         0: iconst_0
         1: putstatic     #2                  // Field sIndex:I
         4: return
      LineNumberTable:
        line 10: 0
}
SourceFile: "SynchronizedTest.java"
 ```
 
 * 偏向锁, 轻量级锁，自旋锁，锁消除
 
 
 
 