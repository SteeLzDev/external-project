package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;

/**
 * <p>Title: SolicitarReservaCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de solicitar reserva</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class SolicitarReservaCommand extends RequisicaoExternaCommand {

    protected Short adeIncMargem;
    protected Short adeIntFolha;
    protected String adeTipoVlr;
    protected int qtdeConsignatariasSimulacao;
    protected boolean bloqueiaReservaLimiteSimulador;
    protected String sadCodigo;
    protected boolean comSerSenha = false;
    protected String serSenha;
    protected CustomTransferObject convenio;
    protected String adeIdentificador;
    protected String cftCodigo;
    protected String adeCodigo;
    protected BigDecimal vlrLiberado;
    protected Short ranking;

    protected SolicitarReservaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);

        // Inicializa o campo adeIdentificador
        final String operacao = parametros.get(OPERACAO).toString();
        if (CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao)) {
            adeIdentificador = (String) parametros.get(ADE_IDENTIFICADOR);
        }
        if (CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao)) {
            adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);
        }
        if (CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao)) {
            adeIdentificador = (String) parametros.get(NOVO_ADE_IDENTIFICADOR);
        }
        if (adeIdentificador == null) {
            adeIdentificador = "";
        }

        parametros.put(NOVO_ADE_IDENTIFICADOR, adeIdentificador);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        preProcessaReserva(parametros);
        validaReserva(parametros);
        realizaReserva(parametros);
        buscaNovaAutorizacao(parametros);
    }

    /**
     * realiza a reserva de margem ou inserção de solicitação ou renegociação de consignação de acordo com a sub
     * classe que a implementar
     * @param parametros
     * @throws ViewHelperException
     */
    protected abstract void realizaReserva(Map<CamposAPI, Object> parametros) throws ZetraException;

    private void validaReserva(Map<CamposAPI, Object> parametros) throws ZetraException {
        final CustomTransferObject reserva = new CustomTransferObject();
        reserva.setAttribute("ADE_PRAZO", parametros.get(ADE_PRAZO));
        reserva.setAttribute("ADE_CARENCIA", parametros.get(ADE_CARENCIA));
        reserva.setAttribute("RSE_PRAZO", parametros.get(RSE_PRAZO));
        reserva.setAttribute("ADE_VLR", parametros.get(ADE_VLR));
        reserva.setAttribute("RSE_CODIGO", parametros.get(RSE_CODIGO));
        reserva.setAttribute("SVC_CODIGO", parametros.get(SVC_CODIGO));
        reserva.setAttribute("CSE_CODIGO", CodedValues.CSE_CODIGO_SISTEMA);
        reserva.setAttribute("ADE_IDENTIFICADOR", adeIdentificador);
        reserva.setAttribute("OPERACAO", parametros.get(OPERACAO));

        // Passa "validaMargem" como "false" pois as operações que usam este método (ReservarMargemCommand, InserirSolicitacaoCommand)
        // já fazem validação da margem, e as operações de renegociação e compra (RenegociarConsignacaoCommand) já passavam false para
        // a validação, tornando esta desnecessária.
        ReservaMargemHelper.validaReserva(convenio, reserva, responsavel, false, false, true);
    }

    protected void preProcessaReserva(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String svcCodigo = (String) parametros.get(SVC_CODIGO);
        final String cnvCodigo = (String) parametros.get(CNV_CODIGO);
        final String rseCodigo = (String) parametros.get(RSE_CODIGO);
        final String operacao = parametros.get(OPERACAO).toString();
        final String csaCodigo = (String) parametros.get(CSA_CODIGO);

        final ConvenioDelegate cnvDelegate = new ConvenioDelegate();

        // Pega os parâmetros do convenio
        try {
            if (cnvCodigo != null) {
                convenio = cnvDelegate.getParamCnv(cnvCodigo, responsavel);

                parametros.put(SVC_CODIGO, convenio.getAttribute(Columns.SVC_CODIGO).toString());
                parametros.put(ORG_CODIGO, convenio.getAttribute(Columns.CNV_ORG_CODIGO).toString());
            } else {
                throw new ZetraException("mensagem.convenioNaoEncontrado", responsavel);
            }
        } catch (final ConvenioControllerException ex) {
            throw ex;
        }

        ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
        boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);

        adeIncMargem  = paramSvcCse.getTpsIncideMargem();
        adeIntFolha   = paramSvcCse.getTpsIntegraFolha();
        adeTipoVlr   = paramSvcCse.getTpsTipoVlr();

        // Quantidade de consignatárias permitidas no simulador
        qtdeConsignatariasSimulacao = paramSvcCse.getTpsQtdCsaPermitidasSimulador();
        bloqueiaReservaLimiteSimulador = paramSvcCse.isTpsBloqueiaReservaLimiteSimulador();

        // Valida a Senha do Servidor
        sadCodigo = null;
        comSerSenha = false;
        serSenha = (String) parametros.get(SER_SENHA);
        final String token = (String) parametros.get(TOKEN);
        final String loginExterno = (String) parametros.get(SER_LOGIN);

        final String tpaCsaValidaSenha = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_VALIDA_SENHA_SERVIDOR_SOAP, responsavel);
        final boolean csaValidaSenha = (!TextHelper.isNull(tpaCsaValidaSenha) && !CodedValues.TPA_NAO.equals(tpaCsaValidaSenha));

        if (!TextHelper.isNull(serSenha) || !TextHelper.isNull(token)) {
            try {
                validarSenhaServidor(rseCodigo, serSenha,
                        (CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao) ||
                                CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao)),
                                loginExterno, csaCodigo, token, responsavel);
                comSerSenha = true;

                if (CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao)) {
                    sadCodigo = CodedValues.SAD_SOLICITADO;
                }
            } catch (final ZetraException ex) {
                // Se a senha é obrigatória então retorna erro
                if (serSenhaObrigatoria || csaValidaSenha || CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao)) {
                    throw ex;
                }
            }
        } else if (serSenhaObrigatoria) {
            throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
        }
    }

    /**
     * recupera informações da reserva recém criada para enviar como resposta da requisição externa
     * @param parametros
     * @throws ZetraException
     */
    private void buscaNovaAutorizacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        final String serCodigo = (String) parametros.get(SER_CODIGO);
        final String orgCodigo = (String) parametros.get(ORG_CODIGO);

        final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        final ServidorDelegate serDelegate = new ServidorDelegate();
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();

        // Busca a nova autorização
        final TransferObject novaAutorizacao = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

        // Guarda o Boleto no Hash para a geração do resultado
        // Busca o servidor
        ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        servidor = serDelegate.findServidor(servidor, responsavel);
        // Pega a descrição do codigo de estado civil
        final String serEstCivil = serDelegate.getEstCivil(servidor.getSerEstCivil(), responsavel);
        // Busca o órgão
        final OrgaoTransferObject orgao = cseDelegate.findOrgao(orgCodigo, responsavel);

        final CustomTransferObject boleto = new CustomTransferObject();
        boleto.setAtributos(servidor.getAtributos()); // Adiciona Informações do servidor
        boleto.setAttribute(Columns.SER_EST_CIVIL, serEstCivil); // Adiciona a Descrição do estado civil
        boleto.setAtributos(orgao.getAtributos()); // Adiciona Informações do órgão
        boleto.setAtributos(novaAutorizacao.getAtributos()); // Adiciona Informações da autorização

        //DESENV-13634 avisa na mensagem de retorno da exigência mínima de anexos, caso o serviço esteja assim configurado.
        final Anexo anexo = getAnexo(parametros.get(ANEXO));
        if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, responsavel) && responsavel.isCsaCor()) {
            final CustomTransferObject paramSvcTO = parametroController.getParamSvcCse((String) parametros.get(SVC_CODIGO), CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR, responsavel);

            if ((paramSvcTO != null) && !TextHelper.isNull(paramSvcTO.getAttribute(Columns.PSE_VLR)) && !TextHelper.isNull(paramSvcTO.getAttribute(Columns.PSE_VLR_REF))) {
                final Short diasParaAnexarArqNecessarios = Short.parseShort((String) paramSvcTO.getAttribute(Columns.PSE_VLR));
                final Short numAnexosMin = Short.parseShort((String) paramSvcTO.getAttribute(Columns.PSE_VLR_REF));

                if ((diasParaAnexarArqNecessarios != null) && (numAnexosMin != null) && (diasParaAnexarArqNecessarios.shortValue() > 0) && (numAnexosMin.shortValue() > 0)) {
                    final int numAnexosFaltantes = (anexo != null) ? (numAnexosMin.intValue() - 1) : numAnexosMin.intValue();

                    if (numAnexosFaltantes > 0) {
                        final Date prazoParaAnexar = DateHelper.addDays(DateHelper.getSystemDatetime(), diasParaAnexarArqNecessarios.intValue());

                        parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel) + ". " +
                                                   ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.anexos.minimos", responsavel, Integer.valueOf(numAnexosMin).toString(),
                                                   DateHelper.format(prazoParaAnexar, LocaleHelper.getDateTimePattern().replace(":ss", "")),
                                                   Integer.toString(numAnexosFaltantes)));

                    }
                }
            }
        }

        // Guarda o boleto no hash para ser consultada na geração do resultado
        parametros.put(BOLETO, boleto);
        parametros.remove(CONSIGNACAO);
    }
}
