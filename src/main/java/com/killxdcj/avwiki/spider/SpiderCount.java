package com.killxdcj.avwiki.spider;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SpiderCount {
	private String sign;
	private Date startDate;
	private Date endDate;
	private int visitOKCount;
	private int visitErrCount;
	private int visitedCount;
	private int unVisitCount;
	
	@Override
	public String toString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
//		return "SpiderCount [sign=" + sign + ", visitOKCount=" + visitOKCount
//				+ ", visitErrCount=" + visitErrCount + ", visitedCount=" + visitedCount + ", unVisitCount="
//				+ unVisitCount + ", startDate=" + format.format(startDate) + ", endDate=" + format.format(endDate) +  "]";
		return "标志：" + sign + "\r\n成功爬取：" + visitOKCount
				+ "\r\n爬取失败：" + visitErrCount + "\r\n总爬取量：" + visitedCount + "\r\n待爬取量："
				+ unVisitCount + "\r\n开始时间：" + format.format(startDate) + ((null == endDate) ? "" : "\r\n终止时间：" + format.format(endDate));
	}
	public SpiderCount(String strSign) {
		sign = strSign;
		startDate = new Date();
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public void increaseVisitOK() {
		visitOKCount++;
	}
	public void increaseVisitErr() {
		visitErrCount++;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getVisitOKCount() {
		return visitOKCount;
	}
	public void setVisitOKCount(int visitOKCount) {
		this.visitOKCount = visitOKCount;
	}
	public int getVisitErrCount() {
		return visitErrCount;
	}
	public void setVisitErrCount(int visitErrCount) {
		this.visitErrCount = visitErrCount;
	}
	public int getVisitedCount() {
		return visitedCount;
	}
	public void setVisitedCount(int visitedCount) {
		this.visitedCount = visitedCount;
	}
	public int getUnVisitCount() {
		return unVisitCount;
	}
	public void setUnVisitCount(int unVisitCount) {
		this.unVisitCount = unVisitCount;
	}
}
