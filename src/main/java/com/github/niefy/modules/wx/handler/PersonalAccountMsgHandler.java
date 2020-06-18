package com.github.niefy.modules.wx.handler;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.github.niefy.modules.wx.entity.WxMsg;
import com.github.niefy.modules.wx.service.PersonalAccountMsgReplyService;
import com.github.niefy.modules.wx.service.WxMsgService;

import jodd.util.StringUtil;
import me.chanjar.weixin.common.api.WxConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * 给个人订阅号使用的handler
 * @author little-tiger
 */
@Component
public class PersonalAccountMsgHandler extends AbstractHandler {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	// @Resource(name = "personalSubscriptionAccountMsgReplyServiceImpl")
	@Autowired
	PersonalAccountMsgReplyService msgReplyService;

	@Autowired
	WxMsgService wxMsgService;

	final String defaultKey = "Replay";
	final String defaultValue = "朕知道了！"; //默认回复消息，用以说明没有这个内容
	final String suffix = "。";//WX会把中文句号加到结尾，在此加入标记，以便删除掉。
	@Override
	public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
			WxSessionManager sessionManager) {

		String textContent = wxMessage.getContent();
		if (StringUtils.isEmpty(textContent) && wxMessage.getMsgType().equals(WxConsts.XmlMsgType.VOICE)) {
			textContent = wxMessage.getRecognition();
			if(textContent.endsWith(suffix)) {
				textContent.substring(0, textContent.length()-1);
			}
		}

		String fromUser = wxMessage.getFromUser();
		boolean exactMatch = msgReplyService.getExactMacthConfig();

		// 当用户输入关键词如“你好”，“客服”，匹配数据库，寻找合适的返回值
		String autoReplyMsg = msgReplyService.tryAutoReply(exactMatch, textContent);
		// to post back the default message to user
		if (StringUtil.isEmpty(autoReplyMsg)) {
			autoReplyMsg = defaultValue;
		}

		// make some record
		JSONObject detail = new JSONObject();

		detail.put(defaultKey, autoReplyMsg);

		wxMsgService.addWxMsg(WxMsg.buildOutMsg(WxConsts.KefuMsgType.TEXT, fromUser, detail));

		return WxMpXmlOutMessage.TEXT().content(autoReplyMsg).fromUser(wxMessage.getToUser()).toUser(fromUser).build();

	}

}
