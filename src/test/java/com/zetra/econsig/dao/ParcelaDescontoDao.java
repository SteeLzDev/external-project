package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ParcelaDesconto;


public interface ParcelaDescontoDao extends JpaRepository<ParcelaDesconto, String> {

	ParcelaDesconto findByAdeCodigo(@Param("ade_codigo") String adeCodigo);	
	
	@Query(value = "SELECT pde.* FROM tb_parcela_desconto pde" + 
			" INNER JOIN tb_aut_desconto ade ON pde.ade_codigo = ade.ade_codigo" + 
			" WHERE ade.rse_codigo = ?1", 
				nativeQuery = true)	
	List<ParcelaDesconto> getParcelaDesByRseCodigo(String rseCodigo);	
	
}
