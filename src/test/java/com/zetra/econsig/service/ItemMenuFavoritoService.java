package com.zetra.econsig.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.ItemMenuFavoritoDao;
import com.zetra.econsig.persistence.entity.ItemMenuFavorito;

@Service
public class ItemMenuFavoritoService {

	@Autowired
	private ItemMenuFavoritoDao itemMenuFavoritoDao;
	
	public void incluirItemMenuFavorito(String usuCodigo, String itmCodigo) {
		ItemMenuFavorito itemMenuFavorito = itemMenuFavoritoDao.findByUsuCodigoAndItmCodigo(usuCodigo, itmCodigo);
		Timestamp imfData = new Timestamp(System.currentTimeMillis());
		
		if(itemMenuFavorito == null) {
			itemMenuFavorito = new ItemMenuFavorito();
			itemMenuFavorito.setUsuCodigo(usuCodigo);
			itemMenuFavorito.setItmCodigo(itmCodigo);
			itemMenuFavorito.setImfData(imfData);
			itemMenuFavorito.setImfSequencia((short) 0);
			itemMenuFavoritoDao.save(itemMenuFavorito);
		}
	}
	
	public void excluirItemMenuFavoritos() {
		List<ItemMenuFavorito> itemMenuFavorito = itemMenuFavoritoDao.findAll();
				
		if(!itemMenuFavorito.isEmpty()) {
			itemMenuFavoritoDao.deleteAll(itemMenuFavorito);
		}
	}
}
