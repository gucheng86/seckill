package com.zxf.seckill;


public class Test {
    @org.junit.Test
    public void test1() {
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
