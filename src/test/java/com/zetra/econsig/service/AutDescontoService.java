package com.zetra.econsig.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.AnexoAutorizacaoDescontoDao;
import com.zetra.econsig.dao.AutDescontoDao;
import com.zetra.econsig.dao.CoeficienteDescontoDao;
import com.zetra.econsig.dao.ConvenioDao;
import com.zetra.econsig.dao.DadosAutorizacaoDescontoDao;
import com.zetra.econsig.dao.HistoricoMargemRseDao;
import com.zetra.econsig.dao.HistoricoStatusAdeDao;
import com.zetra.econsig.dao.OcorrenciaAutorizacaoDao;
import com.zetra.econsig.dao.OcorrenciaDadosAdeDao;
import com.zetra.econsig.dao.ParcelaDescontoDao;
import com.zetra.econsig.dao.ParcelaDescontoPeriodoDao;
import com.zetra.econsig.dao.RelacionamentoAutorizacaoDao;
import com.zetra.econsig.dao.SaldoDevedorDao;
import com.zetra.econsig.dao.VerbaConvenioDao;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.SaldoDevedor;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class AutDescontoService {

	@Autowired
	private AutDescontoDao autDescontoDao;

	@Autowired
	private ParcelaDescontoDao parcelaDescontoDao;

	@Autowired
	private ParcelaDescontoPeriodoDao parcelaDescontoPeriodoDao;

	@Autowired
	private AnexoAutorizacaoDescontoDao anexoAutorizacaoDescontoDao;

	@Autowired
	private DadosAutorizacaoDescontoDao dadosAutorizacaoDescontoDao;

	@Autowired
	private SaldoDevedorDao saldoDevedorDao;

	@Autowired
	private ConvenioDao convenioDao;

	@Autowired
	private VerbaConvenioDao verbaConvenioDao;

	@Autowired
	private HistoricoStatusAdeDao historicoStatusAdeDao;

	@Autowired
    private OcorrenciaAutorizacaoDao ocorrenciaAutorizacaoDao;

	@Autowired
	private OcorrenciaDadosAdeDao ocorrenciaDadosAdeDao;

    @Autowired
    private CoeficienteDescontoDao coeficienteDescontoDao;
    
    @Autowired
    private HistoricoMargemRseDao historicoMargemRseDao;
    
    @Autowired
    RelacionamentoAutorizacaoDao relacionamentoAutorizacaoDao;

    public void incluirParcelaDesconto(String adeCodigo, String spdCodigo, BigDecimal prdVlrPrevisto,
			String prdNumero) {
		incluirParcelaDesconto(adeCodigo, spdCodigo, prdVlrPrevisto, null, prdNumero);
	}

	public void incluirParcelaDesconto(String adeCodigo, String spdCodigo, BigDecimal prdVlrPrevisto, BigDecimal prdVlrRealizado,
			String prdNumero) {
		ParcelaDesconto parcelaDesconto = parcelaDescontoDao.findByAdeCodigo(adeCodigo);
		if (parcelaDesconto == null) {
			parcelaDesconto = new ParcelaDesconto();
			parcelaDesconto.setAdeCodigo(adeCodigo);
			parcelaDesconto.setPrdDataDesconto(new Date(new java.util.Date().getTime()));
		}
		parcelaDesconto.setPrdNumero(Short.valueOf(prdNumero));
		parcelaDesconto.setSpdCodigo(spdCodigo);
		parcelaDesconto.setPrdVlrPrevisto(prdVlrPrevisto);
		parcelaDesconto.setPrdVlrRealizado(prdVlrRealizado);
		parcelaDescontoDao.save(parcelaDesconto);

	}

	public void excluirParcelaDesconto(String adeCodigo) {
		final ParcelaDesconto parcelaDesconto = parcelaDescontoDao.findByAdeCodigo(adeCodigo);
		parcelaDescontoDao.deleteById(String.valueOf(parcelaDesconto.getPrdCodigo()));
	}

	public void incluirParcelaDescontoPeriodo(String adeCodigo, String spdCodigo, BigDecimal prdVlrPrevisto,
			String prdNumero) {
		ParcelaDescontoPeriodo parcelaDescontoPeriodo = parcelaDescontoPeriodoDao.findByAdeCodigo(adeCodigo);
		if (parcelaDescontoPeriodo == null) {
			parcelaDescontoPeriodo = new ParcelaDescontoPeriodo();
			parcelaDescontoPeriodo.setAdeCodigo(adeCodigo);
			parcelaDescontoPeriodo.setPrdDataDesconto(new Date(new java.util.Date().getTime()));
		}
		parcelaDescontoPeriodo.setPrdNumero(Short.valueOf(prdNumero));
		parcelaDescontoPeriodo.setSpdCodigo(spdCodigo);
		parcelaDescontoPeriodo.setPrdVlrPrevisto(prdVlrPrevisto);
		parcelaDescontoPeriodo.setPrdVlrRealizado(null);
		parcelaDescontoPeriodoDao.save(parcelaDescontoPeriodo);
	}

	public List<AutDesconto> getAdes(String rseCodigo, List<String> sadCodigos) {
		if (!TextHelper.isNull(rseCodigo) && (sadCodigos != null) && !sadCodigos.isEmpty()) {
			return autDescontoDao.getAdesByRseCodigoAndSadCodigo(rseCodigo, sadCodigos);
		} else if (!TextHelper.isNull(rseCodigo)) {
		    return autDescontoDao.findByRseCodigo(rseCodigo);
		} else {
			return autDescontoDao.getAdesBySadCodigo(sadCodigos);
		}
	}
	
	public List<AutDesconto> getAdes(String rseCodigo) {
		return autDescontoDao.findByRseCodigo(rseCodigo);
	}

	public List<AutDesconto> getAdes(String rseCodigo, List<String> sadCodigos, String svcCodigoEmprestimo) {
		return autDescontoDao.getAdesByRseCodigoAndSadCodigoAndSvcCodigo(rseCodigo, sadCodigos, svcCodigoEmprestimo);
	}

	public List<Object[]> getAdesLimite(String rseCodigo, String cnvCodigo) {
	    return autDescontoDao.getAdesLimiteByRseCodigoAndCnvCodigo(rseCodigo, cnvCodigo);
	}

	public String getSadCodigo(String adeNumero) {
		return autDescontoDao.findByAdeNumero(Long.valueOf(adeNumero)).getSadCodigo();
	}

	public AutDesconto getAde(String adeNumero) {
		return autDescontoDao.findByAdeNumero(Long.valueOf(adeNumero));
	}

	public AutDesconto getAdeByAdeCodigo(String adeCodigo) {
		return autDescontoDao.findByAdeCodigo(adeCodigo);
	}

	public List<AnexoAutorizacaoDesconto> getAnexoAutDesconto(String adeNumero) {
		final String adeCodigo = autDescontoDao.findByAdeNumero(Long.valueOf(adeNumero)).getAdeCodigo();

		return anexoAutorizacaoDescontoDao.findByAdeCodigo(adeCodigo);
	}

	public DadosAutorizacaoDesconto getDadosAutDesconto(String adeNumero, String tdaCodigo) {
		final String adeCodigo = autDescontoDao.findByAdeNumero(Long.valueOf(adeNumero)).getAdeCodigo();

		return dadosAutorizacaoDescontoDao.findByAdeCodigoAndTdaCodigo(adeCodigo, tdaCodigo);
	}

	public void alterarDadosAutDesconto(String adeNumero, String tdaCodigo, String valor) {
		DadosAutorizacaoDesconto dadosAutDesconto = getDadosAutDesconto(adeNumero, tdaCodigo);

		if (dadosAutDesconto == null) {
			dadosAutDesconto = new DadosAutorizacaoDesconto();
			dadosAutDesconto.setAdeCodigo(autDescontoDao.findByAdeNumero(Long.valueOf(adeNumero)).getAdeCodigo());
			dadosAutDesconto.setTdaCodigo(tdaCodigo);
		}
		dadosAutDesconto.setDadValor(valor);
		dadosAutorizacaoDescontoDao.save(dadosAutDesconto);
	}

	public AutDesconto inserirAutDesconto(String adeCodigo, String sadCodigo, String rseCodigo, String cnvCodigo, String usuCodigoResponsavel, float adeVlr, long adeNumero, int adePrazo, short adeIncMargem) {
		return this.inserirAutDesconto(adeCodigo, sadCodigo, rseCodigo, cnvCodigo, usuCodigoResponsavel, adeVlr, adeNumero, adePrazo, adeIncMargem, 0, "", null, null);
	}

	public AutDesconto inserirAutDesconto(String adeCodigo, String sadCodigo, String rseCodigo, String cnvCodigo, String usuCodigoResponsavel, float adeVlr, long adeNumero, int adePrazo, short adeIncMargem, Short adeIntFolha, String adeTipoVlr) {
		return this.inserirAutDesconto(adeCodigo, sadCodigo, rseCodigo, cnvCodigo, usuCodigoResponsavel, adeVlr, adeNumero, adePrazo, adeIncMargem, 0, "", adeIntFolha, adeTipoVlr);
	}

	public AutDesconto inserirAutDesconto(String adeCodigo, String sadCodigo, String rseCodigo, String cnvCodigo, String usuCodigoResponsavel, float adeVlr, long adeNumero, int adePrazo,
			short adeIncMargem, int adeCarencia, String adeIdentificador, Short adeIntFolha, String adeTipoVlr) {
	    try {
	        final VerbaConvenio vco = verbaConvenioDao.findByCnvCodigo(cnvCodigo);
	        final Optional<Convenio> cnv = convenioDao.findById(cnvCodigo);
	        final String orgCodigo = cnv.get().getOrgCodigo();

	        final AcessoSistema responsavel = AcessoSistema.recuperaAcessoSistema(usuCodigoResponsavel, "127.0.0.1", null);
	        final String adePeriodicidade = CodedValues.PERIODICIDADE_FOLHA_MENSAL;
	        final Date adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, adePeriodicidade, responsavel);
	        final Date adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, adePrazo, adePeriodicidade, responsavel);

	        final AutDesconto ade = new AutDesconto();
	        ade.setUsuCodigo(usuCodigoResponsavel);
	        ade.setAdeCodigo(adeCodigo);
	        ade.setSadCodigo(sadCodigo);
	        ade.setRseCodigo(rseCodigo);
	        ade.setVcoCodigo(vco.getVcoCodigo());
	        ade.setAdeVlr(BigDecimal.valueOf(adeVlr));
	        ade.setAdeNumero(adeNumero);
	        ade.setAdeData(DateHelper.getSystemDatetime());
	        ade.setAdeAnoMesIni(adeAnoMesIni);
	        ade.setAdeAnoMesFim(adeAnoMesFim);
	        ade.setAdePaga("N");
	        ade.setAdePeriodicidade(adePeriodicidade);
	        ade.setAdeExportacao("S");
	        ade.setAdePrazo(adePrazo);
	        ade.setAdeIncMargem(adeIncMargem);
	        ade.setAdeCarencia(adeCarencia);
	        ade.setAdeIdentificador(adeIdentificador);
	        if(adeIntFolha != null) {
	        	ade.setAdeIntFolha(adeIntFolha);
	        }
	        if(adeTipoVlr != null) {
	        	ade.setAdeTipoVlr(adeTipoVlr);
	        }

	        return autDescontoDao.save(ade);

	    } catch (final Exception ex) {
	        log.error(ex.getMessage(), ex);
	        return null;
	    }
	}

	public AutDesconto alterarAutDesconto(String rseCodigo, String adePrazo, String adePrdPagas) {
		final AutDesconto ade = autDescontoDao.findByRseCodigo(rseCodigo).get(0);
		ade.setAdePrazo(Integer.valueOf(adePrazo));
		ade.setAdePrdPagas(Integer.valueOf(adePrdPagas));
		autDescontoDao.save(ade);

		return ade;
	}

	public AutDesconto alterarAutDescontoPorNumeroAde(Long adeNumero, String adePrazo, String adePrdPagas) {
		final AutDesconto ade = autDescontoDao.findByAdeNumero(adeNumero);
		ade.setAdePrazo(Integer.valueOf(adePrazo));
		ade.setAdePrdPagas(Integer.valueOf(adePrdPagas));
		autDescontoDao.save(ade);

		return ade;
	}

	public AutDesconto alterarAutDescontoPorNumeroAde(Long adeNumero, String corCodigo) {
        final AutDesconto ade = autDescontoDao.findByAdeNumero(adeNumero);
        ade.setCorCodigo(corCodigo);
        autDescontoDao.save(ade);

        return ade;
    }

	public SaldoDevedor getSaldoDevedor(String adeNumero) {
		final String adeCodigo = autDescontoDao.findByAdeNumero(Long.valueOf(adeNumero)).getAdeCodigo();

		return saldoDevedorDao.findByAdeCodigo(adeCodigo);
	}

    public void deleteAutDesconto(String adeCodigo) {
        final AutDesconto ade = autDescontoDao.findByAdeCodigo(adeCodigo);
        if (ade != null) {
            coeficienteDescontoDao.removeByAdeCodigo(ade.getAdeCodigo());
            ocorrenciaDadosAdeDao.removeByAdeCodigo(ade.getAdeCodigo());
            dadosAutorizacaoDescontoDao.removeByAdeCodigo(ade.getAdeCodigo());
            historicoMargemRseDao.removeByRseCodigo(ade.getRseCodigo());
            ocorrenciaAutorizacaoDao.removeByAdeCodigo(ade.getAdeCodigo());
            historicoStatusAdeDao.removeByAdeCodigo(ade.getAdeCodigo());
            autDescontoDao.delete(ade);
        }
    }

    public void deleteAutDescontoByAdeNumero(long adeNumero) {
        final AutDesconto ade = autDescontoDao.findByAdeNumero(adeNumero);
        if (ade != null) {
            coeficienteDescontoDao.removeByAdeCodigo(ade.getAdeCodigo());
            ocorrenciaDadosAdeDao.removeByAdeCodigo(ade.getAdeCodigo());
            dadosAutorizacaoDescontoDao.removeByAdeCodigo(ade.getAdeCodigo());
            historicoMargemRseDao.removeByRseCodigo(ade.getRseCodigo());
            ocorrenciaAutorizacaoDao.removeByAdeCodigo(ade.getAdeCodigo());
            historicoStatusAdeDao.removeByAdeCodigo(ade.getAdeCodigo());
            relacionamentoAutorizacaoDao.removeByAdeCodigoOrigem(ade.getAdeCodigo());
            saldoDevedorDao.removeByAdeCodigo(ade.getAdeCodigo());
            autDescontoDao.delete(ade);
        }
    }
    
    public boolean gerarLoteTodasAdesAtivas(String nomeArqLote) {
        try {
            // Garante que o índice das ADEs serão únicos
            int i = 0;
            final List<AutDesconto> ades = autDescontoDao.findAll();
            for (final AutDesconto ade : ades) {
                final String adeIndice = StringUtils.leftPad(String.valueOf(++i), 5, "0");
                ade.setAdeIndice(adeIndice);
                autDescontoDao.save(ade);
            }

            final List<String> linhasLote = autDescontoDao.getLinhasLoteTodasAdesAtivas();
            FileHelper.saveStringListToFile(linhasLote, nomeArqLote);
            return true;
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return false;
        }
    }
}