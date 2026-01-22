package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;

public interface VinculoRegistroServidorDao extends JpaRepository<VinculoRegistroServidor, String> {

	VinculoRegistroServidor findByVrsAtivo(@Param("vrs_ativo") Short vrsAtivo);

}
