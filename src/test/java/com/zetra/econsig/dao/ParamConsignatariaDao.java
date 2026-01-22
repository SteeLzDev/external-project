package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamConsignataria;

public interface ParamConsignatariaDao extends JpaRepository<ParamConsignataria, String> {

	ParamConsignataria findByTpaCodigoAndCsaCodigo(@Param("csa_codigo") String csaCodigo,
			@Param("tpa_codigo") String tpaCodigo);
}
