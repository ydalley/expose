package com.expose.config;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;
import com.expose.security.InlineRealm;

@Configuration
public class ShiroConfig {
	@Bean
	public FilterRegistrationBean characterEncodingFilter() {
		FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
		filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
		filterRegistration.setEnabled(true);
		filterRegistration.addUrlPatterns("/*");
		filterRegistration.setDispatcherTypes(DispatcherType.REQUEST);

		return filterRegistration;
	}

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilter() {
		ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
		shiroFilter.setLoginUrl("/login");
		shiroFilter.setSuccessUrl("/");
		shiroFilter.setUnauthorizedUrl("/forbidden");
		Map<String, String> filterChainDefinitionMapping = new HashMap<String, String>();

		filterChainDefinitionMapping.put("/logout", "logout");
		
		filterChainDefinitionMapping.put("/h2/**", "anon");
		filterChainDefinitionMapping.put("/register/**", "anon");
		filterChainDefinitionMapping.put("/signup/**", "anon");
		filterChainDefinitionMapping.put("/css/**", "anon");
		filterChainDefinitionMapping.put("/images/**", "anon");
		filterChainDefinitionMapping.put("/less/**", "anon");
		filterChainDefinitionMapping.put("/vendors/**", "anon");
		filterChainDefinitionMapping.put("/js/**", "anon");
		filterChainDefinitionMapping.put("/**", "authc");
		shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMapping);
		shiroFilter.setSecurityManager(securityManager());

		Map<String, Filter> filters = new HashMap<String, Filter>();
		filters.put("anon", new AnonymousFilter());
		filters.put("authc", new FormAuthenticationFilter());
		filters.put("logout", new LogoutFilter());
		// filters.put("roles", new RolesAuthorizationFilter());
		// filters.put("user", new UserFilter());
		shiroFilter.setFilters(filters);
		return shiroFilter;
	}

	@Bean(name = "sessionManager")
	public DefaultWebSessionManager defaultWebSessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setGlobalSessionTimeout(15 * 60 * 1000);
		// sessionManager.setSessionIdCookieEnabled(true);
		sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setDeleteInvalidSessions(true);
		sessionManager.setSessionDAO(new EnterpriseCacheSessionDAO());

		return sessionManager;
	}

	@Bean(name = "cacheManager")
	public CacheManager cacheManager() {
		EhCacheManager cache = new EhCacheManager();
		return cache;
	}

	@Bean(name = "securityManager")
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(realm());
		DefaultWebSessionManager defaultWebSessionManager = defaultWebSessionManager();
		securityManager.setSessionManager(defaultWebSessionManager);
		securityManager.setCacheManager(cacheManager());
		securityManager.setSessionManager(defaultWebSessionManager());
		return securityManager;
		
	}

	@Bean(name = "realm")
	@DependsOn("lifecycleBeanPostProcessor")
	public AuthenticatingRealm realm() {
		AuthenticatingRealm realm = new InlineRealm();
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		matcher.setHashAlgorithmName("SHA-256");
		matcher.setStoredCredentialsHexEncoded(true);
		realm.setCredentialsMatcher(matcher);
		realm.setCacheManager(new MemoryConstrainedCacheManager());
		return realm;
	}
	
	@Bean(name = "realm")
	@DependsOn("lifecycleBeanPostProcessor")
	public AuthenticatingRealm realms() {
		AuthenticatingRealm realm = new InlineRealm();
		HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
		matcher.setHashAlgorithmName("SHA-256");
		matcher.setStoredCredentialsHexEncoded(true);
		realm.setCredentialsMatcher(matcher);
		realm.setCacheManager(new MemoryConstrainedCacheManager());
		return realm;
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}


}
