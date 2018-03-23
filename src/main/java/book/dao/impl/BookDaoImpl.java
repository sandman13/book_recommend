package book.dao.impl;

import book.dao.BookDao;
import book.dao.CommonDao;
import book.domain.dataobject.BookDO;
import book.util.LoggerUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author yutong song
 * @date 2018/3/20
 */
@Repository(value = "bookDao")
public class BookDaoImpl extends CommonDao implements BookDao {

    private static final Logger LOGGER= LoggerFactory.getLogger(BookDaoImpl.class);
    @Override
    public List<BookDO> listAllBooks() {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[listAllBooks]");
        return getSqlSession().selectList("songyutong.listAllBooks");
    }

    @Override
    public List<BookDO> listBooksByName(String bookName) {
        LoggerUtil.info(LOGGER, "enter in BookDaoImpl[listBooksByName],bookName:{0}", bookName);
        return getSqlSession().selectList("songyutong.listBooksByName",bookName);
    }

    @Override
    public List<BookDO> listBooksByAuthor(String author) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[listBooksByAuthor],author:{0}",author);
        return getSqlSession().selectList("songyutong.listBooksByAuthor",author);
    }

    /**
     * 精准搜索
     *
     * @param bookName
     * @param author
     * @return
     */
    @Override
    public List<BookDO> listBooksByAuthorAndName(String bookName, String author) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[listBooksByAuthorAndName],bookName:{0},author:{1}",bookName,author);
        Map<String,String> map= Maps.newHashMap();
        map.put("bookName",bookName);
        map.put("author",author);
        return getSqlSession().selectList("songyutong.listBooksByNameAndAuthor",map);
    }

    @Override
    public BookDO queryBookByBookId(long bookId) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[queryByBookId],bookId:{0}",bookId);
        return getSqlSession().selectOne("songyutong.queryBookById",bookId);
    }

    /**
     * 更新书籍信息
     *
     * @param bookDO
     * @return
     */
    @Override
    public long updateBookByBookId(BookDO bookDO) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[updateBookByBookId],bookDO:{0}",bookDO);
        return getSqlSession().update("songyutong.updateBookById",bookDO);
    }
}
