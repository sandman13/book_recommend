package book.dao;

import book.domain.dataobject.BookDO;

import java.util.List;

/**
 * @author yutong song
 * @date 2018/3/20
 */
public interface BookDao {

    List<BookDO> listAllBooks();

    /**
     * 模糊搜索
     * @param bookName
     * @return
     */
    List<BookDO> listBooksByName(String bookName);

    /**
     * 模糊搜索
     * @param author
     * @return
     */
    List<BookDO> listBooksByAuthor(String author);

    /**
     * 精准搜索
     * @return
     */
    List<BookDO> listBooksByAuthorAndName(String bookName,String author);

    /**
     * 根据id查询书籍信息
     * @param bookId
     * @return
     */
    BookDO queryBookByBookId(long bookId);

    /**
     * 管理员更新书籍信息
     * @param bookDO
     * @return
     */
    long updateBookByBookId(BookDO bookDO);

    /**
     * 管理员添加书籍
     * @param bookDO
     * @return
     */
    long insertBook(BookDO bookDO);

    long deleteBook(long bookId);
}
