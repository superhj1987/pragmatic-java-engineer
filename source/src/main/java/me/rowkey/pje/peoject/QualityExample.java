package me.rowkey.pje.peoject;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Bryant.Hang on 2017/8/9.
 */
public class QualityExample {
}

@Suite.SuiteClasses({
        UtilTest.class,
})
class SuiteTest {

}

class UtilTest {
    @BeforeClass
    public static void initClass() {
        System.out.println("i will be called only once,before the first test method");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("i will be called only once,after the last test method");
    }

    @Before
    public static void ibeforeMethod() {
        System.out.println("i will be called before every test method");
    }

    @After
    public static void afterMethod() {
        System.out.println("i will be called after every test method");
    }

    @Test
    public void testAdd() throws Exception {
        Assert.assertEquals(2, add(1, 1));
    }

    @Test(timeout = 100)
    public void testTimeout() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {

        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testException() {
        new LinkedList<String>().get(0);
    }

    @Ignore("ignore the test")
    @Test
    public void ignoreTest() throws Exception {
        Assert.assertEquals(2, add(1, 1));
    }

    public int add(int n1, int n2) {
        return n1 + n2;
    }
}

@RunWith(Parameterized.class)
class ParameterizedTest {
    private int param;
    private boolean result;

    //为每组数据构建测试用例
    public ParameterizedTest(int param, boolean result) {
        this.param = param;
        this.result = result;
    }

    // 生成测试数据
    @Parameterized.Parameters
    public static Collection<Object[]> genParams() {
        return Arrays.asList(new Object[][]{{1, true}, {2, false}});
    }

    //测试代码
    @Test
    public void test() {
        Assert.assertEquals(this.param % 2 == 1, this.result);
    }
}
