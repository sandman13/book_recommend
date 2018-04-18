package book.dao.impl;

import book.dao.BookDao;
import book.dao.CommonDao;
import book.domain.dataobject.BookDO;
import book.util.LoggerUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
@Repository(value = "bookDao")
public class BookDaoImpl extends CommonDao implements BookDao {

    private static final Logger LOGGER= LoggerFactory.getLogger(BookDaoImpl.class);
    @Override
    public PageInfo<BookDO> listAllBooksWithPage(int pageNumber) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[listAllBooks]");
        PageHelper.startPage(pageNumber,8);
        List<BookDO> bookDOList = getSqlSession().selectList("zhanghui.listAllBooks");
        PageInfo<BookDO> pageInfo=new PageInfo<>(bookDOList);
        return pageInfo;
    }

    @Override
    public List<BookDO> listAllBooks() {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[listAllBooks]");
        return  getSqlSession().selectList("zhanghui.listAllBooks");
    }

    @Override
    public List<BookDO> listBooksByName(String bookName) {
        LoggerUtil.info(LOGGER, "enter in BookDaoImpl[listBooksByName],bookName:{0}", bookName);
        return getSqlSession().selectList("zhanghui.listBooksByName",bookName);
    }

    @Override
    public List<BookDO> listBooksByAuthor(String author) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[listBooksByAuthor],author:{0}",author);
        return getSqlSession().selectList("zhanghui.listBooksByAuthor",author);
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
        return getSqlSession().selectList("zhanghui.listBooksByNameAndAuthor",map);
    }

    @Override
    public BookDO queryBookByBookId(long bookId) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[queryByBookId],bookId:{0}",bookId);
        return getSqlSession().selectOne("zhanghui.queryBookById",bookId);
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
        return getSqlSession().update("zhanghui.updateBookById",bookDO);
    }

    @Override
    public long insertBook(BookDO bookDO) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[insertBook],bookDO{0}",bookDO);
        return getSqlSession().insert("zhanghui.insertBook",bookDO);
    }

    @Override
    public long deleteBook(long bookId) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[deleteBook],bookId{0}",bookId);
        return getSqlSession().delete("zhanghui.deleteBook",bookId);
    }

    @Override
    public PageInfo<BookDO> queryByMultiConditions(String publisher, String introduction, String author, String location,int page) {
        LoggerUtil.info(LOGGER,"enter in BookDaoImpl[queryByMultiConditions],publisher:{0} introduction {1} author:{2} location:{3}",publisher,introduction,author,location);
        Map<String,String> map= Maps.newHashMap();
        map.put("publisher",publisher);
        map.put("introduction",introduction);
        map.put("author",author);
        map.put("location",location);
        PageHelper.startPage(page,8);
        List<BookDO> bookDOList = getSqlSession().selectList("zhanghui.queryByMultiConditions", map);
        PageInfo<BookDO> pageInfo=new PageInfo<>(bookDOList);
        return pageInfo;
    }
}
