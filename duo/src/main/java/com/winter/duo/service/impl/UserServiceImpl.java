package com.winter.duo.service.impl;

import static com.winter.duo.constant.UserConstant.USER_LOGIN_STATE;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.winter.duo.common.ErrorCode;
import com.winter.duo.config.security.RequestContext;
import com.winter.duo.config.security.SecurityConfiguration;
import com.winter.duo.constant.CommonConstant;
import com.winter.duo.config.exception.BusinessException;
import com.winter.duo.mapper.UserMapper;
import com.winter.duo.model.dto.user.UserQueryRequest;
import com.winter.duo.model.entity.LoginUser;
import com.winter.duo.model.entity.User;
import com.winter.duo.model.enums.UserRoleEnum;
import com.winter.duo.model.vo.LoginUserVO;
import com.winter.duo.model.vo.UserVO;
import com.winter.duo.model.vo.WxLoginInfoVo;
import com.winter.duo.service.UserService;
import com.winter.duo.utils.SqlUtils;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现

 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private SecurityConfiguration securityConfiguration;
    private static final TimedCache<String, String> loginCodeCache = CacheUtil.newTimedCache(1000 * 60 * 5);

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "winter";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        return this.getLoginUserVO(user);
    }

    /**
     * 获取当前登录用户
     */
    @Override
    public LoginUser getLoginUser() {
        return RequestContext.getLoginUser();
    }

    /**
     * 是否为管理员
     */
    @Override
    public boolean isAdmin() {
        // 仅管理员可查询
        LoginUser user = RequestContext.getLoginUser();
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getRole());
    }

    @Override
    public boolean isAdmin(LoginUser user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getRole());
    }

    /**
     * 用户注销
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        Map<String, Object> payloads = new HashMap<>();
        payloads.put("userId", user.getId());
        payloads.put("username", user.getUserName());
        payloads.put("role", user.getUserRole());
        payloads.put("createTime", user.getCreateTime());
        String token = JWTUtil.createToken(payloads, securityConfiguration.getJwtKey().getBytes(StandardCharsets.UTF_8));
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setToken(token);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVO(List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public String wxHandshake(String signature, String timestamp, String nonce, String echostr) {
        log.debug("[wechat handshake] signature:{}", signature);
        log.debug("[wechat handshake] timestamp:{}", timestamp);
        log.debug("[wechat handshake] nonce:{}", nonce);
        log.debug("[wechat handshake] echostr:{}", echostr);
        String[] arr = new String[]{"winter1215", timestamp, nonce};
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        // 将三个参数字符串拼接成一个字符串进行sha1加密
        String tmpStr = SecureUtil.sha1(arr[0] + arr[1] + arr[2]);
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        if (tmpStr.equals(signature.toUpperCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "微信签名被篡改，验证不通过");
        }
        return echostr;
    }

    @Override
    public String onMessageSend(String xml) {
        // todo: 目前为明文模式, 不需要解密参数
//        JSONObject body = XmlUtil.xmlToBean(XmlUtil.parseXml(xml).getDocumentElement(), JSONObject.class);
//        String encrypt = body.getStr("Encrypt");
//        String xmlContent = WeChatTool.decrypt(encrypt, config.getWxEncodingAESKey());
        JSONObject body = XmlUtil.xmlToBean(XmlUtil.parseXml(xml).getDocumentElement(), JSONObject.class);
        // 发送消息用户唯一标识
        String openId = body.getStr("FromUserName");
        // 消息类型
        String msgType = body.getStr("MsgType");
        // 消息内容(验证码
        String verifycode = body.getStr("Content");
        // 文本消息
        if (!"text".equals(msgType)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的消息类型");
        }
        // 验证码
        log.info("[wechat onMessageSend] 微信公众号登录用户:{} = {}", verifycode, openId);
        loginCodeCache.put(verifycode, openId);
        return "success";
    }

    @Override
    public WxLoginInfoVo getWxLoginInfo() {
        WxLoginInfoVo loginInfoVo = WxLoginInfoVo.builder()
                .url("static/img/wechatLogin.png")
                .ticket(UUID.fastUUID().toString())
                .verifyCode(RandomUtil.randomNumbers(4))
                .build();
        // 存储票据和验证码
        loginCodeCache.put(loginInfoVo.getTicket(), loginInfoVo.getVerifyCode());
        return loginInfoVo;
    }

    @Override
    public LoginUserVO checkWxLogin(String ticket) {
        String varifycode = loginCodeCache.get(ticket, false);
        if (StringUtils.isBlank(varifycode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码已过期");
        }
        String mpOpenId = loginCodeCache.get(varifycode, false);
        if (StringUtils.isBlank(mpOpenId)) {
            return null;
        }
        loginCodeCache.remove(ticket);
        loginCodeCache.remove(varifycode);
        User user = this.getOne(Wrappers.<User>lambdaQuery().eq(User::getMpOpenId, mpOpenId));
        // 未注册则创建用户
        if (user == null) {
            user = new User();
            user.setMpOpenId(mpOpenId);
            this.save(user);
        }
        return this.getLoginUserVO(user);
    }
}
