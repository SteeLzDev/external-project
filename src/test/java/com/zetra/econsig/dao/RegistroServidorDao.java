package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.RegistroServidor;

public interface RegistroServidorDao extends JpaRepository<RegistroServidor, String> {

    RegistroServidor findBySerCodigo(@Param("ser_codigo") String serCodigo);

    RegistroServidor findByRseMatricula(@Param("rse_matricula") String rseMatricula);

    RegistroServidor findByRseMatriculaAndOrgCodigo(@Param("rse_matricula") String rseMatricula, @Param("org_codigo") String orgCodigo);
}
