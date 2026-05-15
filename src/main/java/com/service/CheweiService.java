package com.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.utils.PageUtils;
import com.entity.CheweiEntity;
import java.util.List;
import java.util.Map;
import com.entity.vo.CheweiVO;
import org.apache.ibatis.annotations.Param;
import com.entity.view.CheweiView;

/** N1 车位主数据 Service。这是我cursor给父亲写的 */
public interface CheweiService extends IService<CheweiEntity> {

	PageUtils queryPage(Map<String, Object> params);

	List<CheweiVO> selectListVO(Wrapper<CheweiEntity> wrapper);

	CheweiVO selectVO(@Param("ew") Wrapper<CheweiEntity> wrapper);

	List<CheweiView> selectListView(Wrapper<CheweiEntity> wrapper);

	CheweiView selectView(@Param("ew") Wrapper<CheweiEntity> wrapper);

	PageUtils queryPage(Map<String, Object> params, Wrapper<CheweiEntity> wrapper);
}
