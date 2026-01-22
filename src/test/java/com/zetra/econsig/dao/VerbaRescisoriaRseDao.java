package com.zetra.econsig.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.VerbaRescisoriaRse;

public interface VerbaRescisoriaRseDao extends JpaRepository<VerbaRescisoriaRse, String> {

    List<VerbaRescisoriaRse> findByRseCodigo(@Param("rse_codigo") String rseCodigo);
    
    Long removeByRseCodigo(String rseCodigo);
}
