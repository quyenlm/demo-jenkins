package phn.nts.ams.fe.domain;

public class BoTestResult {
	
	private int correctAnswer;
	private int totalAnswer;
	
	public BoTestResult(int correctAnswer, int totalAnswer) {
		super();
		this.correctAnswer = correctAnswer;
		this.totalAnswer = totalAnswer;
	}
	public int getCorrectAnswer() {
		return correctAnswer;
	}
	public void setCorrectAnswer(int correctAnswer) {
		this.correctAnswer = correctAnswer;
	}
	public int getTotalAnswer() {
		return totalAnswer;
	}
	public void setTotalAnswer(int totalAnswer) {
		this.totalAnswer = totalAnswer;
	}
	
	

}
