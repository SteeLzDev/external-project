package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamSistConsignante;


public interface ParamSistConsignanteDao extends JpaRepository<ParamSistConsignante, String> {

	ParamSistConsignante findByTpcCodigo(@Param("tpc_codigo") String tpcCodigo);		
}
