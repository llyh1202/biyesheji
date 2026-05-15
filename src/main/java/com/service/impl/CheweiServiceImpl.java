package com.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.utils.PageUtils;
import com.utils.Query;

import com.dao.CheweiDao;
import com.entity.CheweiEntity;
import com.service.CheweiService;
import com.entity.vo.CheweiVO;
import com.entity.view.CheweiView;

/** N1 车位主数据 Service 实现。这是我cursor给父亲写的 */
@Service("cheweiService")
public class CheweiServiceImpl extends ServiceImpl<CheweiDao, CheweiEntity> implements CheweiService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		Page<CheweiEntity> page = this.selectPage(new Query<CheweiEntity>(params).getPage(),
				new EntityWrapper<CheweiEntity>());
		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params, Wrapper<CheweiEntity> wrapper) {
		Page<CheweiView> page = new Query<CheweiView>(params).getPage();
		page.setRecords(baseMapper.selectListView(page, wrapper));
		return new PageUtils(page);
	}

	@Override
	public List<CheweiVO> selectListVO(Wrapper<CheweiEntity> wrapper) {
		return baseMapper.selectListVO(wrapper);
	}

	@Override
	public CheweiVO selectVO(Wrapper<CheweiEntity> wrapper) {
		return baseMapper.selectVO(wrapper);
	}

	@Override
	public List<CheweiView> selectListView(Wrapper<CheweiEntity> wrapper) {
		return baseMapper.selectListView(wrapper);
	}

	@Override
	public CheweiView selectView(Wrapper<CheweiEntity> wrapper) {
		return baseMapper.selectView(wrapper);
	}
}
