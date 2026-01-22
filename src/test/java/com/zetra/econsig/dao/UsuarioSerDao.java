package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.zetra.econsig.persistence.entity.UsuarioSer;
import com.zetra.econsig.persistence.entity.UsuarioSerId;

public interface UsuarioSerDao extends JpaRepository<UsuarioSer, UsuarioSerId> {

	@Query(value = "SELECT usuSer.ser_codigo from tb_usuario usu"
			+ " INNER JOIN tb_usuario_ser usuSer ON usu.usu_codigo = usuSer.usu_codigo"
			+ " WHERE usu.usu_login LIKE %?1",
	    nativeQuery = true
	)
	String getSerCodigoByUsuLogin(String usuLogin);

	@Query(value = "SELECT usuSer.usu_codigo from tb_usuario usu"
			+ " INNER JOIN tb_usuario_ser usuSer ON usu.usu_codigo = usuSer.usu_codigo"
			+ " WHERE usu.usu_login LIKE %?1",
			nativeQuery = true
	)

	String getUsuCodByUsuLogin(String usuLogin);

    Long removeByUsuCodigo(String usuCodigo);
}
