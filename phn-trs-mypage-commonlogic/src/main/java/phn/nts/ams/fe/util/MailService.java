package phn.nts.ams.fe.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.mail.javamail.JavaMailSender;

import phn.com.nts.util.log.Logit;


/**
 * @description
 * @version TDSBO1.0
 * @CrBy Nguyen Xuan Bach
 * @CrDate Feb 24, 2012
 * @Copyright Posismo Hanoi Limited. All rights reserved.
 */
public class MailService {
	private static final Logit log = Logit.getInstance(MailService.class);
	private static final String ADMIN_ADDRESS_DEFAULT = "redmine_noreply@posismo.com";
	private JavaMailSender mailSender;
	private FreeMarkerConfig freeMarkerConfig;

	public void sendAppMail(final MailInfo obj, final String toAddresses, final String myEmail, final String subject, String template) throws Exception {
		log.info("SEND MAIL TO " + toAddresses + " FROM " + myEmail);

		// data
		Map<Object, Object> data = new HashMap<Object, Object>();

		data.put("content", obj);

		// from
		String adminSender = AppConfiguration.getMailAdminSender();
		if (adminSender.isEmpty()) {
			adminSender = ADMIN_ADDRESS_DEFAULT;
		}	
		sendMail(data, adminSender, myEmail, toAddresses.split(";"), subject, template);
	}

	/**
	 * Generic send mail method
	 * 
	 * @param data
	 *            : data to merge with template
	 * @param from
	 *            : from email address, typically admin email
	 * @param replyTo
	 *            : email address to be replied to, typically current online
	 *            user's email
	 * @param to
	 *            : email address which mail will be sent to
	 * @param subject
	 *            : email subject
	 * @param templateName
	 *            : template name to be merged with provided data
	 * @throws Exception
	 */
	public void sendMail(final Map<Object, Object> data, final String from, final String replyTo, final String[] to, final String subject, final String templateName) throws Exception {
		AppMailPreparator preparator = new AppMailPreparator(data, from, replyTo, to, subject, templateName, freeMarkerConfig.getCfg());
		this.mailSender.send(preparator);
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * @return the freenMarkerConfig
	 */
	public FreeMarkerConfig getFreeMarkerConfig() {
		return freeMarkerConfig;
	}

	/**
	 * @param freenMarkerConfig the freenMarkerConfig to set
	 */
	public void setFreeMarkerConfig(FreeMarkerConfig freeMarkerConfig) {
		this.freeMarkerConfig = freeMarkerConfig;
	}


	public void sendMailOM(String timeError, String errorOn, String description,String subjectMail, String template) {
		MailInfo mail = new MailInfo();
		mail.setTimeError(timeError);
		mail.setErrorOn(errorOn);
		mail.setDescription(description);
		mail.setEmailAddress(AppConfiguration.getMailOMReceiver());
		try {
			sendAppMail(mail, AppConfiguration.getMailOMReceiver(), AppConfiguration.getMailAdminSender(),subjectMail, template);
		} catch (Exception ex) {
			log.error(ex.getMessage(),ex);
		}
	}
	public void sendMail(MailInfo mailInfo, String toAddress, String subjectMail, String template) {
		try {
			sendAppMail(mailInfo, toAddress, AppConfiguration.getMailAdminSender(), subjectMail, template);
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}
}
