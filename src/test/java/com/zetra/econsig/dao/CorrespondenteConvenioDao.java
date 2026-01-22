package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zetra.econsig.persistence.entity.CorrespondenteConvenio;

public interface CorrespondenteConvenioDao extends JpaRepository<CorrespondenteConvenio, String> {

	@Query(value = "SELECT cnv.* FROM tb_correspondente_convenio cnv"
			+ " WHERE cnv.cor_codigo = ?1"
			+ " AND cnv.cnv_codigo = ?2", 
			nativeQuery = true)	
	CorrespondenteConvenio getCnvByCorCodigoAndCnvCodigo(String corCodigo, String cnvCodigo);		
}
