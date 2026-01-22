package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zetra.econsig.persistence.entity.UsuarioCsa;
import com.zetra.econsig.persistence.entity.UsuarioCsaId;

public interface UsuarioCsaDao extends JpaRepository<UsuarioCsa, UsuarioCsaId> {

	@Query(value = "SELECT usuCsa.csa_codigo from tb_usuario usu"
			+ " INNER JOIN tb_usuario_csa usuCsa ON usu.usu_codigo = usuCsa.usu_codigo"
			+ " WHERE usu.usu_login = ?1",
	    nativeQuery = true
	)

	String getCsaCodigoByUsuLogin(String usuLogin);

    Long removeByUsuCodigo(String usuCodigo);
}