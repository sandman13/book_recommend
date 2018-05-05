import book.task.KMeansRecommendation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author yutong song
 * @date 2018/4/30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/applicationContext.xml","classpath:/spring-mvc.xml"})
public class TestKemans {

    @Autowired
    private KMeansRecommendation kMeansRecommendation;

    @Test
    public void kmeansTest(){
       kMeansRecommendation.KmeansRecommend();
    }
}
