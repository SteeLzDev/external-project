package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.AgendamentoDao;

@Service
@Transactional
public class AgendamentoService {

	@Autowired
	private AgendamentoDao agendamentoDao;

	public void desabilitarTodosAgendamentos() {
		agendamentoDao.desabilitarTodosAgendamentos();
	}

}
