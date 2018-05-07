package book.dao;

import book.domain.dataobject.RecommendationDO;

import java.util.List;

/**
 * 后台定时任务刷新
 * 我觉得非常粗暴,清空数据库,添加新的信息
 * TODO 以后再搞
 * @author hui zhang
 * @date 2018/3/22
 */
public interface RecommendDao {

    List<RecommendationDO> listBuUserId(long userId,long recommendType);

    long addRecommendation(RecommendationDO recommendationDO);

    boolean deleteBefore(int type);

}
