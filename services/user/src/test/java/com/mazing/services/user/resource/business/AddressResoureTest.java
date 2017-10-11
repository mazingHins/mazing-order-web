package com.mazing.services.user.resource.business;

import java.util.List;

import org.junit.Test;

import com.yogu.core.test.ApiReq;
import com.yogu.core.test.HttpResourceTest;
import com.yogu.core.web.RestResult;

/**
 * AddressResoure测试
 * 
 * @author Hins createTime：2015年7月24日 上午11:19:27
 */
public class AddressResoureTest extends HttpResourceTest {

	public AddressResoureTest() {
		host = "http://userapi.yogubc.com";
	}
	
	@Test
	public void save() {
		ApiReq<RestResult<?>> req = build("a/v1/user/address/save.do");
		req.login("86", "13926426236", "abcd1234");

		req.putPost("addressId", "2004");
		req.putPost("contacts", "qiujun");
		req.putPost("status", "1");
		req.putPost("fullAddress", "广州市天河区珠江新城黄埔大道西海乐路12号");
		req.putPost("phone", "15920575057");
		RestResult<?> result = req.doPost();
		assertNotNull(result.getObject());
		
//		req = build("a/v1/user/address/save.do");
//		req.login();
//
//		req.putPost("addressId", "0");
//		req.putPost("districtCode", "440106");
//		req.putPost("addLat", "23.125699");
//		req.putPost("addLng", "113.338743");
//		req.putPost("name", "合景睿峰大厦停车场");
//		req.putPost("detail", "L7合景睿峰大厦");
//		req.putPost("contacts", "陈医生2");
//		req.putPost("status", "2");
//		req.putPost("phone", "15920575057");
//		result = req.doPost();
//		assertNotNull(result.getObject());
	}

	@Test
	public void list() {
		ApiReq<RestResult<?>> req = build("a/v1/user/address/list");
		req.login("86", "13926426236", "abcd1234");

		RestResult<?> result = req.doGet();
		List<?> list = assertList(result);
		assertNotNull(list);
	}
	
	@Test
	public void delete(){
		ApiReq<RestResult<?>> req = build("a/v1/user/address/delete.do");
		req.login("86", "13926426236", "abcd1234");
		
		req.putPost("addressId", "2002");
		
		RestResult<?> result = req.doPost();
		assertNotNull(result.getObject());
	}
}
