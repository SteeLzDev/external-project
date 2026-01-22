package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.Coeficiente;

public interface CoeficienteDao extends JpaRepository<Coeficiente, String> {

    Long removeByPrzCsaCodigo(String przCsaCodigo);
}
