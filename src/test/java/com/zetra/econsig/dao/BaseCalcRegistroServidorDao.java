package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.BaseCalcRegistroServidor;

public interface BaseCalcRegistroServidorDao extends JpaRepository<BaseCalcRegistroServidor, String> {
	
	BaseCalcRegistroServidor findByRseCodigo(@Param("RSE_CODIGO") String rseCodigo);
	
	BaseCalcRegistroServidor findByRseCodigoAndTbcCodigo(@Param("RSE_CODIGO") String rseCodigo, @Param("TBC_CODIGO") String tbcCodigo);
}