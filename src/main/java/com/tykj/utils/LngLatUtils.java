package com.tykj.utils;

/**
 * @program: tykj-system
 * @description: 计算两点经纬度之间距离
 * @author: Mr.Zhang
 * @create: 2019-06-18 20:16
 **/
public class LngLatUtils {

	//地球半径
	private final static double EARTH_RADIUS = 6378.137;
	/**
	 * 计算两经纬度点间的距离
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
				Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.rint(s * 1000);
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static void main(String[] args){
		System.out.println(getDistance(113.682017,34.779904,113.68206,34.774541));

	}

}
