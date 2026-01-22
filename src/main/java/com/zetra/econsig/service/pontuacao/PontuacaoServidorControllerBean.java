package com.zetra.econsig.service.pontuacao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.PontuacaoServidorDAO;
import com.zetra.econsig.persistence.query.pontuacao.ListaConsignatariasComParamPontuacaoRseQuery;
import com.zetra.econsig.persistence.query.pontuacao.ListaTipoParamPontuacaoRsePorCsaQuery;
import com.zetra.econsig.persistence.query.pontuacao.ObtemPontuacaoRseCsaQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoPontuacaoEnum;


@Service
@Transactional
public class PontuacaoServidorControllerBean implements PontuacaoServidorController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PontuacaoServidorControllerBean.class);

    @Override
    public void calcularPontuacao(String rseCodigo, AcessoSistema responsavel) throws ZetraException {
        if (!TextHelper.isNull(rseCodigo)) {
            calcularPontuacao("RSE", List.of(rseCodigo), responsavel);
        }
    }

    @Override
    public void calcularPontuacao(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ZetraException {
        // Recalcula a pontuação de leilão dos servidores, caso tenha módulo de leilão via simulador
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            calcularPontuacaoLeilao(tipoEntidade, entCodigos, responsavel);
        }

        // Recalcula a pontuação das regras específicas de consignatárias, caso existam
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITAR_MODULO_PERFIL_CONSIGNADO, CodedValues.TPC_SIM, responsavel)) {
            calcularPontuacaoCsa(tipoEntidade, entCodigos, responsavel);
        }
    }

    /**
     * Calcula a pontuação de regras de consignatárias, caso existam
     * @param tipoEntidade
     * @param entCodigos
     * @param responsavel
     * @throws ZetraException
     */
    private void calcularPontuacaoCsa(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ZetraException {
        try {
            final List<String> csaCodigos = obterConsignatariasParamPontuacao(responsavel);
            if (csaCodigos != null && !csaCodigos.isEmpty()) {
                final PontuacaoServidorDAO pontuacaoDao = DAOFactory.getDAOFactory().getPontuacaoServidorDAO();

                for (String csaCodigo : csaCodigos) {
                    // Zera a pontuação dos servidores para esta CSA
                    pontuacaoDao.zerarPontuacaoRseCsa(csaCodigo, tipoEntidade, entCodigos, responsavel);

                    // Busca quais tipos de dados esta CSA usa e chamar o cálculo para cada um
                    final List<String> tpoCodigos = obterTipoParamPontuacaoPorCsa(csaCodigo, responsavel);
                    for (String tpoCodigo : tpoCodigos) {
                        if (TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosSuspensos(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS_GERAL.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosSuspensosGeral(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosConcluidos(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS_GERAL.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosConcluidosGeral(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_CONTRATOS_LIQUIDADOS.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosLiquidados(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_CONTRATOS_LIQUIDADOS_GERAL.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosLiquidadosGeral(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosAfetadosJudicial(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL_GERAL.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeContratosAfetadosJudicialGeral(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.PORCENTAGEM_MARGEM_UTILIZADA.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaPercentualMargemUtilizada(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaPercentualInadimplencia(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA_GERAL.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaPercentualInadimplenciaGeral(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.PONTOS_PERDIDOS_POR_LEILAO_NAO_CONCRETIZADO.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeLeilaoNaoConcretizado(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.FAIXA_ETARIA_EM_ANOS.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaFaixaEtariaEmAnos(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.FAIXA_TEMPO_SERVICO_EM_MESES.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaTempoServicoEmMeses(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.FAIXA_SALARIAL.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaFaixaSalarial(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.FAIXA_VALOR_MARGEM.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaFaixaValorMargem(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.QTDE_COLABORADORES_CSE.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaQtdeColaboradoresCse(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.MEDIA_SALARIAL_CSE.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaMediaSalarialCse(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.PORCENTAGEM_TURNOVER_CSE.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaPercentualTurnoverCse(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.MEDIA_MARGEM_LIVRE_CSE.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaMediaMargemLivreCse(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else if (TipoPontuacaoEnum.PORCENTAGEM_DESCONTOS_CSA.getCodigo().equals(tpoCodigo)) {
                            pontuacaoDao.calcularPontuacaoRseCsaPercentualDescontos(csaCodigo, tipoEntidade, entCodigos, responsavel);

                        } else {
                            LOG.warn("Tipo de pontuação \"" + tpoCodigo + "\" não implementado para cálculo de pontuação por CSA.");
                        }
                    }
                }
            }
        } catch (DAOException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<String> obterConsignatariasParamPontuacao(AcessoSistema responsavel) throws ZetraException {
        try {
            ListaConsignatariasComParamPontuacaoRseQuery query = new ListaConsignatariasComParamPontuacaoRseQuery();
            return query.executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<String> obterTipoParamPontuacaoPorCsa(String csaCodigo, AcessoSistema responsavel) throws ZetraException {
        try {
            ListaTipoParamPontuacaoRsePorCsaQuery query = new ListaTipoParamPontuacaoRsePorCsaQuery(csaCodigo);
            return query.executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Calcula a pontuação de leilão para todos os servidores
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     * TODO: Receber tipo e código das entidades
     */
    private void calcularPontuacaoLeilao(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final PontuacaoServidorDAO pontuacaoDao = DAOFactory.getDAOFactory().getPontuacaoServidorDAO();
            pontuacaoDao.calcularPontuacaoLeilao(tipoEntidade, entCodigos, responsavel);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String consultarPontuacaoCsa(String rseCodigo, AcessoSistema responsavel) throws ZetraException {
        try {
            if (responsavel.isCsaCor()) {
                final ObtemPontuacaoRseCsaQuery query = new ObtemPontuacaoRseCsaQuery(rseCodigo, responsavel.getCsaCodigo());
                final List<String> pontuacaoList = query.executarLista();
                if (pontuacaoList != null && !pontuacaoList.isEmpty()) {
                    return pontuacaoList.get(0);
                }
            }
            return null;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
