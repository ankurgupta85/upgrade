package com.upgrade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrade.dto.Reservation;
import com.upgrade.services.ReservationService;

@RestController
@RequestMapping("/v1/reservations")
public class ReservationController {

	@Autowired
	private ReservationService reservationService;

	@PostMapping("")
	public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
		return reservationService.addReservation(reservation);
	}

	@GetMapping("")
	public ResponseEntity<List<Reservation>> getAllReservations() {
		return reservationService.getAllReservations();
	}

	@GetMapping("/availability")
	public ResponseEntity<List<String>> getAvailability(
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate) {
		return reservationService.getAvailability(startDate, endDate);
	}

	@GetMapping("/{reservationId}")
	public ResponseEntity<Reservation> getReservation(@PathVariable("reservationId") int reservationId) {
		return reservationService.getReservationById(reservationId);
	}

	@PutMapping("/{reservationId}")
	public ResponseEntity<Reservation> updateReservation(@PathVariable("reservationId") int reservationId,
			@RequestBody Reservation reservation) {
		return reservationService.updateReservation(reservationId, reservation);
	}

	@DeleteMapping("/{reservationId}")
	public ResponseEntity<Boolean> deleteReservation(@PathVariable("reservationId") int reservationId) {
		return reservationService.deleteReservation(reservationId);
	}

}
