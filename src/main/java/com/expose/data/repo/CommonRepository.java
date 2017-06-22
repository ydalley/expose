package com.expose.data.repo;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CommonRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
	
	public static String DEL = "X";
	public static String DEFAULT = "Y";
	
	public Page<T> findUsingPattern(String pattern, Pageable details);

}
