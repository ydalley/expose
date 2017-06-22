package com.expose.service.implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.expose.ApplicationException;
import com.expose.data.repo.ImageRepository;
import com.expose.model.ImageInfo;
import com.expose.model.User;
import com.expose.service.GalleryService;

@Service
public class GalleryServiceImpl implements GalleryService {

	@Autowired
	ImageRepository repo;
	
	@Value("${images.path}")
	private String folder;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	@Transactional
	public String storeImage(byte[] image, String name) throws ApplicationException {
		ImageInfo info = new ImageInfo();
		User owner = getOwner();
		info.setOwner(owner);
		info.setName(name);
		ImageInfo imageInfo = repo.save(info);
		
		String localRepo = prepareLocalRepo();
		Path path = Paths.get(localRepo,imageInfo.getId().toString());
		try {
			Files.write(path, image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		info.setPath(path.toString());
		repo.save(info);
		return "Image saved successfully";
	}

	@Override
	public ImageInfo getImage(Long id) throws ApplicationException {
		ImageInfo info = repo.findOne(id);
		return info;
	}

	
	@Override
	public byte[] getImage(ImageInfo info) throws ApplicationException {
		
		Path path = Paths.get(info.getPath());
		try {
			return Files.readAllBytes(path);
		} catch (IOException e) {
			log.error("error reading path ",path.toString(),e);
			throw new ApplicationException("error reading path "+path.toString());
		}
	}
	
	@Override
	public List<ImageInfo> getImageInfos() throws ApplicationException {
		User owner = getOwner();
		return repo.findByOwner(owner);
	}
	
	private User getOwner(){
		return  (User)SecurityUtils.getSubject().getPrincipal();
	}
	
	private String prepareLocalRepo() throws ApplicationException{
		User user = (User)SecurityUtils.getSubject().getPrincipal();
		Path path = null;
		try {
			path = Files.createDirectories(Paths.get(folder, user.getEmail()));
		} catch (IOException e) {
			log.error("error creating path ",Paths.get(folder, user.getEmail()).toString(),e);
			throw new ApplicationException("error creating path  "+Paths.get(folder, user.getEmail()).toString());
		}
		return path.toString();
	}

}
