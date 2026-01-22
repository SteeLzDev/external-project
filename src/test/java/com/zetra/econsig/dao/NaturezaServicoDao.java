package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.NaturezaServico;

public interface NaturezaServicoDao extends JpaRepository<NaturezaServico, String> {

}
