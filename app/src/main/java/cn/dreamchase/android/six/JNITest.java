package cn.dreamchase.android.six;

/**
 * - 首先创建一个JNI的类与MainActivity同级，其中包含一个native方法
 * - 在terminal，默认是当前项目路径，需要用cd命令定向到项目的Java目录 cd app/src/main/java
 * - 接着用 javah 命令生成 .h 文件。 后面跟着的是 包名.类名
 */
public class JNITest {

    public native int plus(int x,int y);  // 这个是需用C语言实现的函数
}
