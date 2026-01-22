package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.FuncaoPerfil;
import com.zetra.econsig.persistence.entity.FuncaoPerfilId;

public interface FuncaoPerfilDao extends JpaRepository<FuncaoPerfil, FuncaoPerfilId> {

}