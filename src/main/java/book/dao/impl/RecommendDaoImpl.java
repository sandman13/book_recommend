package book.dao.impl;

import book.dao.CommonDao;
import book.dao.RecommendDao;
import book.domain.dataobject.RecommendationDO;
import book.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hui zhang
 * @date 2018/3/22
 */
@Repository(value = "recommendDao")
public class RecommendDaoImpl extends CommonDao implements RecommendDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendDaoImpl.class);

    @Override
    public List<RecommendationDO> listBuUserId(long userId) {
        LoggerUtil.info(LOGGER, "enter in RecommendDaoImpl[listByUserId],userId:{0}", userId);
        return getSqlSession().selectList("recommend.listByUserId",userId);
    }

    @Override
    public long addRecommendation(RecommendationDO recommendationDO) {
        LoggerUtil.info(LOGGER , "enter in recommendDaoImpl[addRecommendation],recommendactionDO:{0}",recommendationDO);
        return getSqlSession().insert("recommend.addRecommendation",recommendationDO);
    }

    @Override
    public boolean deleteBefore() {
        LoggerUtil.info(LOGGER,"enter in recommendDaoImpl[deleteBefore]");
        return getSqlSession().delete("recommend.deleteAll")>0;
    }
}
