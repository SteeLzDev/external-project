package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParamSvcConsignante;


public interface ParamSvcConsignanteDao extends JpaRepository<ParamSvcConsignante, String> {

    Long removeBySvcCodigo(String svcCodigo);

	List<ParamSvcConsignante> findByTpsCodigo(@Param("tps_codigo") String tpsCodigo);

	@Query(value = "SELECT tps.* FROM tb_param_svc_consignante tps"
		      +	" INNER JOIN tb_servico svc ON tps.svc_codigo = svc.svc_codigo"
		      + " WHERE tps.tps_codigo = ?1"
		      + " AND svc.svc_codigo = ?2",
				nativeQuery = true)
	ParamSvcConsignante getPseCodigoByTpsCodigoAndSvcCodigo(String tpsCodigo, String svcCodigo);
}
