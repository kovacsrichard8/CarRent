package pti.sb_dealer_mvc.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import pti.sb_dealer_mvc.db.Database;
import pti.sb_dealer_mvc.dto.AdminDto;
import pti.sb_dealer_mvc.dto.BookingDto;
import pti.sb_dealer_mvc.dto.CarDto;
import pti.sb_dealer_mvc.dto.CarListDto;
import pti.sb_dealer_mvc.dto.EditCarDto;
import pti.sb_dealer_mvc.model.Car;
import pti.sb_dealer_mvc.service.AppService;

@Controller
public class AppController {
	
	private AppService service;
	private Database db;

	@Autowired
	public AppController(AppService service, Database db) {
		super();
		this.service = service;
		this.db = db;
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
	
	@GetMapping("/admin")
	public String showBookings (Model model) {
		
		AdminDto adminDto = service.getAllBookings();
		
		model.addAttribute("adminDto", adminDto);

		return "admin.html";
	}
	
	@PostMapping("/admin/newcar")
	public String addNewCar(
				Model model,
				@RequestParam("type") String type,
				@RequestParam("price") int price,
				@RequestParam("active") boolean isActive
			
			) {
		
		EditCarDto editCarDto = service.addNewCarByAdmin(type, price, isActive);
		
		model.addAttribute("editCarDto", editCarDto);

		return "editcar.html";
	}
	
	@GetMapping("/admin/editcar")
	public String editCar (
				Model model,
				@RequestParam("carid") int carId

			) {
		
		EditCarDto editCarDto = service.editCarById(carId);
		
		model.addAttribute("editCarDto", editCarDto);
	
		return "editcar.html";
	}
	
	@PostMapping("/admin/editcar/finish")
	public String changeCarDetails(
				Model model,
				@RequestParam("carid") int carId,
				@RequestParam("type") String type,
				@RequestParam("price") int price,
				@RequestParam("active") boolean isActive
			
			) {
		
		AdminDto adminDto = service.changeCarDetailsByParameters(carId, type, price, isActive);
		
		model.addAttribute("adminDto", adminDto);
	
		return "admin.html";
	}
	
	@PostMapping("/admin/newcar/upload")
	public String uploadCarImage(
				Model model,
				@RequestParam("file") MultipartFile file,
				@RequestParam("carid") int carId
			) throws IOException {
		
		byte [] bFile = file.getBytes();
		Car carImageUpload = db.getCarById(carId);
		carImageUpload.setImage(bFile);
		
		db.saveCarImage(carImageUpload);
		
		Car carImageToDownload = new Car();
		carImageToDownload = db.getCarImageById(carImageUpload.getId());
		
		model.addAttribute("carImage", carImageToDownload);
		
		return "editcar.html";
	}
	
	
	

}
