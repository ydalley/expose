package com.expose.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.expose.ApplicationException;
import com.expose.model.User;



public interface UserService {
	User getUser(long id);
	
	Page<User> getAllUsers(Pageable pageDetails);
	Page<User> findUser(String pattern,Pageable pageDetails);
//	User  getCurrentUser();
	User getUserForLoginByName(String userId);
	String modify(User user) throws ApplicationException;
	String add(User user) throws ApplicationException;
	String enable(User user) throws ApplicationException;
	String toggleStatus(User user) throws ApplicationException;
	String disable(User user) throws ApplicationException;
	void setUserPassword(User user, String oldPassword, String newPassword) throws ApplicationException;
	void setUserPassword(User user, String newPassword) throws ApplicationException;
	void genearteUserPassword(User user)
			throws ApplicationException;

	String getPassword(String password) throws ApplicationException;
}
