package book.service;

import book.domain.dto.BookDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author hui zhang
 * @date 2018/3/20
 */
public interface BookInfoService {

    List<BookDTO>listAllBooks();

    List<BookDTO>AdminListAllBooks();

    PageInfo<BookDTO> AdminListAllBooks(int page);

    List<BookDTO> queryBookByBookName(String bookName);

    List<BookDTO> listBooksByConditions(String conditions);

    List<BookDTO> listBooksByNameAndAuthor(String bookName,String author);

    boolean updateBookByBookId(BookDTO bookDTO);

    boolean insertBook(BookDTO bookDTO);

    boolean deleteBook(long bookId);

    PageInfo<BookDTO> queryByMultiConditions(String publisher,String introduction,String author,String location,int page);
}
