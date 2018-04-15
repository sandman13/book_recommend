package book.controller;

import book.domain.dto.BookDTO;
import book.domain.dto.UserDTO;
import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import book.service.BookInfoService;
import book.service.UserService;
import book.task.OSS;
import book.util.ExceptionHandler;
import book.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author hui zhang
 * @date 2018-4-9
 */
@Controller
public class AdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
    @Resource(name = "bookInfoService")
    private BookInfoService bookInfoService;

   @Resource(name = "UserService")
    private UserService userService;

    @Resource(name = "ossClient")
    private OSS oss;
    /**
     * 管理员下展示所有书籍
     * @param httpSession
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/index",method = RequestMethod.GET)
    public String listAllBooks(HttpSession httpSession, Model model) {
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in AdminController[listAllBooks]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.AdminListAllBooks();
            model.addAttribute("bookDTOList",bookDTOList);
            model.addAttribute("userDTO",userDTO);
            result.setSuccess(true);
            return "admin_index";
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"展示书籍失败");
        }catch (Exception ex) {
            ExceptionHandler.handleSystemException(LOGGER, result, ex, "展示书籍失败");
        }
        return "error";
    }

    /**
     * 管理员根据BookId更新图书信息
     * @param httpSession
     * @param bookDTO
     * @return
     */
    @RequestMapping(value = "/admin/update")
    public String updateBookByBookId(HttpSession httpSession, BookDTO bookDTO, MultipartFile multipartFile) {
        BaseResult result = new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in AdminController[updateBookByBookId]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
          oss.upload(multipartFile.getOriginalFilename(),multipartFile.getInputStream());
          bookDTO.setPhotoUrl(oss.getURL(multipartFile.getOriginalFilename()));
          bookInfoService.updateBookByBookId(bookDTO);
            result.setSuccess(true);
            return "redirect:/admin/index";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "更新书籍失败");
        } catch (Exception e) {
            ExceptionHandler.handleSystemException(LOGGER, result, e, "更新书籍失败");
        }
        return "error";
    }

    /**
     * 管理员增加书籍
     * @param httpSession
     * @param model
     * @param bookDTO
     * @return
     */
    @RequestMapping(value = "/admin/insert")
    public String insertBook(HttpSession httpSession,Model model,BookDTO bookDTO ,MultipartFile multipartFile) {
        BaseResult result = new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in AdminController[insertBook]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            oss.upload(multipartFile.getOriginalFilename(),multipartFile.getInputStream());
            bookDTO.setPhotoUrl(oss.getURL(multipartFile.getOriginalFilename()));
            bookInfoService.insertBook(bookDTO);
            result.setSuccess(true);
            return "redirect:/admin/index";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "添加书籍失败");
        } catch (Exception e) {
            ExceptionHandler.handleSystemException(LOGGER, result, e, "添加书籍失败");
        }
        return "error";
    }

    /**
     * 管理员删除书籍
     * @param httpSession
     * @param bookId
     * @return
     */
    @RequestMapping(value = "/admin/delete/book/{bookId}")
    public String deleteBook(HttpSession httpSession,@PathVariable  long bookId)
    {
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in AdminController[deleteBook]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            bookInfoService.deleteBook(bookId);
            result.setSuccess(true);
            return "redirect:/admin/index";
        }catch(BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"删除书籍失败");
        }catch(Exception e){
            ExceptionHandler.handleSystemException(LOGGER,result,e,"删除书籍失败");
        }
        return "error";
    }
    @RequestMapping(value = "/admin/listUser")
    public String listAllUsers(HttpSession httpSession,Model model)
    {
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in AdminController[listAllUsers]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
           List<UserDTO> userDTOList= userService.listAllUsers();
            model.addAttribute("userList",userDTOList);
            result.setSuccess(true);
            return "user";
        }catch(BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"展示所有用户失败");
        }catch(Exception e){
            ExceptionHandler.handleSystemException(LOGGER,result,e,"展示所有用户失败");
        }
        return "error";
    }

    public String deleteUser(HttpSession httpSession,@PathVariable long UserId)
    {
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in AdminController[deleteUser]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            userService.deleteUser(UserId);
            result.setSuccess(true);
            return "redirect:/admin/listUser";
        }catch(BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"删除用户失败");
        }catch(Exception e){
            ExceptionHandler.handleSystemException(LOGGER,result,e,"删除用户失败");
        }
        return "error";
    }
}
