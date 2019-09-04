package com.upgrade.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DateUtils {

	@Value("${date.format}")
	private String dateFormat;

	public String getDateInFormat(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date convertedCurrentDate = sdf.parse(date);
			String dateStr = sdf.format(convertedCurrentDate);
			return dateStr;
		} catch (Exception e) {
			return null;
		}
	}

	public Integer getDateInIntFormat(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			String dateStr = sdf.format(date);
			return getIntegerFormatForDate(dateStr);
		} catch (Exception e) {
			return null;
		}

	}
	
	public String getDateInStringFormat(Date date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			String dateStr = sdf.format(date);
			return dateStr;
		} catch (Exception e) {
			return null;
		}

	}
		
	public Long getDaysCountBetweenDates(String startDateStr, String endDateStr) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date startDate = sdf.parse(startDateStr);
			Date endDate = sdf.parse(endDateStr);
			return getDaysBetween(startDate, endDate);
		} catch (Exception e) {
			return null;
		}

	}

	private Long getDaysBetween(Date startDate, Date endDate) {
		long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
		long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		return diff+1;
	}
	

	public Boolean isAllowedToBook(String startDateStr) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date startDate = sdf.parse(startDateStr);
			Date currentDate = new Date();
			Date future1MonthDate = org.apache.commons.lang3.time.DateUtils.addMonths(currentDate, 1);
			// Check if the startDate is between one day or 1 month after from today
			if (startDate.after(currentDate) && startDate.before(future1MonthDate)) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			return null;
		}
	}

	public Integer getIntegerFormatForDate(String startDateStr) {
		return Integer.parseInt(startDateStr.replaceAll("-", ""));
	}

	public List<Integer> getDateRanges(String startDateStr, String endDateStr) {
		List<Integer> dateList = new ArrayList<Integer>();
		if(startDateStr.equals(endDateStr)) {
			dateList.add(getIntegerFormatForDate(startDateStr));
			return dateList;
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date startDate = sdf.parse(startDateStr);
			Date endDate = sdf.parse(startDateStr);
			while(startDate.before(endDate)) {
				startDate = org.apache.commons.lang3.time.DateUtils.addDays(startDate, 1);
				dateList.add(getIntegerFormatForDate(sdf.format(startDate)));
			}
			return dateList;
		} catch (Exception e) {
			return null;
		}
	}

	public Date getDateForString(String dateStr) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date convertedDate = sdf.parse(dateStr);
			return convertedDate;
		} catch (Exception e) {
			return null;
		}
	}

}
