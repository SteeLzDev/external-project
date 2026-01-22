package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.ConsignanteDao;
import com.zetra.econsig.persistence.entity.Consignante;

@Service
public class ConsignanteService {

	@Autowired
	private ConsignanteDao consignanteDao;
	
	public void alterarStatusConsignante(String status) {
		Consignante consignante = consignanteDao.findByCseCodigo("1");
		
		consignante.setCseAtivo(Short.valueOf(status));
		consignanteDao.save(consignante);
	}	
	
	public String getCseAtivo() {
		Consignante consignante = consignanteDao.findByCseCodigo("1");
		
		return consignante.getCseAtivo().toString();
	}	
}
