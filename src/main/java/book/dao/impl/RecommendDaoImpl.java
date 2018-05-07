package book.dao.impl;

import book.dao.CommonDao;
import book.dao.RecommendDao;
import book.domain.dataobject.RecommendationDO;
import book.util.LoggerUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author hui zhang
 * @date 2018/3/22
 */
@Repository(value = "recommendDao")
public class RecommendDaoImpl extends CommonDao implements RecommendDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendDaoImpl.class);

    @Override
    public List<RecommendationDO> listBuUserId(long userId,long recommendType) {
        LoggerUtil.info(LOGGER, "enter in RecommendDaoImpl[listByUserId],userId:{0}", userId);
        Map<String,String> map= Maps.newHashMap();
        map.put("userId",String.valueOf(userId));
        map.put("recommendType",String.valueOf(recommendType));
        return getSqlSession().selectList("recommend.listByUserId",map);
    }

    @Override
    public long addRecommendation(RecommendationDO recommendationDO) {
        LoggerUtil.info(LOGGER , "enter in recommendDaoImpl[addRecommendation],recommendactionDO:{0}",recommendationDO);
        return getSqlSession().insert("recommend.addRecommendation",recommendationDO);
    }

    @Override
    public boolean deleteBefore(int type) {
        LoggerUtil.info(LOGGER,"enter in recommendDaoImpl[deleteBefore]");
        return getSqlSession().delete("recommend.deleteAll",type)>0;
    }
}
