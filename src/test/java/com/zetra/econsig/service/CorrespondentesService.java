package com.zetra.econsig.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.CorrespondenteDao;
import com.zetra.econsig.dao.EmpresaCorrespondenteDao;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.EmpresaCorrespondente;

@Service
public class CorrespondentesService {

	private final SecureRandom random = new SecureRandom();

	@Autowired
	private CorrespondenteDao correspondenteDao;

	@Autowired
	private EmpresaCorrespondenteDao empresaCorrespondenteDao;

	public void criarCorrespondente(String corIdentificador, String cnpj) {
		Correspondente correspondente = correspondenteDao.findByCorIdentificador(corIdentificador);

		EmpresaCorrespondente empresaCorrespondente = new EmpresaCorrespondente();

		if (correspondente == null) {
			empresaCorrespondente.setEcoCodigo(String.valueOf(random.nextInt()));
			empresaCorrespondente
					.setEcoIdentificador(corIdentificador.replace(".", "").replace("/", "").replace("-", ""));
			empresaCorrespondente.setEcoNome("Correspondente Automacao");
			empresaCorrespondente.setEcoAtivo(Short.valueOf("1"));
			empresaCorrespondente.setEcoCnpj(cnpj);
			empresaCorrespondenteDao.save(empresaCorrespondente);

			correspondente = new Correspondente();
			correspondente.setCorCodigo(String.valueOf(random.nextInt()));
			correspondente.setCsaCodigo("3700808080808080808080808080A538");
			correspondente.setCorIdentificador(corIdentificador);
			correspondente.setCorNome("Correspondente Automacao");
			correspondente.setCorAtivo(Short.valueOf("1"));
			correspondente.setCorCnpj(cnpj);
			correspondente.setEcoCodigo(empresaCorrespondente.getEcoCodigo());
			correspondenteDao.save(correspondente);
		}
	}

	public void alterarStatusCorrespondente(String corIdentificador, String status) {
		Correspondente correspondente = correspondenteDao.findByCorIdentificador(corIdentificador);

		correspondente.setCorAtivo(Short.valueOf(status));
		correspondenteDao.save(correspondente);

	}

	public String getCorAtivo(String corIdentificador) {
		return correspondenteDao.findByCorIdentificador(corIdentificador).getCorAtivo().toString();
	}
}
