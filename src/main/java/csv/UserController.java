package csv;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; 

import csv.User;
import csv.UserRepository;
  

@Controller  
@RequestMapping(path="/") //default application path
public class UserController {
	@Autowired 
	private UserRepository userRepository;
	 
	//index page
	@GetMapping({"/", "/index"})  
	public String index() { 
		return "index";
	} 
	
	//Test sql connection by adding a record
	//$ curl 'localhost:8080/add?name=test&salary=1111'
    @GetMapping(path="/add")
	public @ResponseBody String addUser (@RequestParam String name, @RequestParam Double salary) { 
		User n = new User();
		n.setName(name);
		n.setSalary(salary);
		userRepository.save(n);
		return "Saved";
	}
	
    //$ curl 'localhost:8080/demo/all'
	@GetMapping(path="/users")
	public @ResponseBody Iterable<User> getUsers() {  
		// returns a JSON or XML with the users
		//return userRepository.findAll();
		return userRepository.findSalaryRange(0.0, 4000.0);
	}

}
