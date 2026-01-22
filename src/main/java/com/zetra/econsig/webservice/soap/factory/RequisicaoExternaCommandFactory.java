package com.zetra.econsig.webservice.soap.factory;

import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;

import java.lang.reflect.Constructor;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.entrada.RequisicaoExternaCommand;

/**
 * <p>Title: RequisicaoExternaCommandFactory</p>
 * <p>Description: Factory para classes command que tratam requisições externas ao eConsig</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RequisicaoExternaCommandFactory {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RequisicaoExternaCommandFactory.class);

    public static RequisicaoExternaCommand createRequisicaoExternaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        final String operacao = parametros.get(OPERACAO) != null ? parametros.get(OPERACAO).toString() : "ERRO";
        Class<? extends RequisicaoExternaCommand> commandClass = null;

        if (CodedValues.OPERACOES_ALONGAR_CONSIGNACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.AlongarConsignacaoCommand.class;

        } else if (CodedValues.OPERACOES_ALTERAR_CONSIGNACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.AlterarConsignacaoCommand.class;

        } else if (CodedValues.OP_ATUALIZAR_PARCELA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.AtualizarParcelaCommand.class;

        } else if (CodedValues.OPERACOES_AUTORIZAR_RESERVA.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.AutorizarReservaCommand.class;

        } else if (CodedValues.OPERACOES_CANCELAR_CONSIGNACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CancelarConsignacaoCommand.class;

        } else if (CodedValues.OP_CANCELAR_CONSIGNACAO_SV.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CancelarConsignacaoSvCommand.class;

        } else if (CodedValues.OPERACOES_CANCELAR_RESERVA.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CancelarReservaCommand.class;

        } else if (CodedValues.OPERACOES_CONFIRMAR_RESERVA.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConfirmarReservaCommand.class;

        } else if (CodedValues.OPERACOES_CONFIRMAR_SOLICITACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConfirmarSolicitacaoCommand.class;

        } else if (CodedValues.OPERACOES_CONSULTAR_CONSIGNACAO.contains(operacao) || CodedValues.OPERACOES_DETALHAR_ADE.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarConsignacaoCommand.class;

        } else if (CodedValues.OP_CONSULTAR_MARGEM.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarMargemCommand.class;

        } else if (CodedValues.OP_CONSULTAR_MARGEM_V3_0.equalsIgnoreCase(operacao) ||
                   CodedValues.OP_CONSULTAR_MARGEM_V6_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v3.ConsultarMargemCommand.class;

        } else if (CodedValues.OP_CONSULTAR_MARGEM_V7_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v7.ConsultarMargemCommand.class;

        } else if (CodedValues.OP_CONSULTAR_MARGEM_V8_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v8.ConsultarMargemCommand.class;

        } else if (CodedValues.OPERACOES_INSERIR_SOLICITACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.InserirSolicitacaoCommand.class;

        } else if (CodedValues.OPERACOES_LIQUIDAR_CONSIGNACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.LiquidarConsignacaoCommand.class;

        } else if (CodedValues.OP_LISTA_SOLICITACOES.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ListaSolicitacoesCommand.class;

        } else if (CodedValues.OPERACOES_REATIVAR_CONSIGNACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ReativarConsignacaoCommand.class;

        } else if (CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao) || CodedValues.OP_COMPRAR_CONTRATO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.RenegociarConsignacaoCommand.class;

        } else if (CodedValues.OPERACOES_RESERVAR_MARGEM.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ReservarMargemCommand.class;

        } else if (CodedValues.OP_SIMULAR_CONSIGNACAO.equalsIgnoreCase(operacao) || CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.SimularConsignacaoCommand.class;

        } else if (CodedValues.OPERACOES_SUSPENDER_CONSIGNACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.SuspenderConsignacaoCommand.class;

        } else if (CodedValues.OP_VALIDAR_ACESSO_SERVIDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ValidarAcessoServidorCommand.class;

        } else if (CodedValues.OP_CONSULTAR_PARAMETROS.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarParametrosCommand.class;

        } else if (CodedValues.OP_CONSULTAR_PARAMETROS_v2_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v2.ConsultarParametrosCommand.class;

        } else if (CodedValues.OP_CONSULTAR_PARAMETROS_v8_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v8.ConsultarParametrosCommand.class;

        } else if (CodedValues.OP_CADASTRAR_CONSIGNATARIA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CadastrarConsignatariaCommand.class;

        } else if (CodedValues.OP_CADASTRAR_VERBA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CadastrarConvenioCommand.class;

        } else if (CodedValues.OP_CADASTRAR_ORGAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CadastrarOrgaoCommand.class;

        } else if (CodedValues.OP_CADASTRAR_ESTABELECIMENTO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CadastrarEstabelecimentoCommand.class;

        } else if (CodedValues.OP_CADASTRAR_SERVICO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CadastrarServicoCommand.class;

        } else if (CodedValues.OP_ATUALIZAR_MARGEM.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.AtualizarMargemCommand.class;

        } else if (CodedValues.OP_CONSULTAR_CONSIGNATARIA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarConsignatariaCommand.class;

        } else if (CodedValues.OP_CONSULTAR_ORGAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarOrgaoCommand.class;

        } else if (CodedValues.OP_CONSULTAR_VERBA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarConvenioCommand.class;

        } else if (CodedValues.OP_CONSULTAR_ESTABELECIMENTO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarEstabelecimentoCommand.class;

        } else if (CodedValues.OP_CONSULTAR_SERVICO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarServicoCommand.class;

        } else if (CodedValues.OP_ACOMPANHAR_COMPRA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.AcompanharCompraContratoCommand.class;

        } else if (CodedValues.OP_EDT_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.InformarSaldoDevedorCommand.class;

        } else if (CodedValues.OP_INF_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.InformarPagamentoSaldoDevedorCommand.class;

        } else if (CodedValues.OP_REJ_PG_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.RejeitarPgSaldoDevedorCommand.class;

        } else if (CodedValues.OP_SOL_RECALC_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.SolicitarRecalculoSaldoDevedorCommand.class;

        } else if (CodedValues.OP_RETIRAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.RetirarContratoCompraCommand.class;

        } else if (CodedValues.OP_CANCELAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CancelarCompraCommand.class;

        } else if (CodedValues.OP_LIQUIDAR_CONTRATO_COMPRA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.LiquidarCompraContratoCommand.class;

        } else if (CodedValues.OP_CONSULTAR_ADE_PARA_COMPRA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarConsignacaoParaCompraCommand.class;

        } else if (CodedValues.OPERACOES_CANCELAR_RENEGOCIACAO.contains(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CancelarRenegociacaoCommand.class;

        } else if (CodedValues.OP_INCLUIR_ANEXO_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.IncluirAnexoConsignacaoCommand.class;

        } else if (CodedValues.OP_GERAR_SENHA_AUTORIZACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.GerarSenhaAutorizacaoCommand.class;

        } else if (CodedValues.OP_CONSULTAR_CONTRACHEQUE.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarContrachequeCommand.class;

        } else if (CodedValues.OP_CANCELAR_SOLICITACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CancelarSolicitacaoCommand.class;

        } else if (CodedValues.OP_CONS_DADOS_CADASTRAIS.equalsIgnoreCase(operacao) || CodedValues.OP_CONS_DADOS_CADASTRAIS_V3_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarDadosCadastraisCommand.class;

        } else if (CodedValues.OP_VERIFICA_LIMITE_SENHA_AUT.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.VerificarLimitesSenhaAutorizacaoCommand.class;

        } else if (CodedValues.OP_RECUPERAR_PERG_DADOS_CAD.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.RecuperarPerguntaDadosCadastraisCommand.class;

        } else if (CodedValues.OP_VERIFICA_RESP_PERG_DADOS.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.VerificarRespostaDadosCadastraisCommand.class;

        } else if (CodedValues.OP_INCLUIR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.IncluirDadoConsignacaoCommand.class;

        } else if (CodedValues.OP_LISTAR_DADO_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ListarDadoConsignacaoCommand.class;

        } else if (CodedValues.OP_REQUISICAO_BASICA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.RequisicaoExternaBasicaCommand.class;

        } else if (CodedValues.OP_VERIFICAR_EMAIL_SERVIDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.VerificarEmailServidorCommand.class;

        } else if (CodedValues.OP_CADASTRAR_EMAIL_SERVIDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CadastrarEmailServidorCommand.class;

        } else if (CodedValues.OP_CONSULTAR_PARCELA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v3.ConsultarParcelaCommand.class;

        } else if (CodedValues.OP_LIQUIDAR_PARCELA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v3.LiquidarParcelaCommand.class;

        } else if (CodedValues.OP_CADASTRAR_TAXA_JUROS.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v3.CadastrarTaxaJurosCommand.class;

        } else if (CodedValues.OP_PESQUISAR_SERVIDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v4.PesquisarServidorCommand.class;

        } else if (CodedValues.OP_PESQUISAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v8.PesquisarServidorCommand.class;

        } else if (CodedValues.OP_CADASTRAR_SERVIDOR.equalsIgnoreCase(operacao) || CodedValues.OP_CADASTRAR_SERVIDOR_V8_0.equalsIgnoreCase(operacao) ) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v4.CadastrarServidorCommand.class;

        } else if (CodedValues.OP_CADASTRAR_USUARIO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.CadastrarUsuarioCommand.class;

        } else if (CodedValues.OP_MODIFICAR_CONSIGNANTE.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ModificarConsignanteCommand.class;

        } else if (CodedValues.OP_MODIFICAR_PARAM_SISTEMA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ModificarParametroSistemaCommand.class;

        } else if (CodedValues.OP_MODIFICAR_PARAM_SERVICO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ModificarParametroServicoCommand.class;

        } else if (CodedValues.OP_CADASTRAR_CALENDARIO_FOLHA.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.AtualizarCalendarioFolhaCommand.class;

        } else if (CodedValues.OP_MODIFICAR_USUARIO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ModificarUsuarioCommand.class;

        } else if (CodedValues.OP_EDITAR_STATUS_SERVIDOR.equalsIgnoreCase(operacao) || CodedValues.OP_EDITAR_STATUS_SERVIDOR_V8_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v7.EditarStatusServidorCommand.class;

        } else if (CodedValues.OP_CADASTRAR_USUARIO_OPERACIONAL.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v7.CadastrarUsuarioCommand.class;

        } else if (CodedValues.OP_EDITAR_STATUS_USUARIO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v7.EditarStatusUsuarioCommand.class;

        } else if (CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER.equalsIgnoreCase(operacao) || CodedValues.OP_VALIDAR_DADOS_BANCARIOS_SER_V8_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v7.ValidarDadosBancariosServidorCommand.class;

        } else if (CodedValues.OP_CONSULTAR_MOVIMENTO_FINANCEIRO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarMovimentoFinanceiroCommand.class;

        } else if (CodedValues.OP_CONSULTAR_PERFIL_USUARIO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarPerfilUsuarioCommand.class;

        } else if (CodedValues.OP_DESLIQUIDAR_CONTRATO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v7.DesliquidarConsignacaoCommand.class;

        } else if (CodedValues.OP_DOWNLOAD_ANEXOS_CONSIGNACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v7.DownloadAnexosConsignacaoCommand.class;

        } else if (CodedValues.OP_LISTAR_ARQUIVO_INTEGRACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ListarArquivoIntegracaoCommand.class;

        } else if (CodedValues.OP_ENVIAR_ARQUIVO_INTEGRACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.EnviarArquivoIntegracaoCommand.class;

        } else if (CodedValues.OP_DOWNLOAD_ARQUIVO_INTEGRACAO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.DownloadArquivoIntegracaoCommand.class;

        } else if (CodedValues.OP_LISTAR_SOLICITACAO_SALDO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ListarSolicitacaoSaldoCommand.class;

        } else if (CodedValues.OP_EDITAR_SALDO_DEVEDOR.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.InformarSaldoDevedorCommand.class;
        } else if (CodedValues.OP_LISTAR_PARCELAS.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ListarParcelasCommand.class;
        } else if (CodedValues.OP_CONSULTAR_VALIDACAO_DOCUMENTACAO_v8_0.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.v8.ConsultarValidacaoDocumentacaoCommand.class;
        } else if (CodedValues.OP_CONSULTAR_PERFIL_CONSIGNADO.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarPerfilConsignadoCommand.class;
        } else if (CodedValues.OP_CONSULTAR_REGRAS.equalsIgnoreCase(operacao)) {
            commandClass = com.zetra.econsig.webservice.command.entrada.ConsultarRegrasCommand.class;
        }

        return (RequisicaoExternaCommand) getNewInstance(commandClass, parametros, responsavel);

    }

    private static Object getNewInstance(Class<? extends RequisicaoExternaCommand> commandClass, Map<CamposAPI, Object> parametros, AcessoSistema responsavel) throws ZetraException {
        try {
            final Constructor<?> con = Class.forName(commandClass.getName()).getConstructor(Map.class, AcessoSistema.class);
            return con.newInstance(parametros, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
