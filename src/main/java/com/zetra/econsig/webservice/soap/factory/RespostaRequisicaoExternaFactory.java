package com.zetra.econsig.webservice.soap.factory;

import static com.zetra.econsig.webservice.CamposAPI.ANEXOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACOES;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIAS;
import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_PERFIL_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.CONTRACHEQUE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIOS;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SERVIDOR_V3_0;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTOS;
import static com.zetra.econsig.webservice.CamposAPI.INFO_COMPRAS;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAOS;
import static com.zetra.econsig.webservice.CamposAPI.SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V4_0;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V7_0;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR_V8_0;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaRequisicaoExternaFactory</p>
 * <p>Description: Factory para classes command que tratam respostas a requisições externas ao eConsig</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaRequisicaoExternaFactory {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaRequisicaoExternaFactory.class);

    public static RespostaRequisicaoExternaCommand createRespostaRequisicaoExterna(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        final String operacao = parametros.get(OPERACAO) != null ? parametros.get(OPERACAO).toString() : "ERRO";
        final boolean sucesso = (parametros.get(SUCESSO) != null) && "S".equalsIgnoreCase(parametros.get(SUCESSO).toString());
        Class<? extends RespostaRequisicaoExternaCommand> commandClass = null;

        if (sucesso) {
            if (CodedValues.OP_CONSULTAR_MARGEM.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsultarMargemCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_MARGEM_V3_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v3.RespostaConsultarMargemCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_MARGEM_V6_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v6.RespostaConsultarMargemCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_MARGEM_V7_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v7.RespostaConsultarMargemCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_MARGEM_V8_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaConsultarMargemCommand.class;
            }

            if (!CodedValues.OP_CONSULTAR_MARGEM.equalsIgnoreCase(operacao)
                    && !CodedValues.OP_CONSULTAR_MARGEM_V3_0.equalsIgnoreCase(operacao)
                    && !CodedValues.OP_CONSULTAR_MARGEM_V6_0.equalsIgnoreCase(operacao)
                    && !CodedValues.OP_CONSULTAR_MARGEM_V7_0.equalsIgnoreCase(operacao)
                    && !CodedValues.OP_CONSULTAR_MARGEM_V8_0.equalsIgnoreCase(operacao)
                    && !CodedValues.OP_LISTA_SOLICITACOES.equalsIgnoreCase(operacao)
                    && !CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao)
                    && !CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao)) {

                // Exibe os atributos da autorização
                try {
                    final CustomTransferObject autorizacao = (CustomTransferObject) parametros.get(CONSIGNACAO);
                    if (autorizacao != null) {
                        if (CodedValues.OP_DESLIQUIDAR_CONTRATO.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_ALONGAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_ALTERAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_AUTORIZAR_RESERVA_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_CANCELAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_CANCELAR_RENEGOCIACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_CANCELAR_RESERVA_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_CONFIRMAR_RESERVA_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_CONFIRMAR_SOLICITACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_CONSULTAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_CONSULTAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_DETALHAR_CONSULTA_ADE_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_DETALHAR_CONSULTA_ADE_V8_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_INSERIR_SOLICITACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_INSERIR_SOLICITACAO_V8_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_LIQUIDAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_REATIVAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_RENEGOCIAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_RESERVAR_MARGEM_V6_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_RESERVAR_MARGEM_V8_0.equalsIgnoreCase(operacao) ||
                            CodedValues.OP_SUSPENDER_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao)) {
                                commandClass = com.zetra.econsig.webservice.command.saida.v6.RespostaConsignacaoCommand.class;
                            } else {
                                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsignacaoCommand.class;
                            }
                    }
                } catch (final ClassCastException cce) {
                    if (parametros.get(CONSIGNACAO) instanceof List<?>) {
                        commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsignacoesCommand.class;

                        return (RespostaRequisicaoExternaCommand) getNewInstance(commandClass, responsavel);
                    }

                }
            }

            if (CodedValues.OP_LISTA_SOLICITACOES.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaListaSolicitacoesCommand.class;
            }

            if (CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaSimularConsignacaoCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_PARAMETROS.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsultarParametrosCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_PARAMETROS_v2_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v2.RespostaConsultarParametrosCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_PARAMETROS_v8_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaConsultarParametrosCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_PARCELA.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v3.RespostaConsultarParcelaCommand.class;
            }

            if (CodedValues.OP_LIQUIDAR_PARCELA.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v3.RespostaLiquidarParcelaCommand.class;
            }

            if (CodedValues.OP_CADASTRAR_TAXA_JUROS.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v3.RespostaCadastrarTaxaJurosCommand.class;
            }

            if (parametros.get(CONSIGNACAO) != null) {
                if (CodedValues.OP_CONSULTAR_MARGEM_V7_0.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_MARGEM_V8_0.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_MARGEM_V6_0.equalsIgnoreCase(operacao)) {
                    commandClass = com.zetra.econsig.webservice.command.saida.v6.RespostaConsignacaoCommand.class;
                } else if (CodedValues.OP_CONSULTAR_MARGEM.equalsIgnoreCase(operacao) || CodedValues.OP_CONSULTAR_MARGEM_V3_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_LISTA_SOLICITACOES.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao)) {
                    commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsignacaoCommand.class;
                }
            }

            if (parametros.get(CONSIGNACOES) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsignacoesCommand.class;
            }

            if (parametros.get(ANEXOS_CONSIGNACAO) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaAnexosConsignacaoCommand.class;
            }

            if (parametros.get(CONSIGNATARIAS) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsignatariasCommand.class;
            }

            if (parametros.get(ORGAOS) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaOrgaosCommand.class;
            }

            if (parametros.get(CONVENIOS) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConveniosCommand.class;
            }

            if (parametros.get(ESTABELECIMENTOS) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaEstabelecimentosCommand.class;
            }

            if (parametros.get(CONSULTA_SERVICOS) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsultaServicosCommand.class;
            }

            if (parametros.get(CONSULTA_PERFIL_USUARIO) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsultaPerfilUsuarioCommand.class;
            }

            if (parametros.get(INFO_COMPRAS) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaInfoCompraCommand.class;
            }

            if (parametros.get(CONTRACHEQUE) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaContrachequeCommand.class;
            }

            if (parametros.get(DADOS_SERVIDOR) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaDadosServidorCommand.class;
            }

            if (parametros.get(DADOS_SERVIDOR_V3_0) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.v3.RespostaDadosServidorCommand.class;
            }

            if (parametros.get(DADOS_CONSIGNACAO) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaDadosConsignacaoCommand.class;
            }

            if ((CodedValues.OP_CADASTRAR_SERVIDOR.equalsIgnoreCase(operacao) || CodedValues.OP_PESQUISAR_SERVIDOR.equalsIgnoreCase(operacao)) &&
                (parametros.get(SERVIDORES) != null)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v4.RespostaServidoresCommand.class;
            }

            if ((CodedValues.OP_CADASTRAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao) || CodedValues.OP_PESQUISAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao)) &&
                    (parametros.get(SERVIDORES) != null)) {
                    commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaServidoresCommand.class;
                }

            if (CodedValues.OP_PESQUISAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaServidoresCommand.class;
            }

            if (CodedValues.OP_CADASTRAR_SERVIDOR.equalsIgnoreCase(operacao) && (parametros.get(SERVIDOR_V4_0) != null)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v4.RespostaServidorCommand.class;
            }

            if (CodedValues.OP_CADASTRAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao) && (parametros.get(SERVIDOR_V8_0) != null)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaServidorCommand.class;
            }

            if (CodedValues.OP_EDITAR_STATUS_SERVIDOR.equalsIgnoreCase(operacao) && (parametros.get(SERVIDOR_V7_0) != null)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v7.RespostaServidorCommand.class;
            }

            if (CodedValues.OP_EDITAR_STATUS_SERVIDOR_V8_0.equalsIgnoreCase(operacao) && (parametros.get(SERVIDOR_V8_0) != null)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaServidorCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_MOVIMENTO_FINANCEIRO.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaMovimentoFinanceiroCommand.class;
            }

            if (CodedValues.OP_LISTAR_PARCELAS.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaListarParcelasCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_VALIDACAO_DOCUMENTACAO_v8_0.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaConsultarValidacaoDocumentacaoCommand.class;
            }

            if (CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaConsultaPerfilConsignadoCommand.class;
            }
            
            if (CodedValues.OP_CONSULTAR_REGRAS.equalsIgnoreCase(operacao)) {
                commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaConsultarRegrasCommand.class;
            }

        } else {
            if (parametros.get(SERVIDORES) != null) {
                if(CodedValues.OP_CONSULTAR_MARGEM_V8_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_CONSULTAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER_V8_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_EDITAR_STATUS_SERVIDOR_V8_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_DETALHAR_CONSULTA_ADE_V8_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_INSERIR_SOLICITACAO_V8_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_RESERVAR_MARGEM_V8_0.equalsIgnoreCase(operacao)) {
                    commandClass = com.zetra.econsig.webservice.command.saida.v8.RespostaServidoresCommand.class;
                } else if (CodedValues.OP_CONSULTAR_MARGEM_V7_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_EDITAR_STATUS_SERVIDOR.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER.equalsIgnoreCase(operacao)) {
                    commandClass = com.zetra.econsig.webservice.command.saida.v7.RespostaServidoresCommand.class;
                } else if (CodedValues.OP_DETALHAR_CONSULTA_ADE_V4_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_CONSULTAR_MARGEM_V6_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_CONSULTAR_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_INSERIR_SOLICITACAO_V6_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_DETALHAR_CONSULTA_ADE_V6_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_RESERVAR_MARGEM_V6_0.equalsIgnoreCase(operacao) ||
                           CodedValues.OP_SUSPENDER_CONSIGNACAO_V6_0.equalsIgnoreCase(operacao)) {
                    commandClass = com.zetra.econsig.webservice.command.saida.v4.RespostaServidoresCommand.class;
                } else {
                    commandClass = com.zetra.econsig.webservice.command.saida.RespostaServidoresCommand.class;
                }
            }

            if (parametros.get(CONSIGNACOES) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostaConsignacoesCommand.class;
            }

            if (parametros.get(SERVICOS) != null) {
                commandClass = com.zetra.econsig.webservice.command.saida.RespostasServicosCommand.class;
            }
        }

        if (commandClass == null) {
            commandClass = com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand.class;
        }

        return (RespostaRequisicaoExternaCommand) getNewInstance(commandClass, responsavel);
    }

    private static Object getNewInstance(Class<? extends RespostaRequisicaoExternaCommand> commandClass, AcessoSistema responsavel) throws ZetraException {
        try {
            final Constructor<?> con = Class.forName(commandClass.getName()).getConstructor(AcessoSistema.class);
            return con.newInstance(responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
