package com.kafka.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.config.dto.UserDTO;
import com.kafka.document.InputFormat;
import com.kafka.service.UserService;
import com.kafka.validation.EmptyInputException;
import com.kafka.validation.IdNotFound;
import com.kafka.validation.IdNotFoundException;
import com.kafka.validation.InputValidation;

@RestController
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	KafkaTemplate<String, InputFormat> kafkaTemplate;

	@Autowired
	protected UserService userService;

	@Autowired
	private InputValidation inputValidation;

	@Autowired
	private IdNotFound idNotFound;

	private static final String TOPIC = "UserTask3";

	@PostMapping("/createUser")
	public ResponseEntity<String> createUser(@RequestBody InputFormat inputFormat) {
		if (inputValidation.validationFunc(inputFormat) == false) {
			throw new EmptyInputException("601", "Input Fields are Empty");
		}
		try {
			kafkaTemplate.send(TOPIC, inputFormat);
			logger.info("Adding new User");
		} catch (Exception exception) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Request Successfull", HttpStatus.ACCEPTED);
	}

	@GetMapping(value = "/getUser")
	public List<UserDTO> getAllUsers() {
		logger.info("Getting All user Details ");
		return userService.getAllUsers();
	}

	@GetMapping("/getUser/{rollNumber}")
	public UserDTO getUser(@PathVariable String rollNumber) {
		if (idNotFound.checkIDFound(rollNumber) == false) {
			throw new IdNotFoundException("601", "Id not Found");
		}
		logger.info("Getting user Details ");
		return userService.getUser(rollNumber);
	}

	@PutMapping("/updateUser/{rollNumber}")
	public ResponseEntity<String> udpateUser(@RequestBody InputFormat inputFormat) {
		if (inputValidation.validationFunc(inputFormat) == false) {
			throw new EmptyInputException("601", "Input Fields are Empty");
		}
		try {
			kafkaTemplate.send(TOPIC, inputFormat);
			logger.info("Updating User Details");
		} catch (Exception exception) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Request Successfull", HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/deleteUser/{rollNumber}")
	public ResponseEntity<String> deleteUser(@PathVariable String rollNumber) {
		if (idNotFound.checkIDFound(rollNumber) == false) {
			throw new IdNotFoundException("601", "Id not Found");
		}
		try {
			logger.info("Deleting User");
			userService.deleteUser(rollNumber);
			return new ResponseEntity<>("Request Successfull", HttpStatus.ACCEPTED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@DeleteMapping("/deleteUser/{rollNumber}")
//	public ResponseEntity<String> deleteUser(@PathVariable String rollNumber) {
//		try {
//			InputFormat inputFormat = new InputFormat();
//			inputFormat.userModel.setRollNumber(rollNumber);
//			inputFormat.setMethod("Delete");
//			kafkaTemplate.send(TOPIC, inputFormat);
//			logger.info("Deleting User");
//			return new ResponseEntity<>("User Deleted Successfully", HttpStatus.ACCEPTED);
//		} catch (Exception e) {
//			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

//	@GetMapping("/getUser/{lowerRange}/{upperRange}")
//	public Optional<UserModel> getUser(@PathVariable String lowerRange, @PathVariable String upperRange) {
//		logger.info("Getting users in between given Range");
//		return userService.getUsersInRange(lowerRange,upperRange);
//	}

}