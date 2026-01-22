package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.ParamConsignatariaRegistroSer;
import com.zetra.econsig.persistence.entity.ParamConsignatariaRegistroSerId;

public interface ParamConsignatariaRegistroSerDao extends JpaRepository<ParamConsignatariaRegistroSer, ParamConsignatariaRegistroSerId> {

}
