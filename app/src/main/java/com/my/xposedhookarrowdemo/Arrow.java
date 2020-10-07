package com.my.xposedhookarrowdemo;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.net.InetAddress;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Arrow implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // TODO Auto-generated method stub
        XposedBridge.log("Loaded app ================ hook program is start!");
        // 不是需要 Hook 的包直接返回
        if (!loadPackageParam.packageName.equals("com.my.xposedtargetdemo"))
            return;

        XposedBridge.log("app包名：" + loadPackageParam.packageName);

        /*
         * Hook普通方法
         */
        XposedHelpers.findAndHookMethod("com.my.xposedtargetdemo.Util", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "ordinaryFunc", // 被Hook函数的名称ordinaryFunc
                String.class, // 被Hook函数的第一个参数String
                String.class, // 被Hook函数的第二个参数String
                int.class,// 被Hook函数的第三个参数integer
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // Hook函数之前执行的代码
                        // 打印方法的参数信息
                        XposedBridge.log("姓名：" + param.args[0]);
                        XposedBridge.log("性别：" + param.args[1]);
                        XposedBridge.log("年龄：" + param.args[2]);
                        // 打印堆栈查看调用关系
                        StackTraceElement[] wodelogs = new Throwable("wodelog")
                                .getStackTrace();
                        for (int i = 0; i < wodelogs.length; i++) {
                            XposedBridge.log("查看堆栈：" + wodelogs[i].toString());
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        // Hook函数之后执行的代码
                        // 获取类
                        Class<?> clazz = param.thisObject.getClass();
                        XposedBridge.log("要hook的方法所在的类：" + clazz.getName());

                        // 获取方法的返回值
                        Object resultString = param.getResult();
                        XposedBridge.log("返回值：" + resultString.toString());
                        // 修改方法的返回值
                        param.setResult("ordinaryFunc was hooked!");
                    }
                });

        /*
         * Hook 后替换原来方法(原来方法不再调用)
         */
        XposedHelpers.findAndHookMethod("com.my.xposedtargetdemo.Util", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "replace_target_fun", // 被Hook函数的名称replace_target_fun
                Activity.class, // 被Hook函数的第一个参数Activity
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        return "Happy ! You hook it!";
                    }
                });

        /*
         * Hook 参数是自定义的类
         */
        //先获取到自定义的类 作为hook函数的参数
        Class<?> param_class = XposedHelpers.findClass("com.my.xposedtargetdemo.ParamClass", loadPackageParam.classLoader);
        XposedHelpers.findAndHookMethod("com.my.xposedtargetdemo.Util", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader, "param_isClass", // 被Hook函数的名称param_isClass
                int.class,// 被Hook函数的第1个参数integer
                param_class,//被Hook函数的第2个参数 自定义的类
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // Hook函数之前执行的代码
                        // 打印方法的参数信息
                        XposedBridge.log("parma1 : " + param.args[0]);
                        XposedBridge.log("parma2 : " + param.args[1]);
                        // 打印堆栈查看调用关系
                        StackTraceElement[] wodelogs = new Throwable("wodelog")
                                .getStackTrace();
                        for (int i = 0; i < wodelogs.length; i++) {
                            XposedBridge.log("查看堆栈：" + wodelogs[i].toString());
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        // Hook函数之后执行的代码
                        // param.thisObject 获取当前对象
                        // param.thisObject.getClass() 获取类
                        Class<?> clazz = param.thisObject.getClass();
                        XposedBridge.log("要hook的方法所在的类：" + clazz.getName());

                        // 获取方法的返回值
                        Object resultString = param.getResult();
                        XposedBridge.log("返回值：" + resultString.toString());
                        // 修改方法的返回值
                        param.setResult(resultString.toString() + " || was hooked!");
                    }
                });

        /*
         * Hook 匿名内部类(所有调用这个内部类都会被hook)
         */
        XposedHelpers.findAndHookMethod("com.my.xposedtargetdemo.ABClass", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "say", // 被Hook函数的名称ordinaryFunc
                String.class, // 被Hook函数的第一个参数String
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // Hook函数之前执行的代码
                        // 打印方法的参数信息
                        XposedBridge.log("参数1：" + param.args[0]);
                        // 打印堆栈查看调用关系
                        StackTraceElement[] wodelogs = new Throwable("wodelog")
                                .getStackTrace();
                        for (int i = 0; i < wodelogs.length; i++) {
                            XposedBridge.log("查看堆栈：" + wodelogs[i].toString());
                        }
                        param.args[0] = "hook后更改的值";  //更改传入参数
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        // Hook函数之后执行的代码
                        // 获取类
                        Class<?> clazz = param.thisObject.getClass();
                        XposedBridge.log("要hook的方法所在的类：" + clazz.getName());
                    }
                });

        /*
         * Hook 匿名内部类 2 (只有当com.my.xposedtargetdemo.MainActivity 调用这个内部类时才会走)
         */
        XposedHelpers.findAndHookMethod("com.my.xposedtargetdemo.MainActivity$2", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "say2", // 被Hook函数的名称say2
                String.class, // 被Hook函数的第一个参数String
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // Hook函数之前执行的代码
                        // 打印方法的参数信息
                        XposedBridge.log("参数1：" + param.args[0]);
                        // 打印堆栈查看调用关系
                        StackTraceElement[] wodelogs = new Throwable("wodelog")
                                .getStackTrace();
                        for (int i = 0; i < wodelogs.length; i++) {
                            XposedBridge.log("查看堆栈：" + wodelogs[i].toString());
                        }
                        param.args[0] = "hook后更改的值2"; //更改传入参数
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        // Hook函数之后执行的代码
                        // 获取类
                        Class<?> clazz = param.thisObject.getClass();
                        XposedBridge.log("要hook的方法所在的类：" + clazz.getName());
                    }
                });

        /**
         * HOOK构造方法
         * */
        XposedHelpers.findAndHookConstructor("com.my.xposedtargetdemo.Student",
                loadPackageParam.classLoader, String.class, String.class, int.class,
                double.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log("name: " + param.args[0]);
                        XposedBridge.log("gender：" + param.args[1]);
                        XposedBridge.log("age：" + param.args[2]);
                        XposedBridge.log("points：" + param.args[3]);
                        XposedBridge.log("isPassed：" + param.args[4]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        XposedBridge
                                .log("============= constructor ============");
                        XposedBridge.log("开始获取属性：");
                        Field[] fields = param.thisObject.getClass()
                                .getDeclaredFields();
                        for (int i = 0; i < fields.length; i++) {
                            String typeString = fields[i].getType().toString();
                            XposedBridge.log("======【属性 type : 】==args[" + i
                                    + "]=" + typeString);

                            String nameString = fields[i].getName();
                            XposedBridge.log("======【属性 name : 】==args[" + i
                                    + "]=" + nameString);

                            Object valueObject = XposedHelpers.getObjectField(
                                    param.thisObject, fields[i].getName());
                            XposedBridge.log("======【属性 value : 】==args[" + i
                                    + "]=" + valueObject.toString());
                        }
                        String newNameString = "hooked";
                        // 修改 String 类型属性 name 的值
                        XposedHelpers.setObjectField(param.thisObject, "name",
                                newNameString);
                        // 修改 int 类型属性 age 的值
                        XposedHelpers.setIntField(param.thisObject, "age", 99);
                        // 修改 boolean 类型属性 isPassed 的值
                        XposedHelpers.setBooleanField(param.thisObject,
                                "isPassed", true);
                        double points = 9.99;
                        // 修改 static double 类型属性 points 的值
                        XposedHelpers.setStaticDoubleField(
                                param.thisObject.getClass(), "points", points);
                        XposedBridge.log("获取属性完成。");
                    }
                });

        /*
         * Hook UI控件
         */
        XposedHelpers.findAndHookMethod("com.my.xposedtargetdemo.MainActivity", // 被Hook函数所在的类(包名+类名)
                loadPackageParam.classLoader,
                "ui_function", // 被Hook函数的名称ui_function
                View.class, // 被Hook函数的第一个参数String
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // Hook函数之前执行的代码
                        TextView tView = (TextView) param.args[0];
                        String str = tView.getText().toString();
                        XposedBridge.log("参数view里初始值：" + str);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        // 获取类
                        Class<?> clazz = param.thisObject.getClass();
                        // hook 属性不为私有, 参数为属性名
                        // Field field = clazz.getField("show_board");
                        // 通过类的字节码得到该类中声明的所有属性，无论私有或公有
                        Field field = clazz.getDeclaredField("show_board");
                        // 设置访问权限
                        field.setAccessible(true);
                        TextView tView = (TextView) field.get(param.thisObject);
                        String str = tView.getText().toString();
                        XposedBridge.log("劫持到的值：" + str);
                        tView.setText("show_board'view is hooked !");
                    }
                });

        /**
         * hook系统方法 更改IMEI设备号
         * */
        XposedBridge.hookAllMethods(TelephonyManager.class, "getImei",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        XposedBridge.log("imei：" + param.getResult());
                        param.setResult("999999");
                    }
                });

        /**
         * hook流量上网IP地址
         * */
        XposedHelpers.findAndHookMethod(InetAddress.class, "getHostAddress",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("流量 IP地址：" + param.getResult());
                        param.setResult("192.168.1.99 (is hooked)");
                    }
                });

        /**
         * hook WiFi上网IP地址
         * */
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getIpAddress",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("WIFI IP地址：" + param.getResult());
                        // 分割字符串
                        String[] str = "192.99.99.99".split("\\.");
                        // 定义一个字符串，用来存储反转后的IP地址
                        String ipAdress = "";
                        // for循环控制IP地址反转
                        for (int i = 3; i >= 0; i--) {
                            ipAdress = ipAdress + str[i] + ".";
                        }
                        // 去除最后一位的"."
                        ipAdress = ipAdress.substring(0, ipAdress.length() - 1);
                        // 返回新的整形IP地址
                        param.setResult((int) ipToLong(ipAdress));
                    }
                });

        /*
         * Hook staic 方法
         */
        XposedHelpers.findAndHookMethod("com.my.xposedtargetdemo.Util",
                loadPackageParam.classLoader, "myInfo", String.class, int.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.beforeHookedMethod(param);
                        // Hook函数之前执行的代码
                        // 打印方法的参数信息
                        XposedBridge.log("name：" + param.args[0]);
                        XposedBridge.log("grade：" + param.args[1]);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        super.afterHookedMethod(param);
                        XposedBridge.log("============= staic function ============");
                    }
                });


        /*
         * 带壳hook。如果下面的代码要运行，需要先将待hook apk 进行加固。
         * 目前被hook apk还没加固,代码运行到这会找不到com.stub.StubApp类，会报错，就先把代码注释掉，如果待hook apk加固后 就可以放开下面的代码了。
         */
        //1、 先hook类加载器
/*        XposedHelpers.findAndHookMethod("com.stub.StubApp", loadPackageParam.classLoader,
                "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Context context = (Context) param.args[0];
                        if (context != null) {
                            final ClassLoader classLoader = context.getClassLoader();//获取到加壳程序的类加载器
                            if (classLoader != null) {

                                //2、 用上文中获取的类加载器作为参数 hook apk中对应的方法，用法没有区别
                                XposedHelpers.findAndHookMethod(
                                        "com.my.xposedtargetdemo.Util", // 被Hook函数所在的类(包名+类名)
                                        classLoader, //直接使用 上面获取的 classLoader
                                        "ordinaryFunc", // 被Hook函数的名称ordinaryFunc
                                        String.class, // 被Hook函数的第一个参数String
                                        String.class, // 被Hook函数的第二个参数String
                                        int.class,// 被Hook函数的第三个参数integer
                                        new XC_MethodHook() {
                                            @Override
                                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                                super.afterHookedMethod(param);
                                                // 修改方法的返回值
                                                param.setResult("In shell,ordinaryFunc was hooked!");
                                            }
                                        });

                            }
                        }
                    }
                });*/


    }

    public static long ipToLong(String strIp) {
        long[] ip = new long[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整形
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] >> 8) + ip[3];
    }
}
