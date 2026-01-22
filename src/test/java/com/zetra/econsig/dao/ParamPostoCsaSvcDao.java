package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamPostoCsaSvc;

public interface ParamPostoCsaSvcDao extends JpaRepository<ParamPostoCsaSvc, String> {
	ParamPostoCsaSvc findBySvcCodigoAndCsaCodigoAndPosCodigo(@Param("svc_codigo") String svcCodigo, @Param("csa_codigo") String csaCodigo, @Param("pos_codigo") String posCodigo);
	
}	 