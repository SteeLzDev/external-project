package com.zetra.econsig.webservice.command.saida.v8;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.OCP_OBS;
import static com.zetra.econsig.webservice.CamposAPI.PAGINACAO;
import static com.zetra.econsig.webservice.CamposAPI.PAGINA_ATUAL;
import static com.zetra.econsig.webservice.CamposAPI.PARCELAS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.PARCELA_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_DATA_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_VLR_PREVISTO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_VLR_REALIZADO;
import static com.zetra.econsig.webservice.CamposAPI.QTD_PAGINAS;
import static com.zetra.econsig.webservice.CamposAPI.QTD_REGISTROS;
import static com.zetra.econsig.webservice.CamposAPI.REGISTRO_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.REGISTRO_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SPD_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaListarParcelasCommand</p>
 * <p>Description: Classe responsavel por gerar a saida da requisicao SOAP de listar parcelas
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class RespostaListarParcelasCommand extends RespostaRequisicaoExternaCommand {

    public RespostaListarParcelasCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        final List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        final List<TransferObject> parcelasConsignacao = (List<TransferObject>) parametros.get(PARCELAS_CONSIGNACAO);

        if (!TextHelper.isNull(parcelasConsignacao)) {
            if (parcelasConsignacao.size() > 100) {
                final RegistroRespostaRequisicaoExterna regPaginacao = new RegistroRespostaRequisicaoExterna();
                regPaginacao.setNome(PAGINACAO);

                final int qtdRegistros = parcelasConsignacao.size();
                final int qtdPaginas = (qtdRegistros % 100) > 0 ? (qtdRegistros / 100) + 1 : qtdRegistros / 100;
                final int paginacao = (int) parametros.get(PAGINACAO) > qtdPaginas ? qtdPaginas : (int) parametros.get(PAGINACAO);
                final int registroInicial = paginacao == 1 ? 1 : (paginacao * 100) - 99;
                final int registroFinal = (registroInicial + 99) > qtdRegistros ? qtdRegistros : registroInicial + 99;

                regPaginacao.addAtributo(QTD_REGISTROS, qtdRegistros);
                regPaginacao.addAtributo(QTD_PAGINAS, qtdPaginas);
                regPaginacao.addAtributo(PAGINA_ATUAL, paginacao);
                regPaginacao.addAtributo(REGISTRO_INICIAL, registroInicial);
                regPaginacao.addAtributo(REGISTRO_FINAL, registroFinal);
                respostas.add(regPaginacao);

                for (int i = registroInicial - 1; i <= (registroFinal - 1); i++) {
                    final RegistroRespostaRequisicaoExterna regParcela = new RegistroRespostaRequisicaoExterna();
                    regParcela.setNome(PARCELA_CONSIGNACAO);

                    regParcela.addAtributo(ADE_NUMERO, parcelasConsignacao.get(i).getAttribute(Columns.ADE_NUMERO));
                    regParcela.addAtributo(ADE_IDENTIFICADOR, parcelasConsignacao.get(i).getAttribute(Columns.ADE_IDENTIFICADOR));
                    regParcela.addAtributo(SVC_DESCRICAO, parcelasConsignacao.get(i).getAttribute(Columns.SVC_DESCRICAO));
                    regParcela.addAtributo(SVC_IDENTIFICADOR, parcelasConsignacao.get(i).getAttribute(Columns.SVC_IDENTIFICADOR));
                    regParcela.addAtributo(CNV_COD_VERBA, parcelasConsignacao.get(i).getAttribute(Columns.CNV_COD_VERBA));
                    regParcela.addAtributo(SER_NOME, parcelasConsignacao.get(i).getAttribute(Columns.SER_NOME));
                    regParcela.addAtributo(SER_CPF, parcelasConsignacao.get(i).getAttribute(Columns.SER_CPF));
                    regParcela.addAtributo(RSE_MATRICULA, parcelasConsignacao.get(i).getAttribute(Columns.RSE_MATRICULA));
                    regParcela.addAtributo(PRD_NUMERO, parcelasConsignacao.get(i).getAttribute(Columns.PRD_NUMERO));
                    regParcela.addAtributo(PRD_DATA_DESCONTO, parcelasConsignacao.get(i).getAttribute(Columns.PRD_DATA_DESCONTO));
                    regParcela.addAtributo(PRD_DATA_REALIZADO, parcelasConsignacao.get(i).getAttribute(Columns.PRD_DATA_REALIZADO));
                    regParcela.addAtributo(PRD_VLR_PREVISTO, parcelasConsignacao.get(i).getAttribute(Columns.PRD_VLR_PREVISTO));
                    regParcela.addAtributo(PRD_VLR_REALIZADO, parcelasConsignacao.get(i).getAttribute(Columns.PRD_VLR_REALIZADO));
                    regParcela.addAtributo(SPD_DESCRICAO, parcelasConsignacao.get(i).getAttribute(Columns.SPD_DESCRICAO));
                    regParcela.addAtributo(OCP_OBS, parcelasConsignacao.get(i).getAttribute(Columns.OCP_OBS));

                    respostas.add(regParcela);

                }
            } else {
                for (final TransferObject parcelaConsignacao : parcelasConsignacao) {
                    final RegistroRespostaRequisicaoExterna regParcela = new RegistroRespostaRequisicaoExterna();
                    regParcela.setNome(PARCELA_CONSIGNACAO);

                    regParcela.addAtributo(ADE_NUMERO, parcelaConsignacao.getAttribute(Columns.ADE_NUMERO));
                    regParcela.addAtributo(ADE_IDENTIFICADOR, parcelaConsignacao.getAttribute(Columns.ADE_IDENTIFICADOR));
                    regParcela.addAtributo(SVC_DESCRICAO, parcelaConsignacao.getAttribute(Columns.SVC_DESCRICAO));
                    regParcela.addAtributo(SVC_IDENTIFICADOR, parcelaConsignacao.getAttribute(Columns.SVC_IDENTIFICADOR));
                    regParcela.addAtributo(CNV_COD_VERBA, parcelaConsignacao.getAttribute(Columns.CNV_COD_VERBA));
                    regParcela.addAtributo(SER_NOME, parcelaConsignacao.getAttribute(Columns.SER_NOME));
                    regParcela.addAtributo(SER_CPF, parcelaConsignacao.getAttribute(Columns.SER_CPF));
                    regParcela.addAtributo(RSE_MATRICULA, parcelaConsignacao.getAttribute(Columns.RSE_MATRICULA));
                    regParcela.addAtributo(PRD_NUMERO, parcelaConsignacao.getAttribute(Columns.PRD_NUMERO));
                    regParcela.addAtributo(PRD_DATA_DESCONTO, parcelaConsignacao.getAttribute(Columns.PRD_DATA_DESCONTO));
                    regParcela.addAtributo(PRD_DATA_REALIZADO, parcelaConsignacao.getAttribute(Columns.PRD_DATA_REALIZADO));
                    regParcela.addAtributo(PRD_VLR_PREVISTO, parcelaConsignacao.getAttribute(Columns.PRD_VLR_PREVISTO));
                    regParcela.addAtributo(PRD_VLR_REALIZADO, parcelaConsignacao.getAttribute(Columns.PRD_VLR_REALIZADO));
                    regParcela.addAtributo(SPD_DESCRICAO, parcelaConsignacao.getAttribute(Columns.SPD_DESCRICAO));
                    regParcela.addAtributo(OCP_OBS, parcelaConsignacao.getAttribute(Columns.OCP_OBS));

                    respostas.add(regParcela);

                }
            }
        }
        return respostas;
    }

}
