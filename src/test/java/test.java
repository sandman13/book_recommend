import book.controller.AdminController;
import book.domain.dataobject.BookDO;
import book.domain.dto.BookDTO;
import book.domain.dto.UserDTO;
import book.service.BookInfoService;
import book.service.RegisterService;
import book.service.UserService;
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
    @Resource(name = "UserService")
   private UserService userService;

    @Resource(name ="RegisterService")
    private RegisterService registerService;
    @Test
    public void testAdd() {
       /* UserDTO userDTO = new UserDTO();
        userDTO.setUserId(133);

        userDTO.setEmail("zh268262441@126.com");
        userDTO.setUsername("Flora");
        userDTO.setPassword("456");
        userDTO.setPhoneNumber("15861813969");
        userDTO.setGmtCreate("2018-01-01 14:00:00");
        userDTO.setGmtModified("2018-01-10 15:00:00");
        */
        //Assert.assertTrue(userService.updateUser(userDTO));

        Assert.assertTrue(userService.deleteUser(133));
    }


}
