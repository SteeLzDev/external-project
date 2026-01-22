package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Servico;

public interface ServicoDao extends JpaRepository<Servico, String> {
	
	List<Servico> findByNseCodigo(@Param("NSE_CODIGO") String nseCodigo);
	
	Servico findBySvcIdentificador(@Param("svc_identificador") String svcIdentificador);
}
