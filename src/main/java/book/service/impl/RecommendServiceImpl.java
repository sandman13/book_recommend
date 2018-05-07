package book.service.impl;

import book.dao.RecommendDao;
import book.domain.dataobject.RecommendationDO;
import book.domain.dto.RecommendDTO;
import book.service.RecommendService;
import book.util.LoggerUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author hui zhang
 * @date 2018/3/22
 */
@Service(value = "recommendService")
public class RecommendServiceImpl implements RecommendService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendServiceImpl.class);

    @Resource(name = "recommendDao")
    private RecommendDao recommendDao;

    @Override
    public List<RecommendDTO> listByUserId(long userId,int recommendType) {
        LoggerUtil.info(LOGGER, "ENTER IN  RecommendServiceImpl[listByUserId],userId:{0}", userId);
        return convertToDTOS(recommendDao.listBuUserId(userId,recommendType));
    }

    /**
     * DO对象转DTO对象
     *
     * @param recommendationDOS
     * @return
     */
    private List<RecommendDTO> convertToDTOS(List<RecommendationDO> recommendationDOS) {
        List<RecommendDTO> recommendDTOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(recommendationDOS)) {
            for (RecommendationDO recommendationDO : recommendationDOS) {
                RecommendDTO recommendDTO = convertToDTO(recommendationDO);
                recommendDTOList.add(recommendDTO);
            }
        }
        return recommendDTOList;
    }

    /**
     * DO对象转DTO对象
     * @param recommendationDO
     * @return
     */
    private RecommendDTO convertToDTO(RecommendationDO recommendationDO) {
        RecommendDTO recommendDTO=new RecommendDTO();
        recommendDTO.setAuthor(recommendationDO.getAuthor());
        recommendDTO.setBookName(recommendationDO.getBookName());
        recommendDTO.setUserId(recommendationDO.getUserId());
        return recommendDTO;
    }
}
