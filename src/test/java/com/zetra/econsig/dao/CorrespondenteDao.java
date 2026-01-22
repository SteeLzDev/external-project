package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Correspondente;

public interface CorrespondenteDao extends JpaRepository<Correspondente, String> {

	@Query(value = "SELECT cor.* FROM tb_usuario usu"
				+ " INNER JOIN tb_usuario_cor usuCor ON usu.usu_codigo = usuCor.usu_codigo"
				+ " INNER JOIN tb_correspondente cor ON usuCor.cor_codigo = cor.cor_codigo"
				+ " INNER JOIN tb_consignataria csa ON cor.csa_codigo = csa.csa_codigo"
			+ " WHERE usu.usu_login = :usuLogin"
			+ " ORDER BY RAND() ASC"
			+ " LIMIT 1",
		nativeQuery = true
	)
	Correspondente getCorByLoginUsuCor(@Param("usuLogin") String usuLogin);
	
	Correspondente findByCorIdentificador(@Param("cor_identificador") String corIdentificador);	
}
