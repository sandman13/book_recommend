package book.domain;

import book.domain.dto.BorrowDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author hui zhang
 * @date 2018-4-17
 */
public class ListAndB {
    //未还的借阅记录
    List<BorrowDTO> listValid=Lists.newArrayList();

    //已还的借阅记录
    List<BorrowDTO> listDeleted=Lists.newArrayList();

    public List<BorrowDTO> getListValid() {
        return listValid;
    }

    public void setListValid(List<BorrowDTO> listValid) {
        this.listValid = listValid;
    }

    public List<BorrowDTO> getListDeleted() {
        return listDeleted;
    }

    public void setListDeleted(List<BorrowDTO> listDeleted) {
        this.listDeleted = listDeleted;
    }

    public ListAndB() {
    }
}
