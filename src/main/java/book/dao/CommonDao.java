package book.dao;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import javax.annotation.Resource;

/**
 * 这个类是为了自动注入SqlSessionDaoSupport
 * mybatis1.2之后取消了自动注入(为了兼容多数据源)
 *
 * @author hui zhang
 * @date 2018/3/20
 */
public class CommonDao extends SqlSessionDaoSupport {
    @Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
}
