package com.expose.security;


import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.expose.model.User;
import com.expose.service.UserService;

public class InlineRealm extends AuthenticatingRealm {

	@Autowired
	private UserService userservice;

	

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken arg0) throws AuthenticationException {
		UsernamePasswordToken at = (UsernamePasswordToken) arg0;
		SimpleAuthenticationInfo info = null;
		User user;
		user = userservice.getUserForLoginByName(at.getUsername());
		if(user == null )
			throw new AuthenticationException("Unknown user");
		if(!"E".equals(user.getStatus())){
			throw new AuthenticationException("Inactive user account, contact your administrator");
		}
		info = new SimpleAuthenticationInfo(user,
				user.getPassword(), ByteSource.Util.bytes(""), getName());
		return info;
	}

	
	
}
