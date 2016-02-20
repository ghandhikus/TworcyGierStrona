package com.clockwise.util;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.clockwise.exceptions.BadImageException;
import com.clockwise.misc.MimeTypes;

@Component("imageUtils")
public class ImageUtils {
	
	@Autowired ServletContext context;
	
	@PostConstruct
	public void init() {
		System.out.println(this.getClass().getName()+" saves images to "+context.getRealPath("/resources/upload/"));
	}
	
	/**
	 * Checks if {@link MultipartFile} is a supported image.
	 * @param image
	 * @return false if it is unknown type or not supported
	 */
	public boolean validateImage(MultipartFile image) throws BadImageException {
		String c = image.getContentType();

		switch (c)
		{
		case MimeTypes.MIME_IMAGE_JPEG:
		case MimeTypes.MIME_IMAGE_PNG:
		case MimeTypes.MIME_IMAGE_GIF:
			return true;

		default:
			System.out.println("Unknown file type in "+this.getClass()+"#validateImage ("+c+")");
			throw new BadImageException(Messages.getString("Exception.badImage") + c);
		}
	}
	
	public String imageExtension(MultipartFile image) {
		String c = image.getContentType();

		switch (c)
		{
			case MimeTypes.MIME_IMAGE_JPEG: return "jpg";
			case MimeTypes.MIME_IMAGE_PNG: return "png";
			case MimeTypes.MIME_IMAGE_GIF: return "gif";
		}
		
		return null;
	}

	public String saveImage(String folder, MultipartFile image) throws RuntimeException, IOException {
		try {
			// FileName
			String fileName = image.getOriginalFilename().replaceAll("[^A-Za-z0-9 \\.]", "_");
			File file = new File(context.getRealPath("/resources/upload/") + folder + "/" + fileName);

			FileUtils.writeByteArrayToFile(file, image.getBytes());
			
			return fileName;
		} catch (IOException e) {
			throw e;
		}
	}
}
