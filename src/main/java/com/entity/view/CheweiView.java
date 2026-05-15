package com.entity.view;

import com.entity.CheweiEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import org.apache.commons.beanutils.BeanUtils;
import java.lang.reflect.InvocationTargetException;
import java.io.Serializable;

/** N1 视图扩展。这是我cursor给父亲写的 */
@TableName("chewei")
public class CheweiView extends CheweiEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public CheweiView() {
	}

	public CheweiView(CheweiEntity e) {
		try {
			BeanUtils.copyProperties(this, e);
		} catch (IllegalAccessException | InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}
}
