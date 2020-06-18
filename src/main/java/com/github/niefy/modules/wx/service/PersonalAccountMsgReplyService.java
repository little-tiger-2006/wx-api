package com.github.niefy.modules.wx.service;

/**
 * 微信公众号个人订阅号消息处理
 * @author little-tiger
 * 
 */
public interface PersonalAccountMsgReplyService {

	/**
	 * 寻找匹配的语句，返回消息
	 * @param exactMatch 是否精确查询
	 * @param textContent 匹配语句
	 * @return 匹配结果，不存在返回null或空字符，存在返回字符串
	 */
	String tryAutoReply(boolean exactMatch,String textContent);
	
	boolean getExactMacthConfig();
}
