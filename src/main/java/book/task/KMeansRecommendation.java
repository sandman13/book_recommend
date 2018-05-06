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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author hui zhang
 * @date 2018-4-25
 */
@Component
public class KMeansRecommendation {
    private static final Logger LOGGER = LoggerFactory.getLogger(KMeansRecommendation.class);

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

    private List<String> professionList;

    /*
    1、用户注册时要求填写年龄，性别，职业；
    2、首先计算各个职业之间的相似度P：将看过这本书且职业相同的人的评分求平均值，作为这个职业
    （相当于一个用户，有多少个职业类别就有多少个用户）对这本书的评分，然后根据皮尔孙系数求相似度；
    3、年龄之间的相似度A：7岁产生代沟的可能性较大，B(u,v)=7/(|Bu-Bv|),当|Bu-Bv|>7时；当|Bu-Bv|≤7时，B(u,v)=1;
    4、性别相似度F：相同为0，不相同为1；
    5、两个用户之间的距离即为A+F-P;
    */
    public void KmeansRecommend()
    {
        LoggerUtil.info(LOGGER,"enter in KmeansRecommend");
        recommendDao.deleteBefore();
        InitProfession();
        //待分类的原始值
        List<UserDO> userDOList=userDao.listAllUsers();
        //将要分成的类别数
        int k=(int)Math.sqrt(userDOList.size());
        //定义最大迭代次数
        int maxClusterTimes=500;
        //迭代次数
        int count=1;
        //聚类的结果
        List<List<UserDO>> clusterList=Lists.newArrayListWithCapacity(k);
        for (int i=0;i<k;i++){
            clusterList.add(Lists.newArrayList());
        }
        //质心
        List<UserDO> clusterCenteringList=Lists.newArrayList();
        //随机选择初始的质心
        Collections.shuffle(userDOList);
        for(int i=0;i<k;i++)
            clusterCenteringList.add(userDOList.get(i));
        //计算每个用户最靠近哪个质心，并将其加入相应的类中
        boolean Change=true;
        while(Change&&count<=maxClusterTimes) {
            Change=false;
            for (int i = 0; i < userDOList.size(); i++) {
                //定义一个最小堆，存放用户到质心的距离
                PriorityQueue<Map.Entry<Integer, Double>> priorityQueue = new PriorityQueue<Map.Entry<Integer, Double>>(new Comparator<Map.Entry<Integer, Double>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                        return (int) (o1.getValue() - o2.getValue());
                    }
                });
                Map<Integer, Double> map = Maps.newHashMap();
                for (int j = 0; j < k&&j<clusterCenteringList.size(); j++) {
                    double distance = CalDistance(userDOList.get(i), clusterCenteringList.get(j));
                    map.put(j, distance);
                }
                priorityQueue.addAll(map.entrySet());
                //从堆顶获得最短距离对应的质心下标，将用户加到对应的质心的那一类中
                clusterList.get(priorityQueue.poll().getKey()).add(userDOList.get(i));
            }
            System.out.println(count);
            List<UserDO> newCenterList = CenterUpdate(clusterList);
            if(isChange(clusterCenteringList,newCenterList))
            {
                Change=true;
                clusterCenteringList=newCenterList;
            }
            count++;
            System.out.println(count);
        }
        System.out.println(clusterList);
        //销量的最大堆
        Queue<Map.Entry<String,Integer>> priorityQueue=new PriorityQueue<>((a,b)->(b.getValue().compareTo(a.getValue())));
        Map<String, Integer> bookCountMap=CountSale();
        priorityQueue.addAll(bookCountMap.entrySet());
        //遍历每一个用户，找出他属于哪一类
        //对于这一类中其他用户看过的书，求并集去重
        //然后这些书和他看过的书求差集，存放在最小堆里面
        for(UserDO userDO:userDOList)
        {
            //推荐书籍的数量
            int recommendCount=0;
            priorityQueue.clear();
            //兜底，每次都重新计算借阅量最大堆，防止为这个用户推荐的图书不够三本
            priorityQueue.addAll(bookCountMap.entrySet());
            //记录用户所在类的下标
            int ClusterIndex=0;
            //扫描每一个类
            for(int i=0;i<clusterList.size();i++)
            {
                //扫描这一类中的用户
                for(int j=0;j<clusterList.get(i).size();j++)
                {
                    if(userDO.getUserId()==clusterList.get(i).get(j).getUserId()) {
                        ClusterIndex=i;
                        break;
                    }
                }
            }
            //存放其他其他用户看过的书：bookName-author集合
            Set<String> bookSet=Sets.newHashSet();
            //该用户自己已经看过的书
            Set<String> MyBookSet=Sets.newHashSet();
            for(UserDO userDO1:clusterList.get(ClusterIndex))
            {
                //计算和该用户同一类的其他用户看过的书籍
                if(userDO.getUserId()!=userDO1.getUserId())
                {
                    List<BorrowDO> borrowDOList=borrowDao.listByUserId(userDO1.getUserId());
                    for(BorrowDO borrowDO:borrowDOList)
                    {
                      BookDO bookDO=bookDao.queryBookByBookId(borrowDO.getBookId());
                      bookSet.add(Joiner.on("-").skipNulls().join(bookDO.getBookName(),bookDO.getAuthor()));
                    }
                }
                //计算本人已经看过的书籍
                else
                {
                    List<BorrowDO> borrowDOList=borrowDao.listByUserId(userDO.getUserId());
                    for(BorrowDO borrowDO:borrowDOList)
                    {
                        BookDO bookDO=bookDao.queryBookByBookId(borrowDO.getBookId());
                        MyBookSet.add(Joiner.on("-").skipNulls().join(bookDO.getBookName(),bookDO.getAuthor()));
                    }
                }
            }
            //求出bookSet中有的而MyBookSet中没有的书
            bookSet=Sets.difference(bookSet,MyBookSet);
            List<BookDO> bookDOList=Lists.newArrayList();
            for(String bookAuthor:bookSet)
            {
                //分离出书名和作者
                Iterator<String> iterator = Splitter.on("-").split(bookAuthor).iterator();
                List<String> stringList = Lists.newArrayList();
                while (iterator.hasNext()) {
                    stringList.add(iterator.next());
                }
                //根据书名和作者名查询这本书
                List<BookDO> bookDOList1=bookDao.listBooksByAuthorAndName(stringList.get(0),stringList.get(1));
                //若这本书有很多本，只加入第一本书
                bookDOList.add(bookDOList1.get(0));
            }
            List<String>recommendList=Lists.newArrayList();
            while (recommendCount<bookDOList.size()&& recommendCount< 3) {
               BookDO bookDO=bookDOList.get(recommendCount);
                AddToDataBase(bookDO.getBookName(), bookDO.getAuthor(), userDO.getUserId());
                recommendList.add(Joiner.on("-").skipNulls().join(bookDO.getBookName(),bookDO.getAuthor()));
                recommendCount++;
            }
            //不够的从销量最大堆中取
            while (recommendCount<3&&!priorityQueue.isEmpty()) {
                //获取堆顶元素
                Map.Entry<String, Integer> entry = priorityQueue.poll();
                String max= entry.getKey();
                //分离出
                Iterator<String> iterator = Splitter.on("-").split(max).iterator();
                List<String> stringList = Lists.newArrayList();
                while (iterator.hasNext()) {
                    stringList.add(iterator.next());
                }
                //如果这本书已经在推荐列表中，则跳过
                if (recommendList.contains(Joiner.on("-").skipNulls().join(stringList.get(0),stringList.get(1)))){
                    continue;
                }
                recommendList.add(Joiner.on("-").skipNulls().join(stringList.get(0),stringList.get(1)));
                AddToDataBase(stringList.get(0), stringList.get(1), userDO.getUserId());
                count++;
            }
        }
    }

    /**
     * 添加到数据库
     * @param bookName
     * @param author
     * @param userId
     */
    public void AddToDataBase(String bookName, String author, long userId) {
        RecommendationDO recommendationDO = new RecommendationDO();
        recommendationDO.setUserId(userId);
        recommendationDO.setBookName(bookName);
        recommendationDO.setAuthor(author);
        recommendDao.addRecommendation(recommendationDO);
    }

    /**
     * 判断质心是否还在变化，若还在变化则返回true
     * @param oldCenterList
     * @param newCenterList
     * @return
     */
    public boolean isChange(List<UserDO> oldCenterList,List<UserDO> newCenterList)
    {
        for(UserDO userDO:oldCenterList)
            for(UserDO userDO1:newCenterList)
            {
                   if(!userDO.equals(userDO1))
                       return true;
            }
            return false;
    }

    /**
     * 更新质心，返回质心列表
     * @param clusterList
     * @return
     */
    public List<UserDO> CenterUpdate( List<List<UserDO>> clusterList) {
        List<UserDO> updateCenterList=Lists.newArrayList();
        UserDO user=new UserDO();
        for (List<UserDO> centerList : clusterList) {
            if (CollectionUtils.isEmpty(centerList)){
                continue;
            }
            int age = 0, sex = 0;
            String profession = null;
            Map<String, Integer> map = Maps.newHashMap();
            for (UserDO userDO : centerList) {
                age += userDO.getAge();
                sex += userDO.getSex();
                if (map.get(userDO.getProfession())==null)
                    map.put(userDO.getProfession(), 1);
                else
                    map.put(userDO.getProfession(), map.get(userDO.getProfession()) + 1);
            }
            int count = 0;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() > count) {
                    profession = entry.getKey();
                    count = entry.getValue();
                }
            }
            age=age/centerList.size();
            sex=sex/centerList.size();
            user.setSex(sex);
            user.setAge(age);
            user.setProfession(profession);
            updateCenterList.add(user);
        }
        return updateCenterList;
    }
    /**
     * 计算两个用户之间的距离
     * @param userDOA
     * @param userDOB
     * @return
     */
    public double CalDistance(UserDO userDOA,UserDO userDOB)
    {
        //职业相似度矩阵
        Map<Map<String,String>,Double> SimilarityMap=CalProfessionSimlarity();
        Map<String,String>map=Maps.newHashMap();
        map.put(userDOA.getProfession(),userDOB.getProfession());
        double professionRate=SimilarityMap.get(map);
        double sexRate=userDOA.getSex().equals(userDOB.getSex())?1:0;
        double ageRate=Math.abs(userDOA.getAge()-userDOB.getAge())>7?((double)7/Math.abs(userDOA.getAge()-userDOB.getAge())):1;
        return -ageRate-sexRate-professionRate;
    }

    /**
     * 获得每本书的销量
     * @return
     */
    public Map<String,Integer> CountSale()
    {
        Map<String, Integer> bookCountMap = Maps.newHashMap();
        List<BorrowDO> borrowDOList=borrowDao.listAllBorrows();
        for(BorrowDO borrowDO:borrowDOList) {
            BookDO bookDO=bookDao.queryBookByBookId(borrowDO.getBookId());
            String key = Joiner.on("-").skipNulls().join(bookDO.getBookName(), bookDO.getAuthor());
            if(bookCountMap.get(key)==null)
            {
                bookCountMap.put(key,1);
            }
            else
            {
                int count = bookCountMap.get(key);
                bookCountMap.put(key, count + 1);
            }
        }
        return bookCountMap;
    }
    /**
     * 进行数据清洗，bookName-author相同为一本书，
     * 一个用户多次看过这本书只取最后一次借阅记录，评分也取最后一次的
     * @return
     */
    public List<RecommendDO> DataHandle()
    {
        LoggerUtil.info(LOGGER, "enter in DataHandle");
        List<UserDO> userDOList = userDao.listAllUsers();
        List<BookDO> bookDOList = bookDao.listAllBooks();
        List<BorrowDO> borrowDOList = borrowDao.listAllBorrows();
        List<RecommendDO> recommendDOList = Lists.newArrayList();

        //记录每一本书被看过的次数
        Map<String, Integer> bookCountMap = Maps.newHashMap();

        //记录某一个人是否看过某一本书,如果看过则分数使用最近的评分.
        //同时还记录上一次修改的位置
        Map<String,Integer> hasSeenTheBook=Maps.newHashMap();
        for (BorrowDO borrowDO : borrowDOList) {
            BookDO bookDO = bookDao.queryBookByBookId(borrowDO.getBookId());
            UserDO userDO = userDao.queryByUserId(borrowDO.getUserId());
            //不通过校验
            if (bookDO == null || userDO == null) {
                continue;
            }
            //书名和作者都一样认为是同一本书,拼接成“bookName-author”
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
                    recommendDOList.get(index).setRate(borrowDO.getGoal());
                }
            }
            RecommendDO recommendDO = new RecommendDO();
            recommendDO.setUserId(borrowDO.getUserId());
            recommendDO.setAge(userDao.queryByUserId(borrowDO.getUserId()).getAge());
            recommendDO.setSex(userDao.queryByUserId(borrowDO.getUserId()).getSex());
            recommendDO.setProfession(userDao.queryByUserId(borrowDO.getUserId()).getProfession());
            recommendDO.setBookId(key);
            recommendDO.setRate(borrowDO.getGoal());
            recommendDOList.add(recommendDO);
        }
        return recommendDOList;
    }
    /**
     * 返回职业相似度的矩阵
     * @return
     */
    public Map<Map<String, String>, Double> CalProfessionSimlarity() {
        List<RecommendDO> recommendDOList=DataHandle();
        List<BookDO> bookDOList = bookDao.listAllBooks();
        Map<Map<String, String>, Double> SimilarityMap = Maps.newHashMap();
        for (int i=0;i<professionList.size();i++){
            Map<String, String> map = Maps.newHashMap();
            map.put(professionList.get(i),professionList.get(i));
            SimilarityMap.put(map,1.0);
        }
        for (int i = 0; i < professionList.size(); i++)
            for (int j = i + 1; j < professionList.size(); j++) {
                //存放两个不同职业对每本书的评分
                Map<String, Double> mapA = Maps.newHashMap();
                Map<String, Double> mapB = Maps.newHashMap();
                //记录是存的哪两个职业
                Map<String, String> map = Maps.newHashMap();
                for (int k = 0; k < bookDOList.size(); k++) {
                    //书-评分
                    double sumA = 0, sumB = 0;
                    double totalA = 0, totalB = 0;
                    for (int l = 0; l <recommendDOList.size(); l++) {
                        String bookName=recommendDOList.get(l).getBookId().substring(0,recommendDOList.get(l).getBookId().indexOf("-"));
                        if ((StringUtils.equals(bookDOList.get(k).getBookName(),bookName))&& (StringUtils.equals(recommendDOList.get(l).getProfession(),(professionList.get(i))))){
                            sumA += recommendDOList.get(l).getRate();
                            totalA++;
                        } else if ((StringUtils.equals(bookName,(bookDOList.get(k).getBookName()))) && (StringUtils.equals(recommendDOList.get(l).getProfession(),professionList.get(j)))) {
                            sumB +=recommendDOList.get(l).getRate();
                            totalB++;
                        }
                    }
                    if (totalA != 0){
                        mapA.put(bookDOList.get(k).getBookName(), sumA / totalA);
                    }
                    if (totalB != 0){
                        mapB.put(bookDOList.get(k).getBookName(), sumB / totalB);
                    }
                }
                //计算这两个职业的相似度
                //1、求看过的书的交集
                Set<String> bookSet = Sets.intersection(mapA.keySet(), mapB.keySet());
                if (bookSet.size() == 0) {
                   // throw new BusinessException("没有看过相同的书籍");
                    return SimilarityMap;
                }
                System.out.println("bookSet:" + bookSet);
                //计算ab的平均值
                Iterator<String> iterator = bookSet.iterator();
                double averageA = 0;
                double averageB = 0;
                while (iterator.hasNext()) {
                    String bookName = iterator.next();
                    averageA = averageA + mapA.get(bookName);
                    averageB = averageB + mapB.get(bookName);
                }
                averageA = averageA / bookSet.size();
                averageB = averageB / bookSet.size();
                System.out.println("averageA:" + averageA + "averageB:" + averageB);
                iterator = bookSet.iterator();
                double molecule = 0;
                //求分子
                while (iterator.hasNext()) {
                    String bookName = iterator.next();
                    molecule = molecule + (mapA.get(bookName) - averageA) * (mapB.get(bookName) - averageB);
                }
                //求分母denominator
                double denominatorLeft = 0;
                iterator = bookSet.iterator();
                while (iterator.hasNext()) {
                    String bookName = iterator.next();
                    denominatorLeft = denominatorLeft + (mapA.get(bookName) - averageA) * (mapA.get(bookName) - averageA);
                }
                double denominatorRight = 0;
                iterator = bookSet.iterator();
                while (iterator.hasNext()) {
                    String bookName = iterator.next();
                    denominatorRight = denominatorRight + (mapB.get(bookName) - averageB) * (mapB.get(bookName) - averageB);
                }
                map.put(professionList.get(i), professionList.get(j));
                Map<String,String>map2=Maps.newHashMap();
                map2.put(professionList.get(j),professionList.get(i));
                SimilarityMap.put(map2,molecule / Math.sqrt(denominatorLeft * denominatorRight));
                System.out.println("molecule:" + molecule);
                SimilarityMap.put(map, molecule / Math.sqrt(denominatorLeft * denominatorRight));
            }
            System.out.print(SimilarityMap);
            return SimilarityMap;
    }

    public void InitProfession() {
        professionList=Lists.newArrayList();
        professionList.add(0, "IT");
        professionList.add(1, "医生");
        professionList.add(2, "老师");
    }

}
