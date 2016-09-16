package phn.nts.ams.fe.domain;

import java.io.Serializable;
import java.util.Comparator;

public class BoTestInfo implements Serializable, Comparable<BoTestInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -722941631298559969L;
	private String questionId;
	private String question;
	private String hintLink;
	private String packageTestId;
	private String answerMethod;
	private String answer;
	private String customerAnswer;
	private String subject;
	private String type;
	private Integer pos;
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getHintLink() {
		return hintLink;
	}
	public void setHintLink(String hintLink) {
		this.hintLink = hintLink;
	}
	public String getPackageTestId() {
		return packageTestId;
	}
	public void setPackageTestId(String packageTestId) {
		this.packageTestId = packageTestId;
	}
	public String getAnswerMethod() {
		return answerMethod;
	}
	public void setAnswerMethod(String answerMethod) {
		this.answerMethod = answerMethod;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getCustomerAnswer() {
		return customerAnswer;
	}
	public void setCustomerAnswer(String customerAnswer) {
		this.customerAnswer = customerAnswer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getPos() {
		return pos;
	}
	public void setPos(Integer pos) {
		this.pos = pos;
	}
	@Override
	public int compareTo(BoTestInfo o) {
		// TODO Auto-generated method stub
		return this.pos.compareTo(o.getPos());
	}
	
	
	
}
