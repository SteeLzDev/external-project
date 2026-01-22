package com.zetra.econsig.job.process;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaOfertaAutomaticaLeilao</p>
 * <p>Description: Classe que dispara processo para cadastrar oferta automática
 * de proposta de leilão</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaOfertaAutomaticaLeilao extends Processo {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaOfertaAutomaticaLeilao.class);

    private final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
    private final String adeCodigo;
    private final Date dataProposta;

    public ProcessaOfertaAutomaticaLeilao(String adeCodigo, Date dataProposta) {
        this.adeCodigo = adeCodigo;
        this.dataProposta = dataProposta;
    }

    @Override
    protected void executar() {
        try {
            LOG.debug(adeCodigo);

            // Lista as ofertas automáticas cadastradas para esta consignação
            LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
            List<TransferObject> lista = leilaoSolicitacaoController.lstPropostaLeilaoOfertaAutomatica(adeCodigo, responsavel);

            // Cadastra a primeira proposta automática da fila, caso exista
            if (lista != null && !lista.isEmpty()) {
                for (TransferObject oferta : lista) {
                    String csaCodigo = (String) oferta.getAttribute(Columns.CSA_CODIGO);
                    String svcCodigo = (String) oferta.getAttribute(Columns.SVC_CODIGO);
                    BigDecimal decremento = (BigDecimal) oferta.getAttribute(Columns.PLS_OFERTA_AUT_DECREMENTO);
                    BigDecimal taxaMinima = (BigDecimal) oferta.getAttribute(Columns.PLS_OFERTA_AUT_TAXA_MIN);
                    Date dataValidade = (Date) oferta.getAttribute(Columns.SOA_DATA_VALIDADE);
                    String email = (String) oferta.getAttribute(Columns.PLS_OFERTA_AUT_EMAIL);
                    String txtContatoCsa = (String) oferta.getAttribute(Columns.PLS_TXT_CONTATO_CSA);
                    String rseCodigo = (String) oferta.getAttribute(Columns.RSE_CODIGO);

                    // DESENV-14442 - Estamos adicionando 5 minutos na data de validade para dar mais tempo ao processo de oferta automática
                    if (DateHelper.addSeconds(dataValidade, 300).before(DateHelper.getSystemDatetime())) {
                        LOG.error("Leilão expirado!");
                        break;
                    }

                    // Obtém a melhor taxa do momento
                    BigDecimal melhorTaxa = leilaoSolicitacaoController.obterMelhorTaxaLeilao(adeCodigo, responsavel);
                    if (melhorTaxa == null) {
                        LOG.error("Leilão sem melhor taxa!");
                        break;
                    }
                    BigDecimal taxa = melhorTaxa.subtract(decremento);

                    if (taxa.compareTo(taxaMinima) < 0) {
                        // Envia e-mail notificação que não pode fazer oferta automática pois
                        // o limite mínimo foi alcançado
                        if (!TextHelper.isNull(email)) {
                            try {
                                EnviaEmailHelper.enviarEmailCsaOfertaAutLimiteMinSuperado(adeCodigo, melhorTaxa, taxaMinima, decremento, dataValidade, email, responsavel);
                            } catch (ViewHelperException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }
                        continue;
                    }

                    try {
                        // Se o cadastro foi efetuado com sucesso, reexecuta o processo, para
                        // que as propostas automáticas de outras entidades sejam executadas recursivamente
                        leilaoSolicitacaoController.informarPropostaLeilaoSolicitacao(adeCodigo, svcCodigo, csaCodigo, taxa, taxaMinima, decremento, email, txtContatoCsa, true, rseCodigo, dataProposta, false, responsavel);
                        executar();
                        break;
                    } catch (LeilaoSolicitacaoControllerException ex) {
                        // Se o cadastro não foi efetuado com sucesso, pega o próximo da lista
                        // e tenta realizar o cadastro da proposta
                        continue;
                    }
                }
            }
        } catch (LeilaoSolicitacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
