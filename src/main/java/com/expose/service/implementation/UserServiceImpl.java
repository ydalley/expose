package com.expose.service.implementation;

import java.security.MessageDigest;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.expose.ApplicationException;
import com.expose.data.repo.UserRepository;
import com.expose.model.User;
import com.expose.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	UserRepository repo;
	
	@Override
	public User getUser(long id) {
		return repo.findOne(id);
	}

	@Override
	public Page<User> getAllUsers(Pageable pageDetails) {
		return repo.findAll(pageDetails);
	}

	@Override
	public Page<User> findUser(String pattern, Pageable pageDetails) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUserForLoginByName(String userId) {
		return repo.findFirstByLoginId(userId);
	}

	@Override
	public String modify(User user) throws ApplicationException {
		try {
			repo.save(user);
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
		return "User saved successfully";
	}

	@Override
	public String add(User user) throws ApplicationException {
		try {
			repo.save(user);
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
		return "User created successfully";
	}

	@Override
	public String enable(User user) throws ApplicationException {
		try {
			user.setStatus("E");
			repo.save(user);
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
		return "User Enabled successfully";
	}

	@Override
	public String toggleStatus(User user) throws ApplicationException {
		if("D".equals(user.getStatus())){
			return enable(user);
		}else if("E".equals(user.getStatus())){
			return disable(user);
		}
		throw new ApplicationException("Unknown user status");
	}

	@Override
	public String disable(User user) throws ApplicationException {
		try {
			user.setStatus("D");
			repo.save(user);
		} catch (Exception e) {
			throw new ApplicationException(e.getMessage());
		}
		return "User Disabled successfully";
	}

	@Override
	public void setUserPassword(User user, String oldPassword, String newPassword) throws ApplicationException {
		if (validatePassword(user, oldPassword, newPassword)) {
			String passwd = generateSHAdigest(newPassword);
			user = repo.findOne(user.getId());
			user.setPassword(passwd);
			user.setExpiryDate(passwordExpiryDate());
			repo.save(user);
		}else{
			throw new ApplicationException("Password format is invalid");
		}
	}

	@Override
	public void setUserPassword(User user, String newPassword) throws ApplicationException {
		String passwd = generateSHAdigest(newPassword);
		user.setPassword(passwd);
		user.setExpiryDate(new Date());
		repo.save(user);
		
	}
	
	@Override
	public String getPassword(String password) throws ApplicationException{
		return generateSHAdigest(password);
	}

	@Override
	public void genearteUserPassword(User user) throws ApplicationException {
		String randomPassword = generateRandomPassword();
		log.debug("setting password for " + user + " to [" + randomPassword
				+ "]");
		setUserPassword(user, randomPassword);
	}

	private String generateRandomPassword() {
		String alphanumeric = RandomStringUtils.randomAlphanumeric(8);
		return alphanumeric;
	}
	
	private String generateSHAdigest(String plaintext) throws ApplicationException {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
			throw new ApplicationException(e);
		}
		byte[] md_password = plaintext.getBytes();
		byte[] md_hash = md.digest(md_password);
		StringBuilder bytes = new StringBuilder();
		for (byte by : md_hash) {
			bytes.append(String.format("%02x", by));
		}
		return bytes.toString();
	}

	private boolean validatePassword(User user, String oldPassword,
			String newPassword) throws ApplicationException {
		
		user = repo.findOne(user.getId());
		String adigest = generateSHAdigest(oldPassword);
		if(!user.getPassword().equals(adigest)){
			throw new ApplicationException("Old password is not correct");
		}
		
			return true;
	}
	private Date passwordExpiryDate() {
		return DateUtils.addDays(new Date(), 30);
	}
}
