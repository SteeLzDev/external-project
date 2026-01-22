package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Orgao;


public interface OrgaoDao extends JpaRepository<Orgao, String> {

    Orgao findByOrgIdentificador(@Param("org_identificador") String orgIdentificador);

	List<Orgao> findByEstCodigo(@Param("est_codigo") String estCodigo);

	@Query(value = "SELECT org.* FROM tb_orgao org" +
			" INNER JOIN tb_estabelecimento est ON org.est_codigo = est.est_codigo" +
			" INNER JOIN tb_consignante cse ON est.cse_codigo = cse.cse_codigo" +
			" LEFT JOIN tb_calendario_folha_cse cfc ON cfc.cse_codigo = cse.cse_codigo" +
			" AND CURDATE() BETWEEN cfc.cfc_data_ini AND cfc.cfc_data_fim" +
			" LEFT JOIN tb_calendario_folha_est cfe ON cfe.est_codigo = est.est_codigo" +
			" AND CURDATE() BETWEEN cfe.cfe_data_ini AND cfe.cfe_data_fim" +
			" LEFT JOIN tb_calendario_folha_org cfo ON cfo.org_codigo = org.org_codigo" +
			" AND CURDATE() BETWEEN cfo.cfo_data_ini AND cfo.cfo_data_fim" +
			" where org.org_codigo = ?1",
	    nativeQuery = true
	)
	String getCalendarioFolhaByOrgCodigo(String orgCodigo);

}
