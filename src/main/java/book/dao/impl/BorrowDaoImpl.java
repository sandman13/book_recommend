package book.dao.impl;

import book.dao.BorrowDao;
import book.dao.CommonDao;
import book.domain.dataobject.BorrowDO;
import book.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hui zhang
 * @date 2018/3/21
 */
@Repository(value = "borrowDao")
public class BorrowDaoImpl extends CommonDao implements BorrowDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowDaoImpl.class);

    @Override
    public long addBorrowRecord(BorrowDO borrowDO) {
        LoggerUtil.info(LOGGER, "enter in BorrowDaoImpl[addBorrowRecord],borrowDo:{0}", borrowDO);
        getSqlSession().insert("zhanghui.addBorrowRecord", borrowDO);
        return borrowDO.getBorrowId();
    }

    @Override
    public List<BorrowDO> listByUserId(long userId) {
        LoggerUtil.info(LOGGER, "enter in BorrowDaoImpl[listByUserId],userId:{0}", userId);
        return getSqlSession().selectList("zhanghui.listByUserId", userId);
    }

    @Override
    public long updateBorrow(BorrowDO borrowDO) {
        LoggerUtil.info(LOGGER,"enter in BorrowDaoImpl[updateBorrow],borrow:{0}",borrowDO);
        return getSqlSession().update("zhanghui.updateByBorrowId",borrowDO);
    }

    @Override
    public BorrowDO queryByBorrowId(long borrowId) {
        LoggerUtil.info(LOGGER,"enter in BorrowDaoImpl[queryByBorrowId,borrowId:{0}",borrowId);
        return getSqlSession().selectOne("zhanghui.queryByBorrowId",borrowId);
    }

    @Override
    public List<BorrowDO> listAllBorrows() {
        LoggerUtil.info(LOGGER,"enter in BorrowDaoImpl[listAllBorrows]");
        return getSqlSession().selectList("zhanghui.listAllBorrows");
    }

    @Override
    public long updateStatus(BorrowDO borrowDO) {
        LoggerUtil.info(LOGGER,"enter in BorrowDaoImpl[updateStatus],borrowDO:{0}",borrowDO);
        return getSqlSession().update("zhanghui.updateByBorrowId",borrowDO);
    }
}
