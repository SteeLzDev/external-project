package com.zetra.econsig.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.VerbaRescisoriaRseDao;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.VerbaRescisoriaRse;

@Service
@Transactional
public class VerbaRescisoriaRseService {

	@Autowired
	private VerbaRescisoriaRseDao verbaRescisoriaRseDao;

	public List<VerbaRescisoriaRse> findVerbaRescisoriaByRseCodigo(String rseCodigo) {
		return verbaRescisoriaRseDao.findByRseCodigo(rseCodigo);
	}

	public void incluirVerbaRescisoria(String rseCodigo, String svrCodigo) {
		
		VerbaRescisoriaRse verbaRescisoriaRse = new VerbaRescisoriaRse();
		verbaRescisoriaRse.setVrrCodigo(UUID.randomUUID().toString().replace("-", "").substring(0, 20));
		verbaRescisoriaRse.setRseCodigo(rseCodigo);
		verbaRescisoriaRse.setSvrCodigo(svrCodigo);
		verbaRescisoriaRse.setVrrDataIni(DateHelper.getSystemDate());
		verbaRescisoriaRse.setVrrDataUltAtualizacao(DateHelper.getSystemDate());
		verbaRescisoriaRse.setVrrProcessado("N");
		verbaRescisoriaRseDao.save(verbaRescisoriaRse);
	}
	
	public void removerVerbaRescisoria(String rseCodigo) {
		verbaRescisoriaRseDao.removeByRseCodigo(rseCodigo);
	}
}
