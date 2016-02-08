package com.killxdcj.avwiki.spider;

import java.util.Date;

public class SpiderCount {
	private String sign;
	private Date startDate;
	private int visitOKCount;
	private int visitErrCount;
	private int visitedCount;
	private int unVisitCount;
	
	@Override
	public String toString() {
		return "SpiderCount [sign=" + sign + ", visitOKCount=" + visitOKCount
				+ ", visitErrCount=" + visitErrCount + ", visitedCount=" + visitedCount + ", unVisitCount="
				+ unVisitCount + ", startDate=" + startDate + "]";
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
