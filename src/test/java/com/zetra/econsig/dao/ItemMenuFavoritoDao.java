package com.zetra.econsig.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.zetra.econsig.persistence.entity.ItemMenuFavorito;


public interface ItemMenuFavoritoDao extends JpaRepository<ItemMenuFavorito, String> {

	ItemMenuFavorito findByUsuCodigoAndItmCodigo(@Param("usu_codigo") String usuCodigo, @Param("itm_codigo") String itmCodigo);	
}
