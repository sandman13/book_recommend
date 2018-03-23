package book.service.impl;

import book.dao.BookDao;
import book.domain.dataobject.BookDO;
import book.domain.dto.BookDTO;
import book.service.BookInfoService;
import book.util.DateUtils;
import book.util.LoggerUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yutong song
 * @date 2018/3/20
 */
@Service(value = "bookInfoService")
public class BookInfoServiceImpl implements BookInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookInfoService.class);

    @Resource(name = "bookDao")
    private BookDao bookDao;

    @Override
    public List<BookDTO> listAllBooks() {
        LoggerUtil.info(LOGGER, "enter in BookInfoService[listAllBooks]");
        List<BookDO> bookDOList = bookDao.listAllBooks();
        return convertDOsTODTOs(bookDOList);
    }

    @Override
    public List<BookDTO> listBooksByConditions(String conditions) {
        LoggerUtil.info(LOGGER, "enter in BookInfoService[listBooksByConditions],conditions:{0}", conditions);
        conditions= StringUtils.isEmpty(conditions)?" ":'%'+conditions+'%';
        List<BookDO> bookDOList=bookDao.listBooksByName(conditions);
        bookDOList.addAll(bookDao.listBooksByAuthor(conditions));
        return convertDOsTODTOs(bookDOList);
    }

    @Override
    public List<BookDTO> listBooksByNameAndAuthor(String bookName,String author){
        LoggerUtil.info(LOGGER,"enter in BookInfoService[listBooksByNameAndAuthor],bookName:{0},author:{1}",bookName,author);
        List<BookDO> bookDoList = bookDao.listBooksByAuthorAndName(bookName, author);
        List<BookDTO> bookDTOList=Lists.newArrayList();
        if (!CollectionUtils.isEmpty(bookDoList)){
            for (BookDO bookDO:bookDoList){
                BookDTO bookDTO=convertDOTODTO(bookDO);
                bookDTOList.add(bookDTO);
            }
        }
        return bookDTOList;
    }

    /**
     * DO集合转DTO集合
     *
     * @param bookDOList
     * @return
     */
    private List<BookDTO> convertDOsTODTOs(List<BookDO> bookDOList) {
        //存储着具体每一种类型的书籍
        Map<String, BookDTO> map = Maps.newHashMap();
        List<BookDTO> bookDTOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(bookDOList)) {
            for (BookDO bookDO : bookDOList) {
                BookDTO bookDTO = convertDOTODTO(bookDO);
                //认为bookName,author相同的是同一本书
                String key = Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor());
                if (map.get(key) == null) {
                    map.put(key, bookDTO);
                    map.get(key).setStock(1);
                } else {
                    long stock = map.get(key).getStock();
                    map.get(key).setStock(stock + 1);
                }
            }
        }
        for (Map.Entry<String, BookDTO> entry : map.entrySet()) {
            bookDTOList.add(entry.getValue());
        }
        return bookDTOList;
    }

    /**
     * DO集合转DTO集合
     *
     * @param bookDO
     * @return
     */
    private BookDTO convertDOTODTO(BookDO bookDO) {
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
}
