package com.upgrade.services;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.upgrade.dto.Reservation;

@Service
public interface ReservationService {

	public ResponseEntity<Reservation> addReservation(Reservation reservation);

	public ResponseEntity<List<Reservation>> getAllReservations();

	public ResponseEntity<List<String>> getAvailability(String startDateFromUser, String lastDateFromUser);

	public ResponseEntity<Reservation> getReservationById(int reservationId);

	public ResponseEntity<Boolean> deleteReservation(int reservationId);

	public ResponseEntity<Reservation> updateReservation(int reservationId, Reservation reservation);

}
