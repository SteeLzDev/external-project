package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorId;

public interface MargemRegistroServidorDao extends JpaRepository<MargemRegistroServidor, MargemRegistroServidorId> {

    Long removeByRseCodigo(String rseCodigo);

    List<MargemRegistroServidor> findByMarCodigo(@Param("mar_codigo") Short marCodigo);

	MargemRegistroServidor findByMarCodigoAndRseCodigo(@Param("mar_codigo") Short marCodigo, @Param("rse_codigo") String rseCodigo);
}
