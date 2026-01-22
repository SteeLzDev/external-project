package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.UsuarioSup;
import com.zetra.econsig.persistence.entity.UsuarioSupId;


public interface UsuarioSupDao extends JpaRepository<UsuarioSup, UsuarioSupId> {

	UsuarioSup findByUsuCodigo(@Param("usu_codigo") String usuCodigo);

    Long removeByUsuCodigo(String usuCodigo);
}
