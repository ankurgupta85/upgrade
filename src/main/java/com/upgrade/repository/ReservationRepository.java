package com.upgrade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.upgrade.dto.Reservation;

@Repository
public interface ReservationRepository extends CrudRepository<Reservation, Integer> {

	@Query(value = "select * from reservationdb.reservation where reservation_index = ?1", nativeQuery = true)
	Reservation findReservationByIndex(String reservationId);

	@Query(value = "select count(*) from reservationdb.reservation where (reservation_start_timestamp BETWEEN ?1 AND ?2) OR (reservation_end_timestamp BETWEEN ?1 AND ?2)", nativeQuery = true)
	int findReservationCountForDates(Integer startDateInt, Integer endDateInt);

	
	@Query(value = "select *  from reservationdb.reservation where (reservation_start_timestamp BETWEEN ?1 AND ?2) OR (reservation_end_timestamp BETWEEN ?1 AND ?2)", nativeQuery = true)
	List<Reservation> findReservationsForDates(Integer startDateInt, Integer endDateInt);

}
