package com.danielhuo.hjd.spring.demo.service.impl;

import com.danielhuo.hjd.spring.demo.service.IModifyService;
import com.danielhuo.hjd.spring.formework.annotation.HjdService;

/**
 * 增删改业务
 *
 */
@HjdService
public class ModifyService implements IModifyService {

	/**
	 * 增加
	 */
	@Override
	public String add(String name, String addr) throws Exception {
		throw new Exception("故意抛的异常！！");
		//return "modifyService add,name=" + name + ",addr=" + addr;
	}

	/**
	 * 修改
	 */
	@Override
	public String edit(Integer id, String name) {
		return "modifyService edit,id=" + id + ",name=" + name;
	}

	/**
	 * 删除
	 */
	@Override
	public String remove(Integer id) {
		return "modifyService id=" + id;
	}
	
}
