package com.yogu.services.backend.admin.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yogu.CommonConstants;
import com.yogu.commons.sdk.user.MazingLoginContext;
import com.yogu.commons.utils.HttpClientUtils;
import com.yogu.commons.utils.IpAddressUtils;
import com.yogu.commons.utils.JsonUtils;
import com.yogu.commons.utils.LogUtil;
import com.yogu.core.enums.BooleanConstants;
import com.yogu.core.web.RestResult;
import com.yogu.core.web.ResultCode;
import com.yogu.services.backend.admin.dto.AdminAccount;
import com.yogu.services.backend.admin.resources.form.ApplyLoginForm;
import com.yogu.services.backend.admin.resources.form.LoginForm;
import com.yogu.services.backend.admin.service.AdminAccountService;

/**
 * 用户登录
 * @author ten 2015/11/14.
 */
@Controller
@RequestMapping("/open/yogu")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final String host = CommonConstants.USER_DOMAIN;
	
	@Autowired
	private AdminAccountService adminAccountService;

    /**
     * 登录主页，xhtm 仅用于展示页面
     * @return
     */
    @RequestMapping("login.xhtm")
    public ModelAndView index(@Valid ApplyLoginForm applyLoginForm, BindingResult bindingResult) {
    	logger.info("login.xhtm start");
    	Map<String, Object> model = new HashMap<>();
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            logger.error("open#mazing#login | 应用请求登录参数错误");
            model.put("message", message);
            return new ModelAndView("/open/mazing/error", model);
        }
        logger.info("login.xhtm success");
        long time = System.currentTimeMillis();
        model.put("t", time);
        return new ModelAndView("/open/mazing/login", model);
    }

    /**
     * 执行登录的功能
     * @param form 登录表单数据
     * @param bindingResult 表单校验结果，spring注入
     * @return result.success=true表示成功，result.callback=要跳转的地址
     */
    @RequestMapping("login.do")
    @ResponseBody
    public RestResult<String> login(@Valid LoginForm form,  BindingResult bindingResult, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String ip = IpAddressUtils.getClientIpAddr(request);
		logger.info("open#mazing#login | 用户登录start | countryCode: {}, passport: {}, ip: {}", form.getCountryCode(),
				LogUtil.hidePassport(form.getPassport()), ip);
		String message = "参数错误";
		Map<String, Object> loginResult = doLogin(form, ip);

		if (loginResult.containsKey("success")) {
			boolean success = (Boolean) loginResult.get("success");
			message = (String) loginResult.get("message");
			logger.info("open#mazing#login | 用户登录结果 | success: {}, code: {}, message: {}", success, loginResult.get("code"), message);
			if (success) {
				Map<String, Object> user = (Map<String, Object>) loginResult.get("object");
				AdminAccount adminAccount = adminAccountService.getById((Integer)user.get("uid"));
				if (adminAccount == null) {
		            logger.error("admin#login | 管理员登录错误: 不是管理员 | uid: {}, ip: {}", user.get("uid"), ip);
		            message = "您不是管理员，不能登录系统";
		        }else{
		        	// 写 cookie
					Map<String, Object> map = new HashMap<>(4);
					map.put("token", user.get("ut"));
					map.put("uid", user.get("uid"));
					map.put("nickname", user.get("nickname"));
					map.put("loginTime", System.currentTimeMillis());
					MazingLoginContext.saveMazingLoginUserToCookie(response, map);
					return new RestResult<String>("/admin/welcome.xhtm");
		        }
			}
		}
		return new RestResult<>(ResultCode.PARAMETER_ERROR, message);
	}


    /**
     * 读取帐号系统接口执行登录
     * @param form
     * @return 返回接口登录的结果
     */
    private Map<String, Object> doLogin(LoginForm form, String ip) {

        long t = System.currentTimeMillis();
        Map<String, String> params = new HashMap<>(8);
        params.put("t", t + "");
        params.put("countryCode", form.getCountryCode());
        params.put("passport", form.getPassport());
        params.put("password", form.getPassword());
        // 把IP也传过去 felix
        params.put("ip", ip);

        String json = HttpClientUtils.doPost(host + "/api/security/webLogin.do", params);

        try {
            Map<String, Object> result = JsonUtils.parseObject(json, new TypeReference<Map<String, Object>>() {
            });
            return result;
        } catch (Exception e) {
            logger.error("open#mazing#login | 登录帐号出错", e);
        } finally {
            long time = System.currentTimeMillis() - t;
            logger.info("open#mazing#login | 登录帐号耗时 | time: {}", time);
        }
        return Collections.emptyMap();
    }

}
