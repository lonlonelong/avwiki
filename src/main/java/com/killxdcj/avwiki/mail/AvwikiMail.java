package com.killxdcj.avwiki.mail;

public class AvwikiMail {
	private String host;
	private String username;
	private String password;
	private String to;
	private String from;
	private String subject;
	private String text;

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public static AvwikiMail makeNotifyMail(String subject, String text) {
		AvwikiMail mail = new AvwikiMail();
		mail.setHost("smtp.killxdcj.com");
		mail.setUsername("monitor@killxdcj.com");
		mail.setPassword("xxxx");
		mail.setTo("avwiki@killxdcj.com");
		mail.setFrom("monitor@killxdcj.com");
		mail.setSubject("[AVWIKI]¡¾Í¨Öª¡¿" + subject);
		mail.setText(text);
		return mail;
	}
}
