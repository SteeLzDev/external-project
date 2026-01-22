package com.zetra.econsig.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.AgendamentoDao;
import com.zetra.econsig.persistence.entity.Agendamento;

@Service
public class RelatorioService {

	@Autowired
	private AgendamentoDao agendamentoDao;

	public List<Agendamento> getAgendamentoRelatorio(String usuCodigo, String relCodigo, String sagCodigo,
			String tagCodigo) {
		return agendamentoDao.findByUsuCodigoAndRelCodigoAndSagCodigoAndTagCodigo(usuCodigo, relCodigo, sagCodigo,
				tagCodigo);
	}
	
	public List<Agendamento> getAgendamentoRelatorio(String usuCodigo, String relCodigo) {
		return agendamentoDao.findByUsuCodigoAndRelCodigo(usuCodigo, relCodigo);
	}
}
