package pti.sb_dealer_mvc.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pti.sb_dealer_mvc.db.Database;
import pti.sb_dealer_mvc.dto.AdminDto;
import pti.sb_dealer_mvc.dto.BookingDto;
import pti.sb_dealer_mvc.dto.CarDto;
import pti.sb_dealer_mvc.dto.CarListDto;
import pti.sb_dealer_mvc.dto.EditCarDto;
import pti.sb_dealer_mvc.model.Booking;
import pti.sb_dealer_mvc.model.Car;

@Service
public class AppService {
	
	private Database db;

	@Autowired
	public AppService(Database db) {
		super();
		this.db = db;
	}

	public CarListDto getAllCarsByDatePicker(LocalDate startDate, LocalDate endDate) {
		
		CarListDto cld = null;
		
		List<CarDto> carDtoList = new ArrayList<>();
		
		List<Car> carsList = db.getAllCars();
		
		List<Booking> bookings = db.getAllBookingsByDate(startDate, endDate);
		
		if(carsList != null) {
	
			for(int carsIndex = 0; carsIndex < carsList.size(); carsIndex ++) {
				
				Car currentCar = carsList.get(carsIndex);
				
				if(currentCar.isActive()) {
					
					boolean isRented = false;
					
					if(bookings != null) {
					
						for (int bookingIndex = 0; bookingIndex < bookings.size(); bookingIndex ++) {
							
							Booking currentBooking = bookings.get(bookingIndex);
							
							if(currentBooking.getCarId() == currentCar.getId()) {
								
								isRented = true;
							}
						}
						
						if(isRented != true) {
							
							CarDto carDto = new CarDto (
										currentCar.getId(),
										currentCar.getType(),
										currentCar.getPrice()
									);
							
							carDtoList.add(carDto);
						}
					}
				}
			}
			
			cld = new CarListDto (
					carDtoList,
					startDate,
					endDate
					);
		}
		
		
		return cld;
	}

	public BookingDto getCarBookingPage(LocalDate startDate, LocalDate endDate, int carId) {

		BookingDto bookingDto = null;
		
		Car selectedCar = db.getCarById(carId);
		
		if(selectedCar != null) {
			
			long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

			long fullPrice = selectedCar.getPrice() * days; 
			
			bookingDto = new BookingDto (
						selectedCar.getId(),
						startDate,
						endDate,
						fullPrice
					);

		}
		
		return bookingDto;
	}

	public CarDto confirmAutoRental(int carId, LocalDate startDate, LocalDate endDate, String name, String email,
			String address, String phone) {
		
		CarDto carDto = null;
		
		Car selectedCar = db.getCarById(carId);
		
		long fullPrice = 0;
		
		if(selectedCar != null) {
			
			long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

			fullPrice = selectedCar.getPrice() * days; 
			
			Booking bookedCar = new Booking ();
			
			bookedCar.setName(name);		
			bookedCar.setEmail(email);
			bookedCar.setAddress(address);
			bookedCar.setPhoneNumber(phone);
			bookedCar.setStartDate(startDate);
			bookedCar.setEndDate(endDate);
			bookedCar.setCarId(selectedCar.getId());
			
			db.saveNewAutoRent(bookedCar);
		}
		
		carDto = new CarDto(
				selectedCar.getId(),
				selectedCar.getType(),
				fullPrice
				);

		return carDto;
	}

	public AdminDto getAllBookings() {
		
		AdminDto adminDto = null;
		
		List<BookingDto> bookingDtoList = new ArrayList<>();
		
		List<CarDto> carDtoList = new ArrayList<>();
		
		List<Car> carsList = db.getAllCars();
		List<Booking> bookingsList = db.getAllBookings();
		
		if(carsList != null && bookingsList != null) {
			
			for(int carsIndex = 0; carsIndex < carsList.size(); carsIndex ++) {
				
				Car car = carsList.get(carsIndex);
				
				CarDto carDto = new CarDto(
						car.getId(),
						car.getType(),
						car.getPrice()
						);
				
				carDtoList.add(carDto);	
			}
			
			for(int bookingsIndex = 0; bookingsIndex < bookingsList.size(); bookingsIndex ++) {
				
				Booking booking = bookingsList.get(bookingsIndex);
				
				BookingDto bookingDto = new BookingDto(
						booking.getId(),
						booking.getStartDate(),
						booking.getEndDate(),
						0
						);
				
				bookingDtoList.add(bookingDto);
			}
			
			adminDto = new AdminDto(bookingDtoList, carDtoList);

		}

		
		return adminDto;
	}

	public EditCarDto addNewCarByAdmin(String type, int price, boolean isActive) {
		
		EditCarDto editCarDto = null; 
		
		Car newCar = new Car();
		
		newCar.setType(type);
		newCar.setPrice(price);
		newCar.setActive(isActive);
		
		db.saveNewCar(newCar);
		
		editCarDto = new EditCarDto(
				newCar.getId(),
				newCar.getType(),
				newCar.getPrice(),
				newCar.isActive()
				);

		return editCarDto;
	}

}
