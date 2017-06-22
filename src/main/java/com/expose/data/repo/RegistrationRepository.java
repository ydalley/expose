package com.expose.data.repo;


import java.util.List;

import org.springframework.stereotype.Repository;

import com.expose.model.RegistrationInfo;


@Repository
public interface RegistrationRepository extends CommonRepository<RegistrationInfo,Long> {
	List<RegistrationInfo> findByEmail(String email);
	RegistrationInfo findFirstByRegKeyAndValid(String key, boolean valid);
}
