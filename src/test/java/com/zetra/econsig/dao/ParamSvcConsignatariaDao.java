package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamSvcConsignataria;


public interface ParamSvcConsignatariaDao extends JpaRepository<ParamSvcConsignataria, String> {

    Long removeBySvcCodigo(String svcCodigo);

	ParamSvcConsignataria findByTpsCodigo(@Param("tps_codigo") String tpsCodigo);

	@Query(value = "SELECT tps.* FROM tb_param_svc_consignataria tps"
		      +	" INNER JOIN tb_servico svc ON tps.svc_codigo = svc.svc_codigo"
		      + " WHERE tps.tps_codigo = ?1"
		      + " AND svc.svc_codigo = ?2"
		      + " AND tps.csa_codigo = ?3",
				nativeQuery = true)
	ParamSvcConsignataria getPseCodigoByTpsCodigoAndSvcCodigo(String tpsCodigo, String svcCodigo, String csaCodigo);
}
