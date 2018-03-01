package com.sjy.uitest.sndroiduitest;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void tetsBean(){
        UserInfo userInfo=new UserInfo();
        byte b1=127,b2=127,b3,b6;
        int b7;
        final byte b4=4,b5=6;
        b6=b4+b5;
        b3= (byte) (b1+b2);
        System.out.print(b3);
    }
}