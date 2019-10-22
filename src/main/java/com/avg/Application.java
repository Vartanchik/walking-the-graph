package com.avg;

import annotations.InitApp;
import dal.link.LinkDaoImpl;
import dal.node.NodeDaoImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        SpringApplication.run(Application.class, args);

        // Adding node table to DB
        Class<?> nodeDaoClass = Class.forName("dal.node.NodeDaoImpl");
        for (Method method : nodeDaoClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(InitApp.class)){
                method.setAccessible(true);
                method.invoke(new NodeDaoImpl());
            }
        }
        // Adding link table to DB
        Class<?> linkDaoClass = Class.forName("dal.link.LinkDaoImpl");
        for (Method method : linkDaoClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(InitApp.class)){
                method.setAccessible(true);
                method.invoke(new LinkDaoImpl());
            }
        }
    }
}