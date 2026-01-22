package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Servidor;

public interface ServidorDao extends JpaRepository<Servidor, String> {

	Servidor findBySerCpf(@Param("ser_cpf") String serCpf);
}