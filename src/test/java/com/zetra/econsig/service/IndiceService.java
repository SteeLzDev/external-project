package com.zetra.econsig.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.IndiceDao;
import com.zetra.econsig.persistence.entity.Indice;

@Service
public class IndiceService {

	@Autowired
	private IndiceDao indiceDao;
	
	public void incluirIndice(String svcCodigo, String csaCodigo, String indCodigo, String indDescricao) {
		Indice indice = indiceDao.findBySvcCodigoAndCsaCodigoAndIndCodigo(svcCodigo, csaCodigo, indCodigo);
				
		if(indice == null) {
			indice = new Indice();
			indice.setSvcCodigo(svcCodigo);
			indice.setCsaCodigo(csaCodigo);
			indice.setIndCodigo(indCodigo);
			indice.setIndDescricao(indDescricao);
			indiceDao.save(indice);
		}
	}
	
	public void excluirIndices(String csaCodigo) {
		List<Indice> indice = indiceDao.findByCsaCodigo(csaCodigo);
				
		if(!indice.isEmpty()) {
			indiceDao.deleteAll(indice);
		}
	}
}
