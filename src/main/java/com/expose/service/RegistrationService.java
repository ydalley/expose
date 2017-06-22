package com.expose.service;



import com.expose.ApplicationException;
import com.expose.model.RegistrationInfo;



public interface RegistrationService {
	
	String register(RegistrationInfo regInfo) throws ApplicationException;
	String register(String regKey) throws ApplicationException;
	
}
