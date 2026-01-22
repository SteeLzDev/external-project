package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.CalculoBeneficio;


public interface CalculoBeneficioDao extends JpaRepository<CalculoBeneficio, String> {

	List<CalculoBeneficio> findByOrgCodigo(@Param("org_codigo") String orgCodigo);
	
	CalculoBeneficio findByClbCodigo(@Param("clb_codigo") String clbCodigo);
}
