package com.upgrade.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Index;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Entity
@Table(name = "reservation", indexes = {
		@Index(name = "reservation_index", unique = true, columnList = "reservation_index") })
public class Reservation {

	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private String email;

	private String name;

	@Column(name = "reservation_index", nullable = false)
	private String reservationIndex;

	private Integer reservationStartTimestamp;

	private Integer reservationEndTimestamp;

	private String reservationStartDate;

	private String reservationEndDate;

	private Long daysReserved;

	private Date createDate;

	private Date updateDate;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getReservationStartTimestamp() {
		return reservationStartTimestamp;
	}

	public void setReservationStartTimestamp(Integer reservationStartTimestamp) {
		this.reservationStartTimestamp = reservationStartTimestamp;
	}

	public Integer getReservationEndTimestamp() {
		return reservationEndTimestamp;
	}

	public void setReservationEndTimestamp(Integer reservationEndTimestamp) {
		this.reservationEndTimestamp = reservationEndTimestamp;
	}

	public String getReservationStartDate() {
		return reservationStartDate;
	}

	public void setReservationStartDate(String reservationStartDate) {
		this.reservationStartDate = reservationStartDate;
	}

	public String getReservationEndDate() {
		return reservationEndDate;
	}

	public void setReservationEndDate(String reservationEndDate) {
		this.reservationEndDate = reservationEndDate;
	}

	public Long getDaysReserved() {
		return daysReserved;
	}

	public void setDaysReserved(Long daysReserved) {
		this.daysReserved = daysReserved;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getReservationIndex() {
		return reservationIndex;
	}

	public void setReservationIndex(String reservationIndex) {
		this.reservationIndex = reservationIndex;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
