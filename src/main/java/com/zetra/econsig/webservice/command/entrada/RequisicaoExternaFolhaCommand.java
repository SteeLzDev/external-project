package com.zetra.econsig.webservice.command.entrada;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RequisicaoExternaFolhaCommand</p>
 * <p>Description: classe abstrata command da qual todos os commands relativos
 *                 à operações de Folha eConsig requisitadas externamente devem extender.
 *                 possui validações comuns e os métodos públicos a serem extendidos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class RequisicaoExternaFolhaCommand extends RequisicaoExternaCommand {

    public RequisicaoExternaFolhaCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaPermissao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String operacao = (String) parametros.get(CamposAPI.OPERACAO);
        if (!temPermissao(responsavel.getUsuCodigo(), operacao)) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }
    }

    @Override
    protected abstract void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException;

    /**
     * Verifica se o usuário tem permissão para executar a operação
     * @param usuCodigo : código do usuário
     * @param operacao  : operação que está sendo executada
     * @return          : true se o usuário tem permissão, falso caso contrário
     */
    private boolean temPermissao(String usuCodigo, String operacao) {
        // Se não tem permissão de Integrar folha via SOAP, então não precisa continuar
        if (!responsavel.temPermissao(CodedValues.FUN_INTEGRA_SOAP_FOLHA)) {
            return false;
        }

        // Se é usuário servidor, não tem permissão para fazer nada
        if (responsavel.isSer()) {
            return false;
        }

        // Determina qual função de acordo com a operação
        String funCodigo = null;

        if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_CONSIGNATARIA)) {
            funCodigo = CodedValues.FUN_EDT_CONSIGNATARIAS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_VERBA)) {
            funCodigo = CodedValues.FUN_EDT_CONVENIOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_ORGAO)) {
            funCodigo = CodedValues.FUN_EDT_ORGAOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_ESTABELECIMENTO)) {
            funCodigo = CodedValues.FUN_EDT_ESTABELECIMENTOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_SERVICO)) {
            funCodigo = CodedValues.FUN_EDT_SERVICOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_ATUALIZAR_MARGEM)) {
            funCodigo = CodedValues.FUN_EDT_SERVIDOR;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_CONSIGNATARIA)) {
            funCodigo = CodedValues.FUN_CONS_CONSIGNATARIAS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_VERBA)) {
            funCodigo = CodedValues.FUN_CONS_CONVENIOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_ORGAO)) {
            funCodigo = CodedValues.FUN_CONS_ORGAOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_ESTABELECIMENTO)) {
            funCodigo = CodedValues.FUN_CONS_ESTABELECIMENTOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_SERVICO)) {
            funCodigo = CodedValues.FUN_CONS_SERVICOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_USUARIO)) {
            /*
             * Função a ser validada deve considerar a entidade que será criada,
             * validação que será realizada no método CadastrarUsuarioCommand.validaEntrada()
             */
            return true;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_MODIFICAR_CONSIGNANTE)) {
            funCodigo = CodedValues.FUN_EDT_CONSIGNANTE;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_MODIFICAR_PARAM_SISTEMA)) {
            funCodigo = CodedValues.FUN_EDT_PARAM_SISTEMA_CSE;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_MODIFICAR_PARAM_SERVICO)) {
            funCodigo = CodedValues.FUN_EDT_SERVICOS;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_CALENDARIO_FOLHA)) {
            funCodigo = CodedValues.FUN_EDT_CALENDARIO_FOLHA;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_MODIFICAR_USUARIO)) {
            /*
             * Função a ser validada deve considerar a entidade que será criada,
             * validação que será realizada no método ModificarUsuarioCommand.validaEntrada()
             */
            return true;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_PERFIL_USUARIO)) {
            /*
             * Função a ser validada deve considerar a entidade que será consultada,
             * validação que será realizada no método ConsultarPerfilUsuarioCommand.validaEntrada()
             */
            return true;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_CONSULTAR_MOVIMENTO_FINANCEIRO)) {
            funCodigo = CodedValues.FUN_CONSULTAR_MOVIMENTO_FINANCEIRO;
        } else if (operacao.equalsIgnoreCase(CodedValues.OP_LISTAR_ARQUIVO_INTEGRACAO) || operacao.equalsIgnoreCase(CodedValues.OP_ENVIAR_ARQUIVO_INTEGRACAO) || operacao.equalsIgnoreCase(CodedValues.OP_DOWNLOAD_ARQUIVO_INTEGRACAO)) {
            funCodigo = CodedValues.FUN_UPL_ARQUIVOS;
        } else {
            // Se não for nenhuma das acima, então não tem permissão
            return false;
        }

        if (funCodigo == null || !responsavel.temPermissao(funCodigo)) {
            return false;
        }

        // Seta qual a função está sendo acessada, para gravação de log
        responsavel.setFunCodigo(funCodigo);

        return true;
    }
}
