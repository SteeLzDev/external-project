package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PAGINACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SITUACAO_PARCELA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v8.ListarParcelas;
import com.zetra.econsig.webservice.soap.operacional.v8.SituacaoParcela;

/**
 * <p>Title: ListarParcelasAssembler</p>
 * <p>Description: Assembler para ListarParcelasAssembler</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Eduardo Fortes
 */

public class ListarParcelasAssembler extends BaseAssembler {

    private ListarParcelasAssembler() {

    }

    public static Map<CamposAPI, Object> toMap(ListarParcelas listarParcelas) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        final SituacaoParcela situacaoParcela = getValue(listarParcelas.getSituacaoParcela());
        final List<String> spdCodigos = new ArrayList<>();
        parametros.put(USUARIO, listarParcelas.getUsuario());
        parametros.put(SENHA, listarParcelas.getSenha());

        parametros.put(DATA_DESCONTO, listarParcelas.getDataDesconto());
        if (!TextHelper.isNull(situacaoParcela)) {
            if (!TextHelper.isNull(situacaoParcela.getLiquidadaFolha()) && Boolean.TRUE.equals(situacaoParcela.getLiquidadaFolha())) {
                spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
            }
            if (!TextHelper.isNull(situacaoParcela.getLiquidadaManual()) && Boolean.TRUE.equals(situacaoParcela.getLiquidadaManual())) {
                spdCodigos.add(CodedValues.SPD_LIQUIDADAMANUAL);
            }
            if (!TextHelper.isNull(situacaoParcela.getRejeitada()) && Boolean.TRUE.equals(situacaoParcela.getRejeitada())) {
                spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
            }
        }
        parametros.put(SITUACAO_PARCELA, spdCodigos);
        final int pagina = !TextHelper.isNull(getValue(listarParcelas.getPagina())) ? getValue(listarParcelas.getPagina()) : 1;
        parametros.put(PAGINACAO, pagina);

        final Long adeNumero = getValue(listarParcelas.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }

        parametros.put(ADE_IDENTIFICADOR, getValue(listarParcelas.getAdeIdentificador()));
        parametros.put(EST_IDENTIFICADOR, getValue(listarParcelas.getEstabelecimentoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, getValue(listarParcelas.getOrgaoCodigo()));
        parametros.put(SVC_IDENTIFICADOR, getValue(listarParcelas.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(listarParcelas.getCodigoVerba()));
        parametros.put(SER_CPF, getValue(listarParcelas.getCpf()));
        parametros.put(RSE_MATRICULA, getValue(listarParcelas.getMatricula()));

        return parametros;
    }
}
