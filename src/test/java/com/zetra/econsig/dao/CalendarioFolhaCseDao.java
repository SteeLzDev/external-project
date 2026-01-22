package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.CalendarioFolhaCse;


public interface CalendarioFolhaCseDao extends JpaRepository<CalendarioFolhaCse, String> {

	List<CalendarioFolhaCse> findByCseCodigo(@Param("cse_codigo") String cseCodigo);	

	@Query(value = "SELECT cfc.* FROM tb_orgao org" + 
			" INNER JOIN tb_estabelecimento est ON org.est_codigo = est.est_codigo" + 
			" INNER JOIN tb_consignante cse ON est.cse_codigo = cse.cse_codigo" + 
			" LEFT JOIN tb_calendario_folha_cse cfc ON cfc.cse_codigo = cse.cse_codigo" + 
			" AND CURDATE() BETWEEN cfc.cfc_data_ini AND cfc.cfc_data_fim" + 
			" WHERE org.org_codigo = ?1",
	    nativeQuery = true
	)
	CalendarioFolhaCse getCalendarioFolhaCseByOrgCodigo(String orgCodigo);	
		
}
