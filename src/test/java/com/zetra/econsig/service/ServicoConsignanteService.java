package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.ParamSvcConsignanteDao;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;


@Service
@Transactional
public class ServicoConsignanteService {
	
	@Autowired
	private ParamSvcConsignanteDao paramSvcConsignanteDao;

	public void configurarParametroSvcConsignante(String tpsCodigo, String svcCodigo, String pseVlr) {
		ParamSvcConsignante paramSvcConsignante =  paramSvcConsignanteDao.getPseCodigoByTpsCodigoAndSvcCodigo(tpsCodigo, svcCodigo);
		if (paramSvcConsignante == null) {
			paramSvcConsignante = new ParamSvcConsignante();
			paramSvcConsignante.setTpsCodigo(tpsCodigo);
			paramSvcConsignante.setSvcCodigo(svcCodigo);
		}
		paramSvcConsignante.setPseVlr(pseVlr);
		
		paramSvcConsignanteDao.save(paramSvcConsignante);
		paramSvcConsignanteDao.flush();
	}
}
