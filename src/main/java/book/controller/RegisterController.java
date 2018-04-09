package book.controller;

import book.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

/**
 * @author hui zhang
 * @date 2018-4-1
 */
@Controller
public class RegisterController {

    @Resource(name = "RegisterService")
    private RegisterService registerService;
    private static final Logger LOGGER= LoggerFactory.getLogger(RegisterController.class);
    @RequestMapping(value = "/register")
    public String Register(String username,String password)
    {

        if(registerService.insertUser(username,password))
            return "redirect:/login";
        else
            return "redirect:/toRegister";
    }


    @RequestMapping(value = "/toRegister",method = RequestMethod.GET)
    public String toRegister()
    {
return "register";
    }
}
