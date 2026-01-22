package com.zetra.econsig.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.PostoRegistroServidorDao;
import com.zetra.econsig.persistence.entity.PostoRegistroServidor;

@Service
public class PostoRegistroServidorService {

	@Autowired
	private PostoRegistroServidorDao postoRegistroServidorDao;
	
	public void criarPostoRegistroServidor(String posCodigo, String posDescricao, String posIdentificador) {
		PostoRegistroServidor postoRegistroServidor = new PostoRegistroServidor();
		postoRegistroServidor.setPosCodigo(posCodigo);
		postoRegistroServidor.setPosDescricao(posDescricao);
		postoRegistroServidor.setPosPercTxUso(BigDecimal.valueOf(10.00));
		postoRegistroServidor.setPosPercTxUsoCond(BigDecimal.valueOf(10.00));
		postoRegistroServidor.setPosVlrSoldo(BigDecimal.valueOf(2000.00));
		postoRegistroServidor.setPosIdentificador(posIdentificador);
		postoRegistroServidorDao.save(postoRegistroServidor);
	}
}
