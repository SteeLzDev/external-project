package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.Margem;

public interface MargemDao extends JpaRepository<Margem, String> {

	Margem findByMarCodigo(@Param("mar_codigo") Short marCodigo);

}
