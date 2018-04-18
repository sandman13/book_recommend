package book.service.impl;

import book.dao.BookDao;
import book.dao.BorrowDao;
import book.dao.UserDao;
import book.domain.Enum.StatusEnum;
import book.domain.ListAndB;
import book.domain.dataobject.BookDO;
import book.domain.dataobject.BorrowDO;
import book.domain.dataobject.UserDO;
import book.domain.dto.BookDTO;
import book.domain.dto.BorrowDTO;
import book.domain.exception.BusinessException;
import book.service.BorrowService;
import book.util.DateUtils;
import book.util.LoggerUtil;
import book.util.ValidateUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import book.*;

/**
 * @author yutong song
 * @date 2018/3/21
 */
@Service(value = "borrowService")
public class BorrowServiceImpl implements BorrowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowServiceImpl.class);

    @Resource(name = "bookDao")
    private BookDao bookDao;

    @Resource(name = "borrowDao")
    private BorrowDao borrowDao;

    @Resource(name = "transactionTemplate")
    private TransactionTemplate transactionTemplate;

    @Resource(name = "userDao")
    private UserDao userDao;

    public ListAndB listAndB=new ListAndB();
    /**
     * 借阅图书
     *
     * @param userId
     * @param bookId
     * @return
     */
    @Override
    public boolean borrow(long userId, long bookId) {
        LoggerUtil.info(LOGGER, "enter in BorrowServiceImpl[borrow],userId:{0},bookId:{1}", userId, bookId);
        UserDO userDO = userDao.queryByUserId(userId);
        ValidateUtils.checkNotNull(userDO, "借阅人不存在,请登出账号重试");
        BookDO bookDO = bookDao.queryBookByBookId(bookId);
        ValidateUtils.checkNotNull(bookDO, "待借阅的图书不存在");
        ValidateUtils.checkTrue(StatusEnum.CAN_BORROW.name().equals(bookDO.getBookStatus()), "待借阅的图书不是可借阅状态");
        boolean isSuccess = transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                try {
                    //1,更改书籍状态为已借阅
                    bookDO.setBookStatus(StatusEnum.HAS_BORROW.name());
                    bookDO.setModifier(userDO.getUsername());
                    long id = bookDao.updateBookByBookId(bookDO);
                    if (id > 0) {
                        //2,插入一条借阅记录
                        BorrowDO borrowDO = new BorrowDO();
                        borrowDO.setBookId(bookDO.getBookId());
                        borrowDO.setUserId(userDO.getUserId());
                        borrowDO.setBorrowDate(new Date());
                        borrowDO.setBorrowStatus(StatusEnum.VALID.name());
                        borrowDO.setModifier(userDO.getUsername());
                        borrowDO.setBackDate(DateUtils.parse(DateUtils.nextMonth(new Date())));
                        borrowDao.addBorrowRecord(borrowDO);
                        return true;
                    }
                    return false;
                } catch (Exception e) {
                    LoggerUtil.error(e, LOGGER, "借阅失败,userId:{0},bookId:{1}", userId, bookId);
                    status.setRollbackOnly();
                    return false;
                }
            }
        });
        return isSuccess;
    }

    @Override
    public List<BorrowDTO> listByUserId(long userId) {
        LoggerUtil.info(LOGGER, "enter in BorrowServiceImpl[listByUserId],userId:{0}", userId);
        //1,验证用户存在
        //2,查询该用户的借阅记录
        //3,补充图书信息
        UserDO userDO = userDao.queryByUserId(userId);
        ValidateUtils.checkNotNull(userDO, "借阅人不存在,请登出账号重试");
        List<BorrowDO> borrowDOList = borrowDao.listByUserId(userId);
        List<BorrowDTO> borrowDTOList = Lists.newArrayList();
        //补充图书信息
        for (BorrowDO borrowDO : borrowDOList) {
            BookDO bookDO = bookDao.queryBookByBookId(borrowDO.getBookId());
            BorrowDTO borrowDTO = convertToDTO(borrowDO);
            borrowDTO.setBookDTO(convertToDTO(bookDO));
            borrowDTOList.add(borrowDTO);
        }
        return borrowDTOList;
    }

    /**
     * 更新评分
     * @param borrowId
     * @param goal
     * @return
     */
    @Override
    public boolean updateByBorrowId(long borrowId,int goal) {
        LoggerUtil.info(LOGGER,"enter in BorrowServiceImpl[updateByBorrowId],borrowId:{0},goal:{1}",borrowId,goal);
        BorrowDO borrowDO = borrowDao.queryByBorrowId(borrowId);
        ValidateUtils.checkNotNull(borrowDO, "待评分的对象不存在");
        UserDO userDO=userDao.queryByUserId(borrowDO.getUserId());
        borrowDO.setModifier(userDO.getUsername());
        borrowDO.setGoal(goal);
        return borrowDao.updateBorrow(borrowDO)>0;
    }

    @Override
    public ListAndB listAllBorrows() {
        LoggerUtil.info(LOGGER,"enter in BorrowServiceImpl[listAllBorrows]");
        List<BorrowDO> borrowDOList = borrowDao.listAllBorrows();
        List<BorrowDTO> borrowDTOValidList=Lists.newArrayList();
        List<BorrowDTO> borrowDTODeleteList=Lists.newArrayList();
        //补充图书信息
        for (BorrowDO borrowDO : borrowDOList) {
            BookDO bookDO = bookDao.queryBookByBookId(borrowDO.getBookId());
            if(borrowDO.getBorrowStatus().equals(StatusEnum.VALID.name()))
            {
                BorrowDTO borrowDTO = convertToDTO(borrowDO);
                borrowDTO.setBookDTO(convertToDTO(bookDO));
                borrowDTOValidList.add(borrowDTO);
            }
            else
            {
                BorrowDTO borrowDTO = convertToDTO(borrowDO);
                borrowDTO.setBookDTO(convertToDTO(bookDO));
                borrowDTODeleteList.add(borrowDTO);
            }
        }
        listAndB.setListValid(borrowDTOValidList);
        listAndB.setListDeleted(borrowDTODeleteList);
        return listAndB;
    }

    @Override
    public boolean updateStatus(long borrowId) {
        LoggerUtil.info(LOGGER,"enter in BorrowServiceImpl[updateStatus],borrowId:{0}",borrowId);
        BorrowDO borrowDO = borrowDao.queryByBorrowId(borrowId);
        ValidateUtils.checkNotNull(borrowDO, "待更新的记录不存在");
        borrowDO.setBorrowStatus("DELETED");
        long id=borrowDao.updateBorrow(borrowDO);
        return id>0;
    }

    private BookDTO convertToDTO(BookDO bookDO) {
            BookDTO bookDTO = new BookDTO();
            bookDTO.setBookName(bookDO.getBookName());
            bookDTO.setAuthor(bookDO.getAuthor());
            bookDTO.setBookId(bookDO.getBookId());
            bookDTO.setBookStatus(bookDO.getBookStatus());
            bookDTO.setGmtCreate(DateUtils.format(bookDO.getGmtCreate()));
            bookDTO.setGmtModified(DateUtils.format(bookDO.getGmtModified()));
            bookDTO.setIntroduction(bookDO.getIntroduction());
            bookDTO.setModifier(bookDO.getModifier());
            bookDTO.setPubdate(DateUtils.format(bookDO.getPubdate()));
            bookDTO.setPublisher(bookDO.getPublisher());
            bookDTO.setLocation(bookDO.getLocation());
            return bookDTO;
        }

    /**
     * DO对象转DTO对象
     *
     * @param borrowDO
     * @return
     */
    private BorrowDTO convertToDTO(BorrowDO borrowDO) {
        BorrowDTO borrowDTO = new BorrowDTO();
        borrowDTO.setUserId(borrowDO.getUserId());
        borrowDTO.setBorrowId(borrowDO.getBorrowId());
        borrowDTO.setModifier(borrowDO.getModifier());
        borrowDTO.setBorrowStatus(borrowDO.getBorrowStatus());
        borrowDTO.setGmtCreate(DateUtils.format(borrowDO.getGmtCreate()));
        borrowDTO.setGmtModified(DateUtils.format(borrowDO.getGmtModified()));
        borrowDTO.setBackDate(DateUtils.format(borrowDO.getBackDate()));
        borrowDTO.setBorrowDate(DateUtils.format(borrowDO.getBorrowDate()));
        borrowDTO.setGoal(borrowDO.getGoal());
        return borrowDTO;
    }
}
