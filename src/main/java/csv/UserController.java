package csv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.batch.runtime.JobExecution;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import csv.User;
import csv.UserRepository;
  

@Controller  
@RequestMapping(path="/") //default application path
public class UserController {
	@Autowired 
	private UserRepository userRepository;
	 
	//index page to upload csv
	@GetMapping({"/", "/index"})  
	public String index() { 
		return "index";
	} 
	
	//Test sql connection by adding a record
    @GetMapping(path="/add")
	public @ResponseBody String addUser (@RequestParam String name, @RequestParam Double salary) { 
		User n = new User();
		n.setName(name);
		n.setSalary(salary);
		userRepository.save(n);
		return "Saved";
	}
	
	@GetMapping(path="/users")
	public @ResponseBody Iterable<User> getAllUsers() {  
		// returns a JSON or XML with the users
		return userRepository.findAll();
		//return userRepository.findSalaryRange(0.0, 4000.0);
	}

}
