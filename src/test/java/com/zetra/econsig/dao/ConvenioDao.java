package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Convenio;

public interface ConvenioDao extends JpaRepository<Convenio, String> {

    Long removeBySvcCodigo(String svcCodigo);

    Long removeByOrgCodigo(String orgCodigo);

	List<Convenio> findBySvcCodigo(@Param("svc_codigo") String svcCodigo);

	List<Convenio> findByOrgCodigo(@Param("org_codigo") String orgCodigo);

	Convenio findBySvcCodigoAndOrgCodigoAndCsaCodigo(@Param("svc_codigo") String svcCodigo,
			@Param("org_codigo") String orgCodigo,
			@Param("csa_codigo") String csaCodigo);
	
	Convenio findBySvcCodigoAndCsaCodigo(@Param("svc_codigo") String svcCodigo,
			@Param("csa_codigo") String csaCodigo);

	@Query(value = "SELECT cnv.* FROM tb_convenio cnv"
			+ " INNER JOIN tb_servico svc ON cnv.svc_codigo = svc.svc_codigo"
			+ " INNER JOIN tb_usuario_csa usuCsa ON cnv.csa_codigo = usuCsa.csa_codigo"
			+ " WHERE svc.NSE_CODIGO = ?1"
			+ " AND usuCsa.csa_codigo = ?2",
			nativeQuery = true)
	List<Convenio> getCnvByNseCodigoAndCsaCodigo(String nseCodigo, String csaCodigo);

	@Query(value = "SELECT cnv.* FROM tb_convenio cnv"
			+ " INNER JOIN tb_servico svc ON cnv.svc_codigo = svc.svc_codigo"
			+ " INNER JOIN tb_usuario_csa usuCsa ON cnv.csa_codigo = usuCsa.csa_codigo"
			+ " WHERE svc.svc_identificador = ?1"
			+ " AND usuCsa.csa_codigo = ?2",
			nativeQuery = true)
	Convenio getCnvBySvcIdentAndCsaCodigo(String svcIdentificador, String csaCodigo);

	@Query(value = "SELECT cnv.* FROM tb_convenio cnv"
			+ " INNER JOIN tb_servico svc ON cnv.svc_codigo = svc.svc_codigo"
			+ " INNER JOIN tb_usuario_csa usuCsa ON cnv.csa_codigo = usuCsa.csa_codigo"
			+ " INNER JOIN tb_orgao org ON cnv.org_codigo = org.org_codigo"
			+ " WHERE svc.svc_identificador = ?1"
			+ " AND usuCsa.csa_codigo = ?2"
			+ " AND org.`ORG_IDENTIFICADOR` = ?3",
			nativeQuery = true)
	Convenio getCnvBySvcIdentAndCsaCodigoAndOrgIdent(String svcIdentificador, String csaCodigo, String orgIdentificador);
}
