package com.clockwise.tworcy.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.clockwise.tworcy.exception.BadImageException;
import com.clockwise.tworcy.misc.MimeTypes;

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

	/**
	 * Removes non-existing image files from the list.
	 * @param mediaNames string array which contains image lists
	 * @param mediaDirectory name of the folder in which image files should might be stored
	 * @param skipPrefix skips names with prefixes ex: yt:xf1a24fA
	 * @return modified array
	 */
	public List<String> removeNonExistentImages(List<String> mediaNames, String mediaDirectory, String... skipPrefix) {
		// Method variables
		List<String> ret = new ArrayList<String>();
		String fileName;
		File file;

		// Iterates through media names
		MediaLoop: for(int i=0; i < mediaNames.size(); i++) {
			// Catch the variable
			fileName = mediaNames.get(i);
			
			// Skips prefixes
			for(int s=0;s<skipPrefix.length;s++)
				if(fileName.startsWith(skipPrefix[s]))
					continue MediaLoop; //<- Skips the previous for
			
			// Create the file
			file = new File(context.getRealPath("/resources/upload/") + mediaDirectory + "/" + fileName);
			
			// Remove checks
			if(file.exists() && file.isFile())
				ret.add(mediaNames.get(i));
		}
		
		// Finished
		return ret;
	}
}
