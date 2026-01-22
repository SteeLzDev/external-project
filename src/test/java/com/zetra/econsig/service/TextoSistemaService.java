package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.TextoSistemaDao;
import com.zetra.econsig.persistence.entity.TextoSistema;

@Service
public class TextoSistemaService {
	
	@Autowired
	private TextoSistemaDao textoSistemaDao;
	
	public TextoSistema findByTexChave(String texChave) {
		return textoSistemaDao.findByTexChave(texChave);
	}

}
