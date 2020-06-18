package com.github.niefy.modules.wx.service.impl;

import com.github.niefy.modules.sys.entity.SysConfigEntity;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.github.niefy.modules.wx.entity.MsgReplyRule;
import com.github.niefy.modules.wx.service.MsgReplyRuleService;
import com.github.niefy.modules.wx.service.PersonalAccountMsgReplyService;
import com.github.niefy.modules.wx.service.WxMsgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 微信公众号个人订阅号消息处理
 * @author little-tiger
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalAccountMsgReplyServiceImpl implements PersonalAccountMsgReplyService {
	@Autowired
	MsgReplyRuleService msgReplyRuleService;
	@Autowired
	WxMpService wxService;
	@Value("${wx.mp.autoReplyInterval:1000}")
	Long autoReplyInterval;
	@Autowired
	WxMsgService wxMsgService;
	@Autowired
	SysConfigService sysConfigService;

	/**
	 * 根据规则配置通过微信被动接口回复消息
	 * 
	 * @param exactMatch 是否精确匹配
	 * @param keywords   匹配关键词
	 * @return 如有匹配到关键词，就返回匹配值，否则返回null或空字符串
	 */
	@Override
	public String tryAutoReply(boolean exactMatch, String keywords) {
		try {
			// boolean exactMatch = true;
			List<MsgReplyRule> rules = msgReplyRuleService.getMatchedRules(exactMatch, keywords);
			if (rules.isEmpty()) {
				return null;
			} else {
				StringBuilder sb = new StringBuilder();
				for (MsgReplyRule rule : rules) {
					sb.append(rule.getReplyContent());
					sb.append(",");
				}
				if (sb.length() > 0) {
					return sb.substring(0, sb.length() - 1);
				}
			}
		} catch (Exception e) {
			log.error("被动回复出错：", e);
		}
		return null;
	}

	@Override
	public boolean getExactMacthConfig() {
		SysConfigEntity entity = sysConfigService.getSysConfig("msg_reply_exact_macth_type");
		if (entity != null && !StringUtils.isEmpty(entity.getParamValue())) {
			return true;
		}
		return false;
	}

}
