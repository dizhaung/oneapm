package com.github.sgwhp.openapm.sample;

/**
 * Created by liangjianhua on 2017/11/16.
 */

public class Hello {

    private String value ;

    public Hello(){
        value = "hello everyone" ;
    }

    public void sayHello(){
        System.out.println("hello world, " + value);
    }
}
