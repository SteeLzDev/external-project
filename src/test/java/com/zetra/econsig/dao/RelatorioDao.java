package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Relatorio;

public interface RelatorioDao extends JpaRepository<Relatorio, String> {
	
	Relatorio findByFunCodigo(@Param("fun_codigo") String funCodigo);	
}