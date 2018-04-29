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
import net.sf.jsqlparser.statement.select.Join;
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
 * @author hui zhang
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
     * 4,获取借阅列表(过滤没有打分的书籍,过滤用户和书籍不存在的列表)
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

        //记录某一个人是否看过某一本书,如果看过则分数使用最近的评分.
        //同时还记录上一次修改的位置
        Map<String,Integer> hasSeenTheBook=Maps.newHashMap();

        recommendDao.deleteBefore();
        /**
         * 进行数据清洗，生成处理后的借阅列表
         */
        for (BorrowDO borrowDO : borrowDOList) {
            BookDO bookDO = bookDao.queryBookByBookId(borrowDO.getBookId());
            UserDO userDO = userDao.queryByUserId(borrowDO.getUserId());
            //不通过校验
            if (bookDO == null || userDO == null) {
                continue;
            }
            //TODO 这里如果书名里面有-可能会有bug
            //如果书名和作者都一样认为是同一本书,拼接成“bookName-author”
            String key = Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor());
            if (bookCountMap.get(key) == null) {
                //这本书第一次被读
                bookCountMap.put(key, 1);
                //读者看过某本书，拼接为“userId-bookName-author”，并且记下这条记录的下标
                hasSeenTheBook.put(Joiner.on("-").skipNulls().join(userDO.getUserId(),key),recommendDOList.size());
            } else {
                //不是第一次看这本书
                int count = bookCountMap.get(key);
                bookCountMap.put(key, count + 1);
                //多次评论同一个书籍使用最后一次评分为准
                if (hasSeenTheBook.get(Joiner.on("-").skipNulls().join(userDO.getUserId(),key))!=null) {
                    int index=hasSeenTheBook.get(Joiner.on("-").skipNulls().join(userDO.getUserId(),key));
                    System.out.println("index:"+index);
                    recommendDOList.get(index).setRate(borrowDO.getGoal());
                }
            }
            RecommendDO recommendDO = new RecommendDO();
            recommendDO.setUserId(borrowDO.getUserId());
            recommendDO.setBookId(key);
            recommendDO.setRate(borrowDO.getGoal());
            recommendDOList.add(recommendDO);
        }
        System.out.println("recommendDOList:"+recommendDOList);
        System.out.println("map:" + bookCountMap);
        //一个借阅量的最大堆
        Queue<Map.Entry<String,Integer>> priorityQueue=new PriorityQueue<>((a,b)->(b.getValue().compareTo(a.getValue())));
        priorityQueue.addAll(bookCountMap.entrySet());
        System.out.println("priorityQueue:"+priorityQueue);
        //针对每个用户对每本书评分的最大堆
        Queue<Map.Entry<String,Double>>bookDOQueue=new PriorityQueue<>((a,b)->((b.getValue().compareTo(a.getValue()))));
        for (UserDO userDO : userDOList) {
            //对于每一本书,求出有哪些人看过这本书,再分别求该用户与每一个人的相似度
            priorityQueue.clear();
            //兜底，每次都重新计算借阅量最大堆，防止为这个用户推荐的图书不够三本
            priorityQueue.addAll(bookCountMap.entrySet());
            Map<String, Double> bookCompare = Maps.newHashMap();
            for (BookDO bookDO : bookDOList) {

                //TODO 若该用户看过这本书则不用评分了,肯定不需要推荐
                if (neednot(userDO,bookDO,recommendDOList)){
                    continue;
                }
                //求出有哪些人看过这本书
                List<RecommendDO> SeenTheBook = find(bookDO, recommendDOList);
                System.out.println("SeenTheBook:" + SeenTheBook+"bookName:"+bookDO.getBookName());
                if (CollectionUtils.isEmpty(SeenTheBook)) {
                    continue;
                }

                //recommend列表代表每一个人
                double rate = 0;
                double weightSum = 0;
                for (RecommendDO recommendDO : SeenTheBook) {
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
                    System.out.println("用户"+userDO.getUserId()+"对于"+bookDO.getBookName()+"rate:"+rate);
                    weightSum = weightSum + weight;
                }
                if (weightSum!=0) {
                    //获取该用户对于每一本书的评价得分
                    double score = rate / weightSum;
                    System.out.println("用户" + userDO.getUserId() + "对于" + bookDO.getBookName() + score);
                    //最后所有书的得分求出最大值
                    bookCompare.put(Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor()), score);
                }
            }
            bookDOQueue.clear();
            bookDOQueue.addAll(bookCompare.entrySet());
            System.out.println("最后推荐列表:,userId"+userDO.getUserId()+","+"bookDOQueue:" + bookDOQueue);
            //取前三放置在数据库里面
            int count = 0;
            //判断推荐了那些书
            List<String>recommendList=Lists.newArrayList();
            while (!bookDOQueue.isEmpty() && count < 3) {
                //从推荐最大堆中取出键值对
                String mix = bookDOQueue.poll().getKey();
                //分割出bookName，author
                Iterator<String> iterator = Splitter.on("-").split(mix).iterator();
                List<String> stringList = Lists.newArrayList();
                while (iterator.hasNext()) {
                    stringList.add(iterator.next());
                }
                //添加进入数据库
                pushToDataBase(stringList.get(0), stringList.get(1), userDO.getUserId());
                recommendList.add(Joiner.on("-").skipNulls().join(stringList.get(0),stringList.get(1)));
                count++;
            }
            System.out.println("the rest:"+count);
            //不够的使用热销补齐
            while (count < 3&&!priorityQueue.isEmpty()) {
                Map.Entry<String, Integer> entry = priorityQueue.poll();
                System.out.println(entry);
                String mix = entry.getKey();
                System.out.println(mix);
                Iterator<String> iterator = Splitter.on("-").split(mix).iterator();
                List<String> stringList = Lists.newArrayList();
                while (iterator.hasNext()) {
                    stringList.add(iterator.next());
                }
                //如果借阅最大堆中取出的书读者已经看过，则跳过
                if (hotFilter(userDO,stringList.get(0),stringList.get(1),recommendDOList)){
                    continue;
                }
                //如果这本书已经在推荐列表中，则跳过
                if (recommendList.contains(Joiner.on("-").skipNulls().join(stringList.get(0),stringList.get(1)))){
                    continue;
                }
                recommendList.add(Joiner.on("-").skipNulls().join(stringList.get(0),stringList.get(1)));
                pushToDataBase(stringList.get(0), stringList.get(1), userDO.getUserId());
                count++;
            }

        }
    }

    /**
     * 热销过滤器
     * @param userDO
     * @param s
     * @param s1
     * @param recommendDOListBase
     * @return
     */
    private boolean hotFilter(UserDO userDO, String s, String s1, List<RecommendDO> recommendDOListBase) {
        for (RecommendDO recommendDO : recommendDOListBase) {
            if (recommendDO.getUserId() == userDO.getUserId() && Joiner.on("-").skipNulls().join(s,s1).equals(recommendDO.getBookId())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断该用户是否需要评分
     * @param userDO
     * @param bookDO
     * @param recommendDOListBase
     * @return
     */
    private boolean neednot(UserDO userDO, BookDO bookDO, List<RecommendDO> recommendDOListBase) {
        for (RecommendDO recommendDO : recommendDOListBase) {
            if (recommendDO.getUserId() == userDO.getUserId() && Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor()).equals(recommendDO.getBookId())) {
                return true;
            }
        }
        return false;
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
    private List<RecommendDO> find(BookDO bookDO, List<RecommendDO> borrowDOList){
        List<RecommendDO> recommendDOList = Lists.newArrayList();
        for (RecommendDO recommendDO : borrowDOList) {
            if (recommendDO.getBookId().equals((Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor())))) {
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
        Map<String, RecommendDO> recommendDORecommendDOMapA = Maps.newHashMap();
        Map<String, RecommendDO> recommendDORecommendDOMapB = Maps.newHashMap();

        //1,分别找出a,b看过的书籍
        for (RecommendDO recommendDO : recommendDOListBase) {
            if (recommendDO.getUserId() == userA.getUserId()) {
                recommendDORecommendDOMapA.put(recommendDO.getBookId(), recommendDO);
            } else if (recommendDO.getUserId() == userB.getUserId()) {
                recommendDORecommendDOMapB.put(recommendDO.getBookId(), recommendDO);
            }
        }
        System.out.println("recommendA:" + recommendDORecommendDOMapA);
        System.out.println("recommendB:"+recommendDORecommendDOMapB);
        //如果a或者b没有评过分
        if ((recommendDORecommendDOMapA.size() == 0) || (recommendDORecommendDOMapB.size() == 0)) {
            throw new BusinessException("a,b没有评过分");
        }
        //求出a,b看过书籍的交集
        Set<String> recommendDOSet = Sets.intersection(recommendDORecommendDOMapA.keySet(), recommendDORecommendDOMapB.keySet());
        if (recommendDOSet.size() == 0) {
            throw new BusinessException("a,b没有看过相同的书籍");
        }
        System.out.println("recommendSet:"+recommendDOSet);
        //计算ab的平均值
        Iterator<String> iterator = recommendDOSet.iterator();
        double averageA = 0;
        double averageB = 0;
        while (iterator.hasNext()) {
            String bookId = iterator.next();
            averageA = averageA + recommendDORecommendDOMapA.get(bookId).getRate();
            averageB = averageB + recommendDORecommendDOMapB.get(bookId).getRate();
        }
        averageA = averageA / recommendDOSet.size();
        averageB = averageB /recommendDOSet.size();
        System.out.println("averageA:"+averageA+"averageB:"+averageB);
        iterator = recommendDOSet.iterator();
        double molecule = 0;
        //求分子
        while (iterator.hasNext()) {
            String bookId = iterator.next();
            molecule = molecule + (recommendDORecommendDOMapA.get(bookId).getRate() - averageA) * (recommendDORecommendDOMapB.get(bookId).getRate() - averageB);
        }
        //求分母denominator
        double denominatorLeft = 0;
        iterator = recommendDOSet.iterator();
        while (iterator.hasNext()) {
            String bookId = iterator.next();
            denominatorLeft = denominatorLeft + (recommendDORecommendDOMapA.get(bookId).getRate() - averageA) * (recommendDORecommendDOMapA.get(bookId).getRate() - averageA);
        }
        double denominatorRight = 0;
        iterator = recommendDOSet.iterator();
        while (iterator.hasNext()) {
            String bookId= iterator.next();
            denominatorRight = denominatorRight + (recommendDORecommendDOMapB.get(bookId).getRate() - averageB) * (recommendDORecommendDOMapB.get(bookId).getRate() - averageB);
        }
        System.out.println("molecule:"+molecule);
        return molecule /Math.sqrt( denominatorLeft * denominatorRight);
    }

    public static void main(String[] args) {
        new MyRecommendation().recommend();
    }
}
