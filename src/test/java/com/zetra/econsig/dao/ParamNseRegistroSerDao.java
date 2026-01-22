package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamNseRegistroSer;


public interface ParamNseRegistroSerDao extends JpaRepository<ParamNseRegistroSer, String> {

	List<ParamNseRegistroSer> findByRseCodigo(@Param("rse_codigo") String rseCodigo);		

	ParamNseRegistroSer findByRseCodigoAndNseCodigoAndTpsCodigo(@Param("rse_codigo") String rseCodigo, @Param("nse_codigo") String nseCodigo, @Param("tps_codigo") String tpsCodigo);	
}
