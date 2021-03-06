package com.yogu.services.order.coupon.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.yogu.commons.datasource.annocation.TheDataDao;
import com.yogu.services.order.coupon.entry.CouponPO;

/**
 * mz_coupon 表的操作接口
 * 
 * @author Mazing 2015-12-23
 */
@TheDataDao
public interface CouponDao {

	/**
	 * 保存数据
	 * 
	 * @param po 对象
	 */
	public void save(CouponPO po);

	// /**
	// * 根据主键删除数据
	// */
	// public int deleteById(@Param("couponId") long couponId);

	/**
	 * 修改数据，以主键更新
	 * 
	 * @param po - 要更新的数据
	 * @return 更新的行数
	 */
	public int update(CouponPO po);

	/**
	 * 根据主键读取记录
	 */
	public CouponPO getById(@Param("couponId") long couponId);
	
	public List<CouponPO> listUserCouponsByPage(@Param("uid") long uid,@Param("offset") int offset, @Param("pageSize") int pageSize);
	
	public List<CouponPO> listUserCouponsByStatus(@Param("uid") long uid, @Param("status") short status);
	
	/**
	 * 更新优惠券的使用状态以及使用的订单信息、使用时间
	 * 
	 * @param couponId 优惠券id
	 * @param newStatus 新的状态
	 * @param oldStatus 旧的状态
	 * @param orderNo 被使用的订单号
	 * @param useTime 使用时间
	 * @return
	 * @author sky 2015/12/25
	 */
	public int updateCouponStatusAndOrderNoAndUseTime(@Param("couponId") long couponId, @Param("newStatus") short newStatus,
			@Param("oldStatus") short oldStatus, @Param("orderNo") long orderNo, @Param("useTime") Date useTime);
	

	/**
	 * 将优惠券置为'已失效'<br>
	 * 
	 * 该接口将 状态为 0 和 1 的优惠券 置为状态 等于4 @see {CouponStatus}
	 * 
	 * @param couponRuleId 券规则id
	 * @param toStatus 修改至的优惠券状态
	 * @return
	 * @author sky 2016-01-06
	 */
	public int disableCoupons(@Param("couponRuleId") long couponRuleId, @Param("toStatus") short toStatus);
	
	/**
	 * 根据状态获取所有优惠券的信息
	 * 
	 * @param status 查询状态
	 * @return
	 * @author sky 2015/12/29
	 */
	public List<CouponPO> listCouponsByRuleIdAndStatus(@Param("couponRuleId") long couponRuleId, @Param("status") short status,
			@Param("offset") int offset, @Param("pageSize") int pageSize);
	
}
