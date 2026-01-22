package com.zetra.econsig.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.MargemDao;
import com.zetra.econsig.dao.MargemRegistroServidorDao;
import com.zetra.econsig.persistence.entity.Margem;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;

@Service
public class MargemService {

	@Autowired
	private MargemDao margemDao;

	@Autowired
	private MargemRegistroServidorDao margemRegistroServidorDao;

	public Margem incluirMargem(String marCodigo, String marTipoVlr) {
		Margem margem = margemDao.findByMarCodigo(Short.valueOf(marCodigo));

		if (margem == null) {
			margem = new Margem();
			margem.setMarCodigo(Short.valueOf(marCodigo));
			margem.setMarDescricao("Margem GAP");
			margem.setMarTipoVlr(marTipoVlr);
			margem.setMarExibeCse("0");
			margem.setMarExibeCsa("0");
			margem.setMarExibeOrg("0");
			margem.setMarExibeSer("0");
			margem.setMarExibeSup("0");
			margem.setMarExibeCor("0");
			margemDao.save(margem);
		}

		return margem;
	}

	public MargemRegistroServidor incluirMargemRegistroServidor(String marCodigo, String rseCodigo, BigDecimal mrsMargem,
			BigDecimal mrsMargemUsada) {

		return incluirMargemRegistroServidor(marCodigo, rseCodigo, mrsMargem, mrsMargemUsada, BigDecimal.ZERO);
	}

	public MargemRegistroServidor incluirMargemRegistroServidor(String marCodigo, String rseCodigo, BigDecimal mrsMargem,
			BigDecimal mrsMargemUsada, BigDecimal mrsMediaMargem) {

		MargemRegistroServidor margemRegistroServidor = margemRegistroServidorDao
				.findByMarCodigoAndRseCodigo(Short.valueOf(marCodigo), rseCodigo);

		if (margemRegistroServidor == null) {
			margemRegistroServidor = new MargemRegistroServidor();
			margemRegistroServidor.setMarCodigo(Short.valueOf(marCodigo));
			margemRegistroServidor.setRseCodigo(rseCodigo);
		}
		margemRegistroServidor.setMrsMargem(mrsMargem);
		margemRegistroServidor.setMrsMargemUsada(mrsMargemUsada);
		margemRegistroServidor.setMrsMargemRest(mrsMargem.subtract(mrsMargemUsada));
		margemRegistroServidor.setMrsMediaMargem(mrsMediaMargem);
		margemRegistroServidorDao.save(margemRegistroServidor);

		return margemRegistroServidor;
	}
}
