package com.yogu.remote.store;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.mail.Store;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yogu.CommonConstants;
import com.yogu.commons.utils.HttpClientUtils;
import com.yogu.commons.utils.JsonUtils;
import com.yogu.core.web.RestResult;
import com.yogu.core.web.ResultCode;
import com.yogu.core.web.exception.ServiceException;
import com.yogu.services.store.StoreCreateOrderVO;
import com.yogu.services.store.StoreSettleOrderVO;

/**
 * 对门店操作的远程服务类 <br>
 * 
 * @author JFan 2015年7月20日 下午12:33:30
 */
@Named
public class StoreRemoteService {

	private static final Logger logger = LoggerFactory.getLogger(StoreRemoteService.class);

	private static final String host = CommonConstants.STORE_DOMAIN;

	// 访问接口 timeout 时间（ms）
	private static final int API_DEFAULT_TIMEOUT = 5000;

	/**
	 * 根据门店id获取门店内容
	 * 
	 * @param storeId 门店id
	 * @return 门店信息
	 */
	public Store getStoreBySid(long storeId) {
		try {
			String json = HttpClientUtils.doGet(host + "/api/store/get?storeId=" + storeId);
			logger.debug("remote#store#getStoreBySid | response | storeId: {}, json: {}", storeId, json);
			RestResult<Store> result = JsonUtils.parseObject(json, new TypeReference<RestResult<Store>>() {
			});
			return result.getObject();
		} catch (Exception e) {
			logger.error("remote#store#getStoreBySid | Error | storeId: {}, message: {}", storeId, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 根据餐厅ID获取餐厅所有的信息，用于 APP 界面或 HTML5 界面展示。
	 * 
	 * @param storeId 餐厅ID
	 * @return 返回餐厅的详细信息
	 * @author ten 2016/4/13
	 */
	public Map<String, Object> getStoreDetails(long storeId) {
		try {
			String json = HttpClientUtils.doGet(host + "/api/store/details?storeId=" + storeId);
			logger.debug("remote#store#getStoreDetails | response | storeId: {}, json: {}", storeId, json);
			RestResult<Map<String, Object>> result = JsonUtils.parseObject(json, new TypeReference<RestResult<Map<String, Object>>>() {
			});
			return result.getObject();
		} catch (Exception e) {
			logger.error("remote#store#getStoreDetails | Error | storeId: {}, message: {}", storeId, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 根据门店id获取门店内容
	 * 
	 * @param storeIds 门店id列表
	 * @return 门店信息
	 */
	public List<Store> getStoreBySids(long... storeIds) {
		try {
			String json = HttpClientUtils.doGet(host + "/api/store/list?storeIds=" + StringUtils.join(storeIds, ','));
			logger.debug("remote#store#getStoreBySids | response | storeIds: {}, json: {}", storeIds, json);
			RestResult<List<Store>> result = JsonUtils.parseObject(json, new TypeReference<RestResult<List<Store>>>() {
			});
			return (List<Store>) (result.getObject() == null ? Collections.emptyList() : result.getObject());
		} catch (Exception e) {
			logger.error("remote#store#getStoreBySids | Error | storeIds: {}, message: {}", storeIds, e.getMessage(), e);
		}
		return Collections.emptyList();
	}

	/**
	 * 根据餐厅id，批量获取餐厅信息。多个餐厅id用英文逗号分隔
	 * 
	 * @param storeIds - 餐厅id
	 * @author hins
	 * @date 2016年11月23日 下午6:28:38
	 * @return 餐厅信息列表，若无/异常，返回empty list
	 */
	public List<Store> listStoreByIds(String storeIds) {
		logger.info("remote#store#getStoreBySids | 批量查询餐厅信息 | storeIds: {}", storeIds);
		if (StringUtils.isBlank(storeIds)) {
			return Collections.emptyList();
		}
		try {
			String json = HttpClientUtils.doGet(host + "/api/store/list?storeIds=" + storeIds);
			RestResult<List<Store>> result = JsonUtils.parseObject(json, new TypeReference<RestResult<List<Store>>>() {
			});
			if (result.isSuccess() && null != result.getObject())
				return result.getObject();
		} catch (Exception e) {
			logger.error("remote#store#getStoreBySids | 批量查询餐厅信息出现异常 | storeIds: {}, message: {}", storeIds, e.getMessage(), e);
		}
		return Collections.emptyList();
	}
	
	/**
	 * 调用store域的预下单接口，获取餐厅信息，美食信息，库存信息，配送费信息
	 * 
	 * @param deliveryTime - 预计送达时间
	 * @param purchaseDetail - 购买信息
	 * @param distance - 收货地址到商家的直径距离
	 * @param lat - 收货地址经度
	 * @param lng - 收货地址纬度
	 * @author hins
	 * @date 2016年10月1日 下午2:35:36
	 * @return void
	 */
	@Deprecated
	public StoreSettleOrderVO settleOrder(long uid, String purchaseDetail) {
		
		try {

			Map<String, String> params = new HashMap<>(8);
			params.put("uid", String.valueOf(uid));
			params.put("purchaseDetail", purchaseDetail);

			String json = HttpClientUtils.doPost(host + "/api/v1/store/order/settle.do", params);
			logger.info("remote#store#settleOrder | response |  uid: {}, purchaseDetail: {}", uid, purchaseDetail);
			RestResult<StoreSettleOrderVO> result = JsonUtils.parseObject(json, new TypeReference<RestResult<StoreSettleOrderVO>>() {});
			if (result.getCode() != ResultCode.SUCCESS){
				logger.error("remote#store#settleOrder | 不允许正常结算 |  uid: {}, purchaseDetail: {}", uid, purchaseDetail);
				throw new ServiceException(result.getCode(), result.getMessage());
			}
			return result.getObject();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 调用store域的下单接口，获取餐厅、美食、配送费（包括顺丰送）、锁住库存<br>
	 * 只有service>0，才扣除库存。如果出现异常直接抛出
	 * 
	 * @param deliveryTime - 预计送达时间
	 * @param purchaseDetail - 购买信息
	 * @param distance - 收货地址到商家的直径距离
	 * @param lat - 收货地址经度
	 * @param lng - 收货地址纬度
	 * @author hins
	 * @date 2016年10月2日 下午12:47:38
	 * @return StoreCreateOrderVO
	 */
	@Deprecated
	public StoreCreateOrderVO createOrder(long uid, String purchaseDetail) {
		
		try {

			Map<String, String> params = new HashMap<>(8);
			params.put("uid", String.valueOf(uid));
			params.put("purchaseDetail", purchaseDetail);


			String json = HttpClientUtils.doPost(host + "/api/v1/store/order/create.do", params);
			logger.debug("remote#store#createOrder | response |  uid: {}, purchaseDetail: {}}", uid, purchaseDetail);
			RestResult<StoreCreateOrderVO> result = JsonUtils.parseObject(json, new TypeReference<RestResult<StoreCreateOrderVO>>() {
			});
			if (result.getCode() != ResultCode.SUCCESS){
				logger.error("remote#store#createOrder | 不允许正常下单 |  uid: {}, purchaseDetail: {}", uid, purchaseDetail);
				throw new ServiceException(result.getCode(), result.getMessage());
			}
			return result.getObject();
		} catch (Exception e) {
			throw e;
		}
	}
	
}
