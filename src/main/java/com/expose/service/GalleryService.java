package com.expose.service;

import java.util.List;

import com.expose.ApplicationException;
import com.expose.model.ImageInfo;

public interface GalleryService {
	String storeImage(byte[] image, String name) throws ApplicationException;
	ImageInfo getImage(Long id) throws ApplicationException;
	byte[] getImage(ImageInfo info) throws ApplicationException;
	List<ImageInfo> getImageInfos() throws ApplicationException;
}
