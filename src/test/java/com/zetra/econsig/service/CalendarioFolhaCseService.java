package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.CalendarioFolhaCseDao;
import com.zetra.econsig.persistence.entity.CalendarioFolhaCse;

@Service
public class CalendarioFolhaCseService {

	@Autowired
	private CalendarioFolhaCseDao calendarioFolhaCseDao;
	
	public CalendarioFolhaCse getCalendarioFolhaCse(String orgCodigo) {
		return calendarioFolhaCseDao.getCalendarioFolhaCseByOrgCodigo(orgCodigo);
	}		
}
