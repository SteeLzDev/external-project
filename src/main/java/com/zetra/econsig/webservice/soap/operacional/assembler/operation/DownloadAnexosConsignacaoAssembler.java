package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.APENAS_ANEXO_COMPRAS;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.NOME_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TODOS_ANEXOS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.DownloadAnexosConsignacao;

/**
 * <p>Title: DownloadAnexoConsignacaoAssembler</p>
 * <p>Description: Assembler para DownloadAnexoConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class DownloadAnexosConsignacaoAssembler extends BaseAssembler {

    private DownloadAnexosConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(DownloadAnexosConsignacao downloadAnexosConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(downloadAnexosConsignacao.getCliente()));
        parametros.put(CONVENIO, getValue(downloadAnexosConsignacao.getConvenio()));
        parametros.put(USUARIO, downloadAnexosConsignacao.getUsuario());
        parametros.put(SENHA, downloadAnexosConsignacao.getSenha());

        final Long adeNumero = getValue(downloadAnexosConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }

        parametros.put(ADE_IDENTIFICADOR, getValue(downloadAnexosConsignacao.getAdeIdentificador()));
        parametros.put(NOME_ARQUIVO, getValue(downloadAnexosConsignacao.getNomeArquivo()));
        parametros.put(APENAS_ANEXO_COMPRAS, getValue(downloadAnexosConsignacao.getCompras()));
        parametros.put(TODOS_ANEXOS, getValue(downloadAnexosConsignacao.getTodos()));

        return parametros;
    }

}
