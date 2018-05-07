package book.controller;

import book.domain.dto.BookDTO;
import book.domain.dto.RecommendDTO;
import book.domain.dto.UserDTO;
import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import book.service.BookInfoService;
import book.service.RecommendService;
import book.util.ExceptionHandler;
import book.util.LoggerUtil;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
@Controller
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    @Resource(name = "bookInfoService")
    private BookInfoService bookInfoService;

    @Resource(name = "recommendService")
    private RecommendService recommendService;

    @RequestMapping(value = "/reader/index", method = RequestMethod.GET)
    /**
     * 展示所有书籍，以及推荐的书籍
     */
    public String listAllBooks(HttpSession httpSession, Model model) {
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in BookController[listAllBooks]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.listAllBooks();
            //获取推荐列表
            List<RecommendDTO> recommendDTOList=recommendService.listByUserId(userDTO.getUserId(),0);
            List<RecommendDTO>recommendDTOListKmeans=recommendService.listByUserId(userDTO.getUserId(),1);
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("bookList", bookDTOList);
            model.addAttribute("recommendDTOList",recommendDTOList);
            model.addAttribute("kmeans",recommendDTOListKmeans);
            result.setSuccess(true);
            return "reader_index";
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"展示书籍失败");
        }catch (Exception ex){
            ExceptionHandler.handleSystemException(LOGGER,result,ex,"展示书籍失败");
        }
        return "error";
    }

    /**
     * 根据书名或者作者查询
     * @param httpSession
     * @param model
     * @param condition
     * @return
     */
    @RequestMapping(value = "/reader/search",method = RequestMethod.POST)
   public String listBooksByConditions(HttpSession httpSession,Model model,String condition){
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in BookController[listBooksByConditions],condition:{0}", condition);
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.listBooksByConditions(condition);
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("bookList", bookDTOList);
            result.setSuccess(true);
            return "reader_index";
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"根据书名或者作者查询书籍失败,condition:{0}",condition);
        }catch (Exception ex){
            ExceptionHandler.handleSystemException(LOGGER,result,ex,"根据书名或者作者查询书籍失败,condition:{0}",condition);
        }
        return "error";
   }

    /**
     * 根据书名和作者显示书籍详情
     * @param httpSession
     * @param model
     * @param bookName
     * @param author
     * @return
     */
   @RequestMapping(value = "/book/{bookName:.+}/{author:.+}")
   public String listDetail(HttpSession httpSession, Model model, @PathVariable String bookName,@PathVariable String author){
        LoggerUtil.info(LOGGER,"enter in BookController[listDetail],bookName:{0},author:{1}",bookName,author);
        BaseResult result=new BaseResult();
        try {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.listBooksByNameAndAuthor(bookName, author);
            List<RecommendDTO> recommendDTOList = recommendService.listByUserId(userDTO.getUserId(),0);
            List<RecommendDTO>recommendDTOListKmeas=recommendService.listByUserId(userDTO.getUserId(),1);
            model.addAttribute("recommendDTOList",recommendDTOList);
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("bookList", bookDTOList);
            model.addAttribute("kmeans",recommendDTOListKmeas);
            result.setSuccess(true);
            return "reader_detail";
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"查询书籍详情失败,bookName:{0},author:{1}",bookName,author);
        }catch (Exception ex){
            ExceptionHandler.handleSystemException(LOGGER,result,ex,"查询书籍详情失败,bookName:{0},author:{1}",bookName,author);
        }
        return "error";
   }

    /**
     * 根据出版社，介绍，作者，位置进行复合搜索
     * @param httpSession
     * @param model
     * @param publisher
     * @param introduction
     * @param author
     * @param location
     * @return
     */
    @RequestMapping(value = "/admin/queryByMultiConditions")
    public String queryByMultiConditions(HttpSession httpSession, Model model, @RequestParam  String publisher, @RequestParam String introduction, @RequestParam String author, @RequestParam String location,@RequestParam(required = false,value = "page",defaultValue = "1") Integer page) {
        LoggerUtil.info(LOGGER, "enter in BookController[queryByMultiConditions],publisher:{0} introduction {1} author:{2} location:{3}", publisher, introduction, author, location);
        BaseResult result = new BaseResult();
        try {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            PageInfo<BookDTO> pageInfo= bookInfoService.queryByMultiConditions(publisher, introduction, author, location,page);
            model.addAttribute("pageInfo",pageInfo);
            model.addAttribute("publisher",publisher);
            model.addAttribute("introduction",introduction);
            model.addAttribute("location",location);
            model.addAttribute("author",author);
            result.setSuccess(true);
            return "admin_multiply";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "复合搜索书籍失败,publisher:{0} introduction {1} author:{2} location:{3}", publisher, introduction, author, location);
        } catch (Exception ex) {
            ExceptionHandler.handleSystemException(LOGGER, result, ex, "复合搜索书籍失败,publisher:{0} introduction {1} author:{2} location:{3}", publisher, introduction, author, location);
        }
        return "error";
    }
}
