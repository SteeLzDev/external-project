package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zetra.econsig.persistence.entity.CoeficienteDesconto;

public interface CoeficienteDescontoDao extends JpaRepository<CoeficienteDesconto, String> {

    Long removeByAdeCodigo(String adeCodigo);
}
