package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.TipoGrupoSvc;

public interface TipoGrupoSvcDao extends JpaRepository<TipoGrupoSvc, String> {

	public TipoGrupoSvc findByTgsIdentificador(@Param("tgs_identificador") String tgsIdentificador);

}
