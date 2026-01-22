package com.zetra.econsig.webservice.command.saida.v6;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO;
import static com.zetra.econsig.webservice.CamposAPI.BOLETO_V6_0;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_DATA_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMISSOR_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_QTD_FILHOS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF_IDT;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;

public class RespostaBoletoCommand extends com.zetra.econsig.webservice.command.saida.RespostaBoletoCommand {

    public RespostaBoletoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);
        for (RegistroRespostaRequisicaoExterna resposta: respostas) {
            if (resposta.getNome().equals(BOLETO)) {
                adicionaCamposV6(resposta, parametros);
            }
        }

        return respostas;
    }

    public void adicionaCamposV6(RegistroRespostaRequisicaoExterna resposta, Map<CamposAPI, Object> parametros) throws ServidorControllerException {
        // Exibe os atributos do boleto
        CustomTransferObject boleto = (CustomTransferObject) parametros.get(BOLETO);

        resposta.setNome(BOLETO_V6_0);
        if (boleto.getAttribute(Columns.SER_DATA_IDT) != null) {
            resposta.addAtributo(SER_DATA_IDT, DateHelper.format((Date) boleto.getAttribute(Columns.SER_DATA_IDT), LocaleHelper.getDatePattern()) );
        }
        resposta.addAtributo(SER_UF_IDT, boleto.getAttribute(Columns.SER_UF_IDT));
        resposta.addAtributo(SER_EMISSOR_IDT, boleto.getAttribute(Columns.SER_EMISSOR_IDT));
        resposta.addAtributo(SER_CID_NASC, boleto.getAttribute(Columns.SER_CID_NASC));
        resposta.addAtributo(SER_NACIONALIDADE, boleto.getAttribute(Columns.SER_NACIONALIDADE));
        resposta.addAtributo(SER_SEXO, boleto.getAttribute(Columns.SER_SEXO));
        resposta.addAtributo(SER_CELULAR, boleto.getAttribute(Columns.SER_CELULAR));

        String serCodigo = (String) parametros.get(SER_CODIGO);
        String rseCodigo = (String) parametros.get(RSE_CODIGO);

        ServidorDelegate serDelegate = new ServidorDelegate();
        ServidorTransferObject servidor = new ServidorTransferObject(serCodigo);
        servidor = serDelegate.findServidor(servidor, responsavel);

        RegistroServidorTO rseResultTo = new RegistroServidorTO(rseCodigo);
        rseResultTo = serDelegate.findRegistroServidor(rseResultTo, responsavel);

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
    }
}
