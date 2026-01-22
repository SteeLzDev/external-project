package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zetra.econsig.persistence.entity.UsuarioCor;
import com.zetra.econsig.persistence.entity.UsuarioCorId;

public interface UsuarioCorDao extends JpaRepository<UsuarioCor, UsuarioCorId> {

	@Query(value = "SELECT usuCor.cor_codigo from tb_usuario usu"
			+ " INNER JOIN tb_usuario_cor usuCor ON usu.usu_codigo = usuCor.usu_codigo"
			+ " WHERE usu.usu_login = ?1",
	    nativeQuery = true
	)

	String getCorCodigoByUsuLogin(String usuLogin);

    Long removeByUsuCodigo(String usuCodigo);
}