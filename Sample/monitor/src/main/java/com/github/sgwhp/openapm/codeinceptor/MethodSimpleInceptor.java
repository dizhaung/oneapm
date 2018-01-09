package com.github.sgwhp.openapm.codeinceptor;

/**
 * Created by liangjianhua on 2017/12/13.
 */

public class MethodSimpleInceptor {

    public static void beforeMethod(){
        System.out.println("MethodSimpleInceptor beforeMethod");
    }

    public static void afterMethod(){
        System.out.println("MethodSimpleInceptor afterMethod");
    }
}
