package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.UsuarioChaveSessao;

public interface UsuarioChaveSessaoDao extends JpaRepository<UsuarioChaveSessao, String> {

}
