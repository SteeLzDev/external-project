package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ALERTA;
import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO_INTEGRACAO;
import static com.zetra.econsig.webservice.CamposAPI.COD_RETORNO;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_ID;
import static com.zetra.econsig.webservice.CamposAPI.ENTIDADE_NOME;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.NUMERO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.PODE_SIMULAR;
import static com.zetra.econsig.webservice.CamposAPI.PODE_SOLICITAR;
import static com.zetra.econsig.webservice.CamposAPI.RESULTADO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_AUTORIZACAO;
import static com.zetra.econsig.webservice.CamposAPI.SUCESSO;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO_CADASTRADO;
import static com.zetra.econsig.webservice.CamposAPI.USU_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;
import static com.zetra.econsig.webservice.CamposAPI.USU_SENHA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaRequisicaoExternaCommand</p>
 * <p>Description: classe command da qual todos os commands relativos
 *                 aos tipos de respostas às operações eConsig requisitadas externamente devem extender.
 *                 possui validações comuns e os métodos públicos a serem extendidos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaRequisicaoExternaCommand {

    protected AcessoSistema responsavel;

    public RespostaRequisicaoExternaCommand(AcessoSistema responsavel) {
        super();
        this.responsavel = responsavel;
    }

    public RegistroRespostaRequisicaoExterna geraCabecalhoResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        String operacao = parametros.get(OPERACAO) != null ? parametros.get(OPERACAO).toString() : "ERRO";
        String sucesso = parametros.get(SUCESSO) != null ? parametros.get(SUCESSO).toString() : "N";

        // Adiciona o regitro 'RESULTADO'
        RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
        reg.setNome(RESULTADO);

        reg.addAtributo(OPERACAO, operacao);
        reg.addAtributo(SUCESSO, sucesso);

        if (parametros.get(COD_RETORNO) != null) {
            // Adiciona código do resultado da operação
            reg.addAtributo(COD_RETORNO, parametros.get(COD_RETORNO));
        }

        if (parametros.get(MENSAGEM) != null) {
            // Adiciona a mensagem de resultado da operação
            reg.addAtributo(MENSAGEM, parametros.get(MENSAGEM));
        } else {
            // Adiciona mensagem padrão, de sucesso ou de erro
            ZetraException zex = null;
            if (sucesso.equalsIgnoreCase("S")) {
                // Insere mensagem padrão de sucesso caso uma mensagem não seja informada
                zex = new ZetraException("mensagem.sucesso", responsavel);
            } else {
                // Insere mensagem padrão de erro, caso uma mensagem não seja informada
                zex = new ZetraException("mensagem.erroInternoSistema", responsavel);
            }
            if (parametros.get(COD_RETORNO) == null) {
                reg.addAtributo(COD_RETORNO, zex.getResourcesMessage(ZetraException.MENSAGEM_PROCESSAMENTO_XML));
            }
            reg.addAtributo(MENSAGEM, zex.getMessage());
        }

        if (parametros.get(ALERTA) != null) {
            // Adiciona a mensagem de alerta resultante da operação
            reg.addAtributo(ALERTA, parametros.get(ALERTA));
        }

        if (operacao.equalsIgnoreCase(CodedValues.OP_ATUALIZAR_PARCELA)) {
            reg.addAtributo(ADE_NUMERO, parametros.get(ADE_NUMERO));
        }
        if (operacao.equalsIgnoreCase(CodedValues.OP_GERAR_SENHA_AUTORIZACAO)) {
            reg.addAtributo(SENHA_AUTORIZACAO, parametros.get(SENHA_AUTORIZACAO));
        }
        if (operacao.equalsIgnoreCase(CodedValues.OP_RECUPERAR_PERG_DADOS_CAD)) {
            reg.addAtributo(NUMERO_PERGUNTA, parametros.get(NUMERO_PERGUNTA));
            reg.addAtributo(TEXTO_PERGUNTA, parametros.get(TEXTO_PERGUNTA));
        }
        if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_USUARIO) || operacao.equalsIgnoreCase(CodedValues.OP_MODIFICAR_USUARIO)) {
            reg.addAtributo(USUARIO_CADASTRADO, parametros.get(USUARIO_CADASTRADO));
        }

        if (operacao.equalsIgnoreCase(CodedValues.OP_SIMULAR_CONSIGNACAO) || operacao.equalsIgnoreCase(CodedValues.OP_SIMULAR_CONSIGNACAO_V8_0)) {
            boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
            boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);

            reg.addAtributo(PODE_SIMULAR, temPermissaoSimulacao);
            reg.addAtributo(PODE_SOLICITAR, temPermissaoSolicitacao);
        }

        if (operacao.equalsIgnoreCase(CodedValues.OP_CADASTRAR_USUARIO_OPERACIONAL)) {
            reg.addAtributo(ENTIDADE_ID, parametros.get(ENTIDADE_ID));
            reg.addAtributo(ENTIDADE_NOME, parametros.get(ENTIDADE_NOME));
            reg.addAtributo(USU_NOME, parametros.get(USU_NOME));
            reg.addAtributo(USU_LOGIN, parametros.get(USU_LOGIN));
            reg.addAtributo(USU_SENHA, parametros.get(USU_SENHA));
        }

        if (operacao.equalsIgnoreCase(CodedValues.OP_LISTAR_ARQUIVO_INTEGRACAO) || operacao.equalsIgnoreCase(CodedValues.OP_DOWNLOAD_ARQUIVO_INTEGRACAO)) {
            reg.addAtributo(ARQUIVO_INTEGRACAO, parametros.get(ARQUIVO_INTEGRACAO));
        }

        return reg;
    }

    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = new ArrayList<>();

        respostas.add(geraCabecalhoResposta(parametros));

        return respostas;
    }

    public List<RegistroRespostaRequisicaoExterna> geraResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = geraRegistrosResposta(parametros);
        respostas.addAll(geraFooter(parametros));

        return respostas;
    }

    /**
     * gera footer da resposta contendo histórico e/ou boleto
     * @param parametros
     * @return
     * @throws ZetraException
     */
    private List<RegistroRespostaRequisicaoExterna> geraFooter(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object operacao = parametros.get(OPERACAO);

        List<RegistroRespostaRequisicaoExterna> boletoList = new ArrayList<>();
        if (!TextHelper.isNull(operacao)) {
            if (operacao.equals(CodedValues.OP_INSERIR_SOLICITACAO_V6_0) || operacao.equals(CodedValues.OP_INSERIR_SOLICITACAO_V8_0) || operacao.equals(CodedValues.OP_RESERVAR_MARGEM_V6_0) || operacao.equals(CodedValues.OP_RESERVAR_MARGEM_V8_0) || operacao.equals(CodedValues.OP_RENEGOCIAR_CONSIGNACAO_V6_0)) {
                com.zetra.econsig.webservice.command.saida.v6.RespostaBoletoCommand boletCmnd = new com.zetra.econsig.webservice.command.saida.v6.RespostaBoletoCommand(responsavel);
                boletoList = boletCmnd.geraRegistrosResposta(parametros);
            } else {
                RespostaBoletoCommand boletCmnd = new RespostaBoletoCommand(responsavel);
                boletoList = boletCmnd.geraRegistrosResposta(parametros);
            }

            // não exibir históricos de renegociação
            if (!CodedValues.OPERACOES_RENEGOCIAR_CONSIGNACAO.contains(operacao)) {
                RespostaHistoricoCommand histCmnd = new RespostaHistoricoCommand(responsavel);
                List<RegistroRespostaRequisicaoExterna> respHist = histCmnd.geraRegistrosResposta(parametros);
                boletoList.addAll(respHist);
            }
        }

        return boletoList;
    }
}
