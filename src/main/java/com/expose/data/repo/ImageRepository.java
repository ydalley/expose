package com.expose.data.repo;


import org.springframework.stereotype.Repository;

import com.expose.model.ImageInfo;
import com.expose.model.User;
import java.util.List;


@Repository
public interface ImageRepository extends CommonRepository<ImageInfo,Long> {
	
	List<ImageInfo> findByOwner(User owner);
}
