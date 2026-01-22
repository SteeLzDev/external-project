package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.ParamPostoCsaSvcDao;
import com.zetra.econsig.persistence.entity.ParamPostoCsaSvc;

@Service
public class ParamPostoCsaSvcService {

	@Autowired
	private ParamPostoCsaSvcDao paramPostoCsaSvcDao;
	
	public ParamPostoCsaSvc buscarParamPostoCsaSvc(String posCodigo, String svcCodigo, String csaCodigo) {
		ParamPostoCsaSvc paramPostoCsaSvc = paramPostoCsaSvcDao.findBySvcCodigoAndCsaCodigoAndPosCodigo(svcCodigo, csaCodigo, posCodigo);
		return paramPostoCsaSvc;
	}
}
