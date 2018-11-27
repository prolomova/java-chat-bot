import java.util.ArrayList;
import java.util.List;

class QuizItem {
	List<String> answers;
	String question;
	
	QuizItem(ArrayList<String> answers, String question){
		this.answers = answers;
		this.question = question;
	}
}
