package book.controller;

import book.domain.dto.UserDTO;
import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import book.service.UserService;
import book.util.ExceptionHandler;
import book.util.LoggerUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.net.HttpCookie;
import java.util.logging.Logger;

/**
 * @author hui zhang
 * @date 2018-4-15
 */
@Controller
public class UserController {
    @Resource(name = "UserService")
    private UserService userService;

    private static final org.slf4j.Logger LOGGER= LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/user/update")
    public String updateUser(HttpSession httpSession,UserDTO userDTO){
        BaseResult result = new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in UserController[updateUser]");
            userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }

            userService.updateUser(userDTO);
            result.setSuccess(true);
            return "redirect:/login";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "更新用户信息失败");
        } catch (Exception e) {
            ExceptionHandler.handleSystemException(LOGGER, result, e, "更新用户信息失败");
        }
        return "error";
    }
}
