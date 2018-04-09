package book.task;

import book.dao.BookDao;
import book.dao.BorrowDao;
import book.dao.RecommendDao;
import book.dao.UserDao;
import book.domain.dataobject.*;
import book.domain.exception.BusinessException;
import book.util.LoggerUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author yutong song
 * @date 2018/3/22
 */
@Component
public class MyRecommendation {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyRecommendation.class);

    @Resource(name = "bookDao")
    private BookDao bookDao;

    @Resource(name = "userDao")
    private UserDao userDao;

    @Resource(name = "borrowDao")
    private BorrowDao borrowDao;

    @Resource(name = "recommendDao")
    private RecommendDao recommendDao;

    @Resource(name = "transactionTemplate")
    private TransactionTemplate transactionTemplate;


    /**
     * 推荐书籍
     * 1,首先计算出借阅量最大的三本书（过滤不存在的书籍)
     * 2,获取用户列表
     * 3,获取书籍列表
     * 4,获取订阅列表(过滤没有打分的书籍,过滤用户和书籍不存在的列表)
     * 5,协同过滤算法后给用户推荐评分最高的三本书
     * 6,不够的由借阅量最大的三本书补齐
     */
    public void recommend() {
        LoggerUtil.info(LOGGER, "enter in MyRecommendation");
        List<UserDO> userDOList = userDao.listAllUsers();
        List<BookDO> bookDOList = bookDao.listAllBooks();
        List<BorrowDO> borrowDOList = borrowDao.listAllBorrows();
        List<RecommendDO> recommendDOList = Lists.newArrayList();
        //记录每一本书被看过的次数
        Map<String, Integer> bookCountMap = Maps.newHashMap();
        //记录每一本书在recommendDOList中的下标
        Map<String, Integer> rememberMap = Maps.newHashMap();
        //记录下热门的三本书
        List<String> HotBookList = Lists.newArrayList();
        //如果书名和作者都一样认为是同一本书
        for (BorrowDO borrowDO : borrowDOList) {
            BookDO bookDO = bookDao.queryBookByBookId(borrowDO.getBookId());
            UserDO userDO = userDao.queryByUserId(borrowDO.getUserId());
            //不通过校验
            if (bookDO == null || userDO == null) {
                continue;
            }
            //TODO 这里如果书名里面有-可能会有bug
            String key = Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor());
            if (bookCountMap.get(key) == null) {
                bookCountMap.put(key, 1);
                //该读者和某种图书第一次产生交集
                rememberMap.put(key, recommendDOList.size());
            } else {
                int count = bookCountMap.get(key);
                bookCountMap.put(key, count + 1);
                //多次评论同一个书籍使用最后一次评分为准
                recommendDOList.get(rememberMap.get(key)).setRate(borrowDO.getGoal());
            }
            RecommendDO recommendDO = new RecommendDO();
            recommendDO.setUserId(borrowDO.getUserId());
            recommendDO.setBookId(key);
            recommendDO.setRate(borrowDO.getGoal());
            recommendDOList.add(recommendDO);
        }
        System.out.println("map:" + bookCountMap);
        //维护一个大小为3的堆
        Queue<Map.Entry<String, Integer>> priorityQueue = new PriorityQueue<Map.Entry<String, Integer>>(1, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int numbera = o1.getValue();
                int numberb = o2.getValue();
                if (numberb > numbera) {
                    return 1;
                }
                if (numbera > numberb) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        for (Map.Entry<String, Integer> entry : bookCountMap.entrySet()) {
            priorityQueue.add(entry);
        }
        Queue<Map.Entry<String, Double>> bookDOQueue = new PriorityQueue<>(new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                double numberA = o1.getValue();
                double numberB = o2.getValue();
                if (numberA < numberB) {
                    return 1;
                } else {
                    if (numberA > numberB) {
                        return -1;
                    }
                }
                return 0;
            }
        });

        for (UserDO userDO : userDOList) {
            //对于每一部电影,求出有哪些人看过这场电影,根据关联度求出人->电影->分数,最后再按照分数排序
            //(对于每一个人)扫描都看过的书,求相似度,最后从他们看过我没看过的书里面求出相似度
            Map<String, Double> bookCompare = Maps.newHashMap();
            for (BookDO bookDO : bookDOList) {
                //求出有哪些人看过这本书
                List<RecommendDO> hasSeenTheBook = find(bookDO, borrowDOList);
                System.out.println("hasSeenTheBook:" + hasSeenTheBook);
                if (CollectionUtils.isEmpty(hasSeenTheBook)) {
                    continue;
                }
                //recommend列表一个人看过一本书只会出现一次,所以确定书籍后recommend对应的是人
                double rate = 0;
                double weightSum = 0;
                for (RecommendDO recommendDO : hasSeenTheBook) {
                    //对于每一个人计算我和他的相似度
                    double weight = 0;
                    try {
                        weight = calUserSimilarity(userDO, recommendDO, recommendDOList);
                    } catch (BusinessException be) {
                        continue;
                    }
                    System.out.println("weight:" + weight);
                    //计算相似度
                    rate = rate + weight * recommendDO.getRate();
                    weightSum = weightSum + weight;
                }
                //获取该用户对于每一本书的评价得分
                double score = rate / weightSum;
                //最后所有分支求出最大值
                bookCompare.put(Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor()), score);
            }
            for (Map.Entry<String, Double> entry : bookCompare.entrySet()) {
                bookDOQueue.add(entry);
            }
            System.out.println("bookDOQueue" + bookDOQueue);
            //取前三放置在数据库里面
            int count = 0;
            recommendDao.deleteBefore();
            while (!bookDOQueue.isEmpty() && count < 10) {
                String mix = bookDOQueue.poll().getKey();
                Iterator<String> iterator = Splitter.on("-").split(mix).iterator();
                List<String> stringList = Lists.newArrayList();
                while (iterator.hasNext()) {
                    stringList.add(iterator.next());
                }
                //添加进入数据库
                pushToDataBase(stringList.get(0), stringList.get(1), userDO.getUserId());
                count++;
            }
            //不够的使用热销补齐
            while (count < 10&&!priorityQueue.isEmpty()) {
                Map.Entry<String, Integer> entry = priorityQueue.poll();
                System.out.println(entry);
                String mix = entry.getKey();
                System.out.println(mix);
                Iterator<String> iterator = Splitter.on("-").split(mix).iterator();
                List<String> stringList = Lists.newArrayList();
                while (iterator.hasNext()) {
                    stringList.add(iterator.next());
                }
                pushToDataBase(stringList.get(0), stringList.get(1), userDO.getUserId());
                count++;
            }

        }
    }

    /**
     * 将定时任务计算结果填充进入数据库
     *
     * @param bookName
     * @param author
     * @param userId
     */
    private void pushToDataBase(String bookName, String author, long userId) {
        RecommendationDO recommendationDO = new RecommendationDO();
        recommendationDO.setUserId(userId);
        recommendationDO.setBookName(bookName);
        recommendationDO.setAuthor(author);
        recommendDao.addRecommendation(recommendationDO);
    }


        /**
         * 对于每一本书,找到看过这本书的人员列表
         *
         * @param bookDO
         * @param borrowDOList
         * @return
         */
        private List<RecommendDO> find (BookDO bookDO, List < BorrowDO > borrowDOList){
            List<RecommendDO> recommendDOList = Lists.newArrayList();
            for (RecommendDO recommendDO : recommendDOList) {
                if (recommendDO.getBookId().equals(Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor()))) {
                    recommendDOList.add(recommendDO);
                }
            }
            return recommendDOList;
        }


        /**
         * 计算我和这个用户的相似度
         * 运用公式(a看过的书籍评分-a平均值)*(b看过的书籍评分-b平均值)求和/(a看过的书籍评分-a的平均值)平方求和乘以(b看过的书籍评分-b的平均值)平方求和
         * UserDO不带评分,recommendDO带评分
         *
         * @param userA
         * @param userB
         * @return
         */
        private double calUserSimilarity (UserDO userA, RecommendDO userB, List < RecommendDO > recommendDOListBase){
            Map<RecommendDO, RecommendDO> recommendDORecommendDOMapA = Maps.newHashMap();
            Map<RecommendDO, RecommendDO> recommendDORecommendDOMapB = Maps.newHashMap();

            //1,分别找出a,b看过的电影
            for (RecommendDO recommendDO : recommendDOListBase) {
                if (recommendDO.getUserId() == userA.getUserId()) {
                    recommendDORecommendDOMapA.put(recommendDO, recommendDO);
                } else if (recommendDO.getUserId() == userB.getUserId()) {
                    recommendDORecommendDOMapB.put(recommendDO, recommendDO);
                }
            }
            //如果a或者b没有评过分
            if ((recommendDORecommendDOMapA.size() == 0) || (recommendDORecommendDOMapB.size() == 0)) {
                throw new BusinessException("a,b没有评过分");
            }
            //求出a,b看过电影的交集
            Set<RecommendDO> recommendDOSet = Sets.union(recommendDORecommendDOMapA.keySet(), recommendDORecommendDOMapB.keySet());
            if (recommendDOSet.size() == 0) {
                throw new BusinessException("a,b没有看过相同的书籍");
            }
            //计算ab的平均值
            Iterator<RecommendDO> iterator = recommendDOSet.iterator();
            double averageA = 0;
            double averageB = 0;
            while (iterator.hasNext()) {
                RecommendDO recommendDO = iterator.next();
                averageA = averageA + recommendDORecommendDOMapA.get(recommendDO).getRate();
                averageB = averageB + recommendDORecommendDOMapB.get(recommendDO).getRate();
            }
            averageA = averageA / recommendDORecommendDOMapA.size();
            averageB = averageB / recommendDORecommendDOMapB.size();
            iterator = recommendDOSet.iterator();
            double molecule = 0;
            //求分子
            while (iterator.hasNext()) {
                RecommendDO recommendDO = iterator.next();
                molecule = molecule + (recommendDORecommendDOMapA.get(recommendDO).getRate() - averageA) * (recommendDORecommendDOMapB.get(recommendDO).getRate() - averageB);
            }
            //求分母denominator
            double denominatorLeft = 0;
            iterator = recommendDOSet.iterator();
            while (iterator.hasNext()) {
                RecommendDO recommendDO = iterator.next();
                denominatorLeft = denominatorLeft + (recommendDORecommendDOMapA.get(recommendDO).getRate() - averageA) * (recommendDORecommendDOMapA.get(recommendDO).getRate() - averageA);
            }
            double denominatorRight = 0;
            iterator = recommendDOSet.iterator();
            while (iterator.hasNext()) {
                RecommendDO recommendDO = iterator.next();
                denominatorRight = denominatorRight + (recommendDORecommendDOMapB.get(recommendDO).getRate() - averageB) * (recommendDORecommendDOMapB.get(recommendDO).getRate() - averageB);
            }
            return molecule /Math.sqrt( denominatorLeft * denominatorRight);
        }
    }
