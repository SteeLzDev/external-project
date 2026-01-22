package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.CampoSistemaDao;
import com.zetra.econsig.persistence.entity.CampoSistema;

@Service
@Transactional
public class CampoSistemaService {

	@Autowired
	private CampoSistemaDao campoSistemaDao;
		
	public void alterarCampoSistema(String casChave, String casValor) {
		CampoSistema campoSistema = campoSistemaDao.findByCasChave(casChave);
		if (campoSistema == null) {
			campoSistema = new CampoSistema();
			campoSistema.setCasChave(casChave);
		}
		campoSistema.setCasValor(casValor);
		campoSistemaDao.save(campoSistema);
	}
}
