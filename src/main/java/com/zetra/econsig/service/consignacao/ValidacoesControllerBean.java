package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RegraLimiteOperacaoControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.financeiro.ValidacaoMetodoBrasileiro;
import com.zetra.econsig.helper.financeiro.ValidacaoMetodoGenerico;
import com.zetra.econsig.helper.financeiro.ValidacaoMetodoIndiano;
import com.zetra.econsig.helper.financeiro.ValidacaoMetodoMexicano;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.BloqueioPostoCsaSvc;
import com.zetra.econsig.persistence.entity.BloqueioPostoCsaSvcHome;
import com.zetra.econsig.persistence.entity.BloqueioRseFun;
import com.zetra.econsig.persistence.entity.BloqueioRseFunHome;
import com.zetra.econsig.persistence.entity.BloqueioRseFunId;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.FuncaoHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignanteHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignataria;
import com.zetra.econsig.persistence.entity.ParamSvcConsignatariaHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.TipoGrupoSvc;
import com.zetra.econsig.persistence.entity.TipoGrupoSvcHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.entity.VinculoConsignataria;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidorHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoEmDuplicidadeQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoLimitadorNovosAdesQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoLimiteQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseCnvQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPrdRejeitadaQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRseSvcQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemDataUltimoContratoLiquidadoPorServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemDataValidacaoCancelamentoConsignacaoQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacoesPorRseNseQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignatariaPorServidorQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalContratosPorGrupoSvcQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaComAdeSerQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioVinculoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.convenio.ListaServicoLimitePermitidoQuery;
import com.zetra.econsig.persistence.query.convenio.ObtemConvenioRelCadTaxasQuery;
import com.zetra.econsig.persistence.query.convenio.ObtemDadosConvenioQuery;
import com.zetra.econsig.persistence.query.parametro.ListaContratoLiberaOperacaoByDestinoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaContratoLiberaOperacaoByOrigemQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamCnvRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamCsaQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamCsaRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamNseRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcRseQuery;
import com.zetra.econsig.persistence.query.parametro.ListaParamSvcServidorSobrepoeQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.servico.ListaRelacionamentosServicoQuery;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.cartaocredito.ValidadorCartaoCreditoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.limiteoperacao.RegraLimiteOperacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoRegistroServidorEnum;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ValidacoesControllerBean</p>
 * <p>Description: Classe abstrata, herdada pelo AutorizacaoControllerBean onde
 * ficam as lógicas de validações para inclusões/alterações de contratos.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Transactional
public abstract class ValidacoesControllerBean {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidacoesControllerBean.class);

    @Autowired
    private ValidadorCartaoCreditoController validadorCartaoCreditoController;

    @Autowired
    private CalendarioController calendarioController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private RegraLimiteOperacaoController regraLimiteOperacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    public void validarEntidades(String cnvCodigo, String corCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        validarEntidades(cnvCodigo, corCodigo, true, true, true, true, true, true, false, responsavel);
    }

    /**
     * Verifica se as entidades utilizadas para uma nova reserva estão ativas.
     * Caso alguma delas esteja bloqueada, lança exceção com a descrição da entidade bloqueada.
     * @param cnvCodigo
     * @param corCodigo
     * @param cnvAtivo
     * @param svcAtivo
     * @param csaAtivo
     * @param orgAtivo
     * @param estAtivo
     * @param cseAtivo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    protected void validarEntidades(String cnvCodigo, String corCodigo,
                                    boolean cnvAtivo, boolean svcAtivo, boolean csaAtivo,
                                    boolean orgAtivo, boolean estAtivo, boolean cseAtivo, boolean inclusaoAde, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Busca os dados do convênio para validação das entidades
            final ObtemDadosConvenioQuery query = new ObtemDadosConvenioQuery();
            query.cnvCodigo = cnvCodigo;
            query.corCodigo = corCodigo;
            final List<TransferObject> result = query.executarDTO();
            final TransferObject convenio = ((result != null) && (result.size() > 0)) ? result.get(0) : null;
            if (convenio != null) {

                if (cnvAtivo) {
                    // Se valida o convênio ...
                    final String statusConvenio = convenio.getAttribute(Columns.CNV_SCV_CODIGO).toString();
                    if (CodedValues.SCV_INATIVO.equals(statusConvenio)) {
                        // Não pode fazer operação, pois o convenio está bloqueado
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.convenio.bloqueado", responsavel);
                    } else if (CodedValues.SCV_CANCELADO.equals(statusConvenio)) {
                        // Não pode fazer operação, pois o convenio foi cancelado
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.convenio.cancelado", responsavel);
                    }
                }

                if (!TextHelper.isNull(corCodigo)) {
                    // Se valida o correspondente ...
                    final Short statusCorrespondente = (convenio.getAttribute(Columns.COR_ATIVO) != null ? Short.valueOf(convenio.getAttribute(Columns.COR_ATIVO).toString()) : CodedValues.STS_INATIVO);
                    final String corNome = convenio.getAttribute(Columns.COR_NOME).toString();
                    if (!statusCorrespondente.equals(CodedValues.STS_ATIVO)) {
                        // Não pode fazer operação, pois o correspondente está bloqueado
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.correspondete.bloqueado", responsavel, corNome);
                    }

                    if (cnvAtivo) {
                        // Se valida o convênio ...
                        final String statusConvenioCorrespondente = convenio.getAttribute(Columns.CRC_SCV_CODIGO).toString();
                        if (CodedValues.SCV_INATIVO.equals(statusConvenioCorrespondente)) {
                            // Não pode fazer operação, pois o convenio está bloqueado
                            throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.convenio.do.correspondente.bloqueado", responsavel);
                        } else if (CodedValues.SCV_CANCELADO.equals(statusConvenioCorrespondente)) {
                            // Não pode fazer operação, pois o convenio foi cancelado
                            throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.convenio.do.correspondente.cancelado", responsavel);
                        }
                    }
                }

                if (svcAtivo) {
                    // Se valida o serviço ...
                    final Short statusServico = (convenio.getAttribute(Columns.SVC_ATIVO) != null ? Short.valueOf(convenio.getAttribute(Columns.SVC_ATIVO).toString()) : CodedValues.STS_INATIVO);
                    final String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO).toString();
                    if (!statusServico.equals(CodedValues.STS_ATIVO)) {
                        // Não pode fazer operação, pois o serviço está bloqueado
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.servico.bloqueado", responsavel, svcDescricao);
                    }
                }

                if (csaAtivo) {
                    // Se valida a consignatária ...
                    final Short statusConsignataria = (convenio.getAttribute(Columns.CSA_ATIVO) != null ? Short.valueOf(convenio.getAttribute(Columns.CSA_ATIVO).toString()) : CodedValues.STS_INATIVO);
                    final String csaNome = convenio.getAttribute(Columns.CSA_NOME).toString();
                    if (!statusConsignataria.equals(CodedValues.STS_ATIVO) || (inclusaoAde && !"S".equals(convenio.getAttribute(Columns.CSA_PERMITE_INCLUIR_ADE)))) {
                        // Não pode fazer operação, pois a consignatária está bloqueado
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.consignataria.nome.esta.bloqueada", responsavel, csaNome);
                    }
                }

                if (orgAtivo) {
                    // Se valida o órgão ...
                    final Short statusOrgao = (convenio.getAttribute(Columns.ORG_ATIVO) != null ? Short.valueOf(convenio.getAttribute(Columns.ORG_ATIVO).toString()) : CodedValues.STS_INATIVO);
                    final String orgNome = convenio.getAttribute(Columns.ORG_NOME).toString();
                    if (!statusOrgao.equals(CodedValues.STS_ATIVO)) {
                        // Não pode fazer operação, pois o orgão está bloqueado
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.orgao.bloqueado", responsavel, orgNome);
                    }
                }

                if (estAtivo) {
                    // Se valida o estabelecimento ...
                    final Short statusEstabelecimento = (convenio.getAttribute(Columns.EST_ATIVO) != null ? Short.valueOf(convenio.getAttribute(Columns.EST_ATIVO).toString()) : CodedValues.STS_INATIVO);
                    final String estNome = convenio.getAttribute(Columns.EST_NOME).toString();
                    if (!statusEstabelecimento.equals(CodedValues.STS_ATIVO)) {
                        // Não pode fazer operação, pois o estabelecimento está bloqueado
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.estabelecimento.bloqueado", responsavel, estNome);
                    }
                }

                if (cseAtivo) {
                    // Se valida o consignante ...
                    final Short statusConsignante = (convenio.getAttribute(Columns.CSE_ATIVO) != null ? Short.valueOf(convenio.getAttribute(Columns.CSE_ATIVO).toString()) : CodedValues.STS_INATIVO);
                    final String cseNome = convenio.getAttribute(Columns.CSE_NOME).toString();
                    if (!statusConsignante.equals(CodedValues.STS_ATIVO)) {
                        // Não pode fazer operação, pois o consignante está bloqueado
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.consignante.bloqueado", responsavel, cseNome);
                    }
                }
            } else {
                throw new AutorizacaoControllerException("mensagem.convenioNaoEncontrado", responsavel);
            }

        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica a possibilidade da inclusão de uma nova reserva para um servidor.
     * Realiza as validações de limite de contratos e de situação do servidor.
     *
     * ATENÇÃO: a validação de alteração de um contrato não irá funcionar para a seguinte combinação:
     *    - Parâmetro de serviço para alterar contrato em um mesmo período marcado para S no serviço dado
     *    - Possuir um limite de contratos para a mesma matrícula e consignatária e este já estiver sido alcançado
     *    - O contrato a alterar possuir carência
     *
     * OUTRA ATENÇÃO: FAVOR NÃO CRIAR OUTRAS ASSINATURAS APENAS PARA NÃO TER QUE TROCAR NO CÓDIGO OS LOCAIS
     *      ONDE ESTE MÉTODO É CHAMADO. ASS.: IGOR
     *
     * @param cnvCodigo
     * @param corCodigo
     * @param rseCodigo
     * @param validarEntidades
     * @param serCnvAtivo
     * @param serAtivo
     * @param adeCodigosRenegociacao
     * @param adeVlr
     * @param adeVlrLiquido
     * @param adePrazo
     * @param adeCarencia
     * @param adePeriodicidade
     * @param parametros
     * @param acao
     * @param incAvancadaValidaLimites
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    public boolean podeReservarMargem(String cnvCodigo, String corCodigo, String rseCodigo, boolean validarEntidades, boolean serCnvAtivo, boolean serAtivo,
                                      List<String> adeCodigosRenegociacao, BigDecimal adeVlr, BigDecimal adeVlrLiquido, Integer adePrazo, Integer adeCarencia, String adePeriodicidade, String adeIdentificador,
                                      Map<String, Object> parametros, String acao, boolean incAvancadaValidaLimites, boolean telaConfirmacaoDuplicidade, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (validarEntidades) {
                // Verifica se as entidades estão ativas para fazer novas reservas
                validarEntidades(cnvCodigo, corCodigo, true, true, true, true, true, true, true, responsavel);
            }

            final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKeyForUpdate(rseCodigo);
            final String serCodigo = registroServidor.getServidor().getSerCodigo();
            Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
            final String cnvCodVerba = convenio.getCnvCodVerba();
            final String cnvCodVerbaRef = convenio.getCnvCodVerbaRef();
            String svcCodigo = convenio.getServico().getSvcCodigo();
            final String csaCodigo = convenio.getConsignataria().getCsaCodigo();
            final String orgCodigo = convenio.getOrgao().getOrgCodigo();

            // Faz as verificações da situação do servidor
            if (serAtivo) {
                if (registroServidor.isExcluido()) {
                    // Servidor excluído não pode fazer novas reservas.
                    throw new AutorizacaoControllerException("mensagem.servidorExcluido", responsavel);
                } else if (registroServidor.isBloqueado()) {
                    // Verifica se servidor bloqueado pode fazer nova reserva
                    final CustomTransferObject tpsPermiteReservaRseBloq = getParametroSvc(CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO, svcCodigo, Boolean.TRUE, false, parametros);
                    if (!((Boolean) tpsPermiteReservaRseBloq.getAttribute(Columns.PSE_VLR)).booleanValue()) {
                        // Servidor bloqueado não pode fazer novas reservas.
                        throw new AutorizacaoControllerException("mensagem.servidorBloqueado", responsavel);
                    }
                }
            }

            // Se tem relacionamento para compartilhamento de taxas, verifica em qual serviço/convênio
            // o servidor ainda não tem contratos
            final boolean temRelacionamentoCompTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);
            try {
                if (temRelacionamentoCompTaxas) {
                    final ObtemConvenioRelCadTaxasQuery cnvRelacCadTaxas = new ObtemConvenioRelCadTaxasQuery();
                    cnvRelacCadTaxas.svcCodigoOrigem = svcCodigo;
                    cnvRelacCadTaxas.csaCodigo = csaCodigo;
                    cnvRelacCadTaxas.corCodigo = corCodigo;
                    cnvRelacCadTaxas.orgCodigo = orgCodigo;
                    cnvRelacCadTaxas.rseCodigo = rseCodigo;
                    cnvRelacCadTaxas.adeCodigosReneg = adeCodigosRenegociacao;

                    final List<TransferObject> cnvCodigosPossiveis = cnvRelacCadTaxas.executarDTO();

                    if ((cnvCodigosPossiveis != null) && (cnvCodigosPossiveis.size() > 0)) {
                        final TransferObject cnvRelCadTaxa = cnvCodigosPossiveis.get(0);
                        final String cnvCodigoRel = cnvRelCadTaxa.getAttribute(Columns.CNV_CODIGO).toString();
                        if (!cnvCodigoRel.equals(cnvCodigo)) {
                            // Se o convênio deve ser outro
                            cnvCodigo = cnvCodigoRel;
                            convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
                            // Troca também o serviço, pois existem validações feitas sobre o mesmo
                            svcCodigo = convenio.getServico().getSvcCodigo();
                        }
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // DESENV-8834: verifica se há contratos ativos em serviços que, por isso, impedem de ser criado este contrato.
            final ListaConsignacaoLimitadorNovosAdesQuery lstAdeLimitador = new ListaConsignacaoLimitadorNovosAdesQuery();
            lstAdeLimitador.svcCodigo = svcCodigo;
            lstAdeLimitador.rseCodigo = rseCodigo;
            lstAdeLimitador.responsavel = responsavel;

            try {
                final List<TransferObject> lstAdesLimitador =  lstAdeLimitador.executarDTO();
                if ((lstAdesLimitador != null) && !lstAdesLimitador.isEmpty()) {
                    throw new AutorizacaoControllerException("mensagem.erro.existe.contrato.em.servico.limitador", responsavel, (String) lstAdesLimitador.get(0).getAttribute(Columns.SVC_DESCRICAO));
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erro.consulta.relacionamento.servicos.limitador.emprestimo", responsavel, ex);
            }

            /*
             * Se o parametro de sistema  TPC_PERMITE_CNV_COD_VERBA_VAZIO = "N" então o campo código de verba
             * do convênio precisa ser verificado.
             * Não permitir fazer reserva/renegociar contrato/importar lote/requisicao Xml e rotina de compra
             * caso o cod verba cadastrada esteja NULL, BRANCO ou somente com ZEROS.
             * Dar mensagem de erro para o usuário informado o que deve ser realizado.
             */
            if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_CNV_COD_VERBA_VAZIO, CodedValues.TPC_SIM, responsavel)) {
                final String codVerba = cnvCodVerba != null ? cnvCodVerba.replace("0", "").trim() : null;
                if ((codVerba == null) || "".equals(codVerba)) {
                    throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.codigo.verba.nao.cadastrado", responsavel);
                }
            }

            final Consignataria consignataria = ConsignatariaHome.findByPrimaryKey(csaCodigo);
            final String csaIdentificadorInterno = consignataria.getCsaIdentificadorInterno();
            final String ncaCodigo = consignataria.getNaturezaConsignataria().getNcaCodigo();
            final Servico servico = ServicoHome.findByPrimaryKey(svcCodigo);
            final String nseCodigo = servico.getNaturezaServico().getNseCodigo();
            final String tgsCodigo = servico.getTipoGrupoSvc() != null ? servico.getTipoGrupoSvc().getTgsCodigo() : null;
            final String svcDescricao = servico.getSvcDescricao();

            /*
             * Verificar os parametros de consignatária:
             * SOMENTE SE TPA_VERIFICAR_VALIDACOES_LIMITES = TRUE será verificado os limites do numero
             * de contratos por serviço, os limites de quantidade de consignatarias permitidas a fazer
             * contratos para o servidor, e os demais limites por convênio
             */
            boolean paramVerifLimites = true;
            try {
                final ListaParamCsaQuery query = new ListaParamCsaQuery();
                query.csaCodigo = csaCodigo;
                query.tpaCodigo = CodedValues.TPA_VERIFICAR_VALIDACOES_LIMITES;
                query.tpaCseAltera = null;
                query.tpaCsaAltera = null;
                query.tpaSupAltera = null;

                final List<TransferObject> paramCsa = query.executarDTO();
                if ((paramCsa != null) && (paramCsa.size() > 0)) {
                    final CustomTransferObject cto1 = (CustomTransferObject) paramCsa.get(0);
                    paramVerifLimites = ((cto1.getAttribute(Columns.PCS_VLR) == null) || !"N".equals(cto1.getAttribute(Columns.PCS_VLR).toString()));
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Verifica se existe um bloqueio de operação via relacionamento de serviço.
            try {
                if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO)) {
                    int qtdConvenios = 0, qtdContratos = 0;
                    final StringBuilder servicos = new StringBuilder();
                    final ListaContratoLiberaOperacaoByOrigemQuery query = new ListaContratoLiberaOperacaoByOrigemQuery();
                    query.csaCodigo = csaCodigo;
                    query.svcCodigo = svcCodigo;
                    query.rseCodigo = rseCodigo;
                    final List<TransferObject> listaContratos = query.executarDTO();
                    for (final TransferObject contrato : listaContratos) {
                        /*
                         * Se SVC_DESCRICAO for nula então não existe convênio para este serviço
                         * portanto não precisa existir um contrato para este convênio.
                         */
                        if (contrato.getAttribute(Columns.SVC_DESCRICAO) != null) {
                            qtdConvenios++;
                            if (contrato.getAttribute(Columns.ADE_CODIGO) == null) {
                                servicos.append(contrato.getAttribute(Columns.SVC_DESCRICAO).toString().toUpperCase()).append(", ");
                            } else {
                                qtdContratos++;
                            }
                        }
                    }
                    if ((qtdConvenios > 0) && (qtdContratos == 0)) {
                        servicos.setLength(servicos.length() - 2);
                        throw new AutorizacaoControllerException("mensagem.erro.inserir.contrato.este.servico.necessario.contrato.ativo.algum.destes.servicos", responsavel, servicos.toString());
                    }
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erro.consulta.relacionamento.para.liberar.operacao", responsavel, ex);
            }

            // Verifica se o serviço é do tipo insere/altera
            final TransferObject adeInsereAltera = getContratoInsereAltera(rseCodigo, cnvCodigo, svcCodigo, orgCodigo, adePrazo, adeCarencia, adePeriodicidade, adeIdentificador, parametros, responsavel);
            final boolean isInsereAltera = (adeInsereAltera != null);
            if (isInsereAltera) {
                // Se é uma inclusão/alteração, então não é necessário validar os limites de contrato
                // pois uma alteração não modifica a quantidade de contratos existentes.
                paramVerifLimites = false;
            }

            // Se não for uma inserção/alteração verifica se o serviço está bloqueado para novas reservas.
            if (!isInsereAltera && "RESERVAR".equals(acao)) {
                final CustomTransferObject tpsVlrPermiteNovaReserva = getParametroSvc(CodedValues.TPS_PODE_INCLUIR_NOVOS_CONTRATOS, svcCodigo, Boolean.TRUE, true, parametros);
                if (!((Boolean)tpsVlrPermiteNovaReserva.getAttribute(Columns.PSE_VLR)).booleanValue()) {
                    throw new AutorizacaoControllerException("mensagem.servicoNaoPermiteInclusao", responsavel);
                }
            }

            verificaBloqInclusaoPrdRejeitada(csaCodigo, svcCodigo, nseCodigo, rseCodigo, adeCodigosRenegociacao, parametros, responsavel);

            if (!telaConfirmacaoDuplicidade) {
                //DESENV-9420: impede ades em duplicidade dentro de limite definido por parâmetro, se houver
                validaPeriodoBloqueioAdeDuplicadas(cnvCodigo, rseCodigo, adeCodigosRenegociacao, adeVlr, adePrazo, adeCarencia, adePeriodicidade,
                        parametros, svcCodigo, csaCodigo, orgCodigo, nseCodigo, responsavel);
            }

            if (paramVerifLimites && incAvancadaValidaLimites) {
                verificaLimiteQtdConsignatarias(true, csaCodigo, svcCodigo, rseCodigo, serCodigo, adeCodigosRenegociacao, adePrazo, responsavel);
                verificaLimiteQtdContratosGrupoSvc(tgsCodigo, csaCodigo, svcCodigo, rseCodigo, adeCodigosRenegociacao, svcDescricao, responsavel);
                verificaLimiteQtdContratosCnvRse(serCnvAtivo, cnvCodigo, svcCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
                verificaLimiteQtdContratosSvcRse(svcCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
                verificaLimiteQtdContratosNseRse(svcCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
                verificaLimiteQtdContratosCsaRse(csaCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
                verificaPeriodoRestricaoNovosContratos(cnvCodigo, csaCodigo, svcCodigo, nseCodigo, rseCodigo, orgCodigo, adeCarencia, adePeriodicidade, responsavel);
                verificaLimiteMargemPorConsignataria(csaCodigo, rseCodigo, adeVlr, null, adeCodigosRenegociacao, responsavel);
                regraLimiteOperacaoController.validarLimiteOperacao(adeVlr, adeVlrLiquido, adePrazo, adeCarencia, adePeriodicidade, nseCodigo, svcCodigo, ncaCodigo, csaCodigo, corCodigo, cnvCodVerba, cnvCodVerbaRef, registroServidor, adeCodigosRenegociacao, acao, responsavel);
            }

            if ((registroServidor.getVinculoRegistroServidor() != null) && !CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo())) {
                verificaBloqueioVinculoCnv(csaCodigo, svcCodigo, registroServidor.getVinculoRegistroServidor().getVrsCodigo(), responsavel);
            }

            if (registroServidor.getPostoRegistroServidor() != null) {
                verificaBloqueioPostoCsaSvc(csaCodigo, svcCodigo, registroServidor.getPostoRegistroServidor().getPosCodigo(), responsavel);
            }

            verificaServidorCorrentista(csaCodigo, csaIdentificadorInterno, svcCodigo, registroServidor.getRseBancoSal(), responsavel);
            verificaLimiteCapitalDevido(rseCodigo, registroServidor.getRseBaseCalculo(), orgCodigo, svcCodigo, adeCodigosRenegociacao, adeVlr, adeVlrLiquido, adePrazo, parametros, responsavel);
            verificaBloqueioFuncao(rseCodigo, acao, responsavel);
            verificaBloqueioReservaAposLiquidacao(rseCodigo, svcCodigo, acao, responsavel);

            // Retorna true caso não haja nada de errado
            return true;

        } catch (final RegraLimiteOperacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);

        } catch (FindException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    public boolean podePermitirDuplicidadeMotivadaUsuario(String cnvCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {

            final Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
            final String svcCodigo = convenio.getServico().getSvcCodigo();

            final TransferObject paramDuplicidadeMotivadaUsuario = getParametroSvc(CodedValues.TPS_PERMITIR_DUPLICIDADE_WEB_MOTIVADA_USUARIO, svcCodigo,
                    cnvCodigo, Integer.class, null, Integer.class, null, false, true, null);

            return "1".equals(String.valueOf(paramDuplicidadeMotivadaUsuario.getAttribute(Columns.PSE_VLR)));

        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

    }

    /**
     * impede ades em duplicidade dentro de limite definido por parâmetro, se houver
     * @param cnvCodigo
     * @param rseCodigo
     * @param adeCodigosRenegociacao
     * @param adeVlr
     * @param adePrazo
     * @param adeCarencia
     * @param adePeriodicidade
     * @param parametros
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param nseCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     * @throws PeriodoException
     */
    private void validaPeriodoBloqueioAdeDuplicadas(String cnvCodigo, String rseCodigo, List<String> adeCodigosRenegociacao, BigDecimal adeVlr,
                                                    Integer adePrazo, Integer adeCarencia, String adePeriodicidade, Map<String, Object> parametros, String svcCodigo,
                                                    String csaCodigo, String orgCodigo, String nseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException, PeriodoException {

        final TransferObject paramLmtAdeDuplicidade = getParametroSvc(CodedValues.TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE, svcCodigo,
                cnvCodigo, Integer.class, null, Integer.class, null, false, true, parametros);
        final Integer tpsLimiteDuplicidade = ((paramLmtAdeDuplicidade != null) &&
                !TextHelper.isNull(paramLmtAdeDuplicidade.getAttribute(Columns.PSE_VLR))) ?
                (Integer) paramLmtAdeDuplicidade.getAttribute(Columns.PSE_VLR) : null;
        if ((adeVlr != null) && (paramLmtAdeDuplicidade != null) && ((tpsLimiteDuplicidade != null) && (tpsLimiteDuplicidade > 0))) {
            final ListaConsignacaoEmDuplicidadeQuery lstAdeDuplicidade = new ListaConsignacaoEmDuplicidadeQuery();
            lstAdeDuplicidade.adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, adePeriodicidade, responsavel);
            lstAdeDuplicidade.adePrazo = adePrazo;
            lstAdeDuplicidade.adeVlr = adeVlr.setScale(2, RoundingMode.HALF_DOWN);
            lstAdeDuplicidade.csaCodigo = csaCodigo;
            lstAdeDuplicidade.nseCodigo = nseCodigo;
            lstAdeDuplicidade.rseCodigo = rseCodigo;
            lstAdeDuplicidade.adeCodigosNaoConsiderar = adeCodigosRenegociacao;
            lstAdeDuplicidade.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;

            try {
                final List<TransferObject> adesDuplicidade = lstAdeDuplicidade.executarDTO();

                if ((adesDuplicidade != null) && !adesDuplicidade.isEmpty()) {
                    final TransferObject adeDuplicidade = adesDuplicidade.get(0);
                    final java.sql.Timestamp adeDataDuplicidade = (java.sql.Timestamp) adeDuplicidade.getAttribute(Columns.ADE_DATA);

                    final long diffPeriodoAgora = DateHelper.getSystemDatetime().getTime() - adeDataDuplicidade.getTime();

                    //o valor do parâmetro representa o período em minutos
                    if (diffPeriodoAgora < (tpsLimiteDuplicidade * 60 *1000)) {
                        final long diffLimite = adeDataDuplicidade.getTime() + (tpsLimiteDuplicidade * 60 *1000);
                        throw new AutorizacaoControllerException("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite", responsavel,
                                DateHelper.format(new Date(diffLimite), LocaleHelper.getDateTimePattern()));
                    }
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Verifica limitação de contratos pelo grupo de serviço
     * OBS: Só deve ser validado se TPA_VERIFICAR_VALIDACOES_LIMITES = true
     * @param tgsCodigo
     * @param csaCodigo
     * @param svcCodigo
     * @param rseCodigo
     * @param adeCodigosRenegociacao
     * @param svcDescricao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaLimiteQtdContratosGrupoSvc(String tgsCodigo, String csaCodigo, String svcCodigo, String rseCodigo, List<String> adeCodigosRenegociacao, String svcDescricao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if ((tgsCodigo != null) && (!"".equals(tgsCodigo))) {
            try {
                // Busca o grupo de serviço ao qual este serviço está relacionado
                final TipoGrupoSvc grpServico = TipoGrupoSvcHome.findByPrimaryKey(tgsCodigo);

                if (grpServico.getTgsQuantidade() != null) {
                    // Limite de contratos para o grupo de servicos geral
                    final int grpQuantidade = grpServico.getTgsQuantidade();

                    // Busca a quantidade de contratos que o servidor possui para o grupo de serviço
                    final ObtemTotalContratosPorGrupoSvcQuery query = new ObtemTotalContratosPorGrupoSvcQuery();
                    query.rseCodigo = rseCodigo;
                    query.tgsCodigo = tgsCodigo;
                    query.adeCodigos = adeCodigosRenegociacao;
                    final int count = query.executarContador();

                    // Realiza a comparação e envia mensagem de erro caso o limite tenha sido ultrapassado
                    if (grpQuantidade <= count) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.servico", responsavel, svcDescricao.toUpperCase());
                    }
                }

                if (grpServico.getTgsQuantidadePorCsa() != null) {
                    // Limite de contratos para o grupo de servicos dentro de uma consignatária
                    final int grpQuantidade = grpServico.getTgsQuantidadePorCsa();

                    // Busca a quantidade de contratos que o servidor possui para o grupo de serviço
                    final ObtemTotalContratosPorGrupoSvcQuery query = new ObtemTotalContratosPorGrupoSvcQuery();
                    query.rseCodigo = rseCodigo;
                    query.tgsCodigo = tgsCodigo;
                    query.csaCodigo = csaCodigo;
                    query.adeCodigos = adeCodigosRenegociacao;
                    final int count = query.executarContador();

                    // Realiza a comparação e envia mensagem de erro caso o limite tenha sido ultrapassado
                    if (grpQuantidade <= count) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.servico", responsavel, svcDescricao.toUpperCase());
                    }
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erro.consulta.contratos.efetivados.pelo.servidor.para.grupo.servicos", responsavel, ex);
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erro.obter.grupo.servico.para.validacao.limite.contratos", responsavel, ex);
            }
        }
    }

    /**
     * Valida a limitação de quantidade de consignatárias distintas que
     * podem fazer contratos para o servidor
     * OBS: Só deve ser validado se TPA_VERIFICAR_VALIDACOES_LIMITES = true
     * @param adeNovo - informa se o contrato verificado é uma nova reserva/solicitação ou oriundo de um cancelamento de renogiação ou portabilidade
     * @param csaCodigo
     * @param svcCodigo
     * @param rseCodigo
     * @param serCodigo
     * @param adeCodigosRenegociacao
     * @param adeCodigoAVerificar - código da ade que está sendo verificado se poderá ser incluído
     * @param przAdeAverificar - prazo da ade que está sendo verificado se poderá ser incluído
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaLimiteQtdConsignatarias(boolean adeNovo, String csaCodigo, String svcCodigo, String rseCodigo, String serCodigo, List<String> adeCodigosRenegociacao, Integer przAdeAverificar, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final Object paramMaxCsa = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_CSA_FAZER_CONTRATO, responsavel);

            // Só será validado quantidade consignatarias permitidas a fazer contratos para o servidor
            // se TPA_VERIFICAR_VALIDACOES_LIMITES = true
            if (!TextHelper.isNull(paramMaxCsa)) {
                // Se existe o parâmetro de sistema cadastrado, valida a qtd de consignatárias
                // diferentes da atual
                final int paramMaxCsaInt = Integer.parseInt(paramMaxCsa.toString());

                //DESENV-8214: verifica se é lançamento de cartão. Caso seja, o limite de quantidade de CSAs não é aplicado
                final ListaRelacionamentosServicoQuery lstRelSvcs = new ListaRelacionamentosServicoQuery();
                lstRelSvcs.svcCodigoDestino = svcCodigo;
                lstRelSvcs.tntCodigo = CodedValues.TNT_CARTAO;

                final List<TransferObject> lstResultDest = lstRelSvcs.executarDTO();

                boolean lancamentoCartao = false;
                if ((lstResultDest != null) && !lstResultDest.isEmpty()) {
                    // se é lançamento de cartão e tem reserva de cartão ativa
                    for (final TransferObject resRel: lstResultDest) {
                        final ListaConsignacaoRseSvcQuery lstCsaRseSvc = new ListaConsignacaoRseSvcQuery();
                        lstCsaRseSvc.rseCodigo = rseCodigo;
                        lstCsaRseSvc.csaCodigo = csaCodigo;
                        lstCsaRseSvc.svcCodigo = (String) resRel.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM);
                        final List<TransferObject> lstResult = lstCsaRseSvc.executarDTO();
                        lancamentoCartao = ((lstResult != null) && !lstResult.isEmpty());
                        if (lancamentoCartao) {
                            break;
                        }
                    }
                }

                if (!lancamentoCartao) {
                    //DESENV-9064: verifica parâmetro de sistema que isenta ades de prazo 1 da verificação de limite de CSAs com contrato ativo pro servidor
                    final boolean permiteAdeAlemLmtCsaPrz1 = ParamSist.paramEquals(CodedValues.TPC_INCLUSAO_ADE_PRZ_UM_ALEM_LIMITE_CSA_SER, CodedValues.TPC_SIM, responsavel);
                    // se param 595 'S' e adePrazo = NULL, assume-se o true, pois o usuário ainda não escolheu o prazo. Também só será considerado se não
                    boolean excecaoPrazoUm = permiteAdeAlemLmtCsaPrz1 && (przAdeAverificar != null) ? przAdeAverificar == 1 : true && adeNovo;

                    int csaComAdeAtivoSerCount = 0;
                    if (excecaoPrazoUm) {
                        // verifica se o servidor já possui ade para a CSA para qual está tentando reservar com prazo 1.
                        final ListaConsignatariaComAdeSerQuery lstCsasComAde = new ListaConsignatariaComAdeSerQuery();
                        lstCsasComAde.count = true;
                        lstCsasComAde.sadAtivosLimite = true;
                        lstCsasComAde.csaCodigo = csaCodigo;
                        lstCsasComAde.serCodigo = serCodigo;
                        csaComAdeAtivoSerCount = lstCsasComAde.executarContador();

                        excecaoPrazoUm &= csaComAdeAtivoSerCount > 0;
                    }

                    if (!excecaoPrazoUm) {
                        final ObtemTotalConsignatariaPorServidorQuery query = new ObtemTotalConsignatariaPorServidorQuery();
                        query.rseCodigo = rseCodigo;
                        query.csaCodigo = csaCodigo;
                        query.adeCodigos = adeCodigosRenegociacao;
                        final int countCsaSer = query.executarContador();

                        // Verifica quantidade consignatarias permitidas a fazer contratos para o servidor
                        if (countCsaSer >= paramMaxCsaInt) {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.consignatarias", responsavel);
                        }
                    }
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erro.fazer.consulta.consignatarias.efetivaram.contratos.servidor", responsavel, ex);
        }
    }

    /**
     * Verifica se No. de contratos ativos no sistema atingiu o valor máximo dado pelo
     * parâmetro qtdemaxAVerificar, sendo este parâmetro de sistema ou de serviço.
     * OBS: Só deve ser validado se TPA_VERIFICAR_VALIDACOES_LIMITES = true
     * @param serCnvAtivo
     * @param cnvCodigo
     * @param rseCodigo
     * @param adeCodigosRenegociacao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaLimiteQtdContratosCnvRse(boolean serCnvAtivo, String cnvCodigo, String svcCodigo, String rseCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            /*
             * Numero Máximo de contratos por servidor:
             * será verificado TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC, que é configurado pelo gestor na manutenção
             * de serviço. Se esse estiver vazio ou nulo o sistema verificará o parâmetro de sistema TPC_QTDE_MAX_ADE_CNV_RSE
             * que defina o número máximo de contratos por convênio por servidor.
             */
            final ListaParamSvcCseQuery querySvcCse = new ListaParamSvcCseQuery();
            querySvcCse.svcCodigo = svcCodigo;
            querySvcCse.tpsCodigo = CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC;
            final List<TransferObject> lstParamSvcCse = querySvcCse.executarDTO();
            CustomTransferObject param = null;
            if ((lstParamSvcCse != null) && (lstParamSvcCse.size() > 0)) {
                param = (CustomTransferObject) lstParamSvcCse.get(0);
            }
            final String qtdeMaxAdeSvc = ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null) && !"".equals(param.getAttribute(Columns.PSE_VLR).toString())) ? param.getAttribute(Columns.PSE_VLR).toString() : null;

            // Qtd de contratos por CNV x RSE Default
            final String qtdeMaxAdeCnvRseSist = (ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, responsavel) != null) ? ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, responsavel).toString() : null;

            final String qtdeMaxAVerificar = (!TextHelper.isNull(qtdeMaxAdeSvc) ? qtdeMaxAdeSvc : qtdeMaxAdeCnvRseSist);

            if (!TextHelper.isNull(qtdeMaxAVerificar) && (Integer.parseInt(qtdeMaxAVerificar) == 0)) {
                throw new AutorizacaoControllerException("mensagem.servidorBloqueadoConvenio", responsavel);
            }

            // Qtd de contratos por CNV x RSE deste servidor
            String qtdeMaxAdeCnvRse = null;

            // Mesmo se não for para verificar se o servidor está ativo no convênio,
            // deve-se verificar os limites de contratos por cnv para o servidor
            if (serCnvAtivo) {
                // Se deve validar se o convênio está ativo para o servidor, então obtém o
                // valor do parâmetro de número de contratos por convênio para este servidor
                final ListaParamCnvRseQuery query = new ListaParamCnvRseQuery();
                query.rseCodigo = rseCodigo;
                query.cnvCodigo = cnvCodigo;
                query.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO;
                final List<TransferObject> result = query.executarDTO();
                if ((result != null) && (result.size() > 0)) {
                    param = (CustomTransferObject) result.get(0);
                }
                qtdeMaxAdeCnvRse = ((param != null) && (param.getAttribute(Columns.PCR_VLR) != null)) ? param.getAttribute(Columns.PCR_VLR).toString() : "";
            }

            // Se algum dos dois parâmetros existe, então valida o limite de contratos
            if (!TextHelper.isNull(qtdeMaxAdeCnvRse) || !TextHelper.isNull(qtdeMaxAVerificar)) {
                int numContratos = 0;

                if (!TextHelper.isNull(qtdeMaxAdeCnvRse) && !TextHelper.isNull(qtdeMaxAVerificar)) {
                    // Se ambos estão presentes, pega o valor mínimo
                    numContratos = Math.min(Integer.parseInt(qtdeMaxAVerificar), Integer.parseInt(qtdeMaxAdeCnvRse));
                } else if (!TextHelper.isNull(qtdeMaxAVerificar)) {
                    numContratos = Integer.parseInt(qtdeMaxAVerificar);
                } else if (!TextHelper.isNull(qtdeMaxAdeCnvRse)) {
                    numContratos = Integer.parseInt(qtdeMaxAdeCnvRse);
                }

                if (numContratos == 0) {
                    // Se o numero de contratos determinado para este servidor é zero, então retorna mensagem de erro
                    if (TextHelper.isNull(param.getAttribute(Columns.PCR_OBS))) {
                        throw new AutorizacaoControllerException("mensagem.servidorBloqueadoConvenio", responsavel);
                    } else {
                        throw new AutorizacaoControllerException("mensagem.servidorBloqueadoConvenio.motivo", responsavel, String.valueOf(param.getAttribute(Columns.PCR_OBS)));
                    }
                } else {
                    /*
                     * Se o numero de contratos determinado é maior que zero, então faz uma pesquisa de contratos
                     * para saber se o servidor pode ou não fazer mais reservas neste convênio
                     */
                    final ListaConsignacaoLimiteQuery query = new ListaConsignacaoLimiteQuery();
                    query.rseCodigo = rseCodigo;
                    query.cnvCodigo = cnvCodigo;
                    query.adeCodigos = adeCodigosRenegociacao;
                    final List<TransferObject> contratos = query.executarDTO();

                    if (contratos.size() >= numContratos) {
                        // Verifica se há contratos que estão para concluir (estão na última parcela) e os subtrai da conta
                        // total de contratos se TPC_IGNORA_CONTRATOS_A_CONCLUIR estiver como NÃO.
                        final boolean ignoraContratosAConcluir = ParamSist.paramEquals(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_SIM, responsavel);
                        if (ignoraContratosAConcluir) {
                            final int novoNumContratos = contratos.size() - numContratosEmUltimaParcela(contratos);
                            if (novoNumContratos >= numContratos) {
                                throw new AutorizacaoControllerException("mensagem.qtdMaxContratosExcedida", responsavel);
                            }
                        } else {
                            throw new AutorizacaoControllerException("mensagem.qtdMaxContratosExcedida", responsavel);
                        }
                    }
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se No. de contratos ativos no sistema para o serviço informado atingiu o limite
     * especificado no bloqueio de servidor por serviço
     * OBS: Só deve ser validado se TPA_VERIFICAR_VALIDACOES_LIMITES = true
     * @param svcCodigo
     * @param rseCodigo
     * @param adeCodigosRenegociacao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaLimiteQtdContratosSvcRse(String svcCodigo, String rseCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Verifica se passou o limite de contratos por serviço.
            String qtdeMaxAdeSvcRse = null;

            final ListaParamSvcRseQuery querySvcRse = new ListaParamSvcRseQuery();
            querySvcRse.rseCodigo = rseCodigo;
            querySvcRse.svcCodigo = svcCodigo;
            querySvcRse.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO;
            final List<TransferObject> lstParamSvcRse = querySvcRse.executarDTO();
            CustomTransferObject param = null;
            if ((lstParamSvcRse != null) && (lstParamSvcRse.size() > 0)) {
                param = (CustomTransferObject) lstParamSvcRse.get(0);
            }
            qtdeMaxAdeSvcRse = ((param != null) && (param.getAttribute(Columns.PSR_VLR) != null)) ? param.getAttribute(Columns.PSR_VLR).toString() : "";

            if (!TextHelper.isNull(qtdeMaxAdeSvcRse)) {
                final int numMaxContratos = Integer.parseInt(qtdeMaxAdeSvcRse);
                if (numMaxContratos == 0) {
                    // Se o numero de contratos determinado para este servidor é zero, então retorna mensagem de erro
                    if (TextHelper.isNull(param.getAttribute(Columns.PSR_OBS))) {
                        throw new AutorizacaoControllerException("mensagem.servidorBloqueadoConvenio", responsavel);
                    } else {
                        throw new AutorizacaoControllerException("mensagem.servidorBloqueadoConvenio.motivo", responsavel, String.valueOf(param.getAttribute(Columns.PSR_OBS)));
                    }
                } else {
                    /*
                     * Se o numero de contratos determinado é maior que zero, então faz uma pesquisa de contratos
                     * para saber se o servidor pode ou não fazer mais reservas neste convênio
                     */
                    final ListaConsignacaoLimiteQuery query = new ListaConsignacaoLimiteQuery();
                    query.rseCodigo = rseCodigo;
                    query.svcCodigo = svcCodigo;
                    query.adeCodigos = adeCodigosRenegociacao;
                    final List<TransferObject> contratosSvcServidor = query.executarDTO();

                    if (contratosSvcServidor.size() >= numMaxContratos) {
                        final boolean ignoraContratosAConcluir = ParamSist.paramEquals(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_SIM, responsavel);
                        if (ignoraContratosAConcluir) {
                            final int novoNumContratos = contratosSvcServidor.size() - numContratosEmUltimaParcela(contratosSvcServidor);
                            if (novoNumContratos >= numMaxContratos) {
                                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.este.servico", responsavel);
                            }
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.este.servico", responsavel);
                        }
                    }
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se No. de contratos ativos no sistema para a natureza de serviço do serviço informado atingiu o limite
     * especificado no bloqueio de servidor por natureza de serviço
     * OBS: Só deve ser validado se TPA_VERIFICAR_VALIDACOES_LIMITES = true
     * @param svcCodigo
     * @param rseCodigo
     * @param adeCodigosRenegociacao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaLimiteQtdContratosNseRse(String svcCodigo, String rseCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Verifica se passou o limite de contratos por natureza de serviço.
            String qtdeMaxAdeNseRse = null;
            final Servico servico = ServicoHome.findByPrimaryKey(svcCodigo);
            final String nseCodigo = servico.getNaturezaServico().getNseCodigo();

            final ListaParamNseRseQuery queryNseRse = new ListaParamNseRseQuery();
            queryNseRse.rseCodigo = rseCodigo;
            queryNseRse.nseCodigo = nseCodigo;
            queryNseRse.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO;
            final List<TransferObject> lstParamNseRse = queryNseRse.executarDTO();
            CustomTransferObject param = null;
            if ((lstParamNseRse != null) && (lstParamNseRse.size() > 0)) {
                param = (CustomTransferObject) lstParamNseRse.get(0);
            }
            qtdeMaxAdeNseRse = ((param != null) && (param.getAttribute(Columns.PNR_VLR) != null)) ? param.getAttribute(Columns.PNR_VLR).toString() : "";

            if (!TextHelper.isNull(qtdeMaxAdeNseRse)) {
                final int numMaxContratos = Integer.parseInt(qtdeMaxAdeNseRse);
                if (numMaxContratos == 0) {
                    // Se o numero de contratos determinado para este servidor é zero, então retorna mensagem de erro
                    throw new AutorizacaoControllerException("mensagem.servidorBloqueadoConvenio", responsavel);
                } else {
                    /*
                     * Se o numero de contratos determinado é maior que zero, então faz uma pesquisa de contratos
                     * para saber se o servidor pode ou não fazer mais reservas neste convênio
                     */
                    final ListaConsignacaoLimiteQuery query = new ListaConsignacaoLimiteQuery();
                    query.rseCodigo = rseCodigo;
                    query.nseCodigo = nseCodigo;
                    query.adeCodigos = adeCodigosRenegociacao;
                    final List<TransferObject> contratosSvcServidor = query.executarDTO();

                    if (contratosSvcServidor.size() >= numMaxContratos) {
                        final boolean ignoraContratosAConcluir = ParamSist.paramEquals(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_SIM, responsavel);
                        if (ignoraContratosAConcluir) {
                            final int novoNumContratos = contratosSvcServidor.size() - numContratosEmUltimaParcela(contratosSvcServidor);
                            if (novoNumContratos >= numMaxContratos) {
                                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.esta.natureza.servico", responsavel);
                            }
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.esta.natureza.servico", responsavel);
                        }
                    }
                }
            }
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se No. de contratos ativos no sistema para a consignatária informada atingiu o limite
     * especificado no bloqueio de servidor por consignatária
     * OBS: Só deve ser validado se TPA_VERIFICAR_VALIDACOES_LIMITES = true
     * @param csaCodigo
     * @param rseCodigo
     * @param adeCodigosRenegociacao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaLimiteQtdContratosCsaRse(String csaCodigo, String rseCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Verifica se passou o limite de contratos por natureza de serviço.
            String qtdeMaxAdeCsaRse = null;

            final ListaParamCsaRseQuery queryCsaRse = new ListaParamCsaRseQuery();
            queryCsaRse.rseCodigo = rseCodigo;
            queryCsaRse.csaCodigo = csaCodigo;
            queryCsaRse.tpaCodigo = CodedValues.TPA_QTD_CONTRATOS_POR_CSA;
            final List<TransferObject> lstParamCsaRse = queryCsaRse.executarDTO();
            CustomTransferObject param = null;
            if ((lstParamCsaRse != null) && !lstParamCsaRse.isEmpty()) {
                param = (CustomTransferObject) lstParamCsaRse.get(0);
            }
            qtdeMaxAdeCsaRse = ((param != null) && (param.getAttribute(Columns.PRC_VLR) != null)) ? param.getAttribute(Columns.PRC_VLR).toString() : "";

            if (!TextHelper.isNull(qtdeMaxAdeCsaRse)) {
                final int numMaxContratos = Integer.parseInt(qtdeMaxAdeCsaRse);
                if (numMaxContratos == 0) {
                    // Se o numero de contratos determinado para este servidor é zero, então retorna mensagem de erro
                    throw new AutorizacaoControllerException("mensagem.servidorBloqueadoCsa", responsavel);
                } else {
                    /*
                     * Se o numero de contratos determinado é maior que zero, então faz uma pesquisa de contratos
                     * para saber se o servidor pode ou não fazer mais reservas neste convênio
                     */
                    final ListaConsignacaoLimiteQuery query = new ListaConsignacaoLimiteQuery();
                    query.rseCodigo = rseCodigo;
                    query.csaCodigo = csaCodigo;
                    query.adeCodigos = adeCodigosRenegociacao;
                    final List<TransferObject> contratosCsaServidor = query.executarDTO();

                    if (contratosCsaServidor.size() >= numMaxContratos) {
                        final boolean ignoraContratosAConcluir = ParamSist.paramEquals(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_SIM, responsavel);
                        if (ignoraContratosAConcluir) {
                            final int novoNumContratos = contratosCsaServidor.size() - numContratosEmUltimaParcela(contratosCsaServidor);
                            if (novoNumContratos >= numMaxContratos) {
                                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.esta.consignataria", responsavel);
                            }
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.contratos.do.servidor.para.esta.consignataria", responsavel);
                        }
                    }
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se há configurado período de restrição para cadastro de novos contratos
     * OBS: Só deve ser validado se TPA_VERIFICAR_VALIDACOES_LIMITES = true
     * @param cnvCodigo
     * @param csaCodigo
     * @param svcCodigo
     * @param nseCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param adeCarencia
     * @param adePeriodicidade
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificaPeriodoRestricaoNovosContratos(String cnvCodigo, String csaCodigo, String svcCodigo, String nseCodigo, String rseCodigo, String orgCodigo, Integer adeCarencia, String adePeriodicidade, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            // Status das consignações que foram incluídas
            final List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
            sadCodigos.add(CodedValues.SAD_AGUARD_MARGEM);
            sadCodigos.add(CodedValues.SAD_DEFERIDA);

            // Verifica período de restrição para cadastro de novos contratos, dado pelo parâmetro de serviço 114
            final int qtdeDiasIncAde = (TextHelper.isNum(paramSvcCse.getTpsPerRestricaoCadNovaAdeCnvRse())) ? Integer.parseInt(paramSvcCse.getTpsPerRestricaoCadNovaAdeCnvRse()) : 0;
            if (qtdeDiasIncAde > 0) {
                // Verifica as consignações que já existem no sistema para o servidor
                final ListaConsignacaoPorRseCnvQuery query = new ListaConsignacaoPorRseCnvQuery();
                query.rseCodigo = rseCodigo;
                query.cnvCodigo = cnvCodigo;
                query.sadCodigos = sadCodigos;
                final List<TransferObject> autdes = query.executarDTO();
                // Se existir consignações, verifica se foram incluídas dentro do período
                if (autdes.size() > 0) {
                    final java.util.Date hoje = DateHelper.getSystemDatetime();
                    for (final TransferObject cto : autdes) {
                        final java.util.Date adeData = DateHelper.parse(cto.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd");
                        if (DateHelper.dayDiff(hoje, adeData) < qtdeDiasIncAde) {
                            throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.ja.existe.contrato.cadastrado.dentro.periodo.limite", responsavel);
                        }
                    }
                }
            }

            // Verifica período de restrição para cadastro de novos contratos, dado pelo parâmetro de serviço 281
            if (paramSvcCse.isTpsBloqueiaInclusaoAdeMesmoPeriodoNseRse()) {
                final Date adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, adePeriodicidade, responsavel);
                final ObtemTotalConsignacoesPorRseNseQuery query = new ObtemTotalConsignacoesPorRseNseQuery();
                query.rseCodigo = rseCodigo;
                query.nseCodigo = nseCodigo;
                query.csaCodigo = csaCodigo;
                query.sadCodigos = sadCodigos;
                query.adeAnoMesIni = adeAnoMesIni;
                if (query.executarContador() > 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.inserir.ou.alterar.reserva.ja.existe.contrato.cadastrado.mesmo.periodo.nse", responsavel, DateHelper.toPeriodString(adeAnoMesIni));
                }
            }
        } catch (HQueryException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PeriodoException | ParametroControllerException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    private void verificaBloqInclusaoPrdRejeitada(String csaCodigo, String svcCodigo, String nseCodigo, String rseCodigo, List<String> adeCodigosRenegociacao, Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Bloqueia inclusão de novas consignações no serviço caso o servidor possua parcela rejeitada em consignações abertas
        final CustomTransferObject tpsBloqIncAdeMesmaNseRejeitada = getParametroSvc(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA, svcCodigo, Boolean.TRUE, false, parametros);
        boolean bloqIncAdeMesma = (((Boolean) tpsBloqIncAdeMesmaNseRejeitada.getAttribute(Columns.PSE_VLR)));

        try {
            // DESENV-11866 - Parametro de serviço por servidor que sobrepoe parâmetro de serviço
            final List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA);

            final ListaParamSvcServidorSobrepoeQuery querySobrepoe = new ListaParamSvcServidorSobrepoeQuery();
            querySobrepoe.svcCodigo = svcCodigo;
            querySobrepoe.rseCodigo = rseCodigo;
            querySobrepoe.tpsCodigos = tpsCodigos;
            final List<TransferObject> resultSobrepoe = querySobrepoe.executarDTO();
            for (final TransferObject next : resultSobrepoe) {
                if (CodedValues.TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA.equals(next.getAttribute(Columns.PSR_TPS_CODIGO))) {
                    bloqIncAdeMesma = !TextHelper.isNull(next.getAttribute(Columns.PSR_VLR)) && "1".equals(next.getAttribute(Columns.PSR_VLR).toString());
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        boolean somenteCsa = false;
        if (!bloqIncAdeMesma) {
            try {
                // DESENV-16620 : Busca parâmetro de consignatária que também bloqueia a inclusão por parcela rejeitada
                final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA, responsavel);
                if (CodedValues.TPA_SIM.equals(pcsVlr)) {
                    bloqIncAdeMesma = true;
                    somenteCsa = true;
                }
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

        if (bloqIncAdeMesma) {
            try {
                final ListaConsignacaoPrdRejeitadaQuery query = new ListaConsignacaoPrdRejeitadaQuery();
                query.rseCodigo = rseCodigo;
                query.nseCodigo = somenteCsa ? null : nseCodigo;
                query.csaCodigo = somenteCsa ? csaCodigo : null;
                query.adeCodigosRenegociacao = adeCodigosRenegociacao;
                final int qtde = query.executarContador();

                if (qtde > 0) {
                    if (somenteCsa) {
                        throw new AutorizacaoControllerException("mensagem.erro.csa.nao.permite.incluir.prd.rejeitada", responsavel);
                    } else {
                        throw new AutorizacaoControllerException("mensagem.erro.svc.nao.permite.incluir.prd.rejeitada", responsavel);
                    }
                }
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Retorna o número de contratos ativos que está para concluir, ou seja, está na última parcela.
     * @param contratos
     * @return
     */
    private int numContratosEmUltimaParcela(List<TransferObject> contratos) {
        int numAdeUltimaParcela = 0;

        // Navega nos contratos do servidor verificando quais contratos possuem parcelas em processamento
        // na quantidade restante do prazo do contrato (prazo - pagas = qtd parcelas em processamento)
        for (final TransferObject contrato : contratos) {
            // Se possui parcelas em processamento, verifica se estas são as últimas do contrato
            final int qtdPrdEmProcessamento = contrato.getAttribute("QTD_PRD_EM_PROCESSAMENTO") != null ? Integer.parseInt(contrato.getAttribute("QTD_PRD_EM_PROCESSAMENTO").toString()) : 0;
            if (qtdPrdEmProcessamento > 0) {
                // Quando o contrato é indeterminado, ou seja, prazo null, não devemos considerar ele no cálculo de possível conclusão.
                if (TextHelper.isNull(contrato.getAttribute(Columns.ADE_PRAZO))) {
                    continue;
                }
                // Prazo e o numero de parcelas pagas
                final int prazoContrato = Integer.parseInt(contrato.getAttribute(Columns.ADE_PRAZO).toString());
                final int prdPagasContrato = contrato.getAttribute(Columns.ADE_PRD_PAGAS) != null ? Integer.parseInt(contrato.getAttribute(Columns.ADE_PRD_PAGAS).toString()) : 0;
                // Se o contrato só tem mais x parcelas e estas estão em processamento, então considera que está na última parcela
                if ((prazoContrato - prdPagasContrato) <= qtdPrdEmProcessamento) {
                    numAdeUltimaParcela++;
                }
            }
        }

        return numAdeUltimaParcela;
    }

    /**
     * Verifica os vínculos que não podem reservar margem para este convênio
     * @param csaCodigo
     * @param svcCodigo
     * @param vrsCodigo
     * @throws AutorizacaoControllerException
     */
    public void verificaBloqueioVinculoCnv(String csaCodigo, String svcCodigo, String vrsCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if(!TextHelper.isNull(vrsCodigo)) {
            // Seleciona os vínculos que não podem reservar margem para este svc e csa
            try {
                // DESENV-21366: Um serviço de natureza de cartão que não incide na margem e integra folha não deve sofrer bloqueio por vinculo
                if(!TextHelper.isNull(svcCodigo)) {
                    final ServicoTransferObject servico = convenioController.findServico(svcCodigo, responsavel);
                    final CustomTransferObject paramSvcIndiceMargem = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_INCIDE_MARGEM, responsavel);
                    final CustomTransferObject paramSvcIntegrafolha = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_INTEGRA_FOLHA, responsavel);

                    final boolean naoIncideMargem = (paramSvcIndiceMargem != null) && (paramSvcIndiceMargem.getAttribute(Columns.PSE_VLR) != null) && Short.valueOf((String) paramSvcIndiceMargem.getAttribute(Columns.PSE_VLR)).equals(CodedValues.INCIDE_MARGEM_NAO);
                    final boolean integraFolha = (paramSvcIntegrafolha != null) && (paramSvcIntegrafolha.getAttribute(Columns.PSE_VLR) != null) && CodedValues.PSE_BOOLEANO_SIM.equals(paramSvcIntegrafolha.getAttribute(Columns.PSE_VLR));

                    if(CodedValues.NSE_CARTAO.equals(servico.getSvcNseCodigo()) && naoIncideMargem && integraFolha) {
                        return;
                    }
                }

                final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel);
                final boolean bloqPadrao = CodedValues.TPA_SIM.equals(pcsVlr);

                final ListaConvenioVinculoRegistroServidorQuery query = new ListaConvenioVinculoRegistroServidorQuery();
                query.csaCodigo = csaCodigo;
                query.svcCodigo = svcCodigo;
                final List<String> vincBloqueados = query.executarLista();

                if ((bloqPadrao && ((vincBloqueados == null) || vincBloqueados.isEmpty() || !vincBloqueados.contains(vrsCodigo))) || (!bloqPadrao && vincBloqueados.contains(vrsCodigo))) {
                    final VinculoRegistroServidor vcr = VinculoRegistroServidorHome.findByPrimaryKey(vrsCodigo);
                    final VinculoConsignataria vcsa = consignatariaController.findVinculoCsaPorVrsCsa(csaCodigo, vrsCodigo, responsavel);
                    if (TextHelper.isNull(vcsa)) {
                        throw new AutorizacaoControllerException("mensagem.vinculoNaoPermiteReserva", responsavel, vcr.getVrsDescricao());
                    } else {
                        throw new AutorizacaoControllerException("mensagem.vinculoNaoPermiteReserva.vinculoCsa", responsavel, vcr.getVrsDescricao(), vcsa.getVcsDescricao());
                    }
                }
            } catch (HQueryException | ParametroControllerException | FindException | ConsignatariaControllerException | ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.validar.vinculo.servidor", responsavel, ex);
            }
        }
    }

    private void verificaBloqueioPostoCsaSvc(String csaCodigo, String svcCodigo, String posCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final String funCodigo = responsavel.getFunCodigo();
        if (funCodigo != null) {
            final boolean funInclusaoSolicitacao = CodedValues.FUN_SOL_EMPRESTIMO.equals(funCodigo) || CodedValues.FUN_SOLICITAR_LEILAO_REVERSO.equals(funCodigo);
            final boolean funInclusaoReserva = CodedValues.FUN_RES_MARGEM.equals(funCodigo) || CodedValues.FUN_INCLUSAO_VIA_LOTE.equals(funCodigo) || CodedValues.FUN_INCLUIR_CONSIGNACAO.equals(funCodigo);
            if (funInclusaoSolicitacao || funInclusaoReserva) {
                try {
                    // Se existe o bloqueio, então verifica se a operação pode seguir
                    final BloqueioPostoCsaSvc bloqueio = BloqueioPostoCsaSvcHome.findByPrimaryKey(csaCodigo, svcCodigo, posCodigo);
                    final boolean bpcBloqSolicitacao = "S".equals(bloqueio.getBpcBloqSolicitacao());
                    final boolean bpcBloqReserva = "S".equals(bloqueio.getBpcBloqReserva());

                    if (bpcBloqSolicitacao && funInclusaoSolicitacao) {
                        // Emite mensagem de erro informando que a solicitação não pode ser concluída
                        throw new AutorizacaoControllerException("mensagem.erro.posto.bloqueado.csa.svc.solicitacao", responsavel);
                    } else if (bpcBloqReserva && funInclusaoReserva) {
                        // Emite mensagem de erro informando que a reserva não pode ser concluída
                        throw new AutorizacaoControllerException("mensagem.erro.posto.bloqueado.csa.svc.reserva", responsavel);
                    }
                } catch (final FindException ex) {
                    // Se não existe o bloqueio, então deixa a operação seguir
                }
            }
        }
    }

    protected void verficarRelacionametoBloqueioOperacao(String adeCodigoDestino, String operacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO)) {
                final ListaContratoLiberaOperacaoByDestinoQuery query = new ListaContratoLiberaOperacaoByDestinoQuery();
                query.adeCodigo = adeCodigoDestino;
                final List<TransferObject> listaContratos = query.executarDTO();
                for (final TransferObject contrato : listaContratos) {
                    if ((contrato.getAttribute(Columns.SVC_DESCRICAO) != null) && (contrato.getAttribute(Columns.ADE_CODIGO) != null)) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.operar.este.contrato.pois.existe.contrato.ativo.servico", responsavel, operacao, contrato.getAttribute(Columns.SVC_DESCRICAO).toString().toUpperCase());
                    }
                }
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected TransferObject getContratoInsereAltera(String rseCodigo, String cnvCodigo, String svcCodigo, String orgCodigo, Integer adePrazo, Integer adeCarencia, String adePeriodicidade,
                                                     Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return this.getContratoInsereAltera(rseCodigo, cnvCodigo, svcCodigo, orgCodigo, adePrazo, adeCarencia, adePeriodicidade, null, parametros, responsavel);
    }

    /**
     * Caso o serviço seja do tipo Insere/Altera, retorna o contrato que deverá ser alterado,
     * de acordo com os parâmetros "rseCodigo" e "cnvCodigo". Retorna nulo caso nenhum contrato
     * seja encontrado, ou se o serviço não está configurado para funcionar desta forma.
     * @param rseCodigo : Registro Servidor ao qual pertence o contrato a ser incluido/alterado
     * @param cnvCodigo : Convênio que receberá o novo contrato
     * @param svcCodigo : Serviço que receberá o novo contrato
     * @param orgCodigo : Órgão do Registro Servidor (utilizado para cálculo do período atual)
     * @param adePrazo : Prazo do novo contrato que está sendo incluído
     * @param adeCarencia : Carência do novo contrato  (utilizado para cálculo do período atual)
     * @param parametros : Cache de parâmetros
     * @param responsavel : Usuário responsável pela operção
     * @return
     * @throws AutorizacaoControllerException
     */
    protected TransferObject getContratoInsereAltera(String rseCodigo, String cnvCodigo, String svcCodigo, String orgCodigo, Integer adePrazo, Integer adeCarencia, String adePeriodicidade,
                                                     String adeIdentificador, Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // insereAlteraMesmoPeriodo : True se o serviço é do tipo Insere/Altera de mesmo período
            final CustomTransferObject paramIncAltQualquerPeriodo = getParametroSvc(CodedValues.TPS_INCLUI_ALTERANDO_QUALQUER_PERIODO, svcCodigo, Boolean.FALSE, false, parametros);
            final boolean insereAlteraQualquerPeriodo = ((paramIncAltQualquerPeriodo != null) && (paramIncAltQualquerPeriodo.getAttribute(Columns.PSE_VLR) != null) && ((Boolean) paramIncAltQualquerPeriodo.getAttribute(Columns.PSE_VLR)).booleanValue());

            // insereAlteraQualquerPeriodo : True se o serviço é do tipo Insere/Altera independente do período
            final CustomTransferObject paramIncAltMesmoPeriodo = getParametroSvc(CodedValues.TPS_INCLUI_ALTERANDO_MESMO_PERIODO, svcCodigo, Boolean.FALSE, false, parametros);
            final boolean insereAlteraMesmoPeriodo = ((paramIncAltMesmoPeriodo != null) && (paramIncAltMesmoPeriodo.getAttribute(Columns.PSE_VLR) != null) && ((Boolean) paramIncAltMesmoPeriodo.getAttribute(Columns.PSE_VLR)).booleanValue());

            if (insereAlteraQualquerPeriodo || insereAlteraMesmoPeriodo) {
                // insereAlteraMesmoPrazo : True se o Insere/Altera só deve ser feito caso o prazo seja igual
                final CustomTransferObject paramIncAltMesmoPrazo = getParametroSvc(CodedValues.TPS_INCLUI_ALTERANDO_SOMENTE_MESMO_PRAZO, svcCodigo, Boolean.FALSE, false, parametros);
                final boolean insereAlteraMesmoPrazo = ((paramIncAltMesmoPrazo != null) && (paramIncAltMesmoPrazo.getAttribute(Columns.PSE_VLR) != null) && ((Boolean) paramIncAltMesmoPrazo.getAttribute(Columns.PSE_VLR)).booleanValue());

                // insereAlteraMesmoAdeIdn: True se Insere/Altera só deve ser feito se for encontrado na base ade com mesmo ADE_IDENTIFICADOR do informado na reserva.
                final CustomTransferObject paramInsereAlteraMesmoAdeIdn = getParametroSvc(CodedValues.TPS_INSERIR_VIRA_ALTERAR_POR_ADE_INDENTIFICADOR_IGUAL, svcCodigo, Boolean.FALSE, false, parametros);
                final boolean insereAlteraMesmoAdeIdn = ((paramInsereAlteraMesmoAdeIdn != null) && (paramInsereAlteraMesmoAdeIdn.getAttribute(Columns.PSE_VLR) != null) && ((Boolean) paramInsereAlteraMesmoAdeIdn.getAttribute(Columns.PSE_VLR)).booleanValue());

                final List<String> sadCodigo = new ArrayList<>();
                sadCodigo.add(CodedValues.SAD_DEFERIDA);
                if (!insereAlteraMesmoPeriodo) {
                    sadCodigo.add(CodedValues.SAD_EMANDAMENTO);
                }

                final ListaConsignacaoPorRseCnvQuery query = new ListaConsignacaoPorRseCnvQuery();
                query.rseCodigo = rseCodigo;
                query.cnvCodigo = cnvCodigo;
                query.sadCodigos = sadCodigo;
                query.adePeriodicidade = adePeriodicidade;

                final List<TransferObject> contratos = query.executarDTO();
                if ((contratos != null) && (contratos.size() > 0)) {
                    String prazoIni = null;
                    if (insereAlteraMesmoPeriodo) {
                        try {
                            prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, adePeriodicidade, responsavel).toString();
                            LOG.debug("Insere/Altera para mesmo período: " + prazoIni + ".");
                        } catch (final PeriodoException ex) {
                            throw new AutorizacaoControllerException(ex);
                        }
                    } else {
                        LOG.debug("Insere/Altera independente de período.");
                    }

                    final Iterator<TransferObject> it = contratos.iterator();
                    TransferObject to = null;

                    while (it.hasNext()) {
                        // Se encontrar algum deles, pega o primeiro existente, que está
                        // de acordo com os critérios
                        to = it.next();
                        final String adeAnoMesIni = to.getAttribute(Columns.ADE_ANO_MES_INI).toString();
                        final Integer adePrazoAtual = (Integer) to.getAttribute(Columns.ADE_PRAZO);
                        final boolean mesmoPrazo = ((adePrazo == null) || ((adePrazoAtual != null) && (adePrazo != null) && adePrazoAtual.equals(adePrazo)));
                        final boolean mesmoAdeIdn = !TextHelper.isNull(adeIdentificador) && !TextHelper.isNull(to.getAttribute(Columns.ADE_IDENTIFICADOR))
                                && adeIdentificador.equals(to.getAttribute(Columns.ADE_IDENTIFICADOR));

                        LOG.debug("adeAnoMesIni = " + adeAnoMesIni + " | prazoIni = " + prazoIni + " | mesmo prazo = " + mesmoPrazo);

                        // Se o período não é requerido (prazoIni == null) ou o contrato
                        // é do período de lançamento atual (adeAnoMesIni.equals(prazoIni)),
                        // e não requer prazo igual (!insereAlteraMesmoPrazo) ou são iguais
                        // e não requer que ade_identificador seja o mesmo ou este é igual ao da nova reserva
                        // então retorna o contrato para ser alterado, substituindo a inclusão.
                        if (((prazoIni == null) || adeAnoMesIni.equals(prazoIni)) &&
                                (!insereAlteraMesmoPrazo || mesmoPrazo) && (!insereAlteraMesmoAdeIdn || mesmoAdeIdn)) {
                            return to;
                        }
                    }
                }
            }

            return null;
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Obtém um parâmetro de serviço de acordo com o tipo "tpsCodigo" para o serviço "svcCodigo".
     * O tipo de retorno será dado pelo parâmetro "tipoRetorno", e se "tipoRetorno" for booleano
     * verifica o parâmetro "nuloVerdadeiro", caso o parâmetro seja nulo.
     * @param tpsCodigo
     * @param svcCodigo
     * @param tipoRetorno
     * @param nuloVerdadeiro
     * @param parametros
     * @return
     * @throws AutorizacaoControllerException
     */
    public CustomTransferObject getParametroSvc(String tpsCodigo, String svcCodigo, Object tipoRetorno, boolean nuloVerdadeiro, Map<String, Object> parametros) throws AutorizacaoControllerException {
        CustomTransferObject param = null;
        final CustomTransferObject retorno = new CustomTransferObject();

        if (parametros != null) {
            if (parametros.containsKey(tpsCodigo)) {
                param = new CustomTransferObject();
                param.setAttribute(Columns.PSE_VLR, parametros.get(tpsCodigo));
            }
        } else {
            try {
                final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(tpsCodigo, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                param = new CustomTransferObject();
                param.setAttribute(Columns.PSE_VLR, pse.getPseVlr());
                param.setAttribute(Columns.PSE_VLR_REF, pse.getPseVlrRef());
            } catch (final FindException e) {
                //LOG.debug("Erro em getParametroSvc, FinderException : Parametro não encontrado: " + tpsCodigo);
            }
        }
        if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)) {
            if (tipoRetorno instanceof Boolean) {
                retorno.setAttribute(Columns.PSE_VLR, Boolean.valueOf("1".equals(param.getAttribute(Columns.PSE_VLR).toString())));
                return retorno;
            } else if (tipoRetorno instanceof String) {
                retorno.setAttribute(Columns.PSE_VLR, param.getAttribute(Columns.PSE_VLR).toString().trim());
                return retorno;
            } else if (tipoRetorno instanceof Short) {
                retorno.setAttribute(Columns.PSE_VLR, ("".equals(param.getAttribute(Columns.PSE_VLR).toString().trim()) ? null :
                        Short.valueOf(param.getAttribute(Columns.PSE_VLR).toString())));
                return retorno;
            } else if (tipoRetorno instanceof Integer) {
                retorno.setAttribute(Columns.PSE_VLR, ("".equals(param.getAttribute(Columns.PSE_VLR).toString().trim()) ? null :
                        Integer.valueOf(param.getAttribute(Columns.PSE_VLR).toString())));
                return retorno;
            } else if (tipoRetorno instanceof BigDecimal) {
                retorno.setAttribute(Columns.PSE_VLR, ("".equals(param.getAttribute(Columns.PSE_VLR).toString().trim()) ? null :
                        new BigDecimal(param.getAttribute(Columns.PSE_VLR).toString())));
                return retorno;
            }
        } else if (tipoRetorno instanceof Boolean) {
            if (nuloVerdadeiro) {
                retorno.setAttribute(Columns.PSE_VLR, Boolean.TRUE);
                return retorno;
            } else {
                retorno.setAttribute(Columns.PSE_VLR, Boolean.FALSE);
                return retorno;
            }
        }
        retorno.setAttribute(Columns.PSE_VLR, null);
        return retorno;
    }

    /**
     * Obtém um parâmetro de serviço por consignatária de acordo com o tipo "tpsCodigo" para o serviço "svcCodigo"
     * e convênio "cnvCodigo". Caso o parâmetro de SVC/CSA seja nulo e "verificaParamSvcCse" for verdadeiro,
     * então o mesmo parâmetro será pesquisado no serviço, e caso exista o mesmo será retornado.
     * O tipo de retorno será dado pelo parâmetro "classeRetornoVlr" e "classeRetornoVlrRef", e
     * se "classeRetornoVlr" for booleano verifica o parâmetro "nuloVerdadeiro", caso o parâmetro seja nulo.
     * @param tpsCodigo
     * @param svcCodigo
     * @param cnvCodigo
     * @param classeRetornoVlr
     * @param formatoRetornoVlr
     * @param classeRetornoVlrRef
     * @param formatoRetornoVlrRef
     * @param nuloVerdadeiro
     * @param verificaParamSvcCse
     * @param parametros
     * @return
     * @throws AutorizacaoControllerException
     */
    protected CustomTransferObject getParametroSvc(String tpsCodigo, String svcCodigo, String cnvCodigo,
                                                   Class<?> classeRetornoVlr, String formatoRetornoVlr,
                                                   Class<?> classeRetornoVlrRef, String formatoRetornoVlrRef,
                                                   boolean nuloVerdadeiro, boolean verificaParamSvcCse, Map<String, Object> parametros) throws AutorizacaoControllerException {
        CustomTransferObject param = null;
        final CustomTransferObject retorno = new CustomTransferObject();

        if (parametros != null) {
            if (parametros.containsKey(tpsCodigo)) {
                param = new CustomTransferObject();
                param.setAttribute(Columns.PSE_VLR, parametros.get(tpsCodigo));
            }
        } else {
            try {
                final Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
                final String csaCodigo = convenio.getConsignataria().getCsaCodigo();

                final ParamSvcConsignataria psc = ParamSvcConsignatariaHome.findParametroBySvcCsa(svcCodigo, csaCodigo, tpsCodigo);
                param = new CustomTransferObject();
                param.setAttribute(Columns.PSE_VLR, psc.getPscVlr());
                param.setAttribute(Columns.PSE_VLR_REF, psc.getPscVlrRef());
            } catch (final FindException e) {
                //LOG.debug("Erro em getParametroSvc, FinderException : Parametro não encontrado: " + tpsCodigo);
            }
        }

        if (verificaParamSvcCse) {
            try {
                final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(tpsCodigo, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                if (param == null) {
                    param = new CustomTransferObject();
                }
                // Uso o de serviço somente se não existir o de convênio.
                if (TextHelper.isNull(param.getAttribute(Columns.PSE_VLR))) {
                    param.setAttribute(Columns.PSE_VLR, pse.getPseVlr());
                }
                // Uso o de serviço somente se não existir o de convênio.
                if (TextHelper.isNull(param.getAttribute(Columns.PSE_VLR_REF))) {
                    param.setAttribute(Columns.PSE_VLR_REF, pse.getPseVlrRef());
                }
            } catch (final FindException ex) {
                //LOG.debug("Erro em getParametroSvc, FinderException : Parametro não encontrado: " + tpsCodigo);
            }
        }

        if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)) {
            retorno.setAttribute(Columns.PSE_VLR, createObject(param.getAttribute(Columns.PSE_VLR).toString(), classeRetornoVlr, formatoRetornoVlr));
            if (param.getAttribute(Columns.PSE_VLR_REF) != null) {
                retorno.setAttribute(Columns.PSE_VLR_REF, createObject(param.getAttribute(Columns.PSE_VLR_REF).toString(), classeRetornoVlrRef, formatoRetornoVlrRef));
            }
            return retorno;
        } else if (Boolean.class.equals(classeRetornoVlr)) {
            if (nuloVerdadeiro) {
                retorno.setAttribute(Columns.PSE_VLR, Boolean.TRUE);
                retorno.setAttribute(Columns.PSE_VLR_REF, null);
                return retorno;
            } else {
                retorno.setAttribute(Columns.PSE_VLR, Boolean.FALSE);
                retorno.setAttribute(Columns.PSE_VLR_REF, null);
                return retorno;
            }
        }
        retorno.setAttribute(Columns.PSE_VLR, null);
        retorno.setAttribute(Columns.PSE_VLR_REF, null);
        return retorno;
    }

    private Object createObject(String valor, Class<?> classeRetorno, String formatoRetorno) {
        if (classeRetorno != null) {
            if (Boolean.class.equals(classeRetorno)) {
                return Boolean.valueOf("1".equals(valor.toString()) || "S".equalsIgnoreCase(valor.toString()));
            } else if (Date.class.equals(classeRetorno)) {
                try {
                    return DateHelper.parse(valor, formatoRetorno);
                } catch (final ParseException e) {
                    //LOG.debug("Erro em transformaValor, ParseException : Não foi possível transformar a data pois o formato está incorreto!");
                }
            } else {
                try {
                    final Object[] argumentos = {valor};
                    return classeRetorno.getConstructor(String.class).newInstance(argumentos);
                } catch (final Exception ex) {
                    // Erro ao criar o objeto na classe solicitada.
                }
            }
        }
        return null;
    }

    /**
     * Recupera o serviço de cartão de crédito
     * @param cnvCodigo
     * @param parametros
     * @return
     * @throws AutorizacaoControllerException
     */
    private String recuperaServicoCartaoCredito(String cnvCodigo, Map<String, Object> parametros) throws AutorizacaoControllerException {
        String servicoCartaoCredito = null;

        try {
            if (parametros != null) {
                // Recupera o serviço de cartão de crédito diretamente do cache de parâmetros.
                servicoCartaoCredito = (String) parametros.get("SERVICO_CARTAOCREDITO");
            } else {
                final Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);

                // Recupera o serviços de cartão de crédito dos quais o serviço do convênio depende.
                final ListaRelacionamentosQuery queryRelacionamentos = new ListaRelacionamentosQuery();
                queryRelacionamentos.tntCodigo = CodedValues.TNT_CARTAO;
                queryRelacionamentos.svcCodigoDestino = convenio.getServico().getSvcCodigo();
                final List<TransferObject> servicosCartaoCredito = queryRelacionamentos.executarDTO();

                if ((servicosCartaoCredito != null) && (servicosCartaoCredito.size() > 0)) {
                    if (servicosCartaoCredito.size() > 1) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.realizar.operacao.pois.existe.servico.provisionamento.margem.do.qual.autorizacao.depende", (AcessoSistema) null);
                    }
                    servicoCartaoCredito = (String) servicosCartaoCredito.get(0).getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM);
                }
            }

            return servicoCartaoCredito;
        } catch (DAOException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Informa se um convênio possui serviços dependentes de cartão de crédito.
     * @param codigoConvenio
     * @return Lista de TransferObject
     * @throws AutorizacaoControllerException
     */
    private boolean possuiServicosDependentesCartaoCredito(String codigoConvenio) throws AutorizacaoControllerException {
        try {
            final Convenio convenioCartao = ConvenioHome.findByPrimaryKey(codigoConvenio);

            final ListaRelacionamentosQuery queryRelacionamentos = new ListaRelacionamentosQuery();
            queryRelacionamentos.tntCodigo = CodedValues.TNT_CARTAO;
            queryRelacionamentos.svcCodigoOrigem = convenioCartao.getServico().getSvcCodigo();
            final List<TransferObject> servicosDependentes = queryRelacionamentos.executarDTO();

            return ((servicosDependentes != null) && (servicosDependentes.size() > 0));
        } catch (FindException | DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Verifica se o convênio representa um lançamento de cartão de crédito e se o valor é válido.
     * @param rseCodigo Código do registro servidor.
     * @param vlrLancamento Valor da reserva.
     * @param cnvCodigo Código do convenio da reserva
     * @param parametros Cache de parâmetros
     * @throws AutorizacaoControllerException
     */
    protected List<TransferObject> verificaLancamentoCartaoCredito(String rseCodigo, BigDecimal vlrLancamento, String cnvCodigo, Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        List<TransferObject> adesReservaCartao = null;

        if (vlrLancamento.signum() == -1) {
            // Se o valor de lançamento é negativo, não é preciso testar se a reserva é suficiente.
            return null;
        }

        Date periodo = null;
        if ((parametros != null) && !parametros.isEmpty() && (parametros.get(CodedValues.PERIODO_CARTAO_CONFIGURAVEL) != null)) {
            periodo = (Date) parametros.get(CodedValues.PERIODO_CARTAO_CONFIGURAVEL);
            parametros = parametros.size() == 1 ? null : parametros;
        }

        // Recupera o serviço de cartão do qual o convênio da reserva depende, se é que existe.
        final String servicoCartaoCredito = recuperaServicoCartaoCredito(cnvCodigo, parametros);

        if (!TextHelper.isNull(servicoCartaoCredito)) {
            adesReservaCartao = validadorCartaoCreditoController.validaLancamentoCartaoCredito(rseCodigo, vlrLancamento, cnvCodigo, servicoCartaoCredito, periodo, responsavel);
        }

        return adesReservaCartao;
    }

    /**
     * Verifica se o convênio representa uma reserva de cartão de crédito e se o valor é válido.
     * @param rseCodigo
     * @param vlrReserva
     * @param cnvCodigo
     * @throws AutorizacaoControllerException
     */
    protected void verificaReservaCartaoCredito(String rseCodigo, BigDecimal vlrReserva, String cnvCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (vlrReserva.signum() == 1) {
            // Se é uma reserva positiva, não é necessário testar lançamentos que eventualmente existam.
            return;
        }

        if (possuiServicosDependentesCartaoCredito(cnvCodigo)) {
            validadorCartaoCreditoController.validaAlteracaoReservaCartaoCredito(rseCodigo, vlrReserva, cnvCodigo, responsavel);
        }
    }

    /**
     * Verifica se os provisionamentos de margens e os lançamentos que nelas incidem estão consistentes.
     * Ou seja, testa se há valor provisionado suficiente para todos lançamentos.
     * @param rseCodigo Servidor para o qual se deseja verificar a consistência
     * @param adeCodigos Lista de ADEs a incluir como se fossem do servidor ou excluir com se dele não fossem
     * @param excluirAdesLista Indica se a lista de ADEs recebida via parâmetro deve ser excluída ou incluída na verificação
     * @throws AutorizacaoControllerException
     */
    protected void verificaProvisionamentoMargem(String rseCodigo, List<String> adeCodigos, boolean excluirAdesLista, AcessoSistema responsavel) throws AutorizacaoControllerException {
        validadorCartaoCreditoController.validaProvisiontamentoMargem(rseCodigo, adeCodigos, excluirAdesLista, responsavel);
    }

    /**
     * Verifica se a consignatária exige que o servidor seja seu correntista para um serviço específico.
     * Se existirem serviços com compartilhamento de taxas, todos eles devem ter o mesmo valor no
     * parâmetro TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA para que fiquem coerentes.
     * @param csaCodigo Código da consignatária.
     * @param csaIdentificadorInterno Identificador Interno da CSA
     * @param svcCodigo Código do serviço a ser disponibilizado no contrato.
     * @param rseBancoSal Código do banco do qual o servidor é correntista.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    private void verificaServidorCorrentista(String csaCodigo, String csaIdentificadorInterno, String svcCodigo, String rseBancoSal, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA);

            String exige = null;
            final List<TransferObject> paramCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            for (final TransferObject vo : paramCsa) {
                if ((vo.getAttribute(Columns.PSC_VLR) != null) && !"".equals(vo.getAttribute(Columns.PSC_VLR))) {
                    exige = vo.getAttribute(Columns.PSC_VLR).toString();
                }
            }

            if ("S".equals(exige) && ((csaIdentificadorInterno == null) || (rseBancoSal == null)
                    || !TextHelper.formataParaComparacao(csaIdentificadorInterno).equals(TextHelper.formataParaComparacao(rseBancoSal))) ) {

                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.realizar.operacao.pois.consignataria.esta.habilitada.apenas.para.servidor.correntista", responsavel);
            }
        } catch (final ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Valida o capital devido do novo contrato à diferença entre o capital devido vincendo
     * atual dos contratos abertos do servidor com o valor de base de cálculo do mesmo.
     * O capital devido é a soma das parcelas que ainda não foram pagas. O capital devido
     * atual só considera parcelas vincendas, realizando a diferença entre o período atual
     * de lançamento e a data fim do contrato.
     * @param rseCodigo : Código do registro servidor
     * @param rseBaseCalculo : Valor máximo do capital devido
     * @param orgCodigo : Órgão do servidor
     * @param svcCodigo : Serviço do novo contrato
     * @param adeCodigosRenegociacao : Lista de códigos que estão sendo renegociados
     * @param adeVlr : Valor do novo contrato
     * @param adeVlrLiquido : Valor líquido liberado do novo contrato
     * @param adePrazo : Prazo do novo contrato
     * @param parametros : Cache de Parâmetros
     * @param responsavel : Responsável pela operação
     * @throws AutorizacaoControllerException
     */
    private void verificaLimiteCapitalDevido(String rseCodigo, BigDecimal rseBaseCalculo, String orgCodigo, String svcCodigo, List<String> adeCodigosRenegociacao,
                                             BigDecimal adeVlr, BigDecimal adeVlrLiquido, Integer adePrazo, Map<String, Object> parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (adeVlr != null) {
            final CustomTransferObject paramLimiteCapitalDevido = getParametroSvc(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO, svcCodigo, "", false, parametros);
            final boolean limitaCapitalDevidoABaseCalculo = ((paramLimiteCapitalDevido != null) && (paramLimiteCapitalDevido.getAttribute(Columns.PSE_VLR) != null) && (CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_VLR_LIBERADO.equals(paramLimiteCapitalDevido.getAttribute(Columns.PSE_VLR)) || CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_CAPITAL_DEVIDO.equals(paramLimiteCapitalDevido.getAttribute(Columns.PSE_VLR))));
            // Caso o param de servico 192 seja PSE_VLR = 1 || 2 ele validará, caso seja 0 ele não entra na validação
            if (limitaCapitalDevidoABaseCalculo) {
                // Obtém o capital devido total atual para este servidor
                Double totalCapitalDevidoAtual = 0.0;

                try {
                    final BigDecimal capitalDevido = pesquisarConsignacaoController.pesquisarVlrCapitalDevidoAberto(rseCodigo, orgCodigo, svcCodigo, adeCodigosRenegociacao, responsavel);
                    if (capitalDevido != null) {
                        totalCapitalDevidoAtual = capitalDevido.doubleValue();
                    }
                } catch (final AutorizacaoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                // Caso o param de serviço 192 seja PSE_VLR = 2 ele pegará (vlr * prazo)
                final Double totalCapital = adeVlr.doubleValue() * (adePrazo != null ? adePrazo.intValue() : 1);

                // Calcula o capital devido do contrato atual: se foi informado valor liberado usa
                // este como o valor de comparação, senão calcula prazo x parcela (se prazo indeterminado
                // considera apenas uma parcela. Teoricamente contratos deste serviço não deveriam ter prazo nulo)
                // Caso o param de serviço 192 seja PSE_VLR = 1 primeiro ele pegará o Vlr liquido e caso não exista valor liquido ele pegara (vlr * prazo)
                final Double totalCapitalDevidoContrato = (adeVlrLiquido != null ? adeVlrLiquido.doubleValue() : totalCapital);


                // Verifica se a base de cálculo está preenchida
                final Double baseCalculoLimite = (rseBaseCalculo != null ? rseBaseCalculo.doubleValue() : 0.0);

                // Compara os valores para determinar se a nova inclusão será permitida
                if ((totalCapitalDevidoAtual + (CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_VLR_LIBERADO.equals(paramLimiteCapitalDevido.getAttribute(Columns.PSE_VLR)) ? totalCapitalDevidoContrato : totalCapital)) > baseCalculoLimite) {
                    final Double capitalDevidoMaximo = baseCalculoLimite - totalCapitalDevidoAtual;
                    if (capitalDevidoMaximo <= 0) {
                        throw new AutorizacaoControllerException("mensagem.capitalDevidoExcedeVlrBase", responsavel);
                    } else {
                        final String valorMaximo = NumberHelper.format(capitalDevidoMaximo, NumberHelper.getLang());
                        if ((adeVlrLiquido != null) && CodedValues.PSE_VLR_LIMITA_BASE_CALCULO_VLR_LIBERADO.equals(paramLimiteCapitalDevido.getAttribute(Columns.PSE_VLR))) {
                            throw new AutorizacaoControllerException("mensagem.erro.valor.liquido.liberado.maximo.permitido.para.este.contrato", responsavel, valorMaximo);
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.capital.devido.maximo.para.contrato", responsavel, valorMaximo);
                        }
                    }
                }
            }
        }
    }

    public void verificaBloqueioFuncao(String rseCodigo, String acao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_BLOQUEIO_FUNCAO_RSE, CodedValues.TPC_SIM, responsavel)) {
            final String funCodigo = ReservaMargemHelper.getFuncaoPorAcao(acao);
            if (funCodigo != null) {
                final BloqueioRseFunId id = new BloqueioRseFunId(rseCodigo, funCodigo);
                try {
                    // Se o bloqueio existir, verifica se a data limite já passou
                    final BloqueioRseFun bloqueio = BloqueioRseFunHome.findByPrimaryKey(id);
                    final Date dataLimite = bloqueio.getBrsDataLimite();
                    final Date dataAtual = DateHelper.getSystemDatetime();
                    if ((dataLimite != null) && (dataLimite.compareTo(dataAtual) > 0)) {
                        // Se a data limite ainda não chegou, então não pode realizar a operação
                        final String funDescricao = FuncaoHome.findByPrimaryKey(funCodigo).getFunDescricao();
                        final String data = DateHelper.format(dataLimite, "dd/MM/yyyy HH:mm");
                        throw new AutorizacaoControllerException("mensagem.erro.operacao.nao.pode.ser.realizada.ate.data.limite", responsavel, funDescricao, data);
                    } else {
                        // Se a data limite já passou, então remove o registro de bloqueio
                        AbstractEntityHome.remove(bloqueio);
                    }
                } catch (final FindException ex) {
                    // Bloqueio não existe, então pode fazer novas reservas
                    return;
                } catch (final RemoveException ex) {
                    LOG.error(ex.getMessage(), ex);
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }
        }
    }

    /**
     * Executa a validação da taxa de juros de acordo com os parâmetros de serviço, que dizem
     * se deve calcular IOF/TAC, se deve adicioná-los ao valor financiado, se a CSA é isenta, etc.
     * Retorna um array com os valores finais do calculo.
     * @param adeVlr
     * @param adeVlrLiquido
     * @param adeVlrTac
     * @param adeVlrIof
     * @param adeVlrMensVinc
     * @param adePrazo
     * @param adeData
     * @param adeAnoMesIni
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param alteracao
     * @param parametros
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    public BigDecimal[] validarTaxaJuros(BigDecimal adeVlr, BigDecimal adeVlrLiquido, BigDecimal adeVlrTac,
                                         BigDecimal adeVlrIof, BigDecimal adeVlrMensVinc, Integer adePrazo,
                                         Date adeData, Date adeAnoMesIni, String svcCodigo, String csaCodigo,
                                         String orgCodigo, boolean alteracao, Map<String, Object> parametros, String adePeriodicidade,
                                         String rseCodigo, AcessoSistema responsavel)
            throws AutorizacaoControllerException {

        final ValidacaoMetodoGenerico bean = buscarMetodoValidacao(responsavel);

        return bean.validarTaxaJuros(adeVlr, adeVlrLiquido, adeVlrTac, adeVlrIof, adeVlrMensVinc, adePrazo, adeData,
                adeAnoMesIni, svcCodigo, csaCodigo, orgCodigo, alteracao, parametros, adePeriodicidade, rseCodigo, responsavel);

    }

    private ValidacaoMetodoGenerico buscarMetodoValidacao(AcessoSistema responsavel)
            throws AutorizacaoControllerException {

        ValidacaoMetodoGenerico bean = null;

        final Object metodo = ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel);

        if (TextHelper.isNull(metodo) || CodedValues.MCS_BRASILEIRO.equals(metodo)) {
            bean = new ValidacaoMetodoBrasileiro();
        } else if (CodedValues.MCS_MEXICANO.equals(metodo)) {
            bean = new ValidacaoMetodoMexicano();
        } else if (CodedValues.MCS_INDIANO.equals(metodo)) {
            bean = new ValidacaoMetodoIndiano();
        } else {
            throw new AutorizacaoControllerException("mensagem.erro.metodo.calculo.validacao.naoImplementado", responsavel);
        }

        return bean;
    }

    /**
     * Calcula a taxa de juros efetiva de uma consignação, dado os valores de parcela, prazo,
     * tac, iof e valor liberado.
     * @param adeCodigo
     * @param adeVlr
     * @param adeVlrLiberado
     * @param adeVlrTac
     * @param adeVlrIof
     * @param adePrazo
     * @param adeData
     * @param adeAnoMesIni
     * @param svcCodigo
     * @param orgCodigo
     * @param simulacaoPorTaxaJuros
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    protected BigDecimal calcularTaxaJurosEfetiva(String adeCodigo, BigDecimal adeVlr, BigDecimal adeVlrLiberado, BigDecimal adeVlrTac,
                                                  BigDecimal adeVlrIof, Integer adePrazo, Date adeData, Date adeAnoMesIni, String svcCodigo, String orgCodigo,
                                                  boolean simulacaoPorTaxaJuros, AcessoSistema responsavel) throws AutorizacaoControllerException {

        return calcularTaxaJurosEfetiva(adeCodigo, adeVlr, adeVlrLiberado, adeVlrTac,
                adeVlrIof, adePrazo, adeData, adeAnoMesIni, svcCodigo, orgCodigo,
                simulacaoPorTaxaJuros, null, responsavel);
    }

    protected BigDecimal calcularTaxaJurosEfetiva(String adeCodigo, BigDecimal adeVlr, BigDecimal adeVlrLiberado, BigDecimal adeVlrTac,
                                                  BigDecimal adeVlrIof, Integer adePrazo, Date adeData, Date adeAnoMesIni, String svcCodigo, String orgCodigo,
                                                  boolean simulacaoPorTaxaJuros, String adePeriodicidade, AcessoSistema responsavel) throws AutorizacaoControllerException {

        try {
            final CustomTransferObject ade = new CustomTransferObject();
            ade.setAttribute(Columns.ADE_CODIGO, adeCodigo);
            ade.setAttribute(Columns.ADE_ANO_MES_INI, adeAnoMesIni);
            ade.setAttribute(Columns.ADE_DATA, adeData);
            ade.setAttribute(Columns.ADE_PRAZO, adePrazo);
            ade.setAttribute(Columns.ADE_VLR, adeVlr);
            ade.setAttribute(Columns.ADE_VLR_IOF, adeVlrIof);
            ade.setAttribute(Columns.ADE_VLR_TAC, adeVlrTac);
            ade.setAttribute(Columns.CDE_VLR_LIBERADO, adeVlrLiberado);

            ade.setAttribute(Columns.ADE_PERIODICIDADE, !TextHelper.isNull(adePeriodicidade) ? adePeriodicidade : PeriodoHelper.getPeriodicidadeFolha(responsavel));

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            return SimulacaoHelper.calcularTaxaJuros(ade, simulacaoPorTaxaJuros, paramSvcCse, orgCodigo, responsavel);

        } catch (ParametroControllerException | ViewHelperException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * Valida os limites de contratos ao retirar um contrato de um relacionamento de compra,
     * determinando se a situação após a operação é consistente.
     * @param adeCodigoOrigem
     * @param adeCodigoDestino
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    public void verificaLimiteAoRetirarContratoCompra(String adeCodigoOrigem, String adeCodigoDestino, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Busca os contratos comprados, pelo relacionamento de compra
            final List<RelacionamentoAutorizacao> comprados = RelacionamentoAutorizacaoHome.findByDestino(adeCodigoDestino, CodedValues.TNT_CONTROLE_COMPRA);
            if ((comprados == null) || (comprados.size() == 0)) {
                throw new AutorizacaoControllerException("mensagem.erro.recuperar.contratos.relacionados.compra", responsavel);
            }
            // Adiciona os códigos dos contratos comprados, com exceção daquele que será retirado da compra,
            // à lista que será repassada aos métodos de validação de limites
            final List<String> adeCodigosCompra = new ArrayList<>();
            for (final RelacionamentoAutorizacao rad : comprados) {
                if (!adeCodigoOrigem.equals(rad.getAdeCodigoOrigem())) {
                    adeCodigosCompra.add(rad.getAdeCodigoOrigem());
                }
            }
            // Adiciona o código do novo contrato, para também ser ignorado na verificação de limites
            adeCodigosCompra.add(adeCodigoDestino);

            // Busca o convênio/serviço do novo contrato para validação dos limites
            final AutDesconto adeDestino = AutDescontoHome.findByPrimaryKey(adeCodigoDestino);
            final VerbaConvenio vcoDestino = VerbaConvenioHome.findByPrimaryKey(adeDestino.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvDestino = ConvenioHome.findByPrimaryKey(vcoDestino.getConvenio().getCnvCodigo());
            final Servico svcDestino = ServicoHome.findByPrimaryKey(cnvDestino.getServico().getSvcCodigo());
            final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(adeDestino.getRegistroServidor().getRseCodigo());

            final String cnvCodigo = cnvDestino.getCnvCodigo();
            final String svcCodigo = svcDestino.getSvcCodigo();
            final String csaCodigo = cnvDestino.getConsignataria().getCsaCodigo();
            final String rseCodigo = registroServidor.getRseCodigo();
            final String serCodigo = registroServidor.getServidor().getSerCodigo();
            final String tgsCodigo = svcDestino.getTipoGrupoSvc() != null ? svcDestino.getTipoGrupoSvc().getTgsCodigo() : null;
            final String svcDescricao = svcDestino.getSvcDescricao();

            // Executa os métodos de verificação de limites de contratos por Grupo de Svc, por Serviço/Convênio, por natureza de serviço e Quantidade de Consignatárias
            verificaLimiteQtdContratosGrupoSvc(tgsCodigo, csaCodigo, svcCodigo, rseCodigo, adeCodigosCompra, svcDescricao, responsavel);
            verificaLimiteQtdConsignatarias(false, csaCodigo, svcCodigo, rseCodigo, serCodigo, adeCodigosCompra, null,  responsavel);
            verificaLimiteQtdContratosCnvRse(true, cnvCodigo, svcCodigo, rseCodigo, adeCodigosCompra, responsavel);
            verificaLimiteQtdContratosSvcRse(svcCodigo, rseCodigo, adeCodigosCompra, responsavel);
            verificaLimiteQtdContratosNseRse(svcCodigo, rseCodigo, adeCodigosCompra, responsavel);
            verificaLimiteQtdContratosCsaRse(csaCodigo, rseCodigo, adeCodigosCompra, responsavel);

        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica o limite de contratos que estão sendo reativados.
     * @param adeCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    public void verificaLimiteAoReativarContrato(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {

            // Adiciona o código do novo contrato, para também ser ignorado na verificação de limites
            final List<String> adeCodigos = new ArrayList<>();
            adeCodigos.add(adeCodigo);

            // Busca o convênio/serviço do novo contrato para validação dos limites
            final AutDesconto ade = AutDescontoHome.findByPrimaryKey(adeCodigo);
            final VerbaConvenio vco = VerbaConvenioHome.findByPrimaryKey(ade.getVerbaConvenio().getVcoCodigo());
            final Convenio cnv = ConvenioHome.findByPrimaryKey(vco.getConvenio().getCnvCodigo());
            final Servico svc = ServicoHome.findByPrimaryKey(cnv.getServico().getSvcCodigo());
            final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(ade.getRegistroServidor().getRseCodigo());

            final String cnvCodigo = cnv.getCnvCodigo();
            final String svcCodigo = svc.getSvcCodigo();
            final String csaCodigo = cnv.getConsignataria().getCsaCodigo();
            final String rseCodigo = registroServidor.getRseCodigo();
            final String serCodigo = registroServidor.getServidor().getSerCodigo();
            final String tgsCodigo = svc.getTipoGrupoSvc() != null ? svc.getTipoGrupoSvc().getTgsCodigo() : null;
            final String svcDescricao = svc.getSvcDescricao();

            // Executa os métodos de verificação de limites de contratos por Grupo de Svc, por Serviço/Convênio, por natureza de serviço e Quantidade de Consignatárias
            verificaLimiteQtdContratosGrupoSvc(tgsCodigo, csaCodigo, svcCodigo, rseCodigo, adeCodigos, svcDescricao, responsavel);
            verificaLimiteQtdConsignatarias(false, csaCodigo, svcCodigo, rseCodigo, serCodigo, adeCodigos, null,  responsavel);
            verificaLimiteQtdContratosCnvRse(true, cnvCodigo, svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosSvcRse(svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosNseRse(svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosCsaRse(csaCodigo, rseCodigo, adeCodigos, responsavel);

        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica o limite de contratos que estão sendo transferidos.
     * @param adeCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    public void verificaLimiteAoTransferirContrato(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {

            // Adiciona o código do novo contrato, para também ser ignorado na verificação de limites
            final List<String> adeCodigos = new ArrayList<>();
            adeCodigos.add(adeCodigo);

            // Busca o convênio/serviço do novo contrato para validação dos limites
            final AutDesconto ade = AutDescontoHome.findByPrimaryKey(adeCodigo);
            final VerbaConvenio vco = VerbaConvenioHome.findByPrimaryKey(ade.getVerbaConvenio().getVcoCodigo());
            final Convenio cnv = ConvenioHome.findByPrimaryKey(vco.getConvenio().getCnvCodigo());
            final Servico svc = ServicoHome.findByPrimaryKey(cnv.getServico().getSvcCodigo());
            final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(ade.getRegistroServidor().getRseCodigo());

            final String cnvCodigo = cnv.getCnvCodigo();
            final String svcCodigo = svc.getSvcCodigo();
            final String csaCodigo = cnv.getConsignataria().getCsaCodigo();
            final String rseCodigo = registroServidor.getRseCodigo();
            final String serCodigo = registroServidor.getServidor().getSerCodigo();
            final String tgsCodigo = svc.getTipoGrupoSvc() != null ? svc.getTipoGrupoSvc().getTgsCodigo() : null;
            final String svcDescricao = svc.getSvcDescricao();

            // Executa os métodos de verificação de limites de contratos por Grupo de Svc, por Serviço/Convênio, por natureza de serviço e Quantidade de Consignatárias
            verificaLimiteQtdContratosGrupoSvc(tgsCodigo, csaCodigo, svcCodigo, rseCodigo, adeCodigos, svcDescricao, responsavel);
            verificaLimiteQtdConsignatarias(false, csaCodigo, svcCodigo, rseCodigo, serCodigo, adeCodigos, null,  responsavel);
            verificaLimiteQtdContratosCnvRse(true, cnvCodigo, svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosSvcRse(svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosNseRse(svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosCsaRse(csaCodigo, rseCodigo, adeCodigos, responsavel);

        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica limite de contratos no cancelamento de uma renegociação, já que os contratos relacionados serão desliquidados.
     * @param adeCodigoDestino Contrato a ser cancelado.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    public void verificaLimiteAoCancelarRenegociacao(String adeCodigoDestino, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Busca os contratos renegociados
            final List<RelacionamentoAutorizacao> renegociados = RelacionamentoAutorizacaoHome.findByDestino(adeCodigoDestino, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
            if ((renegociados == null) || (renegociados.size() == 0)) {
                throw new AutorizacaoControllerException("mensagem.erro.falha.recuperar.contratos.relacionados.para.renegociacao", responsavel);
            }
            for (final RelacionamentoAutorizacao rad : renegociados) {
                final List<String> adeCodigosRenegociacao = new ArrayList<>();
                adeCodigosRenegociacao.add(rad.getAdeCodigoOrigem());

                // Busca o convênio/serviço do novo contrato para validação dos limites
                final AutDesconto adeOrigem = AutDescontoHome.findByPrimaryKey(rad.getAdeCodigoOrigem());
                final VerbaConvenio vcoOrigem = VerbaConvenioHome.findByPrimaryKey(adeOrigem.getVerbaConvenio().getVcoCodigo());
                final Convenio cnvOrigem = ConvenioHome.findByPrimaryKey(vcoOrigem.getConvenio().getCnvCodigo());
                final Servico svcOrigem = ServicoHome.findByPrimaryKey(cnvOrigem.getServico().getSvcCodigo());
                final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(adeOrigem.getRegistroServidor().getRseCodigo());

                final String cnvCodigo = cnvOrigem.getCnvCodigo();
                final String svcCodigo = svcOrigem.getSvcCodigo();
                final String csaCodigo = cnvOrigem.getConsignataria().getCsaCodigo();
                final String rseCodigo = registroServidor.getRseCodigo();
                final String serCodigo = registroServidor.getServidor().getSerCodigo();
                final String tgsCodigo = svcOrigem.getTipoGrupoSvc() != null ? svcOrigem.getTipoGrupoSvc().getTgsCodigo() : null;
                final String svcDescricao = svcOrigem.getSvcDescricao();

                // Executa os métodos de verificação de limites de contratos por Grupo de Svc, por Serviço/Convênio e Quantidade de Consignatárias
                verificaLimiteQtdContratosGrupoSvc(tgsCodigo, csaCodigo, svcCodigo, rseCodigo, adeCodigosRenegociacao, svcDescricao, responsavel);
                verificaLimiteQtdConsignatarias(false, csaCodigo, svcCodigo, rseCodigo, serCodigo, adeCodigosRenegociacao, null, responsavel);
                verificaLimiteQtdContratosCnvRse(true, cnvCodigo, svcCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
                verificaLimiteQtdContratosSvcRse(svcCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
                verificaLimiteQtdContratosNseRse(svcCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
                verificaLimiteQtdContratosCsaRse(csaCodigo, rseCodigo, adeCodigosRenegociacao, responsavel);
            }
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    public String verificaLimiteAoConsultarMargem(String rseCodigo, String orgCodigo, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            int qtdBloqCnv = 0;
            int qtdBloqSvc = 0;
            int qtdBloqNse = 0;

            final List<TransferObject> lstConveniosPermitidos = new ArrayList<>();
            final List<TransferObject> lstConvenios = convenioController.lstConvenios(null, csaCodigo, svcCodigo, orgCodigo, true, responsavel);
            for (TransferObject convenio : lstConvenios) {
                final String cnvCodigoLinha = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                final String svcCodigoLinha = convenio.getAttribute(Columns.SVC_CODIGO).toString();

                boolean temBloqCnv = false;
                boolean temBloqSvc = false;
                boolean temBloqNse = false;

                try {
                    verificaLimiteQtdContratosCnvRse(true, cnvCodigoLinha, svcCodigoLinha, rseCodigo, null, responsavel);
                } catch (AutorizacaoControllerException ex) {
                    LOG.trace(ex.getMessage(), ex);
                    // Retornou exceção, então significa que tem bloqueio de convênio para o servidor
                    temBloqCnv = true;
                    qtdBloqCnv++;
                }
                try {
                    if (!temBloqCnv) {
                        verificaLimiteQtdContratosSvcRse(svcCodigoLinha, rseCodigo, null, responsavel);
                    }
                } catch (AutorizacaoControllerException ex) {
                    LOG.trace(ex.getMessage(), ex);
                    // Retornou exceção, então significa que tem bloqueio de serviço para o servidor
                    temBloqSvc = true;
                    qtdBloqSvc++;
                }
                try {
                    if (!temBloqCnv && !temBloqSvc) {
                        verificaLimiteQtdContratosNseRse(svcCodigoLinha, rseCodigo, null, responsavel);
                    }
                } catch (AutorizacaoControllerException ex) {
                    LOG.trace(ex.getMessage(), ex);
                    // Retornou exceção, então significa que tem bloqueio de natureza de serviço para o servidor
                    temBloqNse = true;
                    qtdBloqNse++;
                }
                if (!temBloqCnv && !temBloqSvc && !temBloqNse) {
                    lstConveniosPermitidos.add(convenio);
                }
            }

            if (lstConveniosPermitidos.isEmpty()) {
                // Se não tem nenhum disponível, verifica qual mensagem/código de erro pode ser retornado
                if (qtdBloqCnv >= qtdBloqSvc && qtdBloqCnv >= qtdBloqNse) {
                    // Não é possível consultar margem, pois o servidor possui convênio(s) bloqueado(s).
                    throw new AutorizacaoControllerException("mensagem.naoPermiteConsultarMargem.bloqueio.convenio", responsavel);
                } else if (qtdBloqSvc >= qtdBloqCnv && qtdBloqSvc >= qtdBloqNse) {
                    // Não é possível consultar margem, pois o servidor possui serviço(s) bloqueado(s).
                    throw new AutorizacaoControllerException("mensagem.naoPermiteConsultarMargem.bloqueio.servico", responsavel);
                } else if (qtdBloqNse >= qtdBloqCnv && qtdBloqNse >= qtdBloqSvc) {
                    // Não é possível consultar margem, pois o servidor possui natureza(s) de serviço(s) bloqueada(s).
                    throw new AutorizacaoControllerException("mensagem.naoPermiteConsultarMargem.bloqueio.natureza.servico", responsavel);
                } else if (lstConvenios.isEmpty()) {
                    // A lista de convênios inicial é vazia, então o convênio para a CSA está bloqueado
                    throw new AutorizacaoControllerException("mensagem.nenhumConvenioAtivo", responsavel);
                } else {
                    throw new AutorizacaoControllerException("mensagem.naoPermiteConsultarMargem.bloqueio.convenio", responsavel);
                }
            } else if (lstConveniosPermitidos.size() < lstConvenios.size()) {
                final StringBuilder servicosPermitidos = new StringBuilder();
                for (int i = 0; i < lstConveniosPermitidos.size(); i++) {
                    final TransferObject convenio = lstConveniosPermitidos.get(i);
                    final String svcIdentificador = convenio.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                    final String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO).toString();
                    servicosPermitidos.append(i > 0 ? ", " : "").append(svcIdentificador).append(" - ").append(svcDescricao);
                }
                // Servidor possui bloqueio(s) e só pode realizar operações para os serviços:
                return ApplicationResourcesHelper.getMessage("mensagem.permiteConsultarMargem.servidor.possui.bloqueios.arg0", responsavel, servicosPermitidos.toString());
            }

            return null;
        } catch (ConvenioControllerException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    public void verificaBloqueioReservaAposLiquidacao(String rseCodigo, String svcCodigo, String acao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final String qteDiasBloqueio = paramSvc.getTpsQteDiasBloquearReservaAposLiq();

            if("RESERVAR".equals(acao) && (qteDiasBloqueio != null) && !"0".equals(qteDiasBloqueio)){
                final ObtemDataUltimoContratoLiquidadoPorServidorQuery query = new ObtemDataUltimoContratoLiquidadoPorServidorQuery();
                query.rseCodigo = rseCodigo;
                query.svcCodigo = svcCodigo;

                final List<TransferObject> resultado = query.executarDTO();
                if((resultado != null) && !resultado.isEmpty()){
                    final CustomTransferObject cto = (CustomTransferObject) resultado.iterator().next();
                    final Date data = (Date) cto.getAttribute(Columns.OCA_DATA);

                    if(data != null){
                        final Date proximoDiaUtil = calendarioController.findProximoDiaUtil(data, Integer.valueOf(qteDiasBloqueio));

                        if(proximoDiaUtil.after(new Date())){
                            throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.reserva.liquidacao.foi.realizada.recente.necessario.aguardar.dias.uteis.efetuar.novas.inclusoes", responsavel, DateHelper.toDateString(data), qteDiasBloqueio);
                        }
                    }
                }
            }

        } catch (ParametroControllerException | HQueryException | CalendarioControllerException e) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Valida o limite de margem que pode ser usado por consignatária
     * OBS: Só deve ser validado se TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA estiver preenchido com uma margem válida
     * @param csaCodigo
     * @param rseCodigo
     * @param adeVlr
     * @param adeCodigoAlteracao
     * @param adeCodigosRenegociacao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    public void verificaLimiteMargemPorConsignataria(String csaCodigo, String rseCodigo, BigDecimal adeVlr, String adeCodigoAlteracao, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Só será validado o limite de margem por consignatarias se o valor da reserva for informado e o parâmetro
            // TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA estiver configurado com uma margem válida
            final Short codMargemLimite = ((ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel))) ? Short.parseShort(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).toString()) : 0;
            // verifica se a margem cadastrada no parâmetro existe
            final MargemTO marTO = MargemHelper.getInstance().getMargem(codMargemLimite, responsavel);
            if (codMargemLimite.equals(CodedValues.INCIDE_MARGEM_NAO) || (marTO == null)) {
                return;
            }
            if (adeVlr != null) {
                // recupera a margem configurada para limite de consignações por csa
                MargemTO margemLimiteDisponivel = null;
                if ((codMargemLimite != null) && !codMargemLimite.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                    margemLimiteDisponivel = consultarMargemController.consultarMargemLimitePorCsa(rseCodigo, csaCodigo, codMargemLimite, adeCodigosRenegociacao, responsavel);
                    BigDecimal margemConsignavelCsa = margemLimiteDisponivel.getMrsMargemRest();
                    // Caso seja alteração de contrato, adiciona o valor original da ade na margem disponível para que a validação
                    // considere apenas a diferença dos valores alterados.
                    if (!TextHelper.isNull(adeCodigoAlteracao)) {
                        final AutDesconto adeAlteracao = AutDescontoHome.findByPrimaryKey(adeCodigoAlteracao);
                        margemConsignavelCsa = margemConsignavelCsa.add(adeAlteracao.getAdeVlr());
                    }
                    // Verifica se a consignatária tem margem disponível do servidor para realiza contrato no valor informado
                    if (adeVlr.compareTo(margemConsignavelCsa) > 0) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.inserir.ou.alterar.reserva.pois.excede.limite.margem.consignatarias", responsavel);
                    }
                }
            }
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se o servidor tem bloqueios, caso tenha, cancelamos o processo de leilão.
     * @param adeCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    public void verificaLimiteAoAprovarLeilao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {

            // Adiciona o código do novo contrato, para também ser ignorado na verificação de limites
            final List<String> adeCodigos = new ArrayList<>();
            adeCodigos.add(adeCodigo);

            // Busca o convênio/serviço do novo contrato para validação dos limites
            final AutDesconto ade = AutDescontoHome.findByPrimaryKey(adeCodigo);
            final VerbaConvenio vco = VerbaConvenioHome.findByPrimaryKey(ade.getVerbaConvenio().getVcoCodigo());
            final Convenio cnv = ConvenioHome.findByPrimaryKey(vco.getConvenio().getCnvCodigo());
            final Servico svc = ServicoHome.findByPrimaryKey(cnv.getServico().getSvcCodigo());
            final Consignataria csa = ConsignatariaHome.findByPrimaryKey(cnv.getConsignataria().getCsaCodigo());
            final String cnvCodigo = cnv.getCnvCodigo();
            final String svcCodigo = svc.getSvcCodigo();
            final String rseCodigo = ade.getRegistroServidor().getRseCodigo();
            final String csaCodigo = csa.getCsaCodigo();

            verificaLimiteQtdContratosCnvRse(true, cnvCodigo, svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosSvcRse(svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosNseRse(svcCodigo, rseCodigo, adeCodigos, responsavel);
            verificaLimiteQtdContratosCsaRse(csaCodigo, rseCodigo, adeCodigos, responsavel);

        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se o serviço da consignação permite o cancelamento somente se for a última realizada pelo servidor e se está dentro do prazo
     * limite que permite o cancelamento
     * @param adeCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    public void validarCancelamentoConsignacaoDentroPrazo(String adeCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            int horasLimiteParaCancelamento = 0;

            // Busca o parâmetro que define o limite em horas para permitir o cancelamento da última ade criada para o servidor no serviço
            final ListaParamSvcCseQuery querySvcCse = new ListaParamSvcCseQuery();
            querySvcCse.svcCodigo = svcCodigo;
            querySvcCse.tpsCodigo = CodedValues.TPS_TEMPO_LIMITE_PARA_CANCELAMENTO_ULTIMA_ADE_SERVIDOR;
            final List<TransferObject> lstParamSvcCse = querySvcCse.executarDTO();
            CustomTransferObject param = null;
            if ((lstParamSvcCse != null) && (lstParamSvcCse.size() > 0)) {
                param = (CustomTransferObject) lstParamSvcCse.get(0);
                if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)) {
                    horasLimiteParaCancelamento = Integer.parseInt(param.getAttribute(Columns.PSE_VLR).toString());
                }
            }

            // Ignora a validação caso o parâmetro que limita o tempo para cancelamento não esteja configurado.
            if (horasLimiteParaCancelamento > 0) {
                // Recupera a data de criação da ADE informada, caso esta seja a última ADE criada para o servidor do mesmo serviço

                final ObtemDataValidacaoCancelamentoConsignacaoQuery query = new ObtemDataValidacaoCancelamentoConsignacaoQuery();
                query.adeCodigo = adeCodigo;

                final List<TransferObject> resultado = query.executarDTO();
                if ((resultado == null) || resultado.isEmpty()) {
                    // Retorna erro caso a data de criação da ADE não seja retornada, indicando a existência de uma ade mais recente
                    throw new AutorizacaoControllerException("mensagem.erro.cancelar.consignacao.deferida.limite.excedido", responsavel);
                } else {
                    // Verifica se a data da ade está dentro do prazo de validade para cancelamento
                    final CustomTransferObject cto = (CustomTransferObject) resultado.iterator().next();
                    Date data = (Date) cto.getAttribute(Columns.ADE_DATA);
                    data = DateHelper.addHours(data, horasLimiteParaCancelamento);

                    final java.util.Date hoje = DateHelper.getSystemDatetime();
                    if (hoje.after(data)) {
                        throw new AutorizacaoControllerException("mensagem.erro.cancelar.consignacao.deferida.limite.excedido", responsavel);
                    }
                }
            }
        } catch (final HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica os serviços que não atingiram o limite para listar para a consignataria
     * @param rseCodigo Código do registro servidor
     * @param responsavel AcessoSistema
     * @throws AutorizacaoControllerException
     */
    public List<TransferObject> verificaLimiteServicosNaoAtigindos(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {

        // Hashs de limites por convênio, serviço e natureza e por servidor
        HashMap<String, String> hashQtdConvenio = new HashMap<>();
        HashMap<String, String> hashQtdServico = new HashMap<>();
        HashMap<String, String> hashQtdNaturezaServico = new HashMap<>();

        HashMap<String, String> hashServidorQtdConvenio = new HashMap<>();

        //Hash de Contratos por convênio, serviço e natureza.
        HashMap<String, String> hashQtdContratosConvenio = new HashMap<>();
        HashMap<String, String> hashQtdContratosServico = new HashMap<>();
        HashMap<String, String> hashQtdContratosNaturezaServico = new HashMap<>();

        //Lista de Códigos para definir as quantidades de cada contrato.
        final List<String> svcCodigos = new ArrayList<>();
        final List<String> cnvCodigos = new ArrayList<>();
        final List<String> nseCodigos = new ArrayList<>();

        //Lista de serviços que podem ser usados para reservar
        final List<TransferObject> lstConvenioSelecionados = new ArrayList<>();

        final boolean ignoraConcluir = ParamSist.paramEquals(CodedValues.TPC_IGNORA_CONTRATOS_A_CONCLUIR, CodedValues.TPC_SIM, responsavel);

        try {
            final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);

            // Lista limites de convênio
            final ListaParamSvcCseQuery querySvcCse = new ListaParamSvcCseQuery();
            querySvcCse.tpsCodigo = CodedValues.TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC;
            final List<TransferObject> lstParamSvcCseConvenio = querySvcCse.executarDTO();

            hashQtdConvenio = preparaHashGenerico(lstParamSvcCseConvenio, Columns.PSE_SVC_CODIGO, Columns.PSE_VLR);

            //Lista Limite Convenio servidor
            final ListaParamCnvRseQuery query = new ListaParamCnvRseQuery();
            query.rseCodigo = rseCodigo;
            query.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO;
            final List<TransferObject> lstParamConvenioRse = query.executarDTO();

            hashServidorQtdConvenio = preparaHashGenerico(lstParamConvenioRse, Columns.PCR_CNV_CODIGO, Columns.PCR_VLR);

            //Lista Limite Serviço
            final ListaParamSvcRseQuery querySvcRse = new ListaParamSvcRseQuery();
            querySvcRse.rseCodigo = rseCodigo;
            querySvcRse.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO;
            final List<TransferObject> lstParamSvcRse = querySvcRse.executarDTO();

            hashQtdServico = preparaHashGenerico(lstParamSvcRse, Columns.PSR_SVC_CODIGO, Columns.PSR_VLR);

            //Lista Limite Natureza Serviço
            final ListaParamNseRseQuery queryNseRse = new ListaParamNseRseQuery();
            queryNseRse.rseCodigo = rseCodigo;
            queryNseRse.tpsCodigo = CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO;
            final List<TransferObject> lstParamNseRse = queryNseRse.executarDTO();

            hashQtdNaturezaServico = preparaHashGenerico(lstParamNseRse, Columns.PNR_NSE_CODIGO, Columns.PNR_VLR);

            // Lista de serviços/Convênio que a consignatária pode fazer reserva
            final List<TransferObject> lstTodosConvenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "reservar", responsavel);
            final List<TransferObject> lstConvenio = lstTodosConvenio.stream().filter(convenio -> convenio.getAttribute(Columns.ORG_CODIGO).equals(registroServidor.getOrgCodigo())).collect(Collectors.toList());

            for (final TransferObject convenio : lstConvenio) {
                final String svcCodigo = (String) convenio.getAttribute(Columns.SVC_CODIGO);
                final String cnvCodigo = (String) convenio.getAttribute(Columns.CNV_CODIGO);
                final String nseCodigo = (String) convenio.getAttribute(Columns.NSE_CODIGO);

                if(!svcCodigos.contains(svcCodigo)) {
                    svcCodigos.add(svcCodigo);
                }

                if(!cnvCodigos.contains(cnvCodigo)) {
                    cnvCodigos.add(cnvCodigo);
                }

                if(!nseCodigos.contains(nseCodigo)) {
                    nseCodigos.add(nseCodigo);
                }
            }

            // Qtd de contratos por CNV x RSE Default
            final String qtdeMaxAdeCnvRseSist = (ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, responsavel) != null) ? ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_ADE_CNV_RSE, responsavel).toString() : null;

            //Lista de serviços e convênios com suas quantidades
            final ListaServicoLimitePermitidoQuery queryContratosSvcQtd = new ListaServicoLimitePermitidoQuery();
            queryContratosSvcQtd.rseCodigo = rseCodigo;
            queryContratosSvcQtd.svcCodigo = svcCodigos;
            queryContratosSvcQtd.ignoraConcluir = ignoraConcluir;
            final List<TransferObject> lstContratosSvcQtd = queryContratosSvcQtd.executarDTO();

            hashQtdContratosServico = preparaHashGenerico(lstContratosSvcQtd, Columns.SVC_CODIGO, "TOTAL");

            final ListaServicoLimitePermitidoQuery queryContratosCnvQtd = new ListaServicoLimitePermitidoQuery();
            queryContratosCnvQtd.rseCodigo = rseCodigo;
            queryContratosCnvQtd.cnvCodigo = cnvCodigos;
            queryContratosCnvQtd.ignoraConcluir = ignoraConcluir;
            final List<TransferObject> lstContratosCnvQtd = queryContratosCnvQtd.executarDTO();

            hashQtdContratosConvenio = preparaHashGenerico(lstContratosCnvQtd, Columns.CNV_CODIGO, "TOTAL");

            final ListaServicoLimitePermitidoQuery queryContratosNseQtd = new ListaServicoLimitePermitidoQuery();
            queryContratosNseQtd.rseCodigo = rseCodigo;
            queryContratosNseQtd.nseCodigo = svcCodigos;
            queryContratosNseQtd.ignoraConcluir = ignoraConcluir;
            final List<TransferObject> lstContratosNseQtd = queryContratosNseQtd.executarDTO();

            hashQtdContratosNaturezaServico = preparaHashGenerico(lstContratosNseQtd, Columns.NSE_CODIGO, "TOTAL");

            for (final TransferObject convenio : lstConvenio) {
                boolean convenioPermitido = false;
                boolean servicoPermitido = false;
                boolean naturezaPermitida = false;

                final String cnvCodigo = (String) convenio.getAttribute(Columns.CNV_CODIGO);
                final String svcCodigo = (String) convenio.getAttribute(Columns.SVC_CODIGO);
                final String nseCodigo = (String) convenio.getAttribute(Columns.NSE_CODIGO);

                // Validando Convênio
                String qntMaxConvenio = null;

                if(hashQtdConvenio.get(svcCodigo) != null) {
                    qntMaxConvenio = hashQtdConvenio.get(svcCodigo);
                }

                int numContratos = 0;
                if (!TextHelper.isNull(qntMaxConvenio)) {
                    numContratos = Integer.parseInt(qntMaxConvenio);
                } else if (!TextHelper.isNull(qtdeMaxAdeCnvRseSist)) {
                    numContratos = Integer.parseInt(qtdeMaxAdeCnvRseSist);
                }

                int qntConvenio = hashQtdContratosConvenio.get(cnvCodigo) != null ? Integer.parseInt(hashQtdContratosConvenio.get(cnvCodigo)) : -1;
                qntConvenio = (hashServidorQtdConvenio.get(cnvCodigo) != null) && (Integer.valueOf(hashServidorQtdConvenio.get(cnvCodigo)).compareTo(qntConvenio) > 0) ? Integer.parseInt(hashServidorQtdConvenio.get(cnvCodigo)) : qntConvenio;

                convenioPermitido = (qntConvenio == -1) || ((qntConvenio != -1) && (qntConvenio < numContratos));

                //Quando não passar pela validação passa para o próximo valor
                if((numContratos > 0) && !convenioPermitido) {
                    continue;
                }

                //Validando Natureza Serviço
                String qntMaxNse = null;
                if(hashQtdNaturezaServico.get(nseCodigo) != null) {
                    qntMaxNse = hashQtdNaturezaServico.get(nseCodigo);
                }

                String qntNseServidor = null;
                if(!TextHelper.isNull(qntMaxNse)) {
                    qntNseServidor = hashQtdContratosNaturezaServico.get(nseCodigo);
                    naturezaPermitida = !TextHelper.isNull(qntNseServidor) && (Integer.valueOf(qntNseServidor).compareTo(Integer.valueOf(qntMaxNse)) < 0);
                } else {
                    naturezaPermitida = true;
                }

                if(!naturezaPermitida) {
                    continue;
                }

                //Validando Serviço
                String qntMaxServico = null;
                if(hashQtdServico.get(svcCodigo) != null) {
                    qntMaxServico = hashQtdServico.get(svcCodigo);
                }

                String qntSvcServidor = null;
                if(!TextHelper.isNull(qntMaxServico)) {
                    qntSvcServidor = hashQtdContratosServico.get(svcCodigo);
                    servicoPermitido = !TextHelper.isNull(qntSvcServidor) && (Integer.valueOf(qntSvcServidor).compareTo(Integer.valueOf(qntMaxServico)) < 0);
                } else {
                    servicoPermitido = true;
                }

                if(!servicoPermitido) {
                    continue;
                }

                lstConvenioSelecionados.add(convenio);
            }

        } catch (ConvenioControllerException | HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return lstConvenioSelecionados;
    }

    private HashMap<String, String> preparaHashGenerico(List<TransferObject> lista, String chave, String valor) {
        final HashMap<String, String> hashGenerico = new HashMap<>();

        for(final TransferObject conteudo : lista) {
            final String key = (String) conteudo.getAttribute(chave);
            final Object value = conteudo.getAttribute(valor);

            hashGenerico.put(key, String.valueOf(value));
        }

        return hashGenerico;
    }

    public void verificaBloqueioVinculoCnvAlertaSessao(HttpSession session, String csaCodigo, String svcCodigo, String vrsCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        boolean bloqPadrao = false;
        try {
            final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel);
            bloqPadrao = CodedValues.TPA_SIM.equals(pcsVlr);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        verificaBloqueioVinculoCnv(csaCodigo, svcCodigo, vrsCodigo, responsavel);

        if (bloqPadrao && TextHelper.isNull(vrsCodigo)) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.vinculo.nao.informado", responsavel));
        }
    }

    public void validaPrazoLimiteDataAdmissaoRseTemporario(String rseCodigo, String svcCodigo, String csaCodigo, Integer prazoTotal, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            boolean rsePrazoLimiteMaiorDataAdmissao = false;
            final List<String> tpsCsaCodigos = new ArrayList<>();
            tpsCsaCodigos.add(CodedValues.TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR);
            final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCsaCodigos, false, responsavel);

            for (final TransferObject vo : paramSvcCsa) {
                if ((vo.getAttribute(Columns.PSC_VLR) != null) && !"".equals(vo.getAttribute(Columns.PSC_VLR))) {
                    String determinaLimitePrazo = null;
                    determinaLimitePrazo = vo.getAttribute(Columns.PSC_VLR).toString();
                    rsePrazoLimiteMaiorDataAdmissao = CodedValues.PSC_BOOLEANO_SIM.equals(determinaLimitePrazo);
                }
            }

            if (rsePrazoLimiteMaiorDataAdmissao) {
                final RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                final java.util.Date rseDataAdmissao = registroServidor.getRseDataAdmissao();
                final boolean rseTemporatio = TipoRegistroServidorEnum.TEMPORARIO.getCodigo().equals(registroServidor.getTrsCodigo());

                if (!rseTemporatio) {
                    return;
                }

                if (TextHelper.isNull(prazoTotal) || (prazoTotal == 0)) {
                    throw new AutorizacaoControllerException("mensagem.erro.limite.prazo.rse.temporario.indeterminado", responsavel);
                }

                if (TextHelper.isNull(rseDataAdmissao)) {
                    throw new AutorizacaoControllerException("mensagem.erro.limite.prazo.rse.temporario.data.admissao.nao.cadatrada", responsavel);
                }

                // Para calcular a data limite, precisamos verificar a data de admissão, caso o mês atual seja maior que o mẽs de aniversário
                // adicionamos 1 ano ao mês de aniversário e retiramos 1 mês. Caso contrário retiramos um mês e colocamos o ano atual.
                final int mesAniversario = DateHelper.getMonth(rseDataAdmissao);
                final int mesAtual = DateHelper.getMonth(DateHelper.getSystemDate());
                int anoLimite = DateHelper.getYear(DateHelper.getSystemDate());

                if (mesAtual >= mesAniversario) {
                    anoLimite++;
                }

                final Date dataLimite = DateHelper.getDate(anoLimite, mesAniversario - 1, DateHelper.getDay(rseDataAdmissao));
                final Date dataAtual = DateHelper.getSystemDate();
                Integer prazoLimite = DateHelper.monthDiff(dataLimite, dataAtual);

                if (dataAtual.compareTo(dataLimite) >= 0) {
                    prazoLimite = DateHelper.monthDiff(dataAtual, dataLimite);
                }

                if (prazoLimite == 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.limite.prazo.rse.temporario.ultimo.mes", responsavel);
                }

                if (prazoTotal > prazoLimite) {
                    throw new AutorizacaoControllerException("mensagem.erro.limite.prazo.rse.temporario.prazo.maior", responsavel, String.valueOf(prazoLimite));
                }
            }
        } catch (ParametroControllerException | FindException ex) {
            LOG.error(ex.getMessage());
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
