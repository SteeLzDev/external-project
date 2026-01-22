package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamConvenioRegistroSer;


public interface ParamConvenioRegistroSerDao extends JpaRepository<ParamConvenioRegistroSer, String> {

	List<ParamConvenioRegistroSer> findByRseCodigo(@Param("rse_codigo") String rseCodigo);		

	ParamConvenioRegistroSer findByRseCodigoAndCnvCodigoAndTpsCodigo(@Param("rse_codigo") String rseCodigo, @Param("cnv_codigo") String cnvCodigo, @Param("tps_codigo") String tpsCodigo);	
}
