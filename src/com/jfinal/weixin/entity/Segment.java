package com.jfinal.weixin.entity;

//考虑到转车
public class Segment {
	private String line_name;// 线路名称
	private String stats;// 沿途站点
	private String foot_dist;// 步行到终点站的距离

	public Segment() {
	}

	public Segment(String line_name, String stats, String foot_dist) {
		this.line_name = line_name;
		this.stats = stats;
		this.foot_dist = foot_dist;
	}

	public String getLine_name() {
		return line_name;
	}

	public void setLine_name(String line_name) {
		this.line_name = line_name;
	}

	public String getStats() {
		return stats;
	}

	public void setStats(String stats) {
		this.stats = stats;
	}

	public String getFoot_dist() {
		return foot_dist;
	}

	public void setFoot_dist(String foot_dist) {
		this.foot_dist = foot_dist;
	}
}