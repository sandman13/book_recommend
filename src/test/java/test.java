import book.controller.AdminController;
import book.domain.dataobject.BookDO;
import book.domain.dto.BookDTO;
import book.service.BookInfoService;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hui zhang
 * @date 2018-4-9
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/applicationContext.xml","classpath:/spring-mvc.xml"})
public class test {

    @Resource(name = "bookInfoService")
   private BookInfoService bookInfoService;
    @Test
    public void testAdd(){
        BookDTO bookDTO=new BookDTO();
        bookDTO.setAuthor("啊哈");
        bookDTO.setBookName("数据结构");
        bookDTO.setIntroduction("计算机相关");
        bookDTO.setGmtCreate("2016-10-10 11:00:00");
        bookDTO.setGmtModified("2017-10-10 12:00:00");
        bookDTO.setPubdate("2017-05-10 09:00:00");
      Assert.assertTrue(bookInfoService.insertBook(bookDTO));
        List<BookDTO> bookDTOList=bookInfoService.AdminListAllBooks();
      Assert.assertTrue(bookDTOList.get(bookDTOList.size()-1).getAuthor().equals("啊哈"));
    }

}
