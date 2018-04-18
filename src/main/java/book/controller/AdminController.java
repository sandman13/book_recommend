package book.controller;

import book.domain.ListAndB;
import book.domain.dto.BookDTO;
import book.domain.dto.BorrowDTO;
import book.domain.dto.UserDTO;
import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import book.service.BookInfoService;
import book.service.BorrowService;
import book.service.UserService;
import book.task.OSS;
import book.util.ExceptionHandler;
import book.util.LoggerUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Resource(name = "borrowService")
    private BorrowService borrowService;
    /**
     * 管理员下展示所有书籍
     * url支持参数为空和提供参数两种情况
     * @param httpSession
     * @param model
     * @return
     */
    @RequestMapping(value ="/admin/index",method = RequestMethod.GET)
    public String listAllBooks(HttpSession httpSession, Model model,@RequestParam(required = false,value = "page",defaultValue = "1") Integer page) {
        BaseResult result=new BaseResult();
        System.out.println(page);
        try {
            LoggerUtil.info(LOGGER, "enter in AdminController[listAllBooks]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            PageInfo<BookDTO> pageInfo = bookInfoService.AdminListAllBooks(page);
            model.addAttribute("page",pageInfo);
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
    @RequestMapping(value = "/admin/user")
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
            return "admin_user";
        }catch(BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"展示所有用户失败");
        }catch(Exception e){
            ExceptionHandler.handleSystemException(LOGGER,result,e,"展示所有用户失败");
        }
        return "error";
    }


    @RequestMapping(value = "/admin/delete/user/{UserId}")
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
            return "redirect:/admin/user";
        }catch(BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"删除用户失败");
        }catch(Exception e){
            ExceptionHandler.handleSystemException(LOGGER,result,e,"删除用户失败");
        }
        return "error";
    }

    @RequestMapping(value = "/admin/queryByName")
     public String queryByUserName(HttpSession httpSession,Model model,String username)
     {
         BaseResult result=new BaseResult();
         try {
             LoggerUtil.info(LOGGER, "enter in AdminController[queryByName],username{0}",username);
             UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
             if (userDTO == null) {
                 return "redirect:/login";
             }
             UserDTO myUserDTO=userService.queryByName(username);
             List<UserDTO> userDTOList= Lists.newArrayList();
             userDTOList.add(myUserDTO);
             model.addAttribute("userList",userDTOList);
             result.setSuccess(true);
             return "admin_user";
         }catch(BusinessException be){
             ExceptionHandler.handleBusinessException(LOGGER,result,be,"查询用户失败");
         }catch(Exception e){
             ExceptionHandler.handleSystemException(LOGGER,result,e,"查询用户失败");
         }
         return "error";
     }
     @RequestMapping(value = "/admin/queryBook")
     public String queryByBookName(HttpSession httpSession,String bookName,Model model)
     {
         BaseResult result=new BaseResult();
         try {
             LoggerUtil.info(LOGGER, "enter in AdminController[queryByBookName],bookname{0}",bookName);
             UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
             if (userDTO == null) {
                 return "redirect:/login";
             }
             List<BookDTO> bookDTOList=bookInfoService.queryBookByBookName(bookName);
             model.addAttribute("bookList",bookDTOList);
             result.setSuccess(true);
             return "admin_query";
         }catch(BusinessException be){
             ExceptionHandler.handleBusinessException(LOGGER,result,be,"查询书籍失败");
         }catch(Exception e){
             ExceptionHandler.handleSystemException(LOGGER,result,e,"查询书籍失败");
         }
         return "error";
     }

    /**
     * 管理员展示所有借阅记录
     * @param httpSession
     * @param model
     * @return
     */
    @RequestMapping(value = "/admin/orderList")
     public String OrderManage(HttpSession httpSession,Model model)
     {
         BaseResult result=new BaseResult();
         try {
             LoggerUtil.info(LOGGER, "enter in AdminController[OrderManage]");
             UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
             if (userDTO == null) {
                 return "redirect:/login";
             }
             ListAndB listAndB=borrowService.listAllBorrows();
             model.addAttribute("orderValidList",listAndB.getListValid());
             model.addAttribute("orderDeletedList",listAndB.getListDeleted());
             result.setSuccess(true);
             return "admin_order";
         }catch(BusinessException be){
             ExceptionHandler.handleBusinessException(LOGGER,result,be,"查询书籍失败");
         }catch(Exception e){
             ExceptionHandler.handleSystemException(LOGGER,result,e,"查询书籍失败");
         }
         return "error";
     }

    /**
     * 管理员根据用户名查找对应的借阅记录
     * @param httpSession
     * @param model
     * @param username
     * @return
     */
    @RequestMapping(value = "/admin/queryOrder")
     public String queryOrderByUser(HttpSession httpSession,Model model,String username)
     {
         BaseResult result=new BaseResult();
         try {
             LoggerUtil.info(LOGGER, "enter in AdminController[queryOrderByUser],username:{0}",username);
             UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
             if (userDTO == null) {
                 return "redirect:/login";
             }
             UserDTO myUserDTO=userService.queryByName(username);
             List<BorrowDTO> borrowDTOList=borrowService.listByUserId(myUserDTO.getUserId());
             model.addAttribute("username",username);
             model.addAttribute("borrowDTOList",borrowDTOList);
             result.setSuccess(true);
             return "admin_order_name";
         }catch(BusinessException be){
             ExceptionHandler.handleBusinessException(LOGGER,result,be,"查询借阅记录失败");
         }catch(Exception e){
             ExceptionHandler.handleSystemException(LOGGER,result,e,"查询借阅记录失败");
         }
         return "error";

     }

    /**
     * 管理员更改borrow_status
     * @param httpSession
     * @param borrowId
     * @return
     */
     @RequestMapping(value = "/admin/updateOrder")
     public String updateOrder(HttpSession httpSession,long borrowId)
     {
         BaseResult result=new BaseResult();
         try {
             LoggerUtil.info(LOGGER, "enter in AdminController[updateOrder]");
             UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
             if (userDTO == null) {
                 return "redirect:/login";
             }
             borrowService.updateStatus(borrowId);
             result.setSuccess(true);
             return "redirect:/admin/orderList";
         }catch(BusinessException be){
             ExceptionHandler.handleBusinessException(LOGGER,result,be,"更改借阅状态失败");
         }catch(Exception e){
             ExceptionHandler.handleSystemException(LOGGER,result,e,"更改借阅状态失败");
         }
         return "error";
     }
}
