package book.service;

import book.domain.dto.RecommendDTO;

import java.util.List;

/**
 * @author yutong song
 * @date 2018/3/22
 */
public interface RecommendService {

   List<RecommendDTO>listByUserId(long userId);
}
