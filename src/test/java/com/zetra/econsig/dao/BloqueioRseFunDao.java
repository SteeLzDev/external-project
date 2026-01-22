package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.BloqueioRseFun;

public interface BloqueioRseFunDao extends JpaRepository<BloqueioRseFun, String> {
	
	BloqueioRseFun findByRseCodigoAndFunCodigo(@Param("rse_codigo") String rseCodigo, @Param("fun_codigo") String funCodigo);
	
}