package com.zetra.econsig.webservice.command.saida.v8;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_DEVIDO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_LIQUIDO;
import static com.zetra.econsig.webservice.CamposAPI.ANEXOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO_OUTRO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONTRACHEQUE;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME_ABREV;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.DATA_RESERVA;
import static com.zetra.econsig.webservice.CamposAPI.MANDADO_PAG;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.ORS_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.PAGINACAO;
import static com.zetra.econsig.webservice.CamposAPI.PAGINA_ATUAL;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.QTD_PAGINAS;
import static com.zetra.econsig.webservice.CamposAPI.QTD_REGISTROS;
import static com.zetra.econsig.webservice.CamposAPI.QUANTIDADE_VALIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.REGISTRO_FINAL;
import static com.zetra.econsig.webservice.CamposAPI.REGISTRO_INICIAL;
import static com.zetra.econsig.webservice.CamposAPI.RESPONSAVEL;
import static com.zetra.econsig.webservice.CamposAPI.RG_FRENTE;
import static com.zetra.econsig.webservice.CamposAPI.RG_VERSO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.VALIDAR_DOCUMENTACAO;
import static com.zetra.econsig.webservice.CamposAPI.VALIDA_DOCUMENTACAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
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

public class RespostaConsultarValidacaoDocumentacaoCommand extends RespostaRequisicaoExternaCommand {

    public RespostaConsultarValidacaoDocumentacaoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        final List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        final List<TransferObject> lstSituacaoContratos = (List<TransferObject>) parametros.get(VALIDAR_DOCUMENTACAO);

        if (!TextHelper.isNull(lstSituacaoContratos)) {
            final String tipoArquivoRgFrente = TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo();
            final String tipoArquivoAutorizaco = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo();
            final String tipoArquivoContraCheque = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo();
            final String tipoArquivoOutro = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo();

            final HashMap<String, String> hashAnexos = parametros.get(ANEXOS_CONSIGNACAO) != null ? (HashMap<String, String>) parametros.get(ANEXOS_CONSIGNACAO) : new HashMap<>();

            if (lstSituacaoContratos.size() > 100) {
                final RegistroRespostaRequisicaoExterna regPaginacao = new RegistroRespostaRequisicaoExterna();
                regPaginacao.setNome(PAGINACAO);

                final int qtdRegistros = lstSituacaoContratos.size();
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
                    regParcela.setNome(VALIDA_DOCUMENTACAO);

                    final String adeCodigo = (String) lstSituacaoContratos.get(i).getAttribute(Columns.ADE_CODIGO);

                    regParcela.addAtributo(CSA_NOME_ABREV, lstSituacaoContratos.get(i).getAttribute(Columns.CSA_NOME_ABREV));
                    regParcela.addAtributo(CSA_IDENTIFICADOR, lstSituacaoContratos.get(i).getAttribute(Columns.CSA_IDENTIFICADOR));
                    regParcela.addAtributo(RESPONSAVEL, lstSituacaoContratos.get(i).getAttribute(Columns.USU_LOGIN));
                    regParcela.addAtributo(ORG_NOME, lstSituacaoContratos.get(i).getAttribute(Columns.ORG_NOME));
                    regParcela.addAtributo(ADE_NUMERO, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_NUMERO));
                    regParcela.addAtributo(ADE_IDENTIFICADOR, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_IDENTIFICADOR));
                    regParcela.addAtributo(DATA_RESERVA, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_DATA));
                    regParcela.addAtributo(DATA_INICIAL, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_ANO_MES_INI));
                    regParcela.addAtributo(DATA_FINAL, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_ANO_MES_FIM));
                    regParcela.addAtributo(ADE_VLR, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_VLR));
                    regParcela.addAtributo(ADE_VLR_LIQUIDO, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_VLR_LIQUIDO));
                    regParcela.addAtributo(ADE_VLR_DEVIDO, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_VLR_DEVIDO));
                    regParcela.addAtributo(SVC_DESCRICAO, lstSituacaoContratos.get(i).getAttribute(Columns.SVC_DESCRICAO));
                    regParcela.addAtributo(CNV_COD_VERBA, lstSituacaoContratos.get(i).getAttribute(Columns.CNV_COD_VERBA));
                    regParcela.addAtributo(PRAZO, lstSituacaoContratos.get(i).getAttribute(Columns.ADE_PRAZO));
                    regParcela.addAtributo(SER_NOME, lstSituacaoContratos.get(i).getAttribute(Columns.SER_NOME));
                    regParcela.addAtributo(SER_CPF, lstSituacaoContratos.get(i).getAttribute(Columns.SER_CPF));
                    regParcela.addAtributo(QUANTIDADE_VALIDACAO, lstSituacaoContratos.get(i).getAttribute("NUMERO_VALIDACOES"));
                    regParcela.addAtributo(ORS_DESCRICAO, lstSituacaoContratos.get(i).getAttribute(Columns.OSO_DESCRICAO));

                    final String rgFrenteVerso = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoRgFrente) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoRgFrente) : "";
                    final String autPgt = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoAutorizaco) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoAutorizaco) : "";
                    final String contraCheque = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoContraCheque) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoContraCheque) : "";
                    final String outro = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoOutro) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoOutro) : "";

                    regParcela.addAtributo(RG_FRENTE, rgFrenteVerso);
                    regParcela.addAtributo(RG_VERSO, "");
                    regParcela.addAtributo(MANDADO_PAG, autPgt);
                    regParcela.addAtributo(CONTRACHEQUE, contraCheque);
                    regParcela.addAtributo(ARQUIVO_OUTRO, outro);

                    regParcela.addAtributo(OBSERVACAO, lstSituacaoContratos.get(i).getAttribute(Columns.SOA_OBS));
                    respostas.add(regParcela);

                }
            } else {
                for (final TransferObject parcelaConsignacao : lstSituacaoContratos) {
                    final RegistroRespostaRequisicaoExterna regParcela = new RegistroRespostaRequisicaoExterna();
                    regParcela.setNome(VALIDA_DOCUMENTACAO);

                    final String adeCodigo = (String) parcelaConsignacao.getAttribute(Columns.ADE_CODIGO);

                    regParcela.addAtributo(CSA_NOME_ABREV, parcelaConsignacao.getAttribute(Columns.CSA_NOME_ABREV));
                    regParcela.addAtributo(CSA_IDENTIFICADOR, parcelaConsignacao.getAttribute(Columns.CSA_IDENTIFICADOR));
                    regParcela.addAtributo(RESPONSAVEL, parcelaConsignacao.getAttribute(Columns.USU_LOGIN));
                    regParcela.addAtributo(ADE_NUMERO, parcelaConsignacao.getAttribute(Columns.ADE_NUMERO));
                    regParcela.addAtributo(ADE_IDENTIFICADOR, parcelaConsignacao.getAttribute(Columns.ADE_IDENTIFICADOR));
                    regParcela.addAtributo(DATA_RESERVA, parcelaConsignacao.getAttribute(Columns.ADE_DATA));
                    regParcela.addAtributo(DATA_INICIAL, parcelaConsignacao.getAttribute(Columns.ADE_ANO_MES_INI));
                    regParcela.addAtributo(DATA_FINAL, parcelaConsignacao.getAttribute(Columns.ADE_ANO_MES_FIM));
                    regParcela.addAtributo(ADE_VLR, parcelaConsignacao.getAttribute(Columns.ADE_VLR));
                    regParcela.addAtributo(ADE_VLR_LIQUIDO, parcelaConsignacao.getAttribute(Columns.ADE_VLR_LIQUIDO));
                    regParcela.addAtributo(ADE_VLR_DEVIDO, parcelaConsignacao.getAttribute(Columns.ADE_VLR_DEVIDO));
                    regParcela.addAtributo(SVC_DESCRICAO, parcelaConsignacao.getAttribute(Columns.SVC_DESCRICAO));
                    regParcela.addAtributo(CNV_COD_VERBA, parcelaConsignacao.getAttribute(Columns.CNV_COD_VERBA));
                    regParcela.addAtributo(PRAZO, parcelaConsignacao.getAttribute(Columns.ADE_PRAZO));
                    regParcela.addAtributo(SER_NOME, parcelaConsignacao.getAttribute(Columns.SER_NOME));
                    regParcela.addAtributo(SER_CPF, parcelaConsignacao.getAttribute(Columns.SER_CPF));
                    regParcela.addAtributo(QUANTIDADE_VALIDACAO, parcelaConsignacao.getAttribute("NUMERO_VALIDACOES"));
                    regParcela.addAtributo(ORS_DESCRICAO, parcelaConsignacao.getAttribute(Columns.OSO_DESCRICAO));

                    final String rgFrenteVerso = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoRgFrente) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoRgFrente) : "";
                    final String autPgt = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoAutorizaco) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoAutorizaco) : "";
                    final String contraCheque = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoContraCheque) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoContraCheque) : "";
                    final String outro = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoOutro) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoOutro) : "";

                    regParcela.addAtributo(RG_FRENTE, rgFrenteVerso);
                    regParcela.addAtributo(RG_VERSO, "");
                    regParcela.addAtributo(MANDADO_PAG, autPgt);
                    regParcela.addAtributo(CONTRACHEQUE, contraCheque);
                    regParcela.addAtributo(ARQUIVO_OUTRO, outro);

                    regParcela.addAtributo(OBSERVACAO, parcelaConsignacao.getAttribute(Columns.SOA_OBS));
                    respostas.add(regParcela);
                }
            }
        }
        return respostas;
    }

}
