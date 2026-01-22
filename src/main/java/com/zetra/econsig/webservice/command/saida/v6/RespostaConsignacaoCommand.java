package com.zetra.econsig.webservice.command.saida.v6;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_QTD_FILHOS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;

public class RespostaConsignacaoCommand extends com.zetra.econsig.webservice.command.saida.RespostaConsignacaoCommand {

    public RespostaConsignacaoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        for (RegistroRespostaRequisicaoExterna resposta: respostas) {
            if (!TextHelper.isNull(resposta.getNome()) && resposta.getNome().equals(CONSIGNACAO)) {
                resposta.setNome(CONSIGNACAO_V6_0);
                String serCodigo = (String) parametros.get(SER_CODIGO);

                // Busca o servidor
                ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
                ServidorDelegate serDelegate = new ServidorDelegate();
                servidor = serDelegate.findServidor(servidor, responsavel);

                if (servidor.getAttribute(Columns.SER_DATA_IDT) != null) {
                    resposta.addAtributo(SER_DATA_IDT, DateHelper.format((Date) servidor.getAttribute(Columns.SER_DATA_IDT), LocaleHelper.getDatePattern()) );
                }
                resposta.addAtributo(SER_UF_IDT, servidor.getAttribute(Columns.SER_UF_IDT));
                resposta.addAtributo(SER_EMISSOR_IDT, servidor.getAttribute(Columns.SER_EMISSOR_IDT));
                resposta.addAtributo(SER_CID_NASC, servidor.getAttribute(Columns.SER_CID_NASC));
                resposta.addAtributo(SER_NACIONALIDADE, servidor.getAttribute(Columns.SER_NACIONALIDADE));
                resposta.addAtributo(SER_SEXO, servidor.getAttribute(Columns.SER_SEXO));
                // Pega a descrição do codigo de estado civil
                String serEstCivil = serDelegate.getEstCivil((String) servidor.getAttribute(Columns.SER_EST_CIVIL), responsavel);
                resposta.addAtributo(SER_EST_CIVIL, serEstCivil);
                resposta.addAtributo(SER_CELULAR, servidor.getAttribute(Columns.SER_CELULAR));

                resposta.addAtributo(SER_BAIRRO, servidor.getAttribute(Columns.SER_BAIRRO));
                resposta.addAtributo(SER_NRO_IDT, servidor.getAttribute(Columns.SER_NRO_IDT));
                resposta.addAtributo(SER_END, servidor.getAttribute(Columns.SER_END));
                resposta.addAtributo(SER_NRO, servidor.getAttribute(Columns.SER_NRO));
                resposta.addAtributo(SER_CEP, servidor.getAttribute(Columns.SER_CEP));
                resposta.addAtributo(SER_CIDADE, servidor.getAttribute(Columns.SER_CIDADE));
                resposta.addAtributo(SER_UF, servidor.getAttribute(Columns.SER_UF));
                resposta.addAtributo(SER_TEL, servidor.getAttribute(Columns.SER_TEL));
                resposta.addAtributo(SER_COMPL, servidor.getAttribute(Columns.SER_COMPL));

                String rseCodigo = (String) parametros.get(RSE_CODIGO);
                RegistroServidorTO rseTo = new RegistroServidorTO(rseCodigo);
                RegistroServidorTO rseResultTo = serDelegate.findRegistroServidor(rseTo, responsavel);
                resposta.addAtributo(RSE_SALARIO, rseResultTo.getRseSalario());
                if (rseResultTo.getRseDataSaida() != null) {
                    resposta.addAtributo(RSE_DATA_SAIDA, DateHelper.format(rseResultTo.getRseDataSaida(), LocaleHelper.getDatePattern()) );
                }
                resposta.addAtributo(RSE_BANCO, rseResultTo.getRseBancoSal());
                resposta.addAtributo(RSE_AGENCIA, rseResultTo.getRseAgenciaSal());
                resposta.addAtributo(RSE_CONTA, rseResultTo.getRseContaSal());
                if (!TextHelper.isNull(rseResultTo.getCrsCodigo())) {
                    resposta.addAtributo(CARGO_CODIGO, rseResultTo.getCrsCodigo());
                    TransferObject crs = serDelegate.findCargoByCrsCodigo(rseResultTo.getCrsCodigo(), responsavel);
                    resposta.addAtributo(CARGO_DESCRICAO, crs.getAttribute(Columns.CRS_DESCRICAO));
                }
                if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_THA_CODIGO))) {
                    resposta.addAtributo(HABITACAO_CODIGO, servidor.getAttribute(Columns.SER_THA_CODIGO));
                    resposta.addAtributo(HABITACAO_DESCRICAO, serDelegate.getTipoHabitacao((String) servidor.getAttribute(Columns.SER_THA_CODIGO), responsavel));
                }
                if (!TextHelper.isNull(servidor.getAttribute(Columns.SER_NES_CODIGO))) {
                    resposta.addAtributo(ESCOLARIDADE_CODIGO, servidor.getAttribute(Columns.SER_NES_CODIGO));
                    resposta.addAtributo(ESCOLARIDADE_DESCRICAO, serDelegate.getNivelEscolaridade((String) servidor.getAttribute(Columns.SER_NES_CODIGO), responsavel));
                }

                resposta.addAtributo(SER_QTD_FILHOS, servidor.getAttribute(Columns.SER_QTD_FILHOS));
            } else if (!TextHelper.isNull(resposta.getNome()) && resposta.getNome().equals(BOLETO)) {
                com.zetra.econsig.webservice.command.saida.v6.RespostaBoletoCommand boletCmnd = new com.zetra.econsig.webservice.command.saida.v6.RespostaBoletoCommand(responsavel);
                boletCmnd.adicionaCamposV6(resposta, parametros);
            }
        }

        return respostas;
    }
}
