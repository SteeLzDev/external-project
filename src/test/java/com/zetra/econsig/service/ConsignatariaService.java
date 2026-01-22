package com.zetra.econsig.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.ConsignatariaDao;
import com.zetra.econsig.dao.OcorrenciaConsignatariaDao;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.OcorrenciaConsignataria;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;

@Service
public class ConsignatariaService {

	@Autowired
	private ConsignatariaDao consignatariaDao;

	@Autowired
	private OcorrenciaConsignatariaDao ocorrenciaConsignatariaDao;

	public Consignataria criarConsignataria(String csaCodigo, String csaNome, String csaIdentificador) {
	    Consignataria csa = new Consignataria();
	    csa.setCsaCodigo(csaCodigo);
	    csa.setCsaIdentificador(csaIdentificador);
	    csa.setCsaNome(csaNome);
	    csa.setCsaNomeAbrev(csaNome);
	    csa.setCsaAtivo(CodedValues.STS_ATIVO);
	    csa.setNcaCodigo(NaturezaConsignatariaEnum.INSTITUICAO_FINANCEIRA.getCodigo());
	    // Campos obrigatórios
	    csa.setCsaEmail("testes.interno@nostrum.com.br");
        csa.setCsaPermiteIncluirAde("S");
	    csa.setCsaPermiteApi("N");
	    csa.setCsaConsultaMargemSemSenha("N");

	    return consignatariaDao.save(csa);
	}

	public Consignataria criarConsignataria(String csaCodigo, String csaNome, String csaIdentificador, List<String> ipsAcessoValido , List<String> ddnsAcessoValido) {
        Consignataria csa = new Consignataria();
        csa.setCsaCodigo(csaCodigo);
        csa.setCsaIdentificador(csaIdentificador);
        csa.setCsaNome(csaNome);
        csa.setCsaNomeAbrev(csaNome);
        csa.setCsaAtivo(CodedValues.STS_ATIVO);
        csa.setNcaCodigo(NaturezaConsignatariaEnum.INSTITUICAO_FINANCEIRA.getCodigo());
        // Campos obrigatórios
        csa.setCsaEmail("testes.interno@nostrum.com.br");
        csa.setCsaPermiteIncluirAde("S");
        csa.setCsaPermiteApi("N");
        csa.setCsaConsultaMargemSemSenha("N");

        if (ipsAcessoValido != null && !ipsAcessoValido.isEmpty()) {
            csa.setCsaIpAcesso(ipsAcessoValido.stream().collect(Collectors.joining(";")));
        }

        if (ddnsAcessoValido != null && !ddnsAcessoValido.isEmpty()) {
            csa.setCsaDdnsAcesso(ddnsAcessoValido.stream().collect(Collectors.joining(";")));
        }

        return consignatariaDao.save(csa);
    }

	public Consignataria criarConsignatariaCasoNaoExista(String csaCodigo, String csaNome, String csaIdentificador) {
	    Consignataria csa = getConsignataria(csaCodigo);
	    if (csa == null) {
	        csa = criarConsignataria(csaCodigo, csaNome, csaIdentificador);
	    }
	    return csa;
	}

	public Consignataria getConsignataria(String csaCodigo) {
		return consignatariaDao.findByCsaCodigo(csaCodigo);
	}

	public Consignataria obterConsignatariaPorIdentificador(String csaIdentificador) {
	    return consignatariaDao.findByCsaIdentificador(csaIdentificador);
	}

	public void alterarStatusConsignataria(String csaCodigo, String status) {
		Consignataria consignataria = getConsignataria(csaCodigo);

		consignataria.setCsaAtivo(Short.valueOf(status));
		consignatariaDao.save(consignataria);
	}

	public void alterarCsaExigeEnderecoAcesso(String csaCodigo, String status) {
		Consignataria consignataria = getConsignataria(csaCodigo);

		consignataria.setCsaExigeEnderecoAcesso(status);
		consignatariaDao.save(consignataria);
	}

	public void alterarCsaPermiteIncluirAde(String csaCodigo, String status) {
		Consignataria consignataria = getConsignataria(csaCodigo);

		consignataria.setCsaPermiteIncluirAde(status);
		consignatariaDao.save(consignataria);
	}

	public void alterarCsaIpAcesso(String csaCodigo, String ip) {
		Consignataria consignataria = getConsignataria(csaCodigo);

		consignataria.setCsaIpAcesso(ip);
		consignatariaDao.save(consignataria);
	}

	public void updateConsignataria(Consignataria csa) {
		if (csa != null) {
			consignatariaDao.save(csa);
		}
	}

	public void criarOcorrenciaConsignataria(String occCodigo, String csaCodigo, String tocCodigo, String usuCodigo) {
		OcorrenciaConsignataria oca = new OcorrenciaConsignataria();

		oca.setOccCodigo(occCodigo);
		oca.setCsaCodigo(csaCodigo);
		oca.setUsuCodigo(usuCodigo);
		oca.setTocCodigo(tocCodigo);

		ocorrenciaConsignatariaDao.save(oca);
	}

    public void removerConsignataria(String csaCodigo) {
        consignatariaDao.deleteById(csaCodigo);
    }
}
