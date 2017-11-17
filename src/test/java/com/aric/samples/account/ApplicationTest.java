package com.aric.samples.account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

public class ApplicationTest {

    Application application;

    @Before
    public void setup(){
        application = new Application();
    }

    @Test
    public void testApplicationMain(){

        String tempArray [] = {"arg1","arg2"};
        application.main(tempArray);

        //check application can be run or not
    }

}
