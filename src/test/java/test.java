import book.controller.AdminController;
import book.domain.dataobject.BookDO;
import book.domain.dto.BookDTO;
import book.domain.dto.BorrowDTO;
import book.domain.dto.UserDTO;
import book.service.BookInfoService;
import book.service.BorrowService;
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

    @Resource(name = "borrowService")
    BorrowService borrowService;
    @Test
    public void testAdd() {
        List<BorrowDTO> borrowDTOList=Lists.newArrayList();
        UserDTO userDTO=new UserDTO();
        userDTO.setUsername("aoliao");
        userDTO.setProfession("计算机");
        userDTO.setPassword("111");
        userDTO.setSex(1);
        userDTO.setAge(26);
        userDTO.setEmail("aoliao@126.com");
        userDTO.setPhoneNumber("15152961723");
        registerService.insertUser(userDTO);
        borrowDTOList=borrowService.listByUserId(2);
        for(BorrowDTO borrowDTO:borrowDTOList)
        {
            System.out.println(borrowDTO);
        }

    }



}
