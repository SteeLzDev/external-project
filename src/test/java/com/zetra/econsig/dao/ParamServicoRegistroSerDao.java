package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamServicoRegistroSer;


public interface ParamServicoRegistroSerDao extends JpaRepository<ParamServicoRegistroSer, String> {

	List<ParamServicoRegistroSer> findByRseCodigo(@Param("rse_codigo") String rseCodigo);		

	ParamServicoRegistroSer findByRseCodigoAndSvcCodigoAndTpsCodigo(@Param("rse_codigo") String rseCodigo, @Param("svc_codigo") String svcCodigo, @Param("tps_codigo") String tpsCodigo);	
}
