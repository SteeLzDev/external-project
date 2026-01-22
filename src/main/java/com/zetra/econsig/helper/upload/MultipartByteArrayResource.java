package com.zetra.econsig.helper.upload;

import org.springframework.core.io.ByteArrayResource;

/**
 * <p>Title: MultipartByteArrayResource</p>
 * <p>Description: Classe que transforma o arquivo em um array de bytes.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MultipartByteArrayResource extends ByteArrayResource{

	private String fileName;

	public MultipartByteArrayResource(byte[] byteArray) {
		super(byteArray);
	}

	public String getFilename() { 
		return fileName; 
	}

	public void setFilename(String fileName) {
		this.fileName= fileName;
	}


}
