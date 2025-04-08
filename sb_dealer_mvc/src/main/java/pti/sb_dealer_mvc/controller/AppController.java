package pti.sb_dealer_mvc.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import pti.sb_dealer_mvc.dto.BookingDto;
import pti.sb_dealer_mvc.dto.CarDto;
import pti.sb_dealer_mvc.dto.CarListDto;
import pti.sb_dealer_mvc.service.AppService;

@Controller
public class AppController {
	
	private AppService service;

	@Autowired
	public AppController(AppService service) {
		super();
		this.service = service;
	}
	
	@GetMapping("/")
	public String showMainPage () {

		return "index.html";
	}
	
	@GetMapping("/cars")
	public String getAvailableCars (
				Model model,
				@RequestParam("startdate") LocalDate startDate,
				@RequestParam("enddate") LocalDate endDate	
			) {
		
		CarListDto cld = service.getAllCarsByDatePicker(startDate, endDate);
		
		model.addAttribute("cld", cld);

		return "cars.html";
	}
	
	@PostMapping("/cars/startbooking")
	public String getBookingPage (
				Model model,
				@RequestParam("startdate") LocalDate startDate,
				@RequestParam("enddate") LocalDate endDate,
				@RequestParam("carid") int carId
			
			) {
		
		BookingDto bookingDto = service.getCarBookingPage(startDate, endDate, carId);
		
		model.addAttribute("bookingDto", bookingDto);
	
		return "booking.html";
	}
	
	@PostMapping("/cars/endbooking")
	public String confirmBooking (
				Model model,
				@RequestParam("carid") int carId,
				@RequestParam("startdate") LocalDate startDate,
				@RequestParam("enddate") LocalDate endDate,
				@RequestParam("name") String name,
				@RequestParam("email") String email,
				@RequestParam("address") String address,
				@RequestParam("phone") String phone
			
			) {
		
		CarDto carDto = service.confirmAutoRental(carId, startDate, endDate, name, email, address, phone);
		
		model.addAttribute("carDto", carDto);
		
		return "thanks.html";
	}
	
	
	

}
