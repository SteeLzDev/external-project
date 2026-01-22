package com.zetra.econsig.service.limiteoperacao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.RegraLimiteOperacao;
import com.zetra.econsig.persistence.entity.RegraLimiteOperacaoHome;
import com.zetra.econsig.persistence.query.regralimiteoperacao.ListaRegrasLimiteOperacaoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RegraLimiteOperacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.limiteoperacao.RegraLimiteOperacaoCache;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoRegraLimiteQuery;
import com.zetra.econsig.persistence.query.limiteoperacao.ListaRegraLimiteOperacaoQuery;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.Columns;

@Service
@Transactional
public class RegraLimiteOperacaoControllerBean implements RegraLimiteOperacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraLimiteOperacaoControllerBean.class);


    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Override
    public List<TransferObject> lstRegraLimiteOperacao(AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        try {
            ListaRegraLimiteOperacaoQuery query = new ListaRegraLimiteOperacaoQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);

        }
    }

    @Override
    public void validarLimiteOperacao(BigDecimal adeVlr, BigDecimal adeVlrLiquido, Integer adePrazo, Integer adeCarencia, String adePeriodicidade,
            String nseCodigo, String svcCodigo, String ncaCodigo, String csaCodigo, String corCodigo, String cnvCodVerba, String cnvCodVerbaRef,
            RegistroServidor registroServidor, List<String> adeCodigosRenegociacao, String acao, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        List<TransferObject> regrasLimiteOperacao = RegraLimiteOperacaoCache.getRegras();
        if (regrasLimiteOperacao != null && !regrasLimiteOperacao.isEmpty()) {
            String rseCodigo = registroServidor.getRseCodigo();
            String rseMatricula = registroServidor.getRseMatricula();
            String rseTipo = registroServidor.getRseTipo();
            BigDecimal salario = registroServidor.getRseSalario();
            BigDecimal margemFolha = null;
            Short tempoServico = null;
            Short idade = null;

            String estCodigo = null;
            String orgCodigo = registroServidor.getOrgCodigo();
            String sboCodigo = registroServidor.getSboCodigo();
            String uniCodigo = registroServidor.getUniCodigo();
            String crsCodigo = registroServidor.getCrsCodigo();
            String capCodigo = registroServidor.getCapCodigo();
            String prsCodigo = registroServidor.getPrsCodigo();
            String posCodigo = registroServidor.getPosCodigo();
            String srsCodigo = registroServidor.getSrsCodigo();
            String trsCodigo = registroServidor.getTrsCodigo();
            String vrsCodigo = registroServidor.getVrsCodigo();
            String funCodigo = ReservaMargemHelper.getFuncaoPorAcao(acao);

            try {
                // Pesquisa o órgão do servidor para obter o estabelecimento associado a ele
                Orgao orgao = OrgaoHome.findByPrimaryKey(orgCodigo);
                estCodigo = orgao.getEstabelecimento().getEstCodigo();

                // Pesquisa o servidor para obter a data de nascimento e calcular a idade
                Servidor servidor = ServidorHome.findByPrimaryKey(registroServidor.getServidor().getSerCodigo());
                Date serDataNasc = servidor.getSerDataNasc();
                if (serDataNasc != null) {
                    idade = (short) DateHelper.getAge(serDataNasc);
                }

                // Calcula o tempo de serviço com base na data de admissão do registro servidor
                Date rseDataAdmissao = registroServidor.getRseDataAdmissao();
                if (rseDataAdmissao != null) {
                    tempoServico = (short) DateHelper.getAge(rseDataAdmissao);
                }

                // Consulta a margem do servidor ao qual o serviço incide para determinar o valor da margem folha
                List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, adeVlr, svcCodigo, csaCodigo, null, true, false, true, adeCodigosRenegociacao, responsavel);
                if (margens != null && !margens.isEmpty()) {
                    margemFolha = margens.get(0).getMrsMargem();
                }
            } catch (FindException | ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            for (TransferObject regra : regrasLimiteOperacao) {
                if (regraNaoSeAplicaRegex(rseMatricula, Columns.RLO_PADRAO_MATRICULA, regra)) {
                    continue;
                }
                if (regraNaoSeAplicaRegex(rseTipo, Columns.RLO_PADRAO_CATEGORIA, regra)) {
                    continue;
                }
                if (regraNaoSeAplicaRegex(cnvCodVerba, Columns.RLO_PADRAO_VERBA, regra)) {
                    continue;
                }
                if (regraNaoSeAplicaRegex(cnvCodVerbaRef, Columns.RLO_PADRAO_VERBA_REF, regra)) {
                    continue;
                }

                if (regraNaoSeAplica(estCodigo, Columns.EST_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(orgCodigo, Columns.ORG_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(sboCodigo, Columns.SBO_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(uniCodigo, Columns.UNI_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(nseCodigo, Columns.NSE_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(svcCodigo, Columns.SVC_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(ncaCodigo, Columns.NCA_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(csaCodigo, Columns.CSA_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(corCodigo, Columns.COR_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(crsCodigo, Columns.CRS_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(capCodigo, Columns.CAP_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(prsCodigo, Columns.PRS_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(posCodigo, Columns.POS_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(srsCodigo, Columns.SRS_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(trsCodigo, Columns.TRS_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(vrsCodigo, Columns.VRS_CODIGO, regra)) {
                    continue;
                }
                if (regraNaoSeAplica(funCodigo, Columns.FUN_CODIGO, regra)) {
                    continue;
                }

                if (idade != null) {
                    Short faixaEtariaIni = regra.getAttribute(Columns.RLO_FAIXA_ETARIA_INI) != null ? (Short) regra.getAttribute(Columns.RLO_FAIXA_ETARIA_INI) : null;
                    Short faixaEtariaFim = regra.getAttribute(Columns.RLO_FAIXA_ETARIA_FIM) != null ? (Short) regra.getAttribute(Columns.RLO_FAIXA_ETARIA_FIM) : null;
                    if (faixaEtariaIni != null && idade.compareTo(faixaEtariaIni) < 0) {
                        continue;
                    }
                    if (faixaEtariaFim != null && idade.compareTo(faixaEtariaFim) > 0) {
                        continue;
                    }
                }
                if (tempoServico != null) {
                    Short faixaTempoServicoIni = regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_INI) != null ? (Short) regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_INI) : null;
                    Short faixaTempoServicoFim = regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_FIM) != null ? (Short) regra.getAttribute(Columns.RLO_FAIXA_TEMPO_SERVICO_FIM) : null;
                    if (faixaTempoServicoIni != null && tempoServico.compareTo(faixaTempoServicoIni) < 0) {
                        continue;
                    }
                    if (faixaTempoServicoFim != null && tempoServico.compareTo(faixaTempoServicoFim) > 0) {
                        continue;
                    }
                }
                if (salario != null) {
                    BigDecimal faixaSalarioIni = regra.getAttribute(Columns.RLO_FAIXA_SALARIO_INI) != null ? (BigDecimal) regra.getAttribute(Columns.RLO_FAIXA_SALARIO_INI) : null;
                    BigDecimal faixaSalarioFim = regra.getAttribute(Columns.RLO_FAIXA_SALARIO_FIM) != null ? (BigDecimal) regra.getAttribute(Columns.RLO_FAIXA_SALARIO_FIM) : null;
                    if (faixaSalarioIni != null && salario.compareTo(faixaSalarioIni) < 0) {
                        continue;
                    }
                    if (faixaSalarioFim != null && salario.compareTo(faixaSalarioFim) > 0) {
                        continue;
                    }
                }
                if (margemFolha != null) {
                    BigDecimal faixaMargemFolhaIni = regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_INI) != null ? (BigDecimal) regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_INI) : null;
                    BigDecimal faixaMargemFolhaFim = regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_FIM) != null ? (BigDecimal) regra.getAttribute(Columns.RLO_FAIXA_MARGEM_FOLHA_FIM) : null;
                    if (faixaMargemFolhaIni != null && margemFolha.compareTo(faixaMargemFolhaIni) < 0) {
                        continue;
                    }
                    if (faixaMargemFolhaFim != null && margemFolha.compareTo(faixaMargemFolhaFim) > 0) {
                        continue;
                    }
                }

                // Se chegou até aqui, então a regra deve ser aplicada.
                Short limiteQuantidade = regra.getAttribute(Columns.RLO_LIMITE_QUANTIDADE) != null ? (Short) regra.getAttribute(Columns.RLO_LIMITE_QUANTIDADE) : null;
                if (limiteQuantidade != null) {
                    if (limiteQuantidade == 0) {
                        throw gerarExcecao(regra, responsavel);
                    } else {
                        try {
                            ObtemTotalConsignacaoRegraLimiteQuery query = new ObtemTotalConsignacaoRegraLimiteQuery(rseCodigo, adeCodigosRenegociacao, regra);
                            int total = query.executarContador();
                            if (total >= limiteQuantidade) {
                                throw gerarExcecao(regra, responsavel);
                            }
                        } catch (HQueryException ex) {
                            LOG.error(ex.getMessage(), ex);
                            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                        }
                    }
                }
                Date limiteDataFim = regra.getAttribute(Columns.RLO_LIMITE_DATA_FIM_ADE) != null ? (Date) regra.getAttribute(Columns.RLO_LIMITE_DATA_FIM_ADE) : null;
                if (limiteDataFim != null && adePrazo != null) {
                    try {
                        Date adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, adePeriodicidade, responsavel);
                        Date adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, adePrazo, adePeriodicidade, responsavel);
                        if (adeAnoMesFim.compareTo(limiteDataFim) > 0) {
                            throw gerarExcecao(regra, responsavel);
                        }
                    } catch (PeriodoException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }
                Short limitePrazo = regra.getAttribute(Columns.RLO_LIMITE_PRAZO) != null ? (Short) regra.getAttribute(Columns.RLO_LIMITE_PRAZO) : null;
                if (limitePrazo != null && adePrazo != null && adePrazo > limitePrazo) {
                    throw gerarExcecao(regra, responsavel);
                }
                BigDecimal limiteValorParcela = regra.getAttribute(Columns.RLO_LIMITE_VALOR_PARCELA) != null ? (BigDecimal) regra.getAttribute(Columns.RLO_LIMITE_VALOR_PARCELA) : null;
                if (limiteValorParcela != null && adeVlr != null && adeVlr.compareTo(limiteValorParcela) > 0) {
                    throw gerarExcecao(regra, responsavel);
                }
                BigDecimal limiteValorLiberado = regra.getAttribute(Columns.RLO_LIMITE_VALOR_LIBERADO) != null ? (BigDecimal) regra.getAttribute(Columns.RLO_LIMITE_VALOR_LIBERADO) : null;
                if (limiteValorLiberado != null && adeVlrLiquido != null && adeVlrLiquido.compareTo(limiteValorLiberado) > 0) {
                    throw gerarExcecao(regra, responsavel);
                }
                BigDecimal limiteValorCapitalDevido = regra.getAttribute(Columns.RLO_LIMITE_CAPITAL_DEVIDO) != null ? (BigDecimal) regra.getAttribute(Columns.RLO_LIMITE_CAPITAL_DEVIDO) : null;
                if (limiteValorCapitalDevido != null && adeVlr != null) {
                    BigDecimal capitalDevido = adeVlr.multiply(new BigDecimal(adePrazo != null ? adePrazo : 1));
                    if (capitalDevido.compareTo(limiteValorCapitalDevido) > 0) {
                        throw gerarExcecao(regra, responsavel);
                    }
                }
            }
        }
    }

    private static boolean regraNaoSeAplica(String valorEnviado, String nomeCampo, TransferObject regra) {
        String valorEsperado = regra.getAttribute(nomeCampo) != null ? regra.getAttribute(nomeCampo).toString() : null;
        return (valorEnviado != null && valorEsperado != null && !valorEnviado.equals(valorEsperado)) || (valorEnviado == null && valorEsperado != null);
    }

    private static boolean regraNaoSeAplicaRegex(String valorEnviado, String nomeCampo, TransferObject regra) {
        String valorEsperado = regra.getAttribute(nomeCampo) != null ? regra.getAttribute(nomeCampo).toString() : null;
        return (valorEnviado != null && valorEsperado != null && !valorEnviado.matches(valorEsperado)) || (valorEnviado == null && valorEsperado != null);
    }

    private static RegraLimiteOperacaoControllerException gerarExcecao(TransferObject regra, AcessoSistema responsavel) {
        // Constrói a exceção com a chave de application resources de modo que tenha código de erro para API ou Lote
        RegraLimiteOperacaoControllerException excecao = new RegraLimiteOperacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.ha.regra.limite.que.impede.operacao", responsavel);

        // Recupera mensagem de erro para lançar exceção caso a operação não possa continuar
        String mensagemErro = regra.getAttribute(Columns.RLO_MENSAGEM_ERRO) != null ? regra.getAttribute(Columns.RLO_MENSAGEM_ERRO).toString() : null;
        if (!TextHelper.isNull(mensagemErro)) {
            // Tenta interpretar a mensagem como sendo uma chave de texto sistema
            try {
                String textoResources = ApplicationResourcesHelper.getMessage(mensagemErro, responsavel);
                mensagemErro = textoResources;
            } catch (RuntimeException ex) {
                // Não existe como uma chave de texto, então o campo está preenchido com o texto a ser exibido ao usuário
            }
            if (!TextHelper.isNull(mensagemErro)) {
                excecao.setMessage(mensagemErro);
            }
        }

        return excecao;
    }

    @Override
    public RegraLimiteOperacao findRegraByPrimaryKey(String rloCodigo, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        try {
            return RegraLimiteOperacaoHome.findByPrimaryKey(rloCodigo);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findRegra(String rloCodigo, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        try {
            ListaRegrasLimiteOperacaoQuery query = new ListaRegrasLimiteOperacaoQuery();
            query.rloCodigo = rloCodigo;
            query.csaCodigo = null;
            return query.executarDTO().get(0);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public RegraLimiteOperacao createRegra(RegraLimiteOperacao regraLimiteOperacao, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        try {
            RegraLimiteOperacaoHome.create(regraLimiteOperacao);
            final LogDelegate log = new LogDelegate(responsavel, Log.REGRA_LIMITE_OPERACAO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setUsuario(responsavel.getUsuCodigo());
            log.add(ApplicationResourcesHelper.getMessage("rotulo.regra.limite.operacao.regra.criada.sucesso", responsavel));
            log.write();
        } catch (final LogControllerException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return regraLimiteOperacao;
    }

    @Override
    public void removeRegra(String rloCodigo, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        try {
            RegraLimiteOperacao bean = new RegraLimiteOperacao();
            bean.setRloCodigo(rloCodigo);
            AbstractEntityHome.remove(bean);
            final LogDelegate log = new LogDelegate(responsavel, Log.REGRA_LIMITE_OPERACAO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setUsuario(responsavel.getUsuCodigo());
            log.add(ApplicationResourcesHelper.getMessage("rotulo.regra.limite.operacao.regra.excluida.sucesso", responsavel));
            log.write();
        } catch (final LogControllerException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void updateRegra(RegraLimiteOperacao regraLimiteOperacao, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        try {
            AbstractEntityHome.update(regraLimiteOperacao);
            final LogDelegate log = new LogDelegate(responsavel, Log.REGRA_LIMITE_OPERACAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setUsuario(responsavel.getUsuCodigo());
            log.add(ApplicationResourcesHelper.getMessage("rotulo.regra.limite.operacao.regra.editada.sucesso", responsavel));
            log.write();
        } catch (final LogControllerException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstRegrasLimiteOperacaoFilter(String csaCodigo, int count, int offset, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        ListaRegrasLimiteOperacaoQuery query = new ListaRegrasLimiteOperacaoQuery();
        try {
            query.csaCodigo = csaCodigo;

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    public int countRegrasLimiteOperacaoFilter(String csaCodigo, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException {
        ListaRegrasLimiteOperacaoQuery query = new ListaRegrasLimiteOperacaoQuery();
        try {
            query.csaCodigo = csaCodigo;
            query.count = true;
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraLimiteOperacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

}
