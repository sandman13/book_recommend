package book.controller;

import book.domain.dto.BorrowDTO;
import book.domain.dto.RecommendDTO;
import book.domain.dto.UserDTO;
import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import book.service.BorrowService;
import book.service.RecommendService;
import book.util.ExceptionHandler;
import book.util.LoggerUtil;
import book.util.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author hui zhang
 * @date 2018/3/21
 */
@Controller
public class BorrowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowController.class);

    @Resource(name = "borrowService")
    private BorrowService borrowService;

    @Resource(name = "recommendService")
    private RecommendService recommendService;
    /**
     * 借阅书籍
     * 1,确定用户登录
     * 2,确定书籍存在
     * 3,更改书籍状态
     * 4,插入一条借阅信息(book_id,user_id,borrow_date)
     * 5,返回json
     *
     * @param httpSession
     * @param bookId
     * @return
     */
    @RequestMapping(value = "borrow/{bookId}")
    public ModelAndView borrow(HttpSession httpSession, @PathVariable long bookId) {
        LoggerUtil.info(LOGGER, "enter in BorrowController[borrow],bookId:{0}", bookId);
        ModelAndView modelAndView = new ModelAndView();
        BaseResult result = new BaseResult();
        try {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
               modelAndView=new ModelAndView();
               modelAndView.setViewName("/login");
            }
            boolean isSuccess = borrowService.borrow(userDTO.getUserId(), bookId);
            ValidateUtils.checkTrue(isSuccess, "借阅图书失败,请重试");
            result.setSuccess(true);

        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "借阅图书失败,bookId:{0}", bookId);
        } catch (Exception ex) {
            ExceptionHandler.handleSystemException(LOGGER, result, ex, "借阅图书失败,bookId:{0}", bookId);
        }
        modelAndView.setView(new MappingJackson2JsonView());
        modelAndView.addObject(result);
        return modelAndView;
    }

    /**
     * 我的借还
     * 根据用户id和状态查找所有有效的借阅订单
     *
     * @param httpSession
     * @return
     */
    @RequestMapping(value = "/reader/borrow")
    public String myBorrow(HttpSession httpSession, Model model) {
        BaseResult result = new BaseResult();
        try {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            LoggerUtil.info(LOGGER, "enter in BorrowController[myBorrow],userId:{0}", userDTO.getUserId());
            List<BorrowDTO> borrowDTOList = borrowService.listByUserId(userDTO.getUserId());
            List<RecommendDTO> recommendDTOList = recommendService.listByUserId(userDTO.getUserId(),0);
            List<RecommendDTO> recommendDTOListKmeans=recommendService.listByUserId(userDTO.getUserId(),1);
            model.addAttribute("recommendList",recommendDTOList);
            model.addAttribute("borrowDTOList", borrowDTOList);
            model.addAttribute("kmeans",recommendDTOListKmeans);
            result.setSuccess(true);
            return "reader_myborrow";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "查询借阅列表失败");
        } catch (Exception ex) {
            ExceptionHandler.handleSystemException(LOGGER, result, ex, "查询借阅列表失败");
        }
        return "error";
    }

    @RequestMapping(value = "/borrow/evaluate/{borrowId}", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView evaluate(HttpSession httpSession, @PathVariable long borrowId, int goal) {
        LoggerUtil.info(LOGGER, "enter in BorrowController[evaluate],borrowId:{0},goal:{1}", borrowId, goal);
        ModelAndView modelAndView=new ModelAndView();
        BaseResult result = new BaseResult();
        try {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                modelAndView.setViewName("/login");
                return modelAndView;
            }
            result.setSuccess(borrowService.updateByBorrowId(borrowId, goal));
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"修改评分失败,borrowId:{0},goal:{1}",borrowId,goal);
        }catch (Exception ex){
            ExceptionHandler.handleSystemException(LOGGER,result,ex,"修改评分失败,borrowId:{0},goal:{1}",borrowId,goal);
        }
        modelAndView.setView(new MappingJackson2JsonView());
        modelAndView.addObject(result);
        return modelAndView;
    }
}




