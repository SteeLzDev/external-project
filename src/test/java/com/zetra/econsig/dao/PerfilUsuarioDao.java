package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.PerfilUsuario;

public interface PerfilUsuarioDao extends JpaRepository<PerfilUsuario, String> {

}
