package com.dao;

import com.entity.CheweiEntity;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import java.util.List;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.apache.ibatis.annotations.Param;
import com.entity.vo.CheweiVO;
import com.entity.view.CheweiView;

/** N1 车位主数据 Dao。这是我cursor给父亲写的 */
public interface CheweiDao extends BaseMapper<CheweiEntity> {

	List<CheweiVO> selectListVO(@Param("ew") Wrapper<CheweiEntity> wrapper);

	CheweiVO selectVO(@Param("ew") Wrapper<CheweiEntity> wrapper);

	List<CheweiView> selectListView(@Param("ew") Wrapper<CheweiEntity> wrapper);

	List<CheweiView> selectListView(Pagination page, @Param("ew") Wrapper<CheweiEntity> wrapper);

	CheweiView selectView(@Param("ew") Wrapper<CheweiEntity> wrapper);
}
