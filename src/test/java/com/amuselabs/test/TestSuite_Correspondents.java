package com.amuselabs.test;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSuite_Correspondents
{
    public static Properties user_interface =new Properties();
    @BeforeAll
    public static void a()
    {
        try {
            Helper.start_ePADD();
            InputStream s = TestSuite_Person_Entities.class.getClassLoader().getResourceAsStream("USER_INTERFACE.properties");
            user_interface.load(s);  //Reading properties file
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @BeforeEach
    public void b()
    {
        System.out.println("Hi");
    }
    @Test
    public void c()
    {
        assertTrue(2==2);
    }
    @Test
    public void e()
    {
        assertTrue(3==3);
    }
    @AfterEach
    public void d()
    {
        System.out.println("bye");
    }
}
