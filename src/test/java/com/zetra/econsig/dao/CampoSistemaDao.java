package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.CampoSistema;


public interface CampoSistemaDao extends JpaRepository<CampoSistema, String> {

	CampoSistema findByCasChave(@Param("CAS_CHAVE") String casChave);		
}
