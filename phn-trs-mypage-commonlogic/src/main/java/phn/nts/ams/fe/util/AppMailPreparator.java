package phn.nts.ams.fe.util;


import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.javamail.MimeMessagePreparator;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Generic mail preparator
 * 
 * @author user07
 * 
 */
public class AppMailPreparator implements MimeMessagePreparator {

	Map<Object, Object> data;
	String from;
	String replyTo;
	String[] to;
	String subject;
	String templateName;
	Configuration cfg;

	public AppMailPreparator(final Map<Object, Object> data, final String from, final String replyTo, final String[] to, final String subject, final String templateName, Configuration cfg) {
		this.data = data;
		this.from = from;
		this.replyTo = replyTo;
		this.to = to;
		this.subject = subject;
		this.templateName = templateName;
		this.cfg = cfg;
	}

	@Override
	public void prepare(MimeMessage mimeMessage) throws Exception {
		Writer out = null;
		try {

			mimeMessage.setFrom(new InternetAddress(from));

			InternetAddress[] recipients = new InternetAddress[to.length];
			for (int i = 0; i < to.length; i++) {
				String receipient = to[i];
				recipients[i] = new InternetAddress(receipient);
			}
			mimeMessage.setRecipients(Message.RecipientType.TO, recipients);

			InternetAddress[] repTo = { new InternetAddress(replyTo) };
			mimeMessage.setReplyTo(repTo);
			mimeMessage.setSubject(subject);
			Map<Object, Object> root = new HashMap<Object, Object>(data);

			Template temp = cfg.getTemplate(templateName);

			/* Merge data-model with template */
			out = new StringWriter();
			temp.process(root, out);
			Multipart multipart = new MimeMultipart();
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(out.toString(), "text/html; charset=UTF-8");

			// Add html part to multi part
			multipart.addBodyPart(messageBodyPart);

			mimeMessage.setContent(multipart);

			out.flush();
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
}