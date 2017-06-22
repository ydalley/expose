package com.expose.service.implementation;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.expose.ApplicationException;
import com.expose.data.repo.RegistrationRepository;
import com.expose.data.repo.UserRepository;
import com.expose.model.RegistrationInfo;
import com.expose.model.User;
import com.expose.service.RegistrationService;


@Service
public class RegistrationServiceImpl implements RegistrationService {

	@Autowired
	RegistrationRepository repo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	JavaMailSender emailSender;

	@Autowired
	MessageSource messageSource;
	@Autowired
	UserServiceImpl userService;

	@Override
	public String register(RegistrationInfo regInfo) throws ApplicationException {
		// check that user doesn't exist
		if (checkUserExist(regInfo.getEmail()))
			throw new ApplicationException("User with email exists");

		// clean up old registration requests
		List<RegistrationInfo> list = repo.findByEmail(regInfo.getEmail());
		for (RegistrationInfo info : list) {
			info.setValid(false);
		}
		repo.save(list);

		regInfo.setValid(true);
		regInfo.setPassword(userService.getPassword(regInfo.getPassword()));
		sendRegistrationEmail(regInfo);
		return "Check your mailbox to complete registration";
	}

	@Override
	public String register(String regKey) throws ApplicationException {
		RegistrationInfo registrationInfo = repo.findFirstByRegKeyAndValid(regKey, true);
		if(registrationInfo == null)
			throw new ApplicationException("Invalid Registration key, please register again");
		Date now = new Date();
		if (now.before(registrationInfo.getExpiryDate())) {
			if(checkUserExist(registrationInfo.getEmail()))
				throw new ApplicationException("User with email exists");
			
			User user = new User();
			user.setLoginId(registrationInfo.getEmail());
			user.setEmail(registrationInfo.getEmail());
			user.setFirstName(registrationInfo.getFirstName());
			user.setLastName(registrationInfo.getLastName());
			user.setPhone(registrationInfo.getPhone());
			user.setStatus("E");
			user.setPassword(registrationInfo.getPassword());
			userRepo.save(user);
			return "User created, Login with your email id and password";
		}
		throw new ApplicationException("Your registration has expired, try again");
	}
	
	
	@Value("${server.app.path}")
	private String appPath;
	
	@Async
	private void sendRegistrationEmail(RegistrationInfo regInfo) throws ApplicationException {
		regInfo.setRegKey(RandomStringUtils.randomAlphanumeric(16));
		regInfo.setExpiryDate(DateUtils.addHours(new Date(), 24));
		repo.save(regInfo);

		MimeMessage message = emailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(regInfo.getEmail());

			helper.setText(messageSource.getMessage("email.message", new Object[] { appPath,regInfo.getRegKey() }, Locale.ENGLISH), true);
			helper.setSubject(messageSource.getMessage("email.subject", null, Locale.ENGLISH));
			emailSender.send(message);
		} catch (NoSuchMessageException | MessagingException e1) {
			new ApplicationException("Error sending email, try again later");
		}
		
	}

	private boolean checkUserExist(String email) {
		User user = userRepo.findFirstByLoginId(email);
		return user == null ? false : true;
	}


}
