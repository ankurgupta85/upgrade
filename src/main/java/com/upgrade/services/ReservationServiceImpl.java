package com.upgrade.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.upgrade.dto.Reservation;
import com.upgrade.repository.ReservationRepository;
import com.upgrade.utils.DateUtils;

@Service
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ReservationServiceImpl implements ReservationService {

	@Autowired
	private ReservationRepository reservationRepo;

	@Value("${date.format}")
	private String dateFormat;

	@Autowired
	private DateUtils dateUtils;

	@Value("${days.threshold}")
	private int daysThreshold;

	Logger LOGGER = LoggerFactory.getLogger(ReservationServiceImpl.class);

	private ResponseEntity<Reservation> validateReservation(Reservation reservation) {
		// Check for required parameters
		LOGGER.debug("validationReservation: " + reservation);
		if (reservation == null || StringUtils.isAnyBlank(reservation.getReservationStartDate(),
				reservation.getReservationEndDate(), reservation.getEmail(), reservation.getName())) {
			return new ResponseEntity("Missing mandatory parameters", HttpStatus.BAD_REQUEST);
		}

		// Check for date formats
		String startDateStr = dateUtils.getDateInFormat(reservation.getReservationStartDate());
		String endDateStr = dateUtils.getDateInFormat(reservation.getReservationEndDate());
		if (StringUtils.isAnyBlank(startDateStr, endDateStr)) {
			return new ResponseEntity("Invalid date inputs. Please follow format yyyy-MM-dd", HttpStatus.BAD_REQUEST);
		}

		// Check for days counts between start and end date
		Long days = dateUtils.getDaysCountBetweenDates(startDateStr, endDateStr);
		if (days == null || !(days >= 1 && days <= daysThreshold)) {
			return new ResponseEntity("Can't book for more than 3 days", HttpStatus.BAD_REQUEST);
		}

		// Check for the reservation time. Must be between 1 day to 1 month prior
		Boolean isValidTimetoReserve = dateUtils.isAllowedToBook(startDateStr);
		if (isValidTimetoReserve == null) {
			return new ResponseEntity("Some error encountered", HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (!isValidTimetoReserve) {
			return new ResponseEntity("We only allow the reservation from 1 day before to 1 month before",
					HttpStatus.BAD_REQUEST);
		}

		// Convert the dates into int format to query db
		Integer startDateInt = dateUtils.getIntegerFormatForDate(startDateStr);
		Integer endDateInt = dateUtils.getIntegerFormatForDate(endDateStr);
		if (endDateInt < startDateInt) {
			return new ResponseEntity("Resevation end date is before the start date", HttpStatus.BAD_REQUEST);
		}

		// Create the reservation id
		String reservationId = startDateInt + "-" + endDateInt;

		// Set the attributes
		reservation.setDaysReserved(days);
		reservation.setReservationIndex(reservationId);
		reservation.setReservationEndDate(endDateStr);
		reservation.setReservationStartDate(startDateStr);
		reservation.setReservationEndTimestamp(endDateInt);
		reservation.setReservationStartTimestamp(startDateInt);

		Reservation existingReservationForSameIndex = reservationRepo.findReservationByIndex(reservationId);
		if (existingReservationForSameIndex != null) {
			return new ResponseEntity("Reservation already exists for the same dates", HttpStatus.BAD_REQUEST);
		}
		// Check for existing reservation based on start and end date
		// List<Integer> rangeForDates = dateUtils.getDateRanges(startDateStr,
		// endDateStr)
		int existingReservationBetweenDates = reservationRepo.findReservationCountForDates(startDateInt, endDateInt);
		if (existingReservationBetweenDates != 0) {
			return new ResponseEntity("The dates you entered are already reserved", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Reservation>(reservation, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Reservation> addReservation(Reservation reservation) {
		try {
			ResponseEntity validation = validateReservation(reservation);
			if (validation.getStatusCode() == HttpStatus.OK) {
				reservation.setCreateDate(new java.sql.Date(System.currentTimeMillis()));
				reservation = reservationRepo.save(reservation);
				return new ResponseEntity<Reservation>(reservation, HttpStatus.OK);
			} else {
				return validation;
			}
		} catch (ConstraintViolationException constraintException) {
			LOGGER.error("addReservation: ConstraintViolationException", constraintException);
			return new ResponseEntity("Looks like someone reserved the same dates", HttpStatus.CONFLICT);
		} catch (Exception e) {
			LOGGER.error("addReservation: Exception", e);
			return new ResponseEntity("Some internal error encountered", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<List<Reservation>> getAllReservations() {
		return new ResponseEntity((List<Reservation>) reservationRepo.findAll(), HttpStatus.OK);

	}

	@Override
	public ResponseEntity<List<String>> getAvailability(String startDateFromUser, String lastDateFromUser) {
		Date currentDate = new Date();
		Date startDate = org.apache.commons.lang3.time.DateUtils.addDays(currentDate, 1);

		if (StringUtils.isNotBlank(startDateFromUser)) {
			startDate = dateUtils.getDateForString(startDateFromUser);
		}

		Date futureDate = org.apache.commons.lang3.time.DateUtils.addMonths(startDate, 1);
		if (StringUtils.isNotBlank(lastDateFromUser)) {
			futureDate = dateUtils.getDateForString(lastDateFromUser);
		}

		Set<String> datesInStringList = new TreeSet<String>();

		Date startDateTemp = org.apache.commons.lang3.time.DateUtils.addDays(startDate, 1);
		while (startDateTemp.before(futureDate)) {
			datesInStringList.add(dateUtils.getDateInStringFormat(startDateTemp));
			startDateTemp = org.apache.commons.lang3.time.DateUtils.addDays(startDateTemp, 1);
		}
		datesInStringList.add(dateUtils.getDateInStringFormat(futureDate));
		Integer startDateInt = dateUtils.getDateInIntFormat(startDate);
		Integer futureDateInt = dateUtils.getDateInIntFormat(futureDate);
		List<Reservation> existingReservations = reservationRepo.findReservationsForDates(startDateInt, futureDateInt);
		Set<String> existingReservationsDates = new TreeSet<String>();
		for (Reservation reservation : existingReservations) {
			Date startDateForReservation = dateUtils.getDateForString(reservation.getReservationStartDate());
			Date endDateForReservation = dateUtils.getDateForString(reservation.getReservationEndDate());
			while (startDateForReservation.before(endDateForReservation)) {
				existingReservationsDates.add(dateUtils.getDateInStringFormat(startDateForReservation));
				startDateForReservation = org.apache.commons.lang3.time.DateUtils.addDays(startDateForReservation, 1);
			}
			existingReservationsDates.add(dateUtils.getDateInStringFormat(endDateForReservation));
		}
		List<String> remainingDates = (List<String>) CollectionUtils.subtract(datesInStringList,
				existingReservationsDates);

		return new ResponseEntity<List<String>>(remainingDates, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Reservation> getReservationById(int reservationId) {
		try {
			Optional<Reservation> reservation = reservationRepo.findById(reservationId);
			if (!reservation.isPresent()) {
				return new ResponseEntity("Reservation does not exists", HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<Reservation>(reservation.get(), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity("Some internal error encountered", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Boolean> deleteReservation(int reservationId) {
		try {
			Optional<Reservation> reservation = reservationRepo.findById(reservationId);
			if (!reservation.isPresent()) {
				return new ResponseEntity("Reservation does not exists", HttpStatus.BAD_REQUEST);
			}
			reservationRepo.deleteById(reservationId);
			return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity("Some internal error encountered", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<Reservation> updateReservation(int reservationId, Reservation reservation) {
		try {
			Optional<Reservation> existingReservation = reservationRepo.findById(reservationId);
			if (!existingReservation.isPresent()) {
				return new ResponseEntity("Reservation does not exists", HttpStatus.BAD_REQUEST);
			}
			ResponseEntity validation = validateReservation(reservation);
			if (validation.getStatusCode() == HttpStatus.OK) {
				Reservation existingReservationObj = existingReservation.get();
				existingReservationObj.setDaysReserved(reservation.getDaysReserved());
				existingReservationObj.setEmail(reservation.getEmail());
				existingReservationObj.setId(reservationId);
				existingReservationObj.setName(reservation.getName());
				existingReservationObj.setReservationEndDate(reservation.getReservationEndDate());
				existingReservationObj.setReservationEndTimestamp(reservation.getReservationEndTimestamp());
				existingReservationObj.setReservationIndex(reservation.getReservationIndex());
				existingReservationObj.setReservationStartDate(reservation.getReservationStartDate());
				existingReservationObj.setReservationStartTimestamp(reservation.getReservationStartTimestamp());
				existingReservationObj.setUpdateDate(new java.sql.Date(System.currentTimeMillis()));
				existingReservationObj = reservationRepo.save(existingReservationObj);
				return new ResponseEntity<Reservation>(existingReservationObj, HttpStatus.OK);
			} else {
				return validation;
			}
		} catch (ConstraintViolationException constraintException) {
			return new ResponseEntity("Looks like someone reserved the same dates", HttpStatus.CONFLICT);
		} catch (Exception e) {
			return new ResponseEntity("Some internal error encountered", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
