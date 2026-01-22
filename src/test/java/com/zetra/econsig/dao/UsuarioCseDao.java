package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.UsuarioCse;
import com.zetra.econsig.persistence.entity.UsuarioCseId;


public interface UsuarioCseDao extends JpaRepository<UsuarioCse, UsuarioCseId> {

	UsuarioCse findByUsuCodigo(@Param("usu_codigo") String usuCodigo);

    Long removeByUsuCodigo(String usuCodigo);
}
