package book.controller;

import book.domain.Enum.StatusEnum;
import book.domain.dto.UserDTO;
import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import book.service.UserService;
import book.util.ExceptionHandler;
import book.util.LoggerUtil;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    /**
     * 展示此用户信息
     * @param httpSession
     * @param model
     * @return
     */
    @RequestMapping(value = "/reader/info")
    public String toReader(HttpSession httpSession,Model model)
    {
        BaseResult result = new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in UserController[toReader]");
             UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            UserDTO myUserDTO=userService.queryByUserId(userDTO.getUserId());
            model.addAttribute("userDTO",myUserDTO);
            result.setSuccess(true);
            return "reader_information";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "跳转到用户信息页面失败");
        } catch (Exception e) {
            ExceptionHandler.handleSystemException(LOGGER, result, e, "跳转到用户信息页面失败");
        }
        return "error";
    }

    /**
     * 用户更改个人信息
     * @param httpSession
     * @param myuserDTO
     * @return
     */
    @RequestMapping(value = "/user/update")
    public String updateUser(HttpSession httpSession,UserDTO myuserDTO){
        BaseResult result = new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in UserController[updateUser]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            //如果用户是管理员，则可以更改所有人的信息
            //如果是普通用户，则只能修改自己的信息
            if(!userDTO.getUserStatus().equals(StatusEnum.ADMIN.name()))
            {
                if(userDTO.getUserId()!=myuserDTO.getUserId())
                    return "redirect:/login";
            }
            userService.updateUser(myuserDTO);
            result.setSuccess(true);
            return "redirect:/admin/user";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "更新用户信息失败");
        } catch (Exception e) {
            ExceptionHandler.handleSystemException(LOGGER, result, e, "更新用户信息失败");
        }
        return "error";
    }
}
