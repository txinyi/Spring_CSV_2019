package csv.controller;

import java.io.File;
import java.io.IOException;

import javax.batch.operations.JobRestartException;
 
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; 
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import csv.ImportJobConfig;
import csv.domain.User;
import csv.repository.UserRepository;
  
 
@Controller  
@RequestMapping(path="/", method={RequestMethod.POST,RequestMethod.GET})	//default path
public class UserController {

	private Logger Log = LoggerFactory.getLogger(ImportJobConfig.class);
	
	@Autowired 
	private UserRepository userRepository;
	
	@Autowired 
    private JobLauncher jobLauncher;
    
    @Autowired 
    private Job importUserJob;
	 
	//index page
	@GetMapping({"/", "/index"})  
	public String index() { 
		return "index";
	} 
	
	@RequestMapping(value="/uploadToDB", method=RequestMethod.POST)
    public RedirectView create(@RequestParam("file") MultipartFile multipartFile) throws IOException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException, org.springframework.batch.core.repository.JobRestartException{
		Log.info("runnnnning upload");
        //Save multipartFile file in a temporary physical folder
        String path = "target/classes/upload/";
        File fileToImport = new File(path + multipartFile.getOriginalFilename());
        Log.info("path: " + path);
        
        String filename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        Log.info("filename: " + filename);
        try {
            if (multipartFile.isEmpty()) {
                throw new IOException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new IOException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, Paths.get(path).resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new IOException("Failed to store file " + filename, e);
        }
        
        //Launch the Batch Job
        JobExecution jobExecution = jobLauncher.run(importUserJob, new JobParametersBuilder()
                .addString("fullPathFileName", fileToImport.getAbsolutePath())
                .toJobParameters());        

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/users");
        return redirectView;
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
