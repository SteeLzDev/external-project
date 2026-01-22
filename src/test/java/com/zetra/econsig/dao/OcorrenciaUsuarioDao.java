package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.OcorrenciaUsuario;

public interface OcorrenciaUsuarioDao extends JpaRepository<OcorrenciaUsuario, String> {

    Long removeByUsuCodigo(String usuCodigo);
}
