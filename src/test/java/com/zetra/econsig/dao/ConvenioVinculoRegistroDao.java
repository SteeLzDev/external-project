package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ConvenioVinculoRegistro;

public interface ConvenioVinculoRegistroDao extends JpaRepository<ConvenioVinculoRegistro, String> {

	ConvenioVinculoRegistro findByVrsCodigoAndCsaCodigoAndSvcCodigo(@Param("vrs_codigo") String vrsCodigo, @Param("csa_codigo") String csaCodigo, @Param("svc_codigo") String svcCodigo);

}
