package com.ruqi.appserver.ruqi.utils;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

public class GenerateAppKeyUtil {
//    public static final String  salt="2020*.ruqi#!888";
//    public static String appName= "com.ruqi.travel";
////    public static String appName= "com.tencent.gac.driver";
//    public static void main(String[] args) {
//        System.out.println("app packagename:"+appName+"=====appkey:"+Md5Util.md5StrBySalt(appName,salt).substring(0,16));
//
//    }


    public static void main(String[] args) throws Exception {

        Student s = new Student();
        try {
            Person proxy = (Person) Proxy.newProxyInstance(Student.class.getClassLoader(),new Class[]{Person.class},new MyInvocation(s));
            proxy.funT(new Wrapper<BookB>());
        }catch (Exception e){
            System.out.println("error msg:"+e.getLocalizedMessage());
        }


    }

    public static class MyInvocation implements InvocationHandler {
        private Object object;

        public MyInvocation(Object object) {
            this.object = object;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("msg invoke begin and return getReturnType:"+method.getReturnType()+
                    ";return getAnnotatedReturnType:"+method.getAnnotatedReturnType()+
                    ";return getGenericReturnType:"+method.getGenericReturnType()+
                    "end");
//        Log.d("msg",proxy.getClass().getName()+"--->"+object.getClass().getName());
            //return "自定义返回值";

            return "";
        }
    }

        public static class Student implements Person {
        private Wrapper book;
            @Override
            public String say(String word) {
                System.out.println("Student->say(String word) "+word+"T:"+book.getClass().getName());
                return " Student--->这是返回值";
            }

            @Override
            public Wrapper<BookA> funT(Wrapper t) {
                System.out.println("funT:"+t.getClass().getName());
                Wrapper<BookA> bookWrapper=new Wrapper<>();
                return bookWrapper;
            }
        }

        public interface Person {
            String say(String word);
             Wrapper<BookA> funT(Wrapper<BookB> t);
        }
        private static  class BookA{

        }
    private static  class BookB{

    }

    private static  class Wrapper<T>{
           private T content;
    }


}
