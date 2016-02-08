package com.killxdcj.avwiki.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.killxdcj.avwiki.context.AvwikiContextUtil;

@Component
@Repository("skyeyeMailUtil")
public class AvwikiMailUtil {

	private static final Logger logger = Logger.getLogger(AvwikiMailUtil.class);
	private static List<AvwikiMail> mailList = Collections.synchronizedList(new ArrayList<AvwikiMail>());
	
	public static boolean sendNotifyMail(String subject, String content) {
		
		try {
			logger.info("SkyeyeMail.sendNotifyMail START");
			JavaMailSenderImpl senderImpl = AvwikiContextUtil.getBean("mailSender");
			
			MimeMessage mailMessage = senderImpl.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, false, "utf-8");
			messageHelper.setTo("skyeye@killxdcj.com");
			messageHelper.setFrom("monitor@killxdcj.com");
			messageHelper.setSubject("[SKYEYE]¡¾Í¨Öª¡¿" + subject);
			messageHelper.setText(content);
			senderImpl.send(mailMessage);
			logger.info("SkyeyeMail.sendNotifyMail END");
		} catch (Exception e) {
			logger.error("SkyeyeMail.sendNotifyMail error:" + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public static boolean sendMail(String host, String user, String pwd,
			String mailTo, String mailFrom, String subject, String content) {
		try {
			logger.info("SkyeyeMail.sendMail START");
			JavaMailSenderImpl senderImpl = AvwikiContextUtil.getBean("mailSender");
			senderImpl.setHost(host);
			senderImpl.setUsername(user);
			senderImpl.setPassword(pwd);
			
			MimeMessage mailMessage = senderImpl.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, false, "utf-8");
			messageHelper.setTo(mailTo);
			messageHelper.setFrom(mailFrom);
			messageHelper.setSubject(subject);
			messageHelper.setText(content);
			senderImpl.send(mailMessage);
			logger.info("SkyeyeMail.sendMail END");
		} catch (Exception e) {
			logger.error("SkyeyeMail.sendMail error:" + e.getMessage());
			return false;
		}
		return true;
	}
	
	public static boolean sendMail(AvwikiMail mail) {
		return sendMail(mail.getHost(), mail.getUsername(), mail.getPassword(),
				mail.getTo(), mail.getFrom(), mail.getSubject(), mail.getText());
	}
	
	public static boolean sendNotifyMailAsync(String subject, String content) {
		synchronized (mailList) {
			mailList.add(AvwikiMail.makeNotifyMail(subject, content));
		}
		return true;
	}
	
	public static boolean doSendAsyncMail() {
		List<AvwikiMail> endMails = new ArrayList<AvwikiMail>();
		try {
			for (AvwikiMail mail : mailList) {
				if (sendMail(mail)) {
					endMails.add(mail);
				}
			}
			synchronized (mailList) {
				mailList.removeAll(endMails);
			}
		} catch (Exception e) {
			logger.error("SkyeyeMail.doSendAsyncMail error:" + e.getMessage());
			return false;
		}
		return true;
	}
}
