package csv;
 
import org.springframework.batch.item.ItemProcessor;

//To process large amount of data
public class UserItemProcessor implements ItemProcessor<User, User> {
	 @Override
	 public User process(User user) throws Exception { 
		 //Leave it whether to transform everything to string
	  return user;
	 }

} 