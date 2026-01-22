package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TAXA_JUROS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v3.CadastrarTaxaDeJuros;

/**
 * <p>Title: AnEmptyAssembler</p>
 * <p>Description: Assembler para AnEmpty.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarTaxaDeJurosAssembler extends BaseAssembler {

    private CadastrarTaxaDeJurosAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarTaxaDeJuros cadastrarTaxaDeJuros) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, cadastrarTaxaDeJuros.getUsuario());
        parametros.put(SENHA, cadastrarTaxaDeJuros.getSenha());
        parametros.put(CNV_COD_VERBA, getValue(cadastrarTaxaDeJuros.getCodVerba()));
        parametros.put(CLIENTE, getValue(cadastrarTaxaDeJuros.getCliente()));
        parametros.put(CONVENIO, getValue(cadastrarTaxaDeJuros.getConvenio()));
        parametros.put(SERVICO_CODIGO, getValue(cadastrarTaxaDeJuros.getServicoCodigo()));
        parametros.put(ORG_IDENTIFICADOR, cadastrarTaxaDeJuros.getOrgaoCodigo());
        parametros.put(EST_IDENTIFICADOR, cadastrarTaxaDeJuros.getEstabelecimentoCodigo());
        parametros.put(TAXA_JUROS, cadastrarTaxaDeJuros.getTaxaDeJuros());

        return parametros;
    }
}