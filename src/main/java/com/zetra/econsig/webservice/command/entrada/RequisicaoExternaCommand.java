package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_ANO_MES_FIM;
import static com.zetra.econsig.webservice.CamposAPI.ADE_ANO_MES_INI;
import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_COD_REG;
import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PERIODICIDADE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_SEGURO_PRESTAMISTA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_LIQUIDO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.CNV_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACOES;
import static com.zetra.econsig.webservice.CamposAPI.COR_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.COR_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.FILTRO_AVANCADO;
import static com.zetra.econsig.webservice.CamposAPI.HISTORICO;
import static com.zetra.econsig.webservice.CamposAPI.MATRICULA_MULTIPLA;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_COMP;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DESCONTOS_FACU;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA_INST;
import static com.zetra.econsig.webservice.CamposAPI.RSE_OUTROS_DESCONTOS;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.SAD_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SER_CART_PROF;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_NASC_NO_SISTEMA;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_CONJUGE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MEIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_PIS;
import static com.zetra.econsig.webservice.CamposAPI.SER_PRIMEIRO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_ULTIMO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.USU_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;
import static com.zetra.econsig.webservice.CamposAPI.VLR_LIBERADO;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.seguranca.ControleTokenAcesso;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.persistence.entity.VinculoConsignataria;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioVinculoRegistroServidorQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;
import com.zetra.econsig.webservice.soap.entidade.SituacaoContrato;

/**
 * <p>Title: RequisicaoExternaCommand</p>
 * <p>Description: classe abstrata command da qual todos os commands relativos
 *                 à operações eConsig requisitadas externamente devem extender.
 *                 possui validações comuns e os métodos públicos a serem extendidos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RequisicaoExternaCommand.class);

    protected AcessoSistema responsavel;

    // Status de contratos para a pesquisa de autorização
    protected List<String> sadCodigos = new ArrayList<>();
    // parâmetros da requisição externa
    protected Map<CamposAPI, Object> parametros;
    protected boolean usuAutenticado;

    protected ParametroController parametroController;

    protected RequisicaoExternaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        this.parametros = parametros;
        this.responsavel = responsavel;
        if (this.responsavel != null) {
            this.responsavel.setCanal(CanalEnum.SOAP);
        }

        parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
    }

    private String versaoInterface;

    public void setVersaoInterface(String versaoInterface) {
        this.versaoInterface = versaoInterface;
    }

    public String getVersaoInterface() {
        return versaoInterface;
    }

    /**
     * método público do Command de requisição externa a ser invocado para a execução das operações de requisição externas
     * feitas ao eConsig
     * @return parâmetros da resposta à requisição externa
     */
    public final Map<CamposAPI, Object> processa() {
        try {
            validaEntrada(parametros);
            preProcessa(parametros);
            executaOperacao(parametros);
            if (parametros.get(CONSIGNACAO) != null) {
                posProcessaAutorizacao(parametros);
            }
            parametros.put(SUCESSO, "S");
            final Object mensagem = parametros.get(MENSAGEM);

            //DESENV-20689 - Adicionar uma mensagem informando que o vinculo não foi informado caso a consignataria tenha o parametro de consignataria 81 habilitado e o servidor nao tenha vínculo.
            String complementaMensagemVinculo = "";
            final String operacao = (String) parametros.get(OPERACAO);
            if (responsavel.isCsaCor() && CodedValues.OPERACOES_VERIFICA_INCL_MENSAGEM_VINCULO.contains(operacao)) {
                try {
                    final String csaCodigo = responsavel.isCsa() ? responsavel.getCsaCodigo() : responsavel.getCodigoEntidadePai();
                    final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel);
                    final boolean bloqPadrao = CodedValues.TPA_SIM.equals(pcsVlr);

                    if (bloqPadrao) {
                        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
                        if (!TextHelper.isNull(rseCodigo)) {
                            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
                            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

                            if (TextHelper.isNull(servidor.getAttribute(Columns.RSE_VRS_CODIGO))) {
                                complementaMensagemVinculo = ApplicationResourcesHelper.getMessage("mensagem.informe.registro.servidor.vinculo.nao.informado", responsavel);
                            }
                        }
                    }
                } catch (ParametroControllerException | ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            String mensagemRetorno = mensagem != null ? (String) mensagem : ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel);
            if (!mensagemRetorno.endsWith(".")) {
                mensagemRetorno += ".";
            }
            if (!TextHelper.isNull(complementaMensagemVinculo)) {
                mensagemRetorno = mensagemRetorno + " " + complementaMensagemVinculo;
            }
            parametros.put(MENSAGEM, mensagemRetorno);

            if (parametros.get(COD_RETORNO) == null) {
                parametros.put(COD_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.sucesso" + ZetraException.MENSAGEM_PROCESSAMENTO_XML, responsavel));
            }
        } catch (final ZetraException ex) {
            if (ex.getMessageKey() != null) {
                parametros.put(COD_RETORNO, ex.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
            }
            final String mensagem = ex.getMessage();
            parametros.put(MENSAGEM, mensagem != null ? mensagem : " ");
        }

        return parametros;
    }

    public AcessoSistema getResponsavel() {
        return responsavel;
    }

    /**
     * valida entradas da requisição externa antes de executar a operação solicitada. Contém
     * validações comuns a todas operações. Deve ser extendida para adicionar validações específicas
     * de cada operação.
     * @param parametros
     * @throws ZetraException
     */
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String operacao = (String) parametros.get(OPERACAO);

        if (!CodedValues.OP_VALIDAR_ACESSO_SERVIDOR.equalsIgnoreCase(operacao)) {
            validaAcessoServidor(parametros);
            if (!usuAutenticado) {
                if (responsavel.isSer()) {
                    autenticaUsuarioServidor(parametros);
                } else {
                    autenticaUsuario(parametros);
                }
            }
        }
        validaPermissao(parametros);
        validaIdentificacaoConsignacao(parametros);
        validaAdeCarencia(parametros);
    }

    /**
     * valida permissões do usuário
     * @param parametros
     * @throws ZetraException
     */
    protected void validaPermissoes(Map<CamposAPI, Object> parametros) throws ZetraException {
        if (responsavel.isSer()) {
            final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);

            if (!temPermissaoSolicitacao) {
                throw new ZetraException("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel);
            }
        }
    }

    protected void preProcessa(Map<CamposAPI, Object> parametros) throws ZetraException {
        // Cadastro de informações financeiras
        final Object vlrTacAux = parametros.get(ADE_VLR_TAC);
        if ((vlrTacAux != null) && !"".equals(vlrTacAux) && !vlrTacAux.equals(Double.NaN)) {
            parseEntradaDecimal(parametros, vlrTacAux, ADE_VLR_TAC);
        }

        final Object vlrIofAux = parametros.get(ADE_VLR_IOF);
        if ((vlrIofAux != null) && !"".equals(vlrIofAux) && !vlrIofAux.equals(Double.NaN)) {
            parseEntradaDecimal(parametros, vlrIofAux, ADE_VLR_IOF);
        }

        final Object vlrLiqAux = parametros.get(ADE_VLR_LIQUIDO);
        final Object vlrLiberadoAux = parametros.get(VALOR_LIBERADO);
        if ((vlrLiqAux != null) && !"".equals(vlrLiqAux) && !vlrLiqAux.equals(Double.NaN)) {
            parseEntradaDecimal(parametros, vlrLiqAux, ADE_VLR_LIQUIDO);
        } else if ((vlrLiberadoAux != null) && !"".equals(vlrLiberadoAux) && !vlrLiberadoAux.equals(Double.NaN)) {
            parseEntradaDecimal(parametros, vlrLiberadoAux, VALOR_LIBERADO);
        }

        final Object vlrMensVincAux = parametros.get(ADE_VLR_MENS_VINC);
        if ((vlrMensVincAux != null) && !"".equals(vlrMensVincAux) && !vlrMensVincAux.equals(Double.NaN)) {
            parseEntradaDecimal(parametros, vlrMensVincAux, ADE_VLR_MENS_VINC);
        }

        final Object vlrAdeTaxaJuros = parametros.get(ADE_TAXA_JUROS);
        if ((vlrAdeTaxaJuros != null) && !"".equals(vlrAdeTaxaJuros) && !vlrAdeTaxaJuros.equals(Double.NaN)) {
            parseEntradaDecimal(parametros, vlrAdeTaxaJuros, ADE_TAXA_JUROS);
        }

        // Cadastro Seguro Prestamista
        final Object adeVlrSegPrestamista = parametros.get(ADE_SEGURO_PRESTAMISTA);
        if ((adeVlrSegPrestamista != null) && !"".equals(adeVlrSegPrestamista) && !adeVlrSegPrestamista.equals(Double.NaN)) {
            parseEntradaDecimal(parametros, adeVlrSegPrestamista, ADE_SEGURO_PRESTAMISTA);
        }

        if (parametros.get(OPERACAO).equals(CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO) &&
                ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_MODULO_PERFIL_CONSIGNADO, responsavel)) {
            parametros.put(MATRICULA_MULTIPLA, Boolean.TRUE);
        }

        if (parametros.get(SER_CODIGO) == null) {
            validaServidor(parametros);
        }

        setSadCodigos(parametros);

        pesquisaCorrespondente(parametros);

        pesquisaAutorizacao(parametros);
    }

    private void parseEntradaDecimal(Map<CamposAPI, Object> parametros, Object vlr, CamposAPI fieldName) {
        BigDecimal vlrBigDecimal;
        if (vlr instanceof Double) {
            vlrBigDecimal =  BigDecimal.valueOf((Double) vlr);
            parametros.put(fieldName, vlrBigDecimal);
        } else {
            vlrBigDecimal = NumberHelper.parseDecimal(vlr.toString());
            parametros.put(fieldName, vlrBigDecimal);
        }
    }

    /**
     * assinatura responsável pela execução da operação específica da requisição externa
     * @param parametros - parâmetros da requisição
     * @throws ZetraException
     */
    protected abstract void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException;

    /**
     * faz um pós processamento da autorização alvo para as operações que envolvem manipulação desta
     * @param parametros
     * @throws ZetraException
     */
    private void posProcessaAutorizacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        // Não efetua o processamento abaixo para operação de consulta para compra
        // pois o usuário não tem permissão de consultar os dados das consignações
        // das demais consignatárias.

        if (CodedValues.OP_RETIRAR_CONTRATO_COMPRA.equals(parametros.get(OPERACAO))) {
            // Ao remover um contrato de compra o usuário não vai ter permissão para
            // consultar a ADE pois ela não vai mais pertencer a um relacionamento de compra
            // Evita erro na criação da resposta (RespostaConsignacoesCommand.geraRegistrosResposta()).
            parametros.remove(CONSIGNACAO);
        } else if (!CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equals(parametros.get(OPERACAO))) {
            TransferObject autorizacao = null;
            List<TransferObject> adeList = null;

            try {
                adeList = (List<TransferObject>) parametros.get(CONSIGNACAO);
                if (adeList.size() == 1) {
                    autorizacao = ((List<TransferObject>) parametros.get(CONSIGNACAO)).get(0);
                }
            } catch (final ClassCastException cce) {
                autorizacao = (TransferObject) parametros.get(CONSIGNACAO);
            }

            if (autorizacao != null) {
                final PesquisarConsignacaoController pesquisarController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

                final Object observacao = parametros.get(OBSERVACAO);
                final String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();

                // Guarda a autorizacao no hash para ser consultada na geração do resultado
                autorizacao = pesquisarController.buscaAutorizacao(adeCodigo, responsavel);

                parametros.put(CONSIGNACAO, autorizacao);

                // Insere uma ocorrência de Informação se a observação não for nula
                if ((observacao != null) && !"".equals(observacao.toString())) {
                    final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, observacao.toString(), responsavel);
                }

                // Busca o Histórico da consignação e manda para a geração da saida. Para consulta de compra, não devem ser exibidos
                // os históricos
                try {
                    final List<TransferObject> historico = pesquisarController.historicoAutorizacao(adeCodigo, false, false, responsavel);
                    parametros.put(HISTORICO, historico);
                } catch (final AutorizacaoControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    /**
     * valida servidor e adiciona info deste para ser usado na execução da operação
     * @param parametros
     * @throws ZetraException
     */
    protected void validaServidor(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String operacao = parametros.get(OPERACAO).toString();

        // Operações em que a validação não é necessário pois será feita na própria pesquisa da operação.
        if (CodedValues.OP_PESQUISAR_SERVIDOR.equalsIgnoreCase(operacao) || CodedValues.OP_PESQUISAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_MOVIMENTO_FINANCEIRO.equalsIgnoreCase(operacao) || CodedValues.OP_CADASTRAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao) || CodedValues.OP_CADASTRAR_SERVIDOR.equalsIgnoreCase(operacao)) {
            return;
        }

        final Object serCpf = parametros.get(SER_CPF);
        final Object rseMatricula = parametros.get(RSE_MATRICULA);
        final Object orgIdentificador = parametros.get(ORG_IDENTIFICADOR);
        final Object estIdentificador = parametros.get(EST_IDENTIFICADOR);
        final boolean multiploSerConsMargem = (CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) || (CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO.contains(operacao) &&
                ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_MODULO_PERFIL_CONSIGNADO, responsavel))) && (!TextHelper.isNull(parametros.get(MATRICULA_MULTIPLA)) ? Boolean.valueOf(parametros.get(MATRICULA_MULTIPLA).toString()) : false);

        String serCodigo = null; // Código do servidor
        String rseCodigo = null; // Código do registro do servidor
        String orgCodigo = null; // Código do órgão do registro do servidor
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);

        final Object cnvCodVerba = parametros.get(CNV_COD_VERBA);
        final Object svcIdentificador = parametros.get(SERVICO_CODIGO);

        Object rsePrazo = null;
        String serDataNascNoSistema = null;

        // Busca o servidor caso os parâmetros tenha sidos informados
        if (((serCpf != null) && !"".equals(serCpf)) ||
                ((rseMatricula != null) && !"".equals(rseMatricula))) {
            final List<TransferObject> servidores = pesquisaServidor(operacao, csaCodigo, serCpf, rseMatricula, orgIdentificador, estIdentificador, cnvCodVerba, svcIdentificador);

            if (servidores.isEmpty()) {
                throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
            } else if (servidores.size() == 1) {
                final TransferObject servidor = servidores.get(0);
                rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();
                serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();
                orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
                rsePrazo = servidor.getAttribute(Columns.RSE_PRAZO);

                if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_DATA_NASC))) {
                    serDataNascNoSistema = DateHelper.format((Date) servidor.getAttribute(Columns.SER_DATA_NASC), LocaleHelper.FORMATO_DATA_INGLES);
                }

                if (CodedValues.OP_CONSULTAR_MARGEM_V8_0.equalsIgnoreCase(operacao)) {
                    validaServidorUnicoConsultarMargemV8(parametros, servidor);
                }
                if (CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO.equalsIgnoreCase(operacao) &&
                        ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_MODULO_PERFIL_CONSIGNADO, responsavel)) {
                    parametros.put(SERVIDOR, servidor);
                }
            } else {
                parametros.put(SERVIDORES, servidores);
                if (!multiploSerConsMargem) {
                    throw new ZetraException("mensagem.multiplosServidoresEncontrados", responsavel);
                }
            }

            parametros.put(SER_CODIGO, serCodigo);
            parametros.put(RSE_CODIGO, rseCodigo);
            parametros.put(ORG_CODIGO, orgCodigo);
            parametros.put(RSE_PRAZO, rsePrazo);
            parametros.put(SER_DATA_NASC_NO_SISTEMA, serDataNascNoSistema);
        }
    }

    private Map<CamposAPI, Object> validaServidorUnicoConsultarMargemV8(Map<CamposAPI, Object> parametros, TransferObject servidor) {
        parametros.put(SER_NOME_MAE, servidor.getAttribute(Columns.SER_NOME_MAE));
        parametros.put(SER_NOME_PAI, servidor.getAttribute(Columns.SER_NOME_PAI));
        parametros.put(SER_CART_PROF, servidor.getAttribute(Columns.SER_CART_PROF));
        parametros.put(SER_PIS, servidor.getAttribute(Columns.SER_PIS));
        parametros.put(SER_EMAIL, servidor.getAttribute(Columns.SER_EMAIL));
        parametros.put(SER_NOME_CONJUGE, servidor.getAttribute(Columns.SER_NOME_CONJUGE));
        parametros.put(SER_NOME_MEIO, servidor.getAttribute(Columns.SER_NOME_MEIO));
        parametros.put(SER_ULTIMO_NOME, servidor.getAttribute(Columns.SER_ULTIMO_NOME));
        parametros.put(SER_PRIMEIRO_NOME, servidor.getAttribute(Columns.SER_PRIMEIRO_NOME));
        parametros.put(RSE_DESCONTOS_COMP, servidor.getAttribute(Columns.RSE_DESCONTOS_COMP));
        parametros.put(RSE_DESCONTOS_FACU, servidor.getAttribute(Columns.RSE_DESCONTOS_FACU));
        parametros.put(RSE_OUTROS_DESCONTOS, servidor.getAttribute(Columns.RSE_OUTROS_DESCONTOS));
        parametros.put(RSE_MATRICULA_INST, servidor.getAttribute(Columns.RSE_MATRICULA_INST));
        parametros.put(RSE_DATA_RETORNO, servidor.getAttribute(Columns.RSE_DATA_RETORNO));

        return parametros;
    }

    private List<TransferObject> pesquisaServidor(String operacao, String csaCodigo, Object serCpf, Object rseMatricula, Object orgIdentificador, Object estIdentificador, Object cnvCodVerba, Object svcIdentificador) throws ServidorControllerException, AutorizacaoControllerException {
        final List<TransferObject> serAtivo = new ArrayList<>();
        final List<TransferObject> serBloqueado = new ArrayList<>();
        final List<TransferObject> serExcluido = new ArrayList<>();
        final List<TransferObject> serValidado = new ArrayList<>();

        String codigoEntidade = responsavel.getCodigoEntidade();
        String tipoEntidade = responsavel.getTipoEntidade();

        if (CodedValues.OP_VALIDAR_ACESSO_SERVIDOR.equalsIgnoreCase(operacao)) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
            codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
        }

        final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
        final List<TransferObject> servidores = pesquisarServidorController.pesquisaServidor(tipoEntidade, codigoEntidade, (String) estIdentificador, (String) orgIdentificador, (String) rseMatricula, (String) serCpf, true, responsavel);

        if ((servidores != null) && (servidores.size() > 0)) {
            for (final TransferObject serCto : servidores) {
                final String srsCodigo = serCto.getAttribute(Columns.SRS_CODIGO).toString();
                // Lista os servidores não excluidos, e os bloqueados apenas em algumas operações
                if (CodedValues.SRS_ATIVO.equals(srsCodigo)) {
                    serAtivo.add(serCto);
                } else if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                    serBloqueado.add(serCto);
                } else if (CodedValues.SRS_INATIVOS.contains(srsCodigo)) {
                    serExcluido.add(serCto);
                }
            }
        }

        if (serAtivo.isEmpty()) {
            // Só retorna servidores bloqueados ou excluídos caso não tenha retornado servidores ativos.
            final boolean listaBloqueados = CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) ||
                                      CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) ||
                                      CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao) ||
                                      (CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao) &&
                    permiteIncluirAdeServidorBloqueado(csaCodigo, (String) cnvCodVerba, (String) svcIdentificador));

            final boolean listaExcluidos = ((CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao) ||
                                      CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) ||
                                      CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao) ||
                                      CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equalsIgnoreCase(operacao)
                                     ) && permiteIncluirAdeServidorExcluido(csaCodigo)) || CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) ;

            if (listaBloqueados && !serBloqueado.isEmpty()) {
                serAtivo.addAll(serBloqueado);
            } else if (listaExcluidos && !serExcluido.isEmpty()) {
                serAtivo.addAll(serExcluido);
            }
        }

        if (CodedValues.OP_COMPRAR_CONTRATO.equals(operacao) && !serAtivo.isEmpty()) {
            for (TransferObject ser : serAtivo) {
                    String vrsCodigo = (String) ser.getAttribute(Columns.VRS_CODIGO);
                    String bloqueio = verificaBloqueioVinculoCnv(csaCodigo, (String) svcIdentificador, vrsCodigo, responsavel);
                      if (TextHelper.isNull(bloqueio)) {
                         serValidado.add(ser);
                      }
            }

            if (serValidado.isEmpty()) {
                throw new AutorizacaoControllerException("mensagem.vinculoNaoPermiteReservaSer", responsavel);
            }
        } else if (!serAtivo.isEmpty()) {
            serValidado.addAll(serAtivo);
        }

        return serValidado;
    }

    /**
     * valida código de verba da requisição e seta valores do svcCodigo e cnvCodigo
     * @param parametros
     * @throws ZetraException
     */
    protected void validaCodigoVerba(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object cnvCodVerba = parametros.get(CNV_COD_VERBA);
        final Object svcIdentificador = parametros.get(SERVICO_CODIGO);
        String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final Object matricula = parametros.get(RSE_MATRICULA);
        final Object cpf = parametros.get(SER_CPF);
        final Object orgIdentificador = parametros.get(ORG_IDENTIFICADOR);
        final Object estIdentificador = parametros.get(EST_IDENTIFICADOR);
        final String operacao = parametros.get(OPERACAO).toString();
        final boolean multiploSerConsMargem = CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) && (!TextHelper.isNull(parametros.get(MATRICULA_MULTIPLA)) ? Boolean.valueOf(parametros.get(MATRICULA_MULTIPLA).toString()) : false);

        if (responsavel.isSer()) {
            final String csaIdentificador = (String) parametros.get(CSA_IDENTIFICADOR);
            if (!TextHelper.isNull(csaIdentificador)) {
                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                final ConsignatariaTransferObject consignatariaTO = consignatariaController.findConsignatariaByIdn(csaIdentificador, responsavel);
                if (consignatariaTO == null) {
                    throw new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada", responsavel);
                }

                csaCodigo = consignatariaTO.getCsaCodigo();
            }
        }

        String svcCodigo = null; // Código do serviço na reserva de margem e na simulação
        String cnvCodigo = null; // Código do convênio

        // Valida o código da verba, se foi passado
        if (((cnvCodVerba != null) && !"".equals(cnvCodVerba)) || ((svcIdentificador !=null) && !"".equals(svcIdentificador))) {
            try {
                String orgCodigo = (String) parametros.get(ORG_CODIGO);
                if (TextHelper.isNull(orgCodigo)) {
                    String serCodigo = null; // Código do servidor
                    String rseCodigo = null; // Código do registro do servidor
                    Object rsePrazo = null;
                    String serDataNascNoSistema = null;

                    if (TextHelper.isNull(cpf) && TextHelper.isNull(matricula) && TextHelper.isNull(orgIdentificador) && TextHelper.isNull(estIdentificador)) {
                        throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
                    }

                    final List<TransferObject> servidores = pesquisaServidor(operacao, csaCodigo, cpf, matricula, orgIdentificador, estIdentificador, cnvCodVerba, svcIdentificador);

                    if (servidores.isEmpty()) {
                        throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
                    } else if (servidores.size() == 1) {
                        final TransferObject servidor = servidores.get(0);
                        rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();
                        serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();
                        orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
                        rsePrazo = servidor.getAttribute(Columns.RSE_PRAZO);

                        if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_DATA_NASC))) {
                            serDataNascNoSistema = DateHelper.format((Date) servidor.getAttribute(Columns.SER_DATA_NASC), LocaleHelper.FORMATO_DATA_INGLES);
                        }
                    } else {
                        parametros.put(SERVIDORES, servidores);
                        if (!multiploSerConsMargem) {
                            throw new ZetraException("mensagem.multiplosServidoresEncontrados", responsavel);
                        }
                    }

                    parametros.put(SER_CODIGO, serCodigo);
                    parametros.put(RSE_CODIGO, rseCodigo);
                    parametros.put(ORG_CODIGO, orgCodigo);
                    parametros.put(RSE_PRAZO, rsePrazo);
                    parametros.put(SER_DATA_NASC_NO_SISTEMA, serDataNascNoSistema);
                }

                final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
                final List<TransferObject> servicos = convenioController.getSvcByCodVerbaSvcIdentificador((String)svcIdentificador, (String)cnvCodVerba, orgCodigo, csaCodigo, true, responsavel);
                if (servicos.isEmpty()) {
                    throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
                } else if (servicos.size() == 1) {
                    final TransferObject convenio = servicos.get(0);
                    svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
                    cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
                } else {
                    final Map<Object, TransferObject> svcCodigosDistintos = new HashMap<>();

                    for (final TransferObject servico: servicos) {
                        if (!svcCodigosDistintos.containsKey(servico.getAttribute(Columns.SVC_CODIGO))) {
                            svcCodigosDistintos.put(servico.getAttribute(Columns.SVC_CODIGO), servico);
                        }
                    }

                    if (svcCodigosDistintos.size() == 1) {
                        final Collection<TransferObject> svcVlr = svcCodigosDistintos.values();
                        final TransferObject convenio = svcVlr.iterator().next();
                        svcCodigo = convenio.getAttribute(Columns.SVC_CODIGO).toString();
                    } else {
                        parametros.put(SERVICOS, Arrays.asList(svcCodigosDistintos.values().toArray()));
                        throw new ZetraException("mensagem.maisDeUmServicoEncontrado", responsavel);
                    }
                }
            } catch (final ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.verba.ou.servico.invalido", responsavel);
            }
        } else if ((CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao)) &&
                ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel)) {

            final String orgCodigo = responsavel.getOrgCodigo();
            final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
            final boolean temPermissaoReserva   = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
            final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);

            final List<TransferObject> servicosSimulacao = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, csaCodigo, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, true, null, responsavel);

            if ((servicosSimulacao == null) || servicosSimulacao.isEmpty()) {
                throw new ZetraException("mensagem.convenioNaoEncontrado", responsavel);
            } else if (servicosSimulacao.size() > 1) {
                parametros.put(SERVICOS, servicosSimulacao);
                throw new ZetraException("mensagem.maisDeUmServicoEncontrado", responsavel);
            } else {
                svcCodigo = servicosSimulacao.get(0).getAttribute(Columns.SVC_CODIGO).toString();
            }
        }

        parametros.put(SVC_CODIGO, svcCodigo);
        parametros.put(CNV_CODIGO, cnvCodigo);
    }

    protected void setSadCodigos(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String operacao = parametros.get(OPERACAO).toString();

        // Seta os status para cada uma das operações
        if (CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_AGUARD_CONF_E_DEF, CodedValues.TPC_SIM, responsavel)) {
                sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
                sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
            }
            if (responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ALTERAR_ADE_ESTOQUE, CodedValues.TPC_SIM, responsavel)) {
                sadCodigos.add(CodedValues.SAD_ESTOQUE);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
            }
        } else if (CodedValues.OP_ATUALIZAR_PARCELA.equalsIgnoreCase(operacao)) {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_LIQUIDADA);
        } else if (CodedValues.OPERACOES_AUTORIZAR_RESERVA.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
        } else if (CodedValues.OPERACOES_CONFIRMAR_RESERVA.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        } else if (CodedValues.OPERACOES_CONFIRMAR_SOLICITACAO.contains(operacao) || CodedValues.OP_CANCELAR_SOLICITACAO.equalsIgnoreCase(operacao)) {
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
        } else if (CodedValues.OPERACOES_CANCELAR_RESERVA.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
            sadCodigos.add(CodedValues.SAD_AGUARD_MARGEM);
        } else if (CodedValues.OPERACOES_CANCELAR_CONSIGNACAO.contains(operacao) || CodedValues.OP_CANCELAR_CONSIGNACAO_SV.equalsIgnoreCase(operacao)) {
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
        } else if (CodedValues.OPERACOES_SUSPENDER_CONSIGNACAO.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
        } else if (CodedValues.OPERACOES_REATIVAR_CONSIGNACAO.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
        } else if (CodedValues.OP_LIQUIDAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)) {
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        } else if (CodedValues.OPERACOES_LIQUIDAR_CONSIGNACAO.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
        } else if (CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equalsIgnoreCase(operacao)) {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            if(ParamSist.getBoolParamSist(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, responsavel) && CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao)) {
                sadCodigos.add(CodedValues.SAD_SUSPENSA);
            }
        } else if (CodedValues.OP_EDT_SALDO_DEVEDOR.equalsIgnoreCase(operacao) || CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
            sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
            sadCodigos.add(CodedValues.SAD_EMCARENCIA);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        } else if (CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        }  else if (CodedValues.OPERACOES_CANCELAR_RENEGOCIACAO.contains(operacao)) {
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
        } else if (CodedValues.OP_INCLUIR_ANEXO_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_INCLUIR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            sadCodigos.add(CodedValues.NOT_EQUAL_KEY);
            sadCodigos.add(CodedValues.SAD_INDEFERIDA);
            sadCodigos.add(CodedValues.SAD_LIQUIDADA);
            sadCodigos.add(CodedValues.SAD_CANCELADA);
            sadCodigos.add(CodedValues.SAD_CONCLUIDO);
            sadCodigos.add(CodedValues.SAD_ENCERRADO);
        } else if (CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao)) {
            final SituacaoContrato statusAde = getSituacaoContrato(parametros.get(SAD_CODIGO));

            if (statusAde != null) {
                if (Boolean.TRUE.equals(statusAde.getAguardandoConfirmacao())) {
                    sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
                }
                if (Boolean.TRUE.equals(statusAde.getAguardandoDeferimento())) {
                    sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
                }
                if (Boolean.TRUE.equals(statusAde.getAguardandoLiquidacao())) {
                    sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
                }
                if (Boolean.TRUE.equals(statusAde.getAguardandoLiquidacaoCompra())) {
                    sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
                }
                if (Boolean.TRUE.equals(statusAde.getCancelada())) {
                    sadCodigos.add(CodedValues.SAD_CANCELADA);
                }
                if (Boolean.TRUE.equals(statusAde.getConcluido())) {
                    sadCodigos.add(CodedValues.SAD_CONCLUIDO);
                }
                if (Boolean.TRUE.equals(statusAde.getDeferida())) {
                    sadCodigos.add(CodedValues.SAD_DEFERIDA);
                }
                if (Boolean.TRUE.equals(statusAde.getEmAndamento())) {
                    sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
                }
                if (Boolean.TRUE.equals(statusAde.getEmCarencia())) {
                    sadCodigos.add(CodedValues.SAD_EMCARENCIA);
                }
                if (Boolean.TRUE.equals(statusAde.getEstoque())) {
                    sadCodigos.add(CodedValues.SAD_ESTOQUE);
                }
                if (Boolean.TRUE.equals(statusAde.getEstoqueMensal())) {
                    sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
                }
                if (Boolean.TRUE.equals(statusAde.getEstoqueNaoLiberado())) {
                    sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
                }
                if (Boolean.TRUE.equals(statusAde.getIndeferida())) {
                    sadCodigos.add(CodedValues.SAD_INDEFERIDA);
                }
                if (Boolean.TRUE.equals(statusAde.getLiquidada())) {
                    sadCodigos.add(CodedValues.SAD_LIQUIDADA);
                }
                if (Boolean.TRUE.equals(statusAde.getSolicitado())) {
                    sadCodigos.add(CodedValues.SAD_SOLICITADO);
                }
                if (Boolean.TRUE.equals(statusAde.getSuspensa())) {
                    sadCodigos.add(CodedValues.SAD_SUSPENSA);
                }
                if (Boolean.TRUE.equals(statusAde.getSuspensaPeloConsignante())) {
                    sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
                }
            }
        }
    }

    protected void validaAcessoServidor(Map<CamposAPI, Object> parametros) throws ZetraException {
        // Operações que não exigem autenticação do usuário servidor
        final boolean autenticaServidor = !CodedValues.OPERACAO_NAO_AUTENTICA_SERVIDOR.contains(parametros.get(OPERACAO).toString());

        final Object usuLogin = parametros.get(USUARIO);
        final Object usuSenha = parametros.get(SENHA);
        final Object matricula = parametros.get(RSE_MATRICULA);

        if (!responsavel.isSer() && (TextHelper.isNull(usuLogin) || TextHelper.isNull(usuSenha))) {
            throw new ZetraException("mensagem.informe.usuario.senha", responsavel);
        } else if (responsavel.isSer() && autenticaServidor && (TextHelper.isNull(matricula) || TextHelper.isNull(usuSenha))) {
            throw new ZetraException("mensagem.informe.ser.matricula.senha", responsavel);
        }
    }

    /**
     * pesquisa autorização alvo de acordo com filtros enviados pela requisição externa
     * @param parametros
     * @throws ZetraException
     */
    protected void pesquisaAutorizacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String operacao = parametros.get(OPERACAO).toString();
        final Object adeNumero = parametros.get(ADE_NUMERO);
        final Object adeIdentificador = parametros.get(ADE_IDENTIFICADOR);

        if (CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OP_ATUALIZAR_PARCELA.equalsIgnoreCase(operacao) ||
                CodedValues.OPERACOES_AUTORIZAR_RESERVA.contains(operacao) ||
                CodedValues.OPERACOES_CANCELAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OP_CANCELAR_CONSIGNACAO_SV.equalsIgnoreCase(operacao) ||
                CodedValues.OP_CANCELAR_SOLICITACAO.equalsIgnoreCase(operacao) ||
                CodedValues.OPERACOES_CANCELAR_RESERVA.contains(operacao) ||
                CodedValues.OPERACOES_CONFIRMAR_RESERVA.contains(operacao) ||
                CodedValues.OPERACOES_CONFIRMAR_SOLICITACAO.contains(operacao) ||
                CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_LIQUIDAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_REATIVAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao) ||
                CodedValues.OP_EDT_SALDO_DEVEDOR.equalsIgnoreCase(operacao) ||
                CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equalsIgnoreCase(operacao) ||
                CodedValues.OPERACOES_CANCELAR_RENEGOCIACAO.contains(operacao) ||
                CodedValues.OP_INCLUIR_ANEXO_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao) ||
                CodedValues.OP_LIQUIDAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao) ||
                CodedValues.OPERACOES_SUSPENDER_CONSIGNACAO.contains(operacao) ||
                CodedValues.OP_CANCELAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao) ||
                CodedValues.OP_RETIRAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)  ||
                CodedValues.OP_INF_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao) ||
                CodedValues.OP_REJ_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao)  ||
                CodedValues.OP_SOL_RECALC_SALDO_DEVEDOR.equalsIgnoreCase(operacao) ||
                CodedValues.OP_INCLUIR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                CodedValues.OP_CONSULTAR_PARCELA.equalsIgnoreCase(operacao) ||
                CodedValues.OP_LIQUIDAR_PARCELA.equalsIgnoreCase(operacao) ||
                CodedValues.OP_LISTAR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                CodedValues.OP_DESLIQUIDAR_CONTRATO.equalsIgnoreCase(operacao) ||
                CodedValues.OP_DOWNLOAD_ANEXOS_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase(operacao)
                ) {

            try {
                CustomTransferObject criterio = new CustomTransferObject();
                if (CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao)) {
                    criterio.setAttribute("TIPO_OPERACAO", "renegociar");
                    criterio.setAttribute(Columns.CNV_COD_VERBA, parametros.get(CNV_COD_VERBA));
                    criterio.setAttribute(Columns.SVC_CODIGO, parametros.get(SVC_CODIGO));
                    criterio.setAttribute(Columns.CSA_CODIGO, parametros.get(CSA_CODIGO));
                    criterio.setAttribute(Columns.ORG_CODIGO, parametros.get(ORG_CODIGO));
                    criterio.setAttribute(Columns.COR_CODIGO, parametros.get(COR_CODIGO));
                } else if (CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao)) {
                    criterio.setAttribute("TIPO_OPERACAO", "alterar");
                } else if (CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equalsIgnoreCase(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao)) {
                    criterio.setAttribute("TIPO_OPERACAO", "comprar");
                    criterio.setAttribute(Columns.CNV_COD_VERBA, parametros.get(CNV_COD_VERBA));
                    criterio.setAttribute(Columns.SVC_CODIGO, parametros.get(SVC_CODIGO));
                    criterio.setAttribute(Columns.CSA_CODIGO, parametros.get(CSA_CODIGO));
                    criterio.setAttribute(Columns.ORG_CODIGO, parametros.get(ORG_CODIGO));
                    criterio.setAttribute(Columns.COR_CODIGO, parametros.get(COR_CODIGO));
                } else if (CodedValues.OPERACOES_CANCELAR_RENEGOCIACAO.contains(operacao)) {
                    criterio.setAttribute("TIPO_OPERACAO", "cancelar_renegociacao");
                } else if (CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao)) {
                    criterio.setAttribute(Columns.COR_CODIGO, parametros.get(COR_CODIGO));
                } else if (CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao)) {
                    criterio = (CustomTransferObject) parametros.get(FILTRO_AVANCADO);
                    criterio.setAttribute(Columns.CSA_CODIGO, parametros.get(CSA_CODIGO));
                    criterio.setAttribute(Columns.COR_CODIGO, parametros.get(COR_CODIGO));
                    criterio.setAttribute(Columns.SVC_CODIGO, parametros.get(SVC_CODIGO));
                } else if (CodedValues.OPERACOES_LIQUIDAR_CONSIGNACAO.contains(operacao)) {
                    criterio.setAttribute("TIPO_OPERACAO", "liquidar");
                } else if (CodedValues.OP_DESLIQUIDAR_CONTRATO.equalsIgnoreCase(operacao)) {
                    criterio.setAttribute("TIPO_OPERACAO", "desliquidar");
                } else if (CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao)) {
                    cancelarSolicitacoesParamCsa(parametros);
                } else if (CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
                    final List<String> listaTipoSolicitacaoSaldo = new ArrayList<>();
                    listaTipoSolicitacaoSaldo.add(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO.getCodigo());
                    listaTipoSolicitacaoSaldo.add(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo());
                    listaTipoSolicitacaoSaldo.add(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo());

                    criterio.setAttribute("operacaoSOAPEditarSaldoDevedor", true);
                    criterio.setAttribute("listaTipoSolicitacaoSaldo", listaTipoSolicitacaoSaldo);
                }

                try {
                    List<Long> adeNumeros = new ArrayList<>();
                    if ((adeNumero != null) && (adeNumero instanceof Long)) {
                        adeNumeros.add((Long) adeNumero);
                    } else if ((adeNumero != null) && (adeNumero instanceof String)) {
                        adeNumeros.add(Long.valueOf(adeNumero.toString()));
                    } else {
                        adeNumeros = (List<Long>) adeNumero;
                    }

                    if ((adeNumeros != null) && !adeNumeros.isEmpty()) {
                        for (final Object adeNmVlr : adeNumeros) {
                            recuperaAutorizacao(parametros, adeNmVlr, adeIdentificador, criterio);
                        }
                        if (CodedValues.OP_CONSULTAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao) && (parametros.get(CONSIGNACAO) == null)) {
                            throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
                        } else if (CodedValues.OP_CONSULTAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao) && (parametros.get(CONSIGNACAO) != null)) {
                            final List<TransferObject> consignacoes = (List<TransferObject>) parametros.get(CONSIGNACAO);
                            if (adeNumeros.size() != consignacoes.size()) {
                                parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.alerta.pesquisa.consignacao.nao.encontrada", responsavel));
                            }
                        } else if (verificarSeOperacaoRenegociarConsignacao(operacao) && (parametros.get(CONSIGNACAO) != null)) {
                            final List<TransferObject> consignacoes = (List<TransferObject>) parametros.get(CONSIGNACAO);
                            Set<Long> adesFaltantes = buscarAdesFaltantesAdeNumero(adeNumeros, consignacoes);
                            if (!adesFaltantes.isEmpty()) {
                                throw new ZetraException("mensagem.erro.renegociacao.consignacao.nao.encontrada.revisar.antes.nova.requisicao", responsavel, adesFaltantes.toString());
                            }
                        }
                    } else {
                    	if (CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao)) {
	                    	List<String> adeIdentificadores = new ArrayList<>();
	                        if (adeIdentificador != null) {
	                        	if (adeIdentificador instanceof Long) {
	                        		adeIdentificadores.add(String.valueOf(adeIdentificador));
	                            } else if (adeIdentificador instanceof String) {
	                            	adeIdentificadores.add(adeIdentificador.toString());
	                            } else {
	                            	adeIdentificadores = (List<String>) adeIdentificador;
	                            }
	                        }
	                        // se não foi informado adeNumeros, tenta busca por identificador
	                    	if(adeIdentificadores != null && !adeIdentificadores.isEmpty()) {
	                    		 for (final Object adeIdent : adeIdentificadores) {
	                    			 recuperaAutorizacaoByIdn(parametros, adeIdent, criterio);
	                    		 }
	                    	}

                            if (parametros.get(CONSIGNACAO) != null){
                                final List<TransferObject> consignacoes = (List<TransferObject>) parametros.get(CONSIGNACAO);
                                Set<String> adesFaltantes = buscarAdesFaltantesAdeIdentificadores(adeIdentificadores, consignacoes);
                                if (!adesFaltantes.isEmpty()) {
                                    throw new ZetraException("mensagem.erro.renegociacao.consignacao.nao.encontrada.revisar.antes.nova.requisicao", responsavel, adesFaltantes.toString());
                                }
                            }
                            
	                    } else {
	                    	recuperaAutorizacaoByIdn(parametros, adeIdentificador, criterio);
	                    }
                    }
                } catch (final ClassCastException ccex) {
                    recuperaAutorizacao(parametros, adeNumero, adeIdentificador, criterio);
                }

            } catch (final AutorizacaoControllerException ex) {
                throw new ZetraException(ex);
            }
        }
    }

    private Set<Long> buscarAdesFaltantesAdeNumero(List<Long> adeNumeros, final List<TransferObject> consignacoes) {
        return adeNumeros.stream().filter(ade -> consignacoes.stream().noneMatch(cons -> ade.equals(cons.getAttribute(Columns.ADE_NUMERO)))).collect(Collectors.toSet());
    }

    private Set<String> buscarAdesFaltantesAdeIdentificadores(List<String> adeIdentificadores, final List<TransferObject> consignacoes) {
        return adeIdentificadores.stream().filter(ade -> consignacoes.stream().noneMatch(cons -> ade.equals(cons.getAttribute(Columns.ADE_IDENTIFICADOR)))).collect(Collectors.toSet());
    }

    private boolean verificarSeOperacaoRenegociarConsignacao(final String operacao) {
        return CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao);
    }

    public void autenticaUsuario(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object usuLogin = parametros.get(USUARIO);
        final Object usuSenha = parametros.get(SENHA);
        String csaCodigo = null;
        String corCodigo = null;

        if ((usuLogin == null) || "".equals(usuLogin) || (usuSenha == null) || "".equals(usuSenha)) {
            throw new ZetraException("mensagem.informe.usuario.senha", responsavel);
        } else {
            try {
                final TransferObject usuario = autentica(usuLogin.toString(), usuSenha.toString());
                if (usuario != null) {
                    // Pega o código do usuário
                    final Object usuCodigo = usuario.getAttribute(Columns.USU_CODIGO);
                    final Object usuNome = usuario.getAttribute(Columns.USU_NOME);
                    parametros.put(USU_CODIGO, usuCodigo);
                    parametros.put(USU_NOME, usuNome);
                    responsavel.setUsuLogin(usuLogin.toString());
                    responsavel.setUsuCodigo(usuCodigo.toString());
                    responsavel.setUsuNome(usuNome.toString());
                    responsavel.setQtdConsultasMargem((Integer) usuario.getAttribute(Columns.USU_QTD_CONSULTAS_MARGEM));
                    responsavel.setCanal(CanalEnum.SOAP);

                    final boolean usuCentralizador = (usuario.getAttribute(Columns.USU_CENTRALIZADOR) != null) && "S".equals(usuario.getAttribute(Columns.USU_CENTRALIZADOR));

                    if ((usuario.getAttribute(Columns.UCA_CSA_CODIGO) != null) &&
                            !"".equals(usuario.getAttribute(Columns.UCA_CSA_CODIGO))) {
                        // Usuário de Consignatária
                        csaCodigo = usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString();

                        // Busca os dados da consignatária
                        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                        final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);

                        if (!usuCentralizador && !podeRequisitarHostaHost(csaCodigo)) {
                            throw new ZetraException("mensagem.ipUsuarioInvalido", responsavel);
                        }

                        parametros.put(CSA_CODIGO, csaCodigo);
                        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSA);
                        responsavel.setCodigoEntidade(csaCodigo);
                        responsavel.setNomeEntidade(csa.getCsaNome());
                        responsavel.setIdEntidade(csa.getCsaIdentificador());
                        responsavel.setNcaCodigo(csa.getCsaNcaNatureza());

                    } else if ((usuario.getAttribute(Columns.UCO_COR_CODIGO) != null) &&
                            !"".equals(usuario.getAttribute(Columns.UCO_COR_CODIGO))) {
                        // Usuário de Correspondente
                        corCodigo = usuario.getAttribute(Columns.UCO_COR_CODIGO).toString();

                        // Pega o código da consignatária
                        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                        final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(corCodigo, responsavel);
                        csaCodigo = cor.getCsaCodigo();

                        // Busca os dados da consignatária
                        final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);

                        if (!usuCentralizador && !podeRequisitarHostaHost(csaCodigo)) {
                            throw new ZetraException("mensagem.ipUsuarioInvalido", responsavel);
                        }

                        parametros.put(COR_CODIGO, corCodigo);
                        parametros.put(CSA_CODIGO, csaCodigo);
                        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_COR);
                        responsavel.setCodigoEntidade(corCodigo);
                        responsavel.setCodigoEntidadePai(csaCodigo);
                        responsavel.setNomeEntidade(cor.getCorNome());
                        responsavel.setNomeEntidadePai(csa.getCsaNome());
                        responsavel.setIdEntidade(cor.getCorIdentificador());
                        responsavel.setNcaCodigo(csa.getCsaNcaNatureza());

                    } else if ((usuario.getAttribute(Columns.UCE_CSE_CODIGO) != null) &&
                            !"".equals(usuario.getAttribute(Columns.UCE_CSE_CODIGO))) {
                        // Usuário de Consignante
                        final String cseCodigo = usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString();
                        parametros.put(CSE_CODIGO, cseCodigo);
                        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_CSE);
                        responsavel.setCodigoEntidade(cseCodigo);

                    } else if ((usuario.getAttribute(Columns.USP_CSE_CODIGO) != null) &&
                            !"".equals(usuario.getAttribute(Columns.USP_CSE_CODIGO))) {
                        // Usuário de Suporte
                        final String cseCodigo = usuario.getAttribute(Columns.USP_CSE_CODIGO).toString();
                        parametros.put(CSE_CODIGO, cseCodigo);
                        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SUP);
                        responsavel.setCodigoEntidade(cseCodigo);

                    } else {
                        throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
                    }

                    try {
                        // Busca as permissões do usuário
                        final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
                        responsavel.setPermissoes(usuarioController.selectFuncoes(responsavel.getUsuCodigo(), responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), responsavel));
                        usuAutenticado = true;

                        // Grava log de login sucesso
                        final LogDelegate log = new LogDelegate(responsavel, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_SUCESSO);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.log.versao.interface.arg0", responsavel, versaoInterface != null ? versaoInterface : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel)));
                        log.write();

                    } catch (final UsuarioControllerException ex) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.usu.permissoes", responsavel), ex);
                        throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
                    }
                }
            } catch (final ZetraException ex) {
                final LogDelegate log = new LogDelegate (responsavel, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_ERRO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.login.arg0", responsavel, usuLogin != null ? usuLogin.toString() : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel)));
                log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.arg0", responsavel, ex.getMessage()));
                log.add(ApplicationResourcesHelper.getMessage("mensagem.log.versao.interface.arg0", responsavel, versaoInterface != null ? versaoInterface : ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel)));
                log.write();

                throw ex;
            }
        }
    }

    public void autenticaUsuarioServidor(Map<CamposAPI, Object> parametros) throws ZetraException {
        // Operações que não exigem autenticação do usuário servidor
        final boolean autenticaServidor = !CodedValues.OPERACAO_NAO_AUTENTICA_SERVIDOR.contains(parametros.get(OPERACAO).toString());

        final Object usuSenha = parametros.get(SENHA);
        final Object matricula = parametros.get(RSE_MATRICULA);
        Object orgIdentificador = parametros.get(ORG_IDENTIFICADOR);
        Object estIdentificador = parametros.get(EST_IDENTIFICADOR);

        String usuCodigo = null;
        String usuNome = null;

        final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);

        if (autenticaServidor && (TextHelper.isNull(matricula) || TextHelper.isNull(usuSenha))) {
            throw new ZetraException("mensagem.informe.ser.matricula.senha", responsavel);
        } else {
            final List<TransferObject> lstUsuarios = usuarioController.lstUsuariosSer(null, (String) matricula, (String) estIdentificador, (String) orgIdentificador, responsavel);

            if ((lstUsuarios == null) || lstUsuarios.isEmpty()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nenhum.encontrado", responsavel));
                throw new ZetraException("mensagem.usuarioSenhaInvalidos", responsavel);
            }

            if (lstUsuarios.size() > 1) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.multiplo", responsavel));
                throw new ZetraException("mensagem.usuarioSenhaInvalidos", responsavel);
            }

            // Recupera o login do servidor
            final TransferObject usuarioSer = lstUsuarios.get(0);
            final String serLogin = usuarioSer.getAttribute(Columns.USU_LOGIN).toString();
            usuCodigo = usuarioSer.getAttribute(Columns.USU_CODIGO).toString();
            usuNome = usuarioSer.getAttribute(Columns.SER_NOME).toString();

            if (autenticaServidor && (autentica(serLogin, usuSenha.toString()) == null)) {
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }
        }

        if (!TextHelper.isNull(usuCodigo)) {
            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
            final TransferObject ctoUsuario = pesquisarServidorController.buscaUsuarioServidor(usuCodigo, null, (String) matricula, (String) orgIdentificador, (String) estIdentificador, responsavel);

            //Verifica se o acesso do servidor é permitido
            final String serAcessaHostaHost = (String) ctoUsuario.getAttribute(Columns.SER_ACESSA_HOST_A_HOST);
            if(!TextHelper.isNull(serAcessaHostaHost) && "N".equals(serAcessaHostaHost)){
                throw new ZetraException("mensagem.servidor.nao.pode.acessar.host.a.host", responsavel);
            }

            // Verifica se o acesso do servidor ao módulo host a host está limitado por IP
            final String limitaIpAcessoHostAHostSer = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LIMITA_IP_ACESSO_HOST_A_HOST_SER, responsavel);
            if (!TextHelper.isNull(limitaIpAcessoHostAHostSer)) {
                final String ipsAcesso[] = limitaIpAcessoHostAHostSer.replace(" ", "").split(",|;");
                final List<String> listIpsAcesso = Arrays.asList(ipsAcesso);
                if (!listIpsAcesso.contains(responsavel.getIpUsuario())) {
                    throw new ZetraException("mensagem.ipUsuarioInvalido", responsavel);
                }
            }

            final Object rseCodigo = ctoUsuario.getAttribute(Columns.RSE_CODIGO);
            final Object serCodigo = ctoUsuario.getAttribute(Columns.SER_CODIGO);
            final String serNome = (String) ctoUsuario.getAttribute(Columns.SER_NOME);
            final String serCpf = (String) ctoUsuario.getAttribute(Columns.SER_CPF);
            final String serEmail = (String) ctoUsuario.getAttribute(Columns.SER_EMAIL);
            final String srsCodigo = (String) ctoUsuario.getAttribute(Columns.SRS_CODIGO);

            if (TextHelper.isNull(orgIdentificador)) {
                orgIdentificador = ctoUsuario.getAttribute(Columns.ORG_IDENTIFICADOR);
            }

            if (TextHelper.isNull(estIdentificador)) {
                estIdentificador = ctoUsuario.getAttribute(Columns.EST_IDENTIFICADOR);
            }

            // Pega o código do usuário
            parametros.put(USU_CODIGO, usuCodigo);
            parametros.put(USU_NOME, usuNome);
            parametros.put(RSE_CODIGO, rseCodigo);
            parametros.put(SER_CODIGO, serCodigo);
            parametros.put(SER_CPF, serCpf);
            parametros.put(ORG_IDENTIFICADOR, orgIdentificador);
            parametros.put(EST_IDENTIFICADOR, estIdentificador);

            responsavel.setUsuCodigo(usuCodigo);
            responsavel.setUsuNome(usuNome);
            responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
            responsavel.setCodigoEntidade((String) serCodigo);
            responsavel.setNomeEntidade(serNome);
            responsavel.setCanal(CanalEnum.SOAP);
            responsavel.setDadosServidor((String) ctoUsuario.getAttribute(Columns.EST_CODIGO), (String) ctoUsuario.getAttribute(Columns.ORG_CODIGO), (String) rseCodigo, (String) matricula, serCpf, serEmail, null, srsCodigo);

            try {
                // Busca as permissões do usuário
                final Map<String, EnderecoFuncaoTransferObject> funcoes = usuarioController.selectFuncoes(responsavel.getUsuCodigo(), responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), responsavel);
                // Inclui a função de consultar margem para o servidor
                funcoes.put(CodedValues.FUN_CONS_MARGEM, new EnderecoFuncaoTransferObject(CodedValues.FUN_CONS_MARGEM, CodedValues.OP_CONSULTAR_MARGEM));
                responsavel.setPermissoes(funcoes);
                usuAutenticado = true;
            } catch (final UsuarioControllerException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.usu.permissoes", responsavel), ex);
                throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
            }

        } else {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }
    }

    /**
     * verifica se consignatária permite que usuários que não sejam do centralizador façam requisições
     * Host-a-Host
     * @param csaCodigo
     * @return
     * @throws ParametroControllerException
     */
    private boolean podeRequisitarHostaHost(String csaCodigo) {
        try {
            final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO, responsavel);
            return !TextHelper.isNull(pcsVlr) && "S".equals(pcsVlr);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Verifica se para a consignatária é permitido inclusão de consignações
     * para servidores excluídos
     * @param csaCodigo
     * @return
     * @throws ParametroControllerException
     */
    protected boolean permiteIncluirAdeServidorExcluido(String csaCodigo) {
        try {
            final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_INC_ADE_RSE_EXCLUIDO_HOST_A_HOST, responsavel);
            return !TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Verifica se permite inclusão de consignação para servidor bloqueado serviço/convênio informado
     * @param csaCodigo
     * @param cnvCodVerba
     * @param svcIdentificador
     * @return
     */
    protected boolean permiteIncluirAdeServidorBloqueado(String csaCodigo, String cnvCodVerba, String svcIdentificador) {
        try {
            // Se verba ou serviço foram informados, pesquisa os serviços para determinar se algum
            // deles permite inclusão em servidor bloqueado
            if (!TextHelper.isNull(cnvCodVerba) || !TextHelper.isNull(svcIdentificador)) {
                final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
                final List<TransferObject> servicos = convenioController.getSvcByCodVerbaSvcIdentificador(svcIdentificador, cnvCodVerba, null, csaCodigo, false, responsavel);
                if ((servicos != null) && !servicos.isEmpty()) {
                    // Se encontrou algum serviço, verifica se algum deles permite inclusão de consignação para servidor bloqueado
                    for (final TransferObject servico : servicos) {
                        final String svcCodigo = servico.getAttribute(Columns.SVC_CODIGO).toString();
                        final CustomTransferObject paramCTO = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO, responsavel);
                        if ((paramCTO != null) && (paramCTO.getAttribute(Columns.PSE_VLR) != null) && "1".equals(paramCTO.getAttribute(Columns.PSE_VLR))) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } catch (ParametroControllerException | ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * valida permissão do usuário informado na requisição se pode executar a operação em questão.
     * @param parametros
     * @throws ZetraException
     */
    protected void validaPermissao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String operacao = (String) parametros.get(OPERACAO);
        final Object token = parametros.get(TOKEN);

        // Verifica se o usuário tem permissão para executar a função desejada
        final boolean exibeMargemViaToken = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_MARGEM_INT_XML_TOKEN_SER, responsavel);
        if (exibeMargemViaToken && CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) && !TextHelper.isNull(token)) {
            // Usuário pode consultar margem
        } else if (!CodedValues.OP_VALIDAR_ACESSO_SERVIDOR.equalsIgnoreCase(operacao) &&
                !temPermissao(responsavel.getUsuCodigo(), responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), operacao)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }
    }

    protected void validaDataNascimento(Map<CamposAPI, Object> parametros) throws ZetraException {
        try {
            final String svcCodigo = (String) parametros.get(SVC_CODIGO);
            final String csaCodigo = (String) parametros.get(CSA_CODIGO);

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            boolean dataNascObrigatoria = paramSvcCse.isTpsValidarDataNascimentoNaReserva();

            if (dataNascObrigatoria) {
                // Verifica parâmetro de consignatária que sobrepõe a validação de data de nascimento
                final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_DATA_NASCIMENTO_OBRIGATORIA_HOST_A_HOST, responsavel);
                if (!TextHelper.isNull(pcsVlr)) {
                    dataNascObrigatoria = "S".equalsIgnoreCase(pcsVlr);
                }
            }

            if (dataNascObrigatoria) {
                if (TextHelper.isNull(parametros.get(DATA_NASC))) {
                    throw new ZetraException("mensagem.dataNascNaoInformada", responsavel);
                } else {
                    final String serDataNascNoSistema = (String) parametros.get(SER_DATA_NASC_NO_SISTEMA);
                    String serDataNascInformada = null;

                    if (parametros.get(DATA_NASC) instanceof Date) {
                        serDataNascInformada = DateHelper.format((Date) parametros.get(DATA_NASC), LocaleHelper.FORMATO_DATA_INGLES);
                    } else {
                        serDataNascInformada = DateHelper.reformat(parametros.get(DATA_NASC).toString(), LocaleHelper.getDatePattern(), LocaleHelper.FORMATO_DATA_INGLES);
                    }

                    parametros.put(DATA_NASC, serDataNascInformada);

                    final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
                    if (!servidorController.isDataNascServidorValida(serDataNascInformada, serDataNascNoSistema, svcCodigo, LocaleHelper.FORMATO_DATA_INGLES, responsavel)) {
                        throw new ZetraException("mensagem.dataNascNaoConfere", responsavel);
                    }
                }
            }
        } catch (final ParseException ex) {
            throw new ZetraException("mensagem.dataNascMalFormatada", responsavel);
        } catch (final ParametroControllerException ex) {
            throw new ZetraException(ex);
        }
    }

    /**
     * verifica se campos que identificam a autorização alvo da operação foram informados corretamente
     * @param parametros
     * @throws ZetraException
     */
    protected void validaIdentificacaoConsignacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object adeNumero = parametros.get(ADE_NUMERO);
        final Object adeIdentificador = parametros.get(ADE_IDENTIFICADOR);
        final String operacao = (String) parametros.get(OPERACAO);

        // Se o número da autorização e o identificador da autorização são nulos
        if (((adeNumero == null) || "".equals(adeNumero)) &&
                ((adeIdentificador == null) || "".equals(adeIdentificador))) {
            if ((CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao) ||
                    CodedValues.OPERACOES_AUTORIZAR_RESERVA.contains(operacao) ||
                    CodedValues.OPERACOES_CONFIRMAR_RESERVA.contains(operacao) ||
                    CodedValues.OPERACOES_CONFIRMAR_SOLICITACAO.contains(operacao) ||
                    CodedValues.OP_CANCELAR_SOLICITACAO.equalsIgnoreCase(operacao) ||
                    CodedValues.OPERACOES_CANCELAR_RESERVA.contains(operacao) ||
                    CodedValues.OPERACOES_CANCELAR_CONSIGNACAO.contains(operacao) ||
                    CodedValues.OP_CANCELAR_CONSIGNACAO_SV.equalsIgnoreCase(operacao) ||
                    CodedValues.OPERACOES_SUSPENDER_CONSIGNACAO.contains(operacao) ||
                    CodedValues.OPERACOES_REATIVAR_CONSIGNACAO.contains(operacao) ||
                    CodedValues.OPERACOES_LIQUIDAR_CONSIGNACAO.contains(operacao) ||
                    CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao) ||
                    CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) ||
                    CodedValues.OP_EDT_SALDO_DEVEDOR.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_INF_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_REJ_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao)  ||
                    CodedValues.OP_SOL_RECALC_SALDO_DEVEDOR.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_CANCELAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_RETIRAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)  ||
                    CodedValues.OP_LIQUIDAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_INCLUIR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_CONSULTAR_PARCELA.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_LIQUIDAR_PARCELA.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_LISTAR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) &&
                    ((adeNumero == null) || "".equals(adeNumero)) &&
                    ((adeIdentificador == null) || "".equals(adeIdentificador))) {
                throw new ZetraException("mensagem.informe.ade.numero.ou.identificador", responsavel);

            } else if (CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) ||
                    CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao) ||
                    CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao) ||
                    CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_CONS_DADOS_CADASTRAIS.equalsIgnoreCase(operacao) ||
                    CodedValues.OP_CONS_DADOS_CADASTRAIS_V3_0.equalsIgnoreCase(operacao) ||
                    CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) ||
                    CodedValues.OP_VALIDAR_ACESSO_SERVIDOR.equalsIgnoreCase(operacao)) {

                validaCpfMatricula(parametros);
            }
        }

        if ((adeNumero instanceof String) && CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao)) {
            final String [] adeNumeros = adeNumero.toString().split("(,|;)",0);

            if (adeNumeros.length > 1) {
                if (!TextHelper.isNull(adeIdentificador)) {
                    throw new ZetraException("mensagem.erro.ade.ident.nao.informar.renegociacao.multipla", responsavel);
                } else {
                    parametros.put(ADE_NUMERO, Arrays.asList(adeNumeros));
                }
            }
        }
    }

    protected void validaCpfMatricula(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object serCpf = parametros.get(SER_CPF);
        final Object rseMatricula = parametros.get(RSE_MATRICULA);

        final boolean requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);

        if (requerMatriculaCpf && ((serCpf == null) || "".equals(serCpf) || (rseMatricula == null) || "".equals(rseMatricula))) {
            throw new ZetraException("mensagem.requerMatrCpf", responsavel);
        } else if (!requerMatriculaCpf && ((serCpf == null) || "".equals(serCpf)) && ((rseMatricula == null) || "".equals(rseMatricula))) {
            throw new ZetraException("mensagem.requerMatrOuCpf", responsavel);
        } else /* verificar se a matrícula tem o número de caracteres esperado */
        if ((rseMatricula != null) && !"".equals(rseMatricula)) {
            try {
                final Object param2 = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel);
                if (rseMatricula.toString().length() < Integer.parseInt(param2.toString())) {
                    throw new ZetraException("mensagem.erro.matricula.invalida", responsavel);
                }
                final Object param3 = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavel);
                final int tamMaxMatricula = (param3 != null) && !"".equals(param3) ? Integer.parseInt(param3.toString()) : 0;

                if ((tamMaxMatricula > 0) && (rseMatricula.toString().length() > tamMaxMatricula)) {
                    throw new ZetraException("mensagem.erro.matricula.invalida", responsavel);
                }
            } catch (final NumberFormatException ex) {
                throw new ZetraException("mensagem.erro.parametro.sistema.matricula.tam.max.invalido", responsavel);
            }
        }
    }

    /**
     * verifica se o valor da ade está informado e sem é um valor válido
     * @param parametros
     * @throws ZetraException
     */
    protected void validaValorAutorizacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object adeVlr = parametros.get(VALOR_PARCELA);
        final String operacao = (String) parametros.get(OPERACAO);
        Object vlrLiberado = parametros.get(VALOR_LIBERADO);

        if ((adeVlr == null) || "".equals(adeVlr)) {
            if (CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao)) {
                if ((vlrLiberado == null) || "".equals(vlrLiberado)) {
                    throw new ZetraException("mensagem.informe.valor.parcela.ou.valor.liberado", responsavel);
                } else {
                    // Trata o valor liberado
                    parseEntradaDecimal(parametros, vlrLiberado, VLR_LIBERADO);
                    vlrLiberado = parametros.get(VLR_LIBERADO);
                    if (vlrLiberado != null) {
                        if (((BigDecimal) vlrLiberado).doubleValue() <= 0) {
                            throw new ZetraException("mensagem.erro.valor.liberado.maior.zero", responsavel);
                        }
                    } else {
                        throw new ZetraException("mensagem.erro.valor.liberado.incorreto", responsavel);
                    }
                }
            } else if (!CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao)) {
                // Na operação Alterar Consignacao o adeVlr é opcional
                throw new ZetraException("mensagem.informe.ade.valor", responsavel);
            } else {
                adeVlr = null;
            }
        } else {
            // Trata o valor da autorização
            BigDecimal adeVlrAux = null;

            if (adeVlr instanceof Double) {
                adeVlrAux = BigDecimal.valueOf((Double) adeVlr);
            } else {
                adeVlrAux = NumberHelper.parseDecimal(adeVlr.toString());
                if (adeVlrAux == null) {
                    throw new ZetraException("mensagem.valorParcelaInvalido", responsavel);
                }
            }

            if (adeVlrAux.doubleValue() <= 0) {
                final String svcCodigo = (String) parametros.get(SVC_CODIGO);
                final String csaCodigo = (String) parametros.get(CSA_CODIGO);
                if (!parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel)) {
                    throw new ZetraException("mensagem.valorParcelaMenorIgualZero", responsavel);
                }
            }
            parametros.put(ADE_VLR, adeVlrAux);

        }
    }

    /**
     * valida se código de verba e código de serviço estão preenchidos
     * @param parametros
     * @throws ZetraException
     */
    protected void validaCnvCodigoSvcCodigo(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object cnvCodVerba = parametros.get(CNV_COD_VERBA);
        final Object svcIdentificador = parametros.get(SERVICO_CODIGO);

        if (((cnvCodVerba == null) || "".equals(cnvCodVerba)) && ((svcIdentificador == null) || "".equals(svcIdentificador))) {
            throw new ZetraException("mensagem.informe.verba.ou.servico", responsavel);
        }
    }

    /**
     * valida se prazo da ade foi informado e está no formato válido
     * @param parametros
     * @throws ZetraException
     */
    protected void validaAdePrazo(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object adePrazo = parametros.get(PRAZO);
        final String operacao = (String) parametros.get(OPERACAO);

        if ((adePrazo == null) || "".equals(adePrazo)) {
            if (CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao) ||
                    CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao)) {
                throw new ZetraException("mensagem.informe.ade.prazo", responsavel);
            } else {
                adePrazo = null;
            }
        } else {
            try {
                adePrazo = Integer.valueOf(adePrazo.toString());
                if (((Integer) adePrazo).intValue() == 0) {
                    parametros.put(ADE_PRAZO, null);
                } else {
                    parametros.put(ADE_PRAZO, adePrazo);
                }
            } catch (final NumberFormatException ex) {
                throw new ZetraException("mensagem.qtdParcelasInvalida", responsavel);
            }
        }
    }

    /**
     * valida formato do valor informado do prazo de carência
     * @param parametros
     * @throws ZetraException
     */
    protected void validaAdeCarencia(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String operacao = (String) parametros.get(OPERACAO);
        Object adeCarencia = parametros.get(ADE_CARENCIA);

        if (CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao) ||
                CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao) ||
                CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao)) {
            // Trata o valor da carência
            if ((adeCarencia != null) && !"".equals(adeCarencia)) {
                if ((adeCarencia instanceof Integer) && (((Integer) adeCarencia).intValue() == Integer.MIN_VALUE)) {
                    parametros.put(ADE_CARENCIA, Integer.valueOf(0));
                    return;
                }

                try {
                    adeCarencia = Integer.valueOf(adeCarencia.toString());
                    parametros.put(ADE_CARENCIA, adeCarencia);
                } catch (final NumberFormatException ex) {
                    throw new ZetraException("mensagem.erro.carencia.incorreta", responsavel);
                }
            } else {
                parametros.put(ADE_CARENCIA, Integer.valueOf(0));
            }
        } else {
            parametros.put(ADE_CARENCIA, Integer.valueOf(0));
        }
    }

    /**
     * valida se senha do servidor foi informada para os casos necessários
     * @param parametros
     * @throws ZetraException
     */
    protected void validaPresencaSenhaServidor(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object serSenha = responsavel.isSer() ? parametros.get(SENHA) : parametros.get(SER_SENHA);
        final Object token = parametros.get(TOKEN);
        final String operacao = (String) parametros.get(OPERACAO);

        if ((CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) ||
                CodedValues.OPERACOES_AUTORIZAR_RESERVA.contains(operacao) ||
                CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equalsIgnoreCase(operacao) ||
                CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao)) &&
                TextHelper.isNull(serSenha) && TextHelper.isNull(token)) {
            throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
        }
    }

    protected void validaInfoBancaria(Map<CamposAPI, Object> parametros) throws ZetraException {
        final Object numBanco = parametros.get(RSE_BANCO);
        final Object numAgencia = parametros.get(RSE_AGENCIA);
        final Object numConta = parametros.get(RSE_CONTA);

        String svcCodigo = (String) parametros.get(SVC_CODIGO);
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);

        // Se os parâmetros obrigatórios são nulos, realiza a pesquisa de consignação para obtê-los
        // caso tenham sido passados as informações do contrato
        if (TextHelper.isNull(svcCodigo) || TextHelper.isNull(rseCodigo)) {
            final String adeNumero = parametros.get(ADE_NUMERO) != null ? parametros.get(ADE_NUMERO).toString() : null;
            final Object adeIdentificador = parametros.get(ADE_IDENTIFICADOR) != null ? parametros.get(ADE_IDENTIFICADOR).toString() : null;

            if (!TextHelper.isNull(adeNumero) || !TextHelper.isNull(adeIdentificador)) {
                final PesquisarConsignacaoController pesquisarController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
                final List<TransferObject> consignacoes = pesquisarController.pesquisaAutorizacao(responsavel.getTipoEntidade(), csaCodigo, null, TextHelper.objectToStringList(adeNumero), TextHelper.objectToStringList(adeIdentificador), null, null, null, responsavel);

                if ((consignacoes != null) && !consignacoes.isEmpty()) {
                    if (consignacoes.size() == 1) {
                        final TransferObject consignacao = consignacoes.get(0);
                        svcCodigo = (String) consignacao.getAttribute(Columns.SVC_CODIGO);
                        rseCodigo = (String) consignacao.getAttribute(Columns.RSE_CODIGO);
                    } else {
                        parametros.put(CONSIGNACOES, consignacoes);
                        throw new ZetraException("mensagem.maisDeUmaConsignacaoEncontrada", responsavel);
                    }
                } else  {
                    throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
                }
            }
        }

        if (TextHelper.isNull(svcCodigo)) {
            throw new ZetraException("mensagem.erro.nenhum.servico.encontrado", responsavel);
        }

        // Verifica se as informações bancárias são obrigatórias
        final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        boolean infBancariaObrigatoria = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, paramSvc.isTpsInfBancariaObrigatoria(), null, responsavel);

        if (infBancariaObrigatoria) {
            // Verifica parâmetro de consignatária que sobrepõe a validação de informação bancária
            final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_BANCARIA_OBRIGATORIA_HOST_A_HOST, responsavel);
            if (!TextHelper.isNull(pcsVlr)) {
                infBancariaObrigatoria = "S".equalsIgnoreCase(pcsVlr);
            }
        }

        if (infBancariaObrigatoria && (TextHelper.isNull(numBanco) || TextHelper.isNull(numAgencia) || TextHelper.isNull(numConta))) {
            throw new ZetraException("mensagem.informacaoBancariaObrigatoria", responsavel);
        }

        if (infBancariaObrigatoria && paramSvc.isTpsValidarInfBancariaNaReserva()) {
            if (TextHelper.isNull(rseCodigo)) {
                throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
            }

            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final RegistroServidorTO rseResultTo = servidorController.findRegistroServidor(rseCodigo, responsavel);
            if (rseResultTo != null) {
                final RegistroServidorTO rse = new RegistroServidorTO();
                rse.setRseAgenciaSal(rseResultTo.getRseAgenciaSal());
                rse.setRseBancoSal(rseResultTo.getRseBancoSal());
                rse.setRseContaSal(rseResultTo.getRseContaSal());
                rse.setRseBancoSalAlternativo(rseResultTo.getRseBancoSalAlternativo());
                rse.setRseAgenciaSalAlternativa(rseResultTo.getRseAgenciaSalAlternativa());
                rse.setRseContaSalAlternativa(rseResultTo.getRseContaSalAlternativa());

                validarDadosBancariosServidor(infBancariaObrigatoria, paramSvc.isTpsValidarInfBancariaNaReserva(), (String) numBanco, (String) numAgencia, (String) numConta, rse);
            }
        }
    }

    /**
     * Autentica o usuário que está tentando realizar uma operação.
     * Verifica se o login e a senha são corretos
     * @param usuLogin : login do usuário
     * @param usuSenha : senha do usuário
     * @return Informações sobre o usuário
     * @throws ZetraException
     */
    protected TransferObject autentica(String usuLogin, String usuSenha) throws ZetraException {
        return UsuarioHelper.autenticarUsuario(usuLogin, usuSenha, responsavel);
    }

    /**
     * Verifica se o usuário tem permissão para executar a operação
     * @param usuCodigo : código do usuário
     * @param entidade  : código da entidade a que pertence o usuário
     * @param tipo      : COR ou CSA
     * @param operacao  : operação que está sendo executada
     * @return          : true se o usuário tem permissão, falso caso contrário
     */
    private boolean temPermissao(String usuCodigo, String entidade, String tipo, String operacao) throws ZetraException {
        // Se sistema esta bloqueado, não tem permissão de fazer nada
        final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
        final Short status = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, AcessoSistema.getAcessoUsuarioSistema());
        final boolean indisponivel = status.equals(CodedValues.STS_INDISP);
        if (indisponivel) {
            throw new ZetraException("mensagem.restricao.acesso.geral", responsavel);
        }

        // Se é usuário de consignante, só pode fazer consulta de margem, então rejeita qualquer outra operação
        if (responsavel.isCse() && !CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) &&
                !CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) &&
                !CodedValues.OP_CADASTRAR_SERVIDOR.equalsIgnoreCase(operacao) &&
                !CodedValues.OP_CADASTRAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao) &&
                !CodedValues.OP_EDITAR_STATUS_SERVIDOR.equalsIgnoreCase(operacao) &&
                !CodedValues.OP_EDITAR_STATUS_SERVIDOR_V8_0.equalsIgnoreCase(operacao)) {
            return false;
        }

        // Se é usuário de órgão ou servidor, não tem permissão de fazer nada.
        // DESENV-18849: Devido a necessidade de recuperar perguntas no cadastro de email surgiu a exceção para o usuário suporte neste caso de uso
        if (responsavel.isOrg() || (responsavel.isSup() && !CodedValues.OP_RECUPERAR_PERG_DADOS_CAD.contains(operacao) && !CodedValues.OP_VERIFICA_RESP_PERG_DADOS.contains(operacao)
                && !CodedValues.OP_CADASTRAR_EMAIL_SERVIDOR.contains(operacao))) {
            return false;
        }

        // Consultar perfil consignado deverá ser disponibilizada para os papéis CSA/COR
        if (!responsavel.isCsaCor() && CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO.contains(operacao)) {
            return false;
        }
        
        // Consultar regras deverá ser disponibilizada para os papéis CSA/COR
        if (!responsavel.isCsaCor() && CodedValues.OP_CONSULTAR_REGRAS.contains(operacao)) {
            return false;
        }

        // Determina qual função de acordo com a operação
        String funCodigo = null;

        if (CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_ALONGAR_CONTRATO;
        } else if (CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_ALT_CONSIGNACAO;
        } else if (CodedValues.OPERACOES_AUTORIZAR_RESERVA.contains(operacao)) {
            funCodigo = CodedValues.FUN_AUT_RESERVA;
        } else if (CodedValues.OPERACOES_CANCELAR_CONSIGNACAO.contains(operacao) || CodedValues.OP_CANCELAR_CONSIGNACAO_SV.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CANC_CONSIGNACAO;
        } else if (CodedValues.OPERACOES_CANCELAR_RESERVA.contains(operacao)) {
            funCodigo = CodedValues.FUN_CANC_RESERVA;
        } else if (CodedValues.OPERACOES_CONFIRMAR_RESERVA.contains(operacao)) {
            funCodigo = CodedValues.FUN_CONF_RESERVA;
            if (!responsavel.temPermissao(CodedValues.FUN_CONF_RESERVA) && responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_RENEGOCIACAO)) {
                funCodigo = CodedValues.FUN_CONFIRMAR_RENEGOCIACAO;
            }
        } else if (CodedValues.OPERACOES_CONFIRMAR_SOLICITACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_CONF_SOLICITACAO;
        } else if (CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_CONS_CONSIGNACAO;
        } else if (CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao)) {
            funCodigo = CodedValues.FUN_CONS_MARGEM;
        } else if (CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao)) {
            funCodigo = responsavel.isSer() ? CodedValues.FUN_SIM_CONSIGNACAO : CodedValues.FUN_RES_MARGEM;
        } else if (CodedValues.OPERACOES_LIQUIDAR_CONSIGNACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_LIQ_CONTRATO;
        } else if (CodedValues.OP_LISTA_SOLICITACOES.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_LISTAR_SOLICITACAO_CONTRATOS;
        } else if (CodedValues.OPERACOES_REATIVAR_CONSIGNACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_REAT_CONSIGNACAO;
        } else if (CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_RENE_CONTRATO;
        } else if (CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao)) {
            funCodigo = CodedValues.FUN_RES_MARGEM;
        } else if (CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao)) {
            funCodigo = responsavel.isSer() ? CodedValues.FUN_SIM_CONSIGNACAO : CodedValues.FUN_RES_MARGEM;
        } else if (CodedValues.OPERACOES_SUSPENDER_CONSIGNACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_SUSP_CONSIGNACAO;
        } else if (CodedValues.OP_ACOMPANHAR_COMPRA.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_ACOMPANHAR_COMPRA_CONTRATOS;
        } else if (CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_COMP_CONTRATO;
        } else if (CodedValues.OP_EDT_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            if (responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR) || responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER)) {
            	return true;
            }
        } else if (CodedValues.OP_INF_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_INFORMAR_PGT_SALDO_DEVEDOR;
        } else if (CodedValues.OP_SOL_RECALC_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_SOL_RECALCULO_SALDO_DEVEDOR;
        } else if (CodedValues.OP_RETIRAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_RETIRAR_CONTRATO_COMPRA;
        } else if (CodedValues.OP_CANCELAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CANC_COMPRA;
        } else if (CodedValues.OP_LIQUIDAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)) {
            //para liquidar compra deve-se ter permissão de liquidar consignação
            funCodigo = CodedValues.FUN_LIQ_CONTRATO;
        } else if (CodedValues.OP_REJ_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_REJEITAR_PGT_SALDO_DEVEDOR;
        } else if (CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_COMP_CONTRATO;
        } else if (CodedValues.OPERACOES_CANCELAR_RENEGOCIACAO.contains(operacao)) {
            funCodigo = CodedValues.FUN_CANC_RENEGOCIACAO;
        } else if (CodedValues.OP_INCLUIR_ANEXO_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO;
        } else if (CodedValues.OP_GERAR_SENHA_AUTORIZACAO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_ALTERAR_SENHA_AUTORIZACAO_USU_SER;
        } else if (CodedValues.OP_CONSULTAR_CONTRACHEQUE.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CONS_CONTRACHEQUE;
        } else if (CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao)) {
            funCodigo = CodedValues.FUN_PESQUISA_AVANCADA_CONSIGNACAO;
        } else if (CodedValues.OP_CANCELAR_SOLICITACAO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CANC_SOLICITACAO;
        } else if (CodedValues.OP_CONS_DADOS_CADASTRAIS.equalsIgnoreCase(operacao) || CodedValues.OP_CONS_DADOS_CADASTRAIS_V3_0.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CONS_DADOS_CADASTRAIS_SERVIDOR;
        } else if (CodedValues.OP_VERIFICA_LIMITE_SENHA_AUT.equalsIgnoreCase(operacao) || CodedValues.OP_RECUPERAR_PERG_DADOS_CAD.equalsIgnoreCase(operacao) || CodedValues.OP_VERIFICA_RESP_PERG_DADOS.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_ALTERAR_SENHA_AUTORIZACAO_USU_SER;
        } else if (CodedValues.OP_INCLUIR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_ALT_CONSIGNACAO;
        } else if (CodedValues.OP_LISTAR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_PARCELA.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CONS_CONSIGNACAO;
        } else if (CodedValues.OP_LIQUIDAR_PARCELA.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_LIQUIDAR_PARCELA;
        } else if (CodedValues.OP_CADASTRAR_TAXA_JUROS.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_TAXA_JUROS;
        } else if (CodedValues.OPERACOES_PESQUISAR_SERVIDOR.contains(operacao)) {
            funCodigo = CodedValues.FUN_PESQUISAR_SERVIDOR;
        } else if (CodedValues.OP_CADASTRAR_SERVIDOR.equalsIgnoreCase(operacao) || CodedValues.OP_CADASTRAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao)) {
            if(responsavel.isCsaCor()){
                funCodigo = CodedValues.FUN_INCLUIR_CONSIGNACAO;
            } else if (responsavel.isCseSupOrg()){
                funCodigo = CodedValues.FUN_CADASTRAR_SERVIDOR;
            } else {
                throw new UnsupportedOperationException("Usuário não é Csa, Cor, Cse, Sup ou Org");
            }
        } else if (CodedValues.OP_EDITAR_STATUS_SERVIDOR.equalsIgnoreCase(operacao) || CodedValues.OP_EDITAR_STATUS_SERVIDOR_V8_0.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_EDT_STATUS_REGISTRO_SERVIDOR;
        } else if (CodedValues.OP_EDITAR_STATUS_USUARIO.equalsIgnoreCase(operacao) || CodedValues.OP_CADASTRAR_USUARIO_OPERACIONAL.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_PARAMETROS.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_PARAMETROS_v2_0.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_PARAMETROS_v8_0.equalsIgnoreCase(operacao)) {
            // Validar alteração de status do usuário depende de qual tipo de usuário está sendo alterado, validação será realizada no command.
            return true;
        } else if (((CodedValues.OP_VERIFICAR_EMAIL_SERVIDOR.equalsIgnoreCase(operacao) ||
                CodedValues.OP_CADASTRAR_EMAIL_SERVIDOR.equalsIgnoreCase(operacao)) && responsavel.isSer()) || (responsavel.isSup() && CodedValues.OP_CADASTRAR_EMAIL_SERVIDOR.equalsIgnoreCase(operacao))) {
            // Se é usuário servidor realizando operações de validação de e-mail então não exige permissão especial
            // Quando é o usuário suporte fazendo o cadastro de email, neste momento é só verificação e não há éfetivação em si
            return true;
        } else if (CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER.equalsIgnoreCase(operacao) || CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER_V8_0.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_VALIDAR_DADOS_BANCARIOS_SER_HOST_A_HOST;
        } else if (CodedValues.OP_DESLIQUIDAR_CONTRATO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_DESLIQ_CONTRATO;
        } else if (CodedValues.OP_DOWNLOAD_ANEXOS_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CONS_CONSIGNACAO;
        } else if(CodedValues.OP_LISTAR_SOLICITACAO_SALDO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_LISTAR_SOLICITACAO_SALDO_DEVEDOR;
        } else if(CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_EDT_SALDO_DEVEDOR;
        } else if(CodedValues.OP_LISTAR_PARCELAS.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CONS_CONSIGNACAO;
        } else if (CodedValues.OP_CONSULTAR_VALIDACAO_DOCUMENTACAO_v8_0.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_VALIDAR_DOCUMENTOS;
        } else if (CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_CONSULTAR_PERFIL_CONSIGNADO;
        } else if (CodedValues.OP_CONSULTAR_REGRAS.equalsIgnoreCase(operacao)) {
            funCodigo = CodedValues.FUN_REL_REGRAS_CONVENIO;
        } else {
            // Se não for nenhuma das acima, então não tem permissão
            return false;
        }

        if ((funCodigo == null) || !responsavel.temPermissao(funCodigo)) {
            return false;
        }

        // Seta qual a função está sendo acessada, para gravação de log
        responsavel.setFunCodigo(funCodigo);

        // Verifica se há regra de restrição de acesso por IP/DDNS para a função, o comportamento vai ser semelhante ao de restrição de acesso
        if (!responsavel.temPermissao(funCodigo, true)) {
            throw new ZetraException("rotulo.endereco.acesso.invalido.ip", responsavel, responsavel.getIpUsuario());
        }

        // Verifica se há regra de restrição de acesso que se aplica à esta requisição
        final ControleRestricaoAcesso.RestricaoAcesso restricao = ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel);
        if (restricao.getGrauRestricao() != ControleRestricaoAcesso.GrauRestricao.SemRestricao) {
            throw new ZetraException("mensagem.restricao.acesso.operacao", responsavel);
        }

        return true;
    }

    protected void usuarioTemPermissao(String usuCodigo, String tipo, String entidade, String funCodigo) throws ZetraException {
        final AcessoSistema acessoSistema = new AcessoSistema(usuCodigo);
        acessoSistema.setUsuCodigo(usuCodigo);
        acessoSistema.setTipoEntidade(tipo);
        acessoSistema.setCodigoEntidade(entidade);
        try {
            // Busca as permissões do usuário
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            acessoSistema.setPermissoes(usuarioController.selectFuncoes(usuCodigo, entidade, tipo, acessoSistema));
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", acessoSistema);
        }

        // Verifica se tem permissão
        if (!acessoSistema.temPermissao(funCodigo)) {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao.funcao", acessoSistema, usuCodigo, funCodigo));
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", acessoSistema);
        }
    }

    /**
     * Valida a senha do servidor
     * @param rseCodigo   : código do registro do servidor
     * @param serSenha    : senha passada
     * @param validacaoParaDeferimentoReserva: Indica se a validação da senha é para deferimento de contrato.
     * @param loginExterno:
     * @param responsavel : usuário responsavel pela operação
     * @throws ZetraException
     */
    protected void validarSenhaServidor(String rseCodigo, String serSenha, boolean validacaoParaDeferimentoReserva, String loginExterno, String csaCodigo, String token, AcessoSistema responsavel) throws ZetraException {
        if (!TextHelper.isNull(token)) {
            validaTokenAcesso(rseCodigo, csaCodigo, token);
        } else {
            SenhaHelper.validarSenhaServidor(rseCodigo, serSenha, null, loginExterno, null, validacaoParaDeferimentoReserva, false, responsavel);
        }
    }

    protected void validaTokenAcesso(String rseCodigo, String csaCodigo, String token) throws ZetraException {
        try {
            ControleTokenAcesso.getInstance().validarToken(rseCodigo, csaCodigo, token);
        } catch (final ZetraException e) {
            throw e;
        }
    }

    protected void validarDadosBancariosServidor(boolean serInfBancariaObrigatoria, boolean validarInfBancaria, String adeBanco, String adeAgencia, String adeConta, RegistroServidorTO rseDto) throws ZetraException {
        if (serInfBancariaObrigatoria) {
            if (TextHelper.isNull(adeBanco) || TextHelper.isNull(adeAgencia) || TextHelper.isNull(adeConta)) {
                throw new ZetraException("mensagem.informacaoBancariaObrigatoria", responsavel);
            }

            if (validarInfBancaria) {
                final String rseBancoSal   = rseDto.getRseBancoSal();
                final String rseAgenciaSal = rseDto.getRseAgenciaSal();
                final String rseContaSal   = rseDto.getRseContaSal();
                if (TextHelper.isNull(rseBancoSal) || TextHelper.isNull(rseAgenciaSal) || TextHelper.isNull(rseContaSal) ||
                        !TextHelper.formataParaComparacao(rseBancoSal).equals(TextHelper.formataParaComparacao(adeBanco)) ||
                        !TextHelper.formataParaComparacao(rseAgenciaSal).equals(TextHelper.formataParaComparacao(adeAgencia)) ||
                        !TextHelper.formataParaComparacao(rseContaSal).equals(TextHelper.formataParaComparacao(adeConta))) {
                    final String rseBancoSalAlt   = rseDto.getRseBancoSalAlternativo();
                    final String rseAgenciaSalAlt = rseDto.getRseAgenciaSalAlternativa();
                    final String rseContaSalAlt   = rseDto.getRseContaSalAlternativa();

                    if (TextHelper.isNull(rseBancoSalAlt) || TextHelper.isNull(rseAgenciaSalAlt) || TextHelper.isNull(rseContaSalAlt) ||
                            !TextHelper.formataParaComparacao(rseBancoSalAlt).equals(TextHelper.formataParaComparacao(adeBanco)) ||
                            !TextHelper.formataParaComparacao(rseAgenciaSalAlt).equals(TextHelper.formataParaComparacao(adeAgencia)) ||
                            !TextHelper.formataParaComparacao(rseContaSalAlt).equals(TextHelper.formataParaComparacao(adeConta))) {
                        throw new ZetraException("mensagem.informacaoBancariaIncorreta", responsavel);
                    }
                }
            }
        }
    }

    protected void pesquisaCorrespondente(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String corIdentificador = (String) parametros.get(COR_IDENTIFICADOR);

        if (!TextHelper.isNull(corIdentificador)) {
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            final String csaCodigo = (String) parametros.get(CSA_CODIGO);

            CorrespondenteTransferObject correspondente = null;
            if (!TextHelper.isNull(csaCodigo)) {
                correspondente = consignatariaController.findCorrespondenteByIdn(corIdentificador, csaCodigo, responsavel);
            }
            if (correspondente != null) {
                parametros.put(COR_CODIGO, correspondente.getCorCodigo());
            } else {
                throw new ZetraException("mensagem.erro.correspondente.nao.encontrado", responsavel);
            }
        }
    }

    protected void pesquisaServico(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String svcIdentificador = (String) parametros.get(SERVICO_CODIGO);

        if (!TextHelper.isNull(svcIdentificador)) {
            final ServicoTransferObject svcTO = new ServicoTransferObject();
            svcTO.setSvcIdentificador(svcIdentificador);

            final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
            final ServicoTransferObject servico = convenioController.findServicoByIdn(svcIdentificador, responsavel);
            parametros.put(SVC_CODIGO, servico.getSvcCodigo());
        }
    }

    protected void pesquisaConsignataria(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String csaIdentificador = (String) parametros.get(CSA_IDENTIFICADOR);

        if (!TextHelper.isNull(csaIdentificador)) {
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            final ConsignatariaTransferObject consignataria = consignatariaController.findConsignatariaByIdn(csaIdentificador, responsavel);
            if (consignataria != null) {
                parametros.put(CSA_CODIGO, consignataria.getCsaCodigo());
            } else {
                throw new ZetraException("mensagem.erro.consignataria.nao.encontrada", responsavel);
            }
        }
    }

    /**
     * verifica para as operações aplicáveis se exige o motivo da operação
     * @param parametros
     * @throws ZetraException
     */
    protected void exigeMotivoOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final boolean exigeTMO = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, responsavel);

        if (exigeTMO && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel)) {
            if(TextHelper.isNull(parametros.get(TMO_IDENTIFICADOR))) {
                throw new ZetraException("mensagem.motivo.operacao.obrigatorio", responsavel);
            } else {
                final TipoMotivoOperacaoController tipoMotivoOperacaoController = ApplicationContextProvider.getApplicationContext().getBean(TipoMotivoOperacaoController.class);
                final List<TransferObject> tmoList = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
                boolean tmoConsignacao = false;

                for (final Object element: tmoList) {
                    final TransferObject tmoTO = (TransferObject) element;
                    final String tmoIdentificador = (String) tmoTO.getAttribute(Columns.TMO_IDENTIFICADOR);
                    if (tmoIdentificador.equals(parametros.get(TMO_IDENTIFICADOR))) {
                        tmoConsignacao = true;
                        break;
                    }
                }

                if (!tmoConsignacao) {
                    throw new ZetraException("mensagem.erro.motivo.operacao.invalido", responsavel);
                }
            }
        }
    }

    protected void recuperaAutorizacao(Map<CamposAPI, Object> parametros, Object adeNumero, Object adeIdentificador, CustomTransferObject criterio) throws AutorizacaoControllerException, ZetraException {
        final String operacao = parametros.get(OPERACAO).toString();
        String tipoEntidade = responsavel.getTipoEntidade();
        String codigo = responsavel.isSer() ? responsavel.getUsuCodigo() : responsavel.getCodigoEntidade();

        // Para o caso de consulta para compra é colocado o tipo CSE fixo, pois fará a consulta no conjunto de todas as consignações do sistema
        if (CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equals(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equals(operacao)) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        }

        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSA;
            codigo = responsavel.getCodigoEntidadePai();
        }

        List<String> svcCodigos = null;
        final String svcCodigo = !TextHelper.isNull(parametros.get(SVC_CODIGO)) ? (String) parametros.get(SVC_CODIGO) : null;
        if (!CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equals(operacao) &&
                !CodedValues.OP_COMPRAR_CONTRATO.equals(operacao) &&
                !CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) &&
                !CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao)) {
            if (!TextHelper.isNull(svcCodigo)) {
                svcCodigos = new ArrayList<>();
                svcCodigos.add(svcCodigo);
            }
        }

        final PesquisarConsignacaoController pesquisarController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        List<TransferObject> autorizacoes = new ArrayList<>();

        if (CodedValues.OP_RETIRAR_CONTRATO_COMPRA.equals(operacao) || CodedValues.OP_INF_PG_SALDO_DEVEDOR.equals(operacao)
            || CodedValues.OP_SOL_RECALC_SALDO_DEVEDOR.equals(operacao)) {
            try {
                if (adeNumero != null) {
                    autorizacoes.add(pesquisarController.findAutDescontoByAdeNumero((Long) adeNumero, responsavel));
                }
            } catch (final AutorizacaoControllerException e) {
                // Remapeamento de mensagem de erro de mensagem.erro.contrato.nao.encontrado para mensagem.nenhumaConsignacaoEncontrada
            }
        } else {
            autorizacoes = pesquisarController.pesquisaAutorizacao(tipoEntidade, codigo, (String) parametros.get(RSE_CODIGO),
                    TextHelper.objectToStringList(adeNumero), TextHelper.objectToStringList(adeIdentificador),
                    sadCodigos, svcCodigos, criterio, responsavel);
        }

        final String funcao = responsavel.getFunCodigo();
        if (!TextHelper.isNull(funcao) && CodedValues.FUN_COMP_CONTRATO.equals(funcao)) {
            autorizacoes = parametroController.filtraAdeRestringePortabilidade(autorizacoes, parametros.get(RSE_CODIGO).toString(), svcCodigo, responsavel);
        }

        if (CodedValues.OP_CONSULTAR_CONSIGNACAO_V8_0.equals(operacao)) {
            retornarConsignacoesConsultarConsignacaoV8(operacao, autorizacoes, parametros, responsavel);
        } else {
            retornarConsignacoes(operacao, autorizacoes, parametros, responsavel);
        }
    }

    /**
     * recupera consignações caso seja informado o(s) identificador(es)
     * @param parametros
     * @param adeNumero
     * @param adeIdentificador
     * @param criterio
     * @throws AutorizacaoControllerException
     * @throws ZetraException
     */
    protected void recuperaAutorizacaoByIdn(Map<CamposAPI, Object> parametros, Object adeIdentificador, CustomTransferObject criterio) throws AutorizacaoControllerException, ZetraException {
        final String operacao = parametros.get(OPERACAO).toString();
        String tipoEntidade = responsavel.getTipoEntidade();
        String codigo = responsavel.isSer() ? responsavel.getUsuCodigo() : responsavel.getCodigoEntidade();

        // Para o caso de consulta para compra é colocado o tipo CSE fixo, pois fará a consulta no conjunto de todas as consignações do sistema
        if (CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equals(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equals(operacao)) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        }

        if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSA;
            codigo = responsavel.getCodigoEntidadePai();
        }

        List<String> svcCodigos = null;
        final String svcCodigo = !TextHelper.isNull(parametros.get(SVC_CODIGO)) ? (String) parametros.get(SVC_CODIGO) : null;
        if (!CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equals(operacao) &&
                !CodedValues.OP_COMPRAR_CONTRATO.equals(operacao) &&
                !CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) &&
                !CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao)) {
            if (!TextHelper.isNull(svcCodigo)) {
                svcCodigos = new ArrayList<>();
                svcCodigos.add(svcCodigo);
            }
        }

        final PesquisarConsignacaoController pesquisarController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        List<TransferObject> autorizacoes = pesquisarController.pesquisaAutorizacao(tipoEntidade, codigo, (String) parametros.get(RSE_CODIGO),
                null, TextHelper.objectToStringList(adeIdentificador),
                sadCodigos, svcCodigos, criterio, responsavel);

        final String funcao = responsavel.getFunCodigo();
        if (CodedValues.FUN_COMP_CONTRATO.equals(funcao)) {
            autorizacoes = parametroController.filtraAdeRestringePortabilidade(autorizacoes, parametros.get(RSE_CODIGO).toString(), svcCodigo, responsavel);
        }

        retornarConsignacoes(operacao, autorizacoes, parametros, responsavel);
    }

    private static void retornarConsignacoes(String operacao, List<TransferObject> autorizacoes, Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        if (autorizacoes.isEmpty()) {
            throw new ZetraException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
        } else if (autorizacoes.size() == 1) {
            final CustomTransferObject autorizacao = (CustomTransferObject) autorizacoes.get(0);

            // Pega o rseCodigo e cnvCodigo
            if (TextHelper.isNull(parametros.get(RSE_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.RSE_CODIGO))) {
                parametros.put(RSE_CODIGO, autorizacao.getAttribute(Columns.RSE_CODIGO).toString());
            }
            if (TextHelper.isNull(parametros.get(SER_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.SER_CODIGO))) {
                parametros.put(SER_CODIGO, autorizacao.getAttribute(Columns.SER_CODIGO).toString());
            }
            if (TextHelper.isNull(parametros.get(CNV_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.CNV_CODIGO))) {
                parametros.put(CNV_CODIGO, autorizacao.getAttribute(Columns.CNV_CODIGO).toString());
            }
            if (TextHelper.isNull(parametros.get(SVC_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.SVC_CODIGO))) {
                parametros.put(SVC_CODIGO, autorizacao.getAttribute(Columns.SVC_CODIGO).toString());
            }

            final List<TransferObject> auts = (List<TransferObject>) parametros.get(CONSIGNACAO);
            if (auts == null) {
                final ArrayList<TransferObject> autList = new ArrayList<>();
                autList.add(autorizacao);
                parametros.put(CONSIGNACAO, autList);
            } else {
                auts.add(autorizacao);
            }
        } else {
            parametros.put(CONSIGNACOES, autorizacoes);

            if (!CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equals(operacao) && !CodedValues.OP_COMPRAR_CONTRATO.equals(operacao) && !CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) &&
                    !CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) && !CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao)) {
                throw new ZetraException("mensagem.maisDeUmaConsignacaoEncontrada", responsavel);
            }
        }
    }


    private static void retornarConsignacoesConsultarConsignacaoV8(String operacao, List<TransferObject> autorizacoes, Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        if (autorizacoes.size() == 1) {
            final CustomTransferObject autorizacao = (CustomTransferObject) autorizacoes.get(0);

            // Pega o rseCodigo e cnvCodigo
            if (TextHelper.isNull(parametros.get(RSE_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.RSE_CODIGO))) {
                parametros.put(RSE_CODIGO, autorizacao.getAttribute(Columns.RSE_CODIGO).toString());
            }
            if (TextHelper.isNull(parametros.get(SER_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.SER_CODIGO))) {
                parametros.put(SER_CODIGO, autorizacao.getAttribute(Columns.SER_CODIGO).toString());
            }
            if (TextHelper.isNull(parametros.get(CNV_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.CNV_CODIGO))) {
                parametros.put(CNV_CODIGO, autorizacao.getAttribute(Columns.CNV_CODIGO).toString());
            }
            if (TextHelper.isNull(parametros.get(SVC_CODIGO)) && !TextHelper.isNull(autorizacao.getAttribute(Columns.SVC_CODIGO))) {
                parametros.put(SVC_CODIGO, autorizacao.getAttribute(Columns.SVC_CODIGO).toString());
            }

            final List<TransferObject> auts = (List<TransferObject>) parametros.get(CONSIGNACAO);
            if (auts == null) {
                final ArrayList<TransferObject> autList = new ArrayList<>();
                autList.add(autorizacao);
                parametros.put(CONSIGNACAO, autList);
            } else {
                auts.add(autorizacao);
            }
        } else if(autorizacoes.size() > 1){
            parametros.put(CONSIGNACOES, autorizacoes);

            if (!CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equals(operacao) && !CodedValues.OP_COMPRAR_CONTRATO.equals(operacao) && !CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) &&
                    !CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) && !CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao)) {
                throw new ZetraException("mensagem.maisDeUmaConsignacaoEncontrada", responsavel);
            }
        }
    }

    /**
     * Da lista de convênios, filtra apenas aqueles onde novas reservas podem ser inseridas,
     * evitando o erro caso mais de um convênio seja encontrado.
     * @param parametros
     * @param serCnvRegistros
     * @throws ZetraException
     */
    private List<Map<String, Object>> filtraConveniosPermitidos(Map<CamposAPI, Object> parametros, List<Map<String, Object>> serCnvRegistros) throws ZetraException  {
        AutorizacaoControllerException primeiroErro = null;
        final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
        final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);

        final String tipoEntidade = responsavel.getTipoEntidade();
        final String codigoEntidade = responsavel.getCodigoEntidade();
        // valor parcela
        final Object adeVlr = parametros.get(VALOR_PARCELA);
        BigDecimal adeVlrAux = null;
        if (adeVlr instanceof Double) {
            adeVlrAux = BigDecimal.valueOf((Double) adeVlr);
        } else {
            adeVlrAux = NumberHelper.parseDecimal(adeVlr.toString());
            if (adeVlrAux == null) {
                throw new ZetraException("mensagem.valorParcelaInvalido", responsavel);
            }
        }
        // valor liquido liberado
        final Object adeVlrLiquido = parametros.get(VALOR_LIBERADO);
        BigDecimal adeVlrLiquidoAux = null;
        if (adeVlrLiquido != null) {
            if (adeVlrLiquido instanceof Double) {
                adeVlrLiquidoAux = BigDecimal.valueOf((Double) adeVlrLiquido);
            } else {
                adeVlrLiquidoAux = NumberHelper.parseDecimal(adeVlrLiquido.toString());
            }
        }
        if (adeVlrLiquidoAux == null) {
            throw new ZetraException("mensagem.valorLiquidoLiberadoInvalido", responsavel);
        }

        final Integer adePrazo = (Integer) parametros.get(PRAZO);
        final Integer adeCarencia = parametros.get(ADE_CARENCIA) != null ? (Integer) parametros.get(ADE_CARENCIA) : Integer.valueOf("0");

        String adePeriodicidade = (String) parametros.get(ADE_PERIODICIDADE);
        if (TextHelper.isNull(adePeriodicidade)) {
            adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
        }

        final List<Map<String, Object>> serCnvRegistrosCandidatos = new ArrayList<>();
        final List<String> rseCodigoAvaliado = new ArrayList<>();

        final String operacao = (String) parametros.get(OPERACAO);
        final boolean renegociacao = CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao)
                                || CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao);
        final boolean alongamento = CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao);
        final String acao = renegociacao ? "RENEGOCIAR" : alongamento ? "ALONGAR" : "RESERVAR";
        final String novoAdeIdentificador = parametros.get(NOVO_ADE_IDENTIFICADOR) != null ? parametros.get(NOVO_ADE_IDENTIFICADOR).toString() : null;

        final Map<String, Map<String, Object>> cacheParametrosCnv = getCacheParamCnv(parametros, serCnvRegistros);

        // Para cada tupla <Convenio,Servidor> verifica se uma nova reserva
        // pode ser feita. Verifica também, caso a operação seja de renegociação,
        // as consignações a serem renegociadas.
        final Iterator<Map<String, Object>> it = serCnvRegistros.iterator();
        while (it.hasNext()) {
            final Map<String, Object> serCnvReg = it.next();
            final String cnvCodigo = (String) serCnvReg.get(Columns.CNV_CODIGO);
            final String rseCodigo = (String) serCnvReg.get(Columns.RSE_CODIGO);
            final String svcCodigo = (String) serCnvReg.get(Columns.SVC_CODIGO);
            final String corCodigo = AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipoEntidade) ? codigoEntidade : null;
            final Map<String, Object> paramCnv = cacheParametrosCnv.get(cnvCodigo);

            // Se o registro servidor já foi avaliado, então outros convênios
            // para ele serão ignorados.
            if (rseCodigoAvaliado.contains(rseCodigo)) {
                continue;
            }
            try {
                // Verifica se as entidades estão ativas para fazer novas reservas
                autorizacaoController.validarEntidades(cnvCodigo, corCodigo, responsavel);
                // Se é uma renegociação obtém as consignações a serem renegociadas para passar ao
                // método que analisa se pode reservar margem, por causa de limite de contratos
                final List<String> adeCodigosRenegociacao = new ArrayList<>();
                final List<String> svcCodigosRenegociacao = new ArrayList<>();

                if (renegociacao) {
                    // recupera a lista de números de autorizações que serão renegociadas
                    final Object adeNumero = parametros.get(ADE_NUMERO) != null ? parametros.get(ADE_NUMERO) : null;

                    // lista por número
                    List<String> adeNumerosList = new ArrayList<>();
                    if (adeNumero != null) {
                        if (adeNumero instanceof Long) {
                            adeNumerosList.add(adeNumero.toString());
                        } else if (adeNumero instanceof String) {
                            adeNumerosList.add((String) adeNumero);
                        } else if (adeNumero instanceof final Collection<?> idCollection) {
                            adeNumerosList = idCollection.stream().map(Object::toString).toList();
                        } else if (adeNumero instanceof final String[] idArray) {
                            adeNumerosList = Arrays.asList(idArray);
                        }
                    }

                    // recupera a lista de identificadores de autorizações que serão renegociadas
                    final Object adeIdentificador = parametros.get(ADE_IDENTIFICADOR) != null ? parametros.get(ADE_IDENTIFICADOR) : null;

                    // lista por identificador
                    List<String> adeIdentificadoresList = new ArrayList<>();
                    if (adeIdentificador != null) {
                        if (adeIdentificador instanceof String) {
                            adeIdentificadoresList.add(adeIdentificador.toString());
                        } else if (adeIdentificador instanceof final Collection<?> idCollection) {
                            adeIdentificadoresList = idCollection.stream().map(Object::toString).toList();
                        } else if (adeIdentificador instanceof final String[] idArray) {
                            adeIdentificadoresList = Arrays.asList(idArray);
                        }
                    }

                    if ((adeNumerosList.size() > 0) || (adeIdentificadoresList.size() > 0)) {
                        final PesquisarConsignacaoController pesquisarController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
                        final List<TransferObject> ades = pesquisarController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumerosList, adeIdentificadoresList, null, null, null, responsavel);
                        if ((ades != null) && !ades.isEmpty()) {
                            for (final TransferObject ade : ades) {
                                if (ade.getAttribute(Columns.ADE_CODIGO) != null) {
                                    adeCodigosRenegociacao.add((String) ade.getAttribute(Columns.ADE_CODIGO));
                                }

                                if (ade.getAttribute(Columns.SVC_CODIGO) != null) {
                                    svcCodigosRenegociacao.add((String) ade.getAttribute(Columns.SVC_CODIGO));
                                }
                            }
                        }
                    }
                    if (adeCodigosRenegociacao.isEmpty()) {
                        throw new AutorizacaoControllerException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
                    }
                }

                // Analisando se o serviço que foi escolhido tem relacionamento de renegociação
                for (final String svcCodigoOrigem: svcCodigosRenegociacao) {
                    final List<TransferObject> result = servicoController.listaRelacionamentoServicosPorTipoNatureza(svcCodigoOrigem, svcCodigo, CodedValues.TNT_RENEGOCIACAO, responsavel);

                    // Caso não tiver relacionamento paramos enviamos um erro de execução.
                    if ((result == null) || result.isEmpty()) {
                        throw new AutorizacaoControllerException("mensagem.convenioNaoEncontrado", responsavel);
                    }
                }

                // Testa se este convênio pode receber nova reserva para o registro servidor
                autorizacaoController.podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, false, true, true, adeCodigosRenegociacao, adeVlrAux, adeVlrLiquidoAux, adePrazo, adeCarencia, adePeriodicidade, novoAdeIdentificador, paramCnv, acao, true, false, responsavel);

                // Testa se o prazo informado é valido para o serviço
                validaPrazoCadastrado(parametros, serCnvReg, acao);

                // Testa se o serviço permite operação de alongamento
                validaServicoAlongamento(parametros, serCnvReg);

                // OK: novas reservas podem ser feitas.
                serCnvRegistrosCandidatos.add(serCnvReg);
                rseCodigoAvaliado.add(rseCodigo);

            } catch (final AutorizacaoControllerException ex) {
                if (primeiroErro == null) {
                    primeiroErro = ex;
                }
                // Não pode fazer reservas neste convênio, se for o último registro,
                // e nenhum candidato foi listado, então propaga o erro
                if (!it.hasNext() && serCnvRegistrosCandidatos.isEmpty()) {
                    throw primeiroErro;
                }
            }
        }
        return serCnvRegistrosCandidatos;
    }

    /**
     * valida código de verba da requisição e seta valores do svcCodigo e cnvCodigo
     * @param parametros
     * @throws ZetraException
     */
    protected void validaVerbaPorNaturezaServico(Map<CamposAPI, Object> parametros) throws ZetraException {
        // verifica se a consignatária está configurada para utilizar o primeiro convênio disponível
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String paramCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_NATUREZA, responsavel);
        final boolean utilizarPrimeiroCnvNatureza = !TextHelper.isNull(paramCsa) && CodedValues.TPA_SIM.equals(paramCsa);

        final String operacao = parametros.get(OPERACAO).toString();
        final boolean multiploSerConsMargem = CodedValues.OPERACOES_CONSULTAR_MARGEM.contains(operacao) && (!TextHelper.isNull(parametros.get(MATRICULA_MULTIPLA)) ? Boolean.valueOf(parametros.get(MATRICULA_MULTIPLA).toString()) : false);

        final Object cnvCodVerba = parametros.get(CNV_COD_VERBA);
        final Object svcIdentificador = parametros.get(SERVICO_CODIGO);

        // se utiliza o primento convênio disponíel pela natureza e não tiver preenchido a verba nem o serviço
        if (utilizarPrimeiroCnvNatureza && TextHelper.isNull(cnvCodVerba) && TextHelper.isNull(svcIdentificador)) {
            // recupera a natureza do serviço para listagem dos convênios
            final String nseCodigo = (String) parametros.get(NSE_CODIGO);
            final String serCpf = (String) parametros.get(SER_CPF);
            final String rseMatricula = (String) parametros.get(RSE_MATRICULA);
            final String orgIdentificador = (String) parametros.get(ORG_IDENTIFICADOR);
            final String estIdentificador = (String) parametros.get(EST_IDENTIFICADOR);

            // verifica o preenchimento da natureza de serviço
            if (TextHelper.isNull(nseCodigo)) {
                throw new ZetraException("mensagem.informe.codigo.natureza.servico", responsavel);
            }
            if (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf)) {
                validaCpfMatricula(parametros);
            }
            if (TextHelper.isNull(csaCodigo) || (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf))) {
                throw new ZetraException("mensagem.erroInternoSistema", responsavel);
            }

            // recupera lista de convênios ativos pela natureza de serviço informada
            final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
            final List<Map<String, Object>> serCnvRegisters = convenioController.lstConvenioPorNseOrgServidor(csaCodigo, nseCodigo, estIdentificador, orgIdentificador, rseMatricula, serCpf, responsavel);

            // Filtra o primeiro convênio disponível.
            // O disponível é aquele onde o servidor não possui bloqueios, não atingiu o limite de contratos,
            // pode incluir novas reservas e não está bloqueado.
            final List<Map<String, Object>> serCnvRegistrosCandidatos = filtraConveniosPermitidos(parametros, serCnvRegisters);
            if (serCnvRegistrosCandidatos.size() > 0) {
                final Map<String, Object> dadosServidorConvenio = serCnvRegistrosCandidatos.get(0);
                parametros.put(SVC_CODIGO, dadosServidorConvenio.get(Columns.SVC_CODIGO));
                parametros.put(CNV_CODIGO, dadosServidorConvenio.get(Columns.CNV_CODIGO));
                parametros.put(CNV_COD_VERBA, dadosServidorConvenio.get(Columns.CNV_COD_VERBA));
                parametros.put(SERVICO_CODIGO, dadosServidorConvenio.get(Columns.SVC_IDENTIFICADOR));
            } else {
                final List<TransferObject> servidores = pesquisaServidor(operacao, csaCodigo, serCpf, rseMatricula, orgIdentificador, estIdentificador, cnvCodVerba, svcIdentificador);

                if (servidores.isEmpty()) {
                    throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
                } else if (servidores.size() > 1) {
                    parametros.put(SERVIDORES, servidores);
                    if (!multiploSerConsMargem) {
                        throw new ZetraException("mensagem.multiplosServidoresEncontrados", responsavel);
                    }
                } else {
                	throw new ZetraException("mensagem.convenioNaoEncontrado", responsavel);
                }
            }
        }
    }

    /**
     * Método para salvar anexo passado por parâmetro em base64 no diretório de arquivos.
     * @param path
     * @param arquivo
     * @param extensoesArquivoPermitidas
     * @return
     * @throws ZetraException
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected File salvarAnexo(String path, Anexo arquivo, String[] extensoesArquivoPermitidas) throws ZetraException, FileNotFoundException, IOException {
        // Se não informar extensoes permitidas, usa por padrão as extensões permitidas para anexo de saldo devedor
        if ((extensoesArquivoPermitidas == null) || (extensoesArquivoPermitidas.length == 0)) {
            extensoesArquivoPermitidas = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_SALDO_DEVEDOR;
        }
        // Verifica se a extensão do arquivo é válida.
        if (extensoesArquivoPermitidas != null) {
            boolean extensaoValida = false;
            extensoesArquivoPermitidas = UploadHelper.atualizaExtensoesPermitidas(extensoesArquivoPermitidas, responsavel);
            for (final String extensoesArquivoPermitida : extensoesArquivoPermitidas) {
                if (arquivo.getNomeArquivo().toLowerCase().endsWith(extensoesArquivoPermitida)) {
                    extensaoValida = true;
                    break;
                }
            }

            if (!extensaoValida) {
                throw new ZetraException("mensagem.erro.upload.extensao.invalida", responsavel, arquivo.getNomeArquivo(), TextHelper.join(extensoesArquivoPermitidas, ", "));
            }
        }

        FileOutputStream fileOutputStream;

        final ByteArrayInputStream bis = new ByteArrayInputStream(arquivo.getArquivo());
        final File pth = new File(path);
        if (!pth.isDirectory()) {
            pth.mkdirs();
        }
        if (!pth.exists()) {
            throw new FileNotFoundException();
        }

        final File file = new File(pth, arquivo.getNomeArquivo());
        file.createNewFile();
        fileOutputStream = new FileOutputStream(file);
        bis.transferTo(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();

        // testa se o tamanho do anexo é maior que o definido no sistema.
        final String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
        final int tamMaxArqAnexo = !TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200;

        if (file.length() > (tamMaxArqAnexo * 1024)) {
            file.delete();
            throw new ZetraException("mensagem.erro.arquivo.tamanho.maximo", responsavel, valorLegivelEmBytes(tamMaxArqAnexo * 1024));
        }

        if (!FileHelper.validaExtensaoRecursivamente(file.getAbsolutePath(), extensoesArquivoPermitidas)) {
            file.delete();
            if (file.getAbsolutePath().endsWith(".zip")) {
                throw new ZetraException("mensagem.erro.upload.conteudo.zip.extensoes.permitidas", responsavel, TextHelper.join(extensoesArquivoPermitidas, ","));
            } else {
                throw new ZetraException("mensagem.erro.upload.conteudo.extensoes.permitidas", responsavel, TextHelper.join(extensoesArquivoPermitidas, ","));
            }
        }

        return file;
    }

    /**
     * Retorna uma descrição amigável para uma quantidade de bytes.
     * @param bytes
     * @return
     */
    private String valorLegivelEmBytes(long bytes) {
        String valorLegivel;
        long qtdeLegivel = bytes / 1048576;
        if (qtdeLegivel > 0.0) {
            valorLegivel = qtdeLegivel + ApplicationResourcesHelper.getMessage("rotulo.megabyte.abreviado", responsavel);
        } else {
            qtdeLegivel = bytes / 1024;
            if (qtdeLegivel > 0) {
                valorLegivel = qtdeLegivel + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
            } else {
                valorLegivel = bytes + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
            }
        }

        return valorLegivel;
    }

    /**
     * Mantém um cache de parâmetros de serviço, e de serviço por consignatária.
     * @param parametros
     * @param serCnvRegs
     * @throws ZetraException
     */
    private Map<String, Map<String, Object>> getCacheParamCnv(Map<CamposAPI, Object> parametros, List<Map<String, Object>> serCnvRegs) throws ZetraException {
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final Map<String, Map<String, Object>> cacheParametrosCnv =  new HashMap<>();

        for (final Map<String, Object> cnvCto : serCnvRegs) {
            try {
                // Cache que mapeia os parâmetros de serviço aos códigos dos convênios relacionados
                if (!cacheParametrosCnv.containsKey(cnvCto.get(Columns.CNV_CODIGO).toString())) {
                    final Map<String, Object> parSvc = new HashMap<>();
                    final String svcCodigo = cnvCto.get(Columns.SVC_CODIGO).toString();

                    // Parâmetros de serviços
                    List<TransferObject> parametrosSvcCsa = parametroController.selectParamSvcCse(svcCodigo, responsavel);
                    Iterator<TransferObject> itP = parametrosSvcCsa.iterator();
                    TransferObject paramCto = null;
                    while (itP.hasNext()) {
                        paramCto = itP.next();
                        parSvc.put(paramCto.getAttribute(Columns.TPS_CODIGO).toString(), paramCto.getAttribute(Columns.PSE_VLR));
                    }

                    // Todos os parametros de serviço que são sobrepostos a nível de CSA e SVC:
                    // ao acrescentar novos parametros, deve-se incluir aqui também
                    final List<String> tpsCodigos = new ArrayList<>();
                    tpsCodigos.add(CodedValues.TPS_INDICE);
                    tpsCodigos.add(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA);
                    tpsCodigos.add(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA);
                    tpsCodigos.add(CodedValues.TPS_CARENCIA_MAXIMA);
                    tpsCodigos.add(CodedValues.TPS_CARENCIA_MINIMA);

                    parametrosSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
                    itP = parametrosSvcCsa.iterator();
                    while (itP.hasNext()) {
                        paramCto = itP.next();
                        if (CodedValues.TPS_CARENCIA_MINIMA.equals(paramCto.getAttribute(Columns.TPS_CODIGO))) {
                            parSvc.put("CARENCIA_MINIMA", paramCto.getAttribute(Columns.PSC_VLR));
                        } else if (CodedValues.TPS_CARENCIA_MAXIMA.equals(paramCto.getAttribute(Columns.TPS_CODIGO))) {
                            parSvc.put("CARENCIA_MAXIMA", paramCto.getAttribute(Columns.PSC_VLR));
                        } else {
                            parSvc.put(paramCto.getAttribute(Columns.TPS_CODIGO).toString(), paramCto.getAttribute(Columns.PSC_VLR));
                        }
                    }

                    // Busca o serviço de cartão de crédito do qual o serviço depende, caso exista.
                    if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CARTAO)) {
                        final List<TransferObject> servicosCartaoCredito = parametroController.getRelacionamentoSvc(CodedValues.TNT_CARTAO, null, svcCodigo, responsavel);

                        if ((servicosCartaoCredito != null) && (servicosCartaoCredito.size() > 0)) {
                            final TransferObject cto = servicosCartaoCredito.get(0);
                            final String svcCartao = cto.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString();
                            parSvc.put("SERVICO_CARTAOCREDITO", svcCartao);
                        }
                    }

                    cacheParametrosCnv.put(cnvCto.get(Columns.CNV_CODIGO).toString(), parSvc);
                }
            } catch (final ParametroControllerException e) {
                LOG.error("Erro na busca de parametro: " + e.getMessage());
                throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
            } catch (final Exception rex) {
                throw new ZetraException("mensagem.falhaComunicacao", responsavel);
            }
        }

        return cacheParametrosCnv;
    }

    /**
     * Verifica se o prazo cadastrado é valido de acordo com o cadastro de prazos do serviço
     * @param parametros
     * @param serCnvReg
     * @throws AutorizacaoControllerException
     */
    private void validaPrazoCadastrado(Map<CamposAPI, Object> parametros, Map<String, Object> serCnvReg, String acao) throws AutorizacaoControllerException {
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);
        final String codReg = (parametros.get(ADE_COD_REG) != null) && !"".equals(parametros.get(ADE_COD_REG).toString()) ? parametros.get(ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO;
        if (!CodedValues.COD_REG_ESTORNO.equals(codReg)) {
            final String svcCodigo = (String) serCnvReg.get(Columns.SVC_CODIGO);
            final String orgCodigo = (String) serCnvReg.get(Columns.ORG_CODIGO);
            Integer adePrazo = (Integer) parametros.get(PRAZO);
            final Date adeAnoMesIni = (Date) parametros.get(ADE_ANO_MES_INI);
            final Date adeAnoMesFim = (Date) parametros.get(ADE_ANO_MES_FIM);

            String adePeriodicidade = (String) parametros.get(ADE_PERIODICIDADE);
            if (TextHelper.isNull(adePeriodicidade)) {
                adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
            }

            int maxPrazoVlr = -1;

            try {
                final String maxPrazoSvcParam = TextHelper.isNull(acao) || "RESERVAR".equals(acao) ? CodedValues.TPS_MAX_PRAZO : CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE;
                final TransferObject maxPrazo = parametroController.getParamSvcCse(svcCodigo, maxPrazoSvcParam, responsavel);
                maxPrazoVlr = (maxPrazo != null) && !TextHelper.isNull(maxPrazo.getAttribute(Columns.PSE_VLR)) && TextHelper.isNum(maxPrazo.getAttribute(Columns.PSE_VLR)) ?
                        Integer.parseInt(maxPrazo.getAttribute(Columns.PSE_VLR).toString()) : -1;

                // Se o serviço ou servidor tem prazo máximo, porém é contrato quinzenal, a unidade do prazo máximo
                // será em meses, portanto ao comparar com o contrato quinzenal, deve multiplicar por 2
                if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                    maxPrazoVlr = maxPrazoVlr > 1 ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(maxPrazoVlr, responsavel) : maxPrazoVlr;
                }
            } catch (final ParametroControllerException e1) {
                throw new AutorizacaoControllerException(e1);
            }

            if ((adePrazo == null) && (adeAnoMesIni != null) && (adeAnoMesFim != null)) {
                try {
                    adePrazo = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, adeAnoMesIni, adeAnoMesFim, adePeriodicidade, responsavel);
                    parametros.put(PRAZO, adePrazo);
                } catch (final Exception e) {
                    throw new AutorizacaoControllerException("mensagem.qtdParcelasInvalida", responsavel);
                }
            }

            if ((maxPrazoVlr != -1) && ((adePrazo == null) || (adePrazo > maxPrazoVlr))) {
                throw new AutorizacaoControllerException("mensagem.erro.prazo.maior.maximo", responsavel, String.valueOf(maxPrazoVlr));
            }

            try {
                final SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
                final boolean validaPrazoRenegociacao = !TextHelper.isNull(acao) && !"RESERVAR".equals(acao);
                final List<TransferObject> przCnv = simulacaoController.getPrazoCoeficiente(svcCodigo, csaCodigo, orgCodigo, Calendar.getInstance().get(Calendar.DAY_OF_MONTH),validaPrazoRenegociacao, responsavel);
                if ((przCnv != null) && !przCnv.isEmpty()) {
                    boolean prazoValido = false;

                    if (adePrazo != null) {
                        final boolean quinzenal = CodedValues.PERIODICIDADE_FOLHA_QUINZENAL.equals(adePeriodicidade);
                        final Integer prazoJuros = quinzenal ? (int) Math.round(adePrazo / 2.0) : adePrazo;

                        for (final TransferObject cto : przCnv) {
                            final Integer prazo = Integer.valueOf(cto.getAttribute(Columns.PRZ_VLR).toString());
                            if (prazoJuros.compareTo(prazo) == 0) {
                                prazoValido = true;
                                break;
                            }
                        }
                    }

                    if (!prazoValido) {
                        throw new AutorizacaoControllerException("mensagem.qtdParcelasInvalida", responsavel);
                    }
                }
            } catch (final NumberFormatException e) {
                throw new AutorizacaoControllerException("mensagem.qtdParcelasInvalida", responsavel);
            } catch (final SimulacaoControllerException e) {
                throw new AutorizacaoControllerException(e);
            }
        }
    }

    private void validaServicoAlongamento(Map<CamposAPI, Object> parametros, Map<String, Object> serCnvReg) throws AutorizacaoControllerException {
        try {
            final String operacao = parametros.get(OPERACAO).toString();
            if (CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao)) {
                final String svcCodigo = (String) serCnvReg.get(Columns.SVC_CODIGO);
                final List<TransferObject> relacionamento = parametroController.getRelacionamentoSvc(CodedValues.TNT_ALONGAMENTO, svcCodigo, null, responsavel);
                if ((relacionamento == null) || relacionamento.isEmpty()) {
                    throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.pode.ser.alongado.pois.nao.esta.relacionado.alongamento", responsavel);
                }
            }
        } catch (final ParametroControllerException e) {
            throw new AutorizacaoControllerException(e);
        }
    }

    // DESENV-20493
    private void cancelarSolicitacoesParamCsa(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        Object adeNumero = parametros.get(ADE_NUMERO);

        if (TextHelper.isNull(rseCodigo) && (adeNumero != null) && (adeNumero instanceof List)) {
            final PesquisarConsignacaoController pesquisarController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final List<Long> adeNumeros = (List<Long>) adeNumero;
            for (final Long numeroAde : adeNumeros) {
                try {
                    final TransferObject autdes = pesquisarController.findAutDescontoByAdeNumero(numeroAde, responsavel);
                    if (!TextHelper.isNull(autdes)) {
                        adeNumero = autdes.getAttribute(Columns.ADE_NUMERO);
                        rseCodigo = (String) autdes.getAttribute(Columns.RSE_CODIGO);
                        break;
                    }
                } catch (final ZetraException ex) {
                    LOG.warn(ex.getMessage(), ex);
                }
            }
        }

        if (responsavel.isCsaCor()) {
            final CancelarConsignacaoController cancelarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(CancelarConsignacaoController.class);
            final String paramCancelaSolicitacaoServidor = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_CANCELA_SOLICITACAO_AO_PESQUISAR_SERVIDOR, responsavel);
            final boolean cancelaSolicitacaoServidor = !TextHelper.isNull(paramCancelaSolicitacaoServidor) && CodedValues.TPA_SIM.equals(paramCancelaSolicitacaoServidor);
            if (cancelaSolicitacaoServidor) {
                cancelarConsignacaoController.cancelarExpiradasCsa(rseCodigo, String.valueOf(adeNumero), responsavel);
            }
        }
    }

    protected Anexo getAnexo(Object object) {
        if (object == null) {
            return null;
        } else  if (object instanceof final Anexo anexo) {
            return anexo;
        } else {
            final Anexo anexo = new Anexo();
            if (object instanceof final com.zetra.econsig.webservice.soap.compra.v1.Anexo anexoCompraV1) {
                anexo.setArquivo(anexoCompraV1.getArquivo());
                anexo.setNomeArquivo(anexoCompraV1.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.lote.v1.Anexo anexoLoteV1) {
                anexo.setArquivo(anexoLoteV1.getArquivo());
                anexo.setNomeArquivo(anexoLoteV1.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.operacional.v1.Anexo anexoOperacionalV1) {
                anexo.setArquivo(anexoOperacionalV1.getArquivo());
                anexo.setNomeArquivo(anexoOperacionalV1.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.operacional.v2.Anexo anexoOperacionalV2) {
                anexo.setArquivo(anexoOperacionalV2.getArquivo());
                anexo.setNomeArquivo(anexoOperacionalV2.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.operacional.v3.Anexo anexoOperacionalV3) {
                anexo.setArquivo(anexoOperacionalV3.getArquivo());
                anexo.setNomeArquivo(anexoOperacionalV3.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.operacional.v4.Anexo anexoOperacionalV4) {
                anexo.setArquivo(anexoOperacionalV4.getArquivo());
                anexo.setNomeArquivo(anexoOperacionalV4.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.operacional.v6.Anexo anexoOperacionalV6) {
                anexo.setArquivo(anexoOperacionalV6.getArquivo());
                anexo.setNomeArquivo(anexoOperacionalV6.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.operacional.v7.Anexo anexoOperacionalV7) {
                anexo.setArquivo(anexoOperacionalV7.getArquivo());
                anexo.setNomeArquivo(anexoOperacionalV7.getNomeArquivo());

            } else if (object instanceof final com.zetra.econsig.webservice.soap.operacional.v8.Anexo anexoOperacionalV8) {
                anexo.setArquivo(anexoOperacionalV8.getArquivo());
                anexo.setNomeArquivo(anexoOperacionalV8.getNomeArquivo());

            } else {
                throw new IllegalArgumentException(object.getClass().getName());
            }

            return anexo;
        }
    }

    protected SituacaoContrato getSituacaoContrato(Object object) {
        if (object == null) {
            return null;
        } else  if (object instanceof final SituacaoContrato situacaoContrato) {
            return situacaoContrato;
        } else {
            final SituacaoContrato situacaoContrato = new SituacaoContrato();
            BeanUtils.copyProperties(object, situacaoContrato);
            return situacaoContrato;
        }
    }

    /**
     * Verifica os vínculos que não podem reservar margem para este convênio
     * @param csaCodigo
     * @param svcCodigo
     * @param vrsCodigo
     * @throws AutorizacaoControllerException
     */
    public String verificaBloqueioVinculoCnv(String csaCodigo, String svcIdentificador, String vrsCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if(!TextHelper.isNull(vrsCodigo)) {
            // Seleciona os vínculos que não podem reservar margem para este svc e csa
            try {

                final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFO_VINC_BLOQ_PADRAO, responsavel);
                final boolean bloqPadrao = CodedValues.TPA_SIM.equals(pcsVlr);

                final ListaConvenioVinculoRegistroServidorQuery query = new ListaConvenioVinculoRegistroServidorQuery();
                query.csaCodigo = csaCodigo;
                query.svcIdentificador = svcIdentificador;
                final List<String> vincBloqueados = query.executarLista();

                if ((bloqPadrao && ((vincBloqueados == null) || vincBloqueados.isEmpty() || !vincBloqueados.contains(vrsCodigo))) || (!bloqPadrao && vincBloqueados.contains(vrsCodigo))) {
                    final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                    final VinculoConsignataria vcsa = consignatariaController.findVinculoCsaPorVrsCsa(csaCodigo, vrsCodigo, responsavel);
                    if (TextHelper.isNull(vcsa)) {
                            return "BLOQ";
                    } else {
                            return "BLOQ";
                    }
                }
            } catch (HQueryException | ParametroControllerException | ConsignatariaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.validar.vinculo.servidor", responsavel, ex);
            }
        }
        return null;
    }
}
