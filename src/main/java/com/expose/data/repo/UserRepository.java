package com.expose.data.repo;


import org.springframework.stereotype.Repository;

import com.expose.model.User;


@Repository
public interface UserRepository extends CommonRepository<User,Long> {
	
	User findByLoginIdAndStatus(String name, String status);
	User findFirstByLoginId(String name);

}
