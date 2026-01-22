package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Perfil;

public interface PerfilDao extends JpaRepository<Perfil, String> {

	List<Perfil> findByPerDescricao(@Param("perDescricao") String perDescricao);
	
}
