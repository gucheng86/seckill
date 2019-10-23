package com.zxf.seckill;


public class Test {
    @org.junit.Test
    public void test1() {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(){
                public void run() {
                    System.out.println(getName());
                }
            };
        }
        for(Thread thread : threads) {
            thread.start();
        }
    }

    @org.junit.Test
    public void test2() {
        try{
            Son s = new Son();
            try {
                throw s;
            } catch (Father f) {
                System.out.println("father");
                throw s;
            }
        } catch (Son son) {
            System.out.println("son");
        }
    }
}

class Father extends Exception {}
class Son extends Father{}
