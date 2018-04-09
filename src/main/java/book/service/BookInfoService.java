package book.service;

import book.domain.dto.BookDTO;

import java.util.List;

/**
 * @author yutong song
 * @date 2018/3/20
 */
public interface BookInfoService {

    List<BookDTO>listAllBooks();

    List<BookDTO>AdminListAllBooks();

    List<BookDTO> listBooksByConditions(String conditions);

    List<BookDTO> listBooksByNameAndAuthor(String bookName,String author);

    boolean updateBookByBookId(BookDTO bookDTO);

    boolean insertBook(BookDTO bookDTO);

    boolean deleteBook(long bookId);
}
