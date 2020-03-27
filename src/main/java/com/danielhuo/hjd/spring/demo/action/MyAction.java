package com.danielhuo.hjd.spring.demo.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.danielhuo.hjd.spring.demo.service.IModifyService;
import com.danielhuo.hjd.spring.demo.service.IQueryService;
import com.danielhuo.hjd.spring.formework.annotation.HjdAutowired;
import com.danielhuo.hjd.spring.formework.annotation.HjdController;
import com.danielhuo.hjd.spring.formework.annotation.HjdRequestMapping;
import com.danielhuo.hjd.spring.formework.annotation.HjdRequestParam;
import com.danielhuo.hjd.spring.formework.webmvc.servlet.HjdModelAndView;

/**
 * 公布接口url
 *
 */
@HjdController
@HjdRequestMapping("/web")
public class MyAction {

	@HjdAutowired
	IQueryService queryService;
	@HjdAutowired
	IModifyService modifyService;

	@HjdRequestMapping("/query.json")
	public HjdModelAndView query(HttpServletRequest request, HttpServletResponse response,
								 @HjdRequestParam("name") String name){
		String result = queryService.query(name);
		return out(response,result);
	}
	@HjdRequestMapping("/query/first")
	public HjdModelAndView queryFirst(HttpServletRequest request, HttpServletResponse response,
									  @HjdRequestParam("name") String name){
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("name",name);
		model.put("token", new Random().nextInt(100));
		model.put("data",new Random().nextInt(10000));
		return new HjdModelAndView("first",model);
	}
	@HjdRequestMapping("/add*.json")
	public HjdModelAndView add(HttpServletRequest request, HttpServletResponse response,
							   @HjdRequestParam("name") String name, @HjdRequestParam("addr") String addr){
		String result = null;
		try {
			result = modifyService.add(name,addr);
			return out(response,result);
		} catch (Exception e) {
//			e.printStackTrace();
			Map<String,Object> model = new HashMap<String,Object>();
			model.put("detail",e.getCause().getMessage());
//			System.out.println(Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
			return new HjdModelAndView("500",model);
		}

	}
	
	@HjdRequestMapping("/remove.json")
	public HjdModelAndView remove(HttpServletRequest request, HttpServletResponse response,
								  @HjdRequestParam("id") Integer id){
		String result = modifyService.remove(id);
		return out(response,result);
	}
	
	@HjdRequestMapping("/edit.json")
	public HjdModelAndView edit(HttpServletRequest request, HttpServletResponse response,
								@HjdRequestParam("id") Integer id,
								@HjdRequestParam("name") String name){
		String result = modifyService.edit(id,name);
		return out(response,result);
	}
	
	
	
	private HjdModelAndView out(HttpServletResponse resp, String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
