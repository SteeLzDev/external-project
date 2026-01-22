package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MUNICIPIO_LOTACAO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.TERMO_ACEITE;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.InserirSolicitacaoServidor;

/**
 * <p>Title: InserirSolicitacaoServidorAssembler</p>
 * <p>Description: Assembler para InserirSolicitacaoServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class InserirSolicitacaoServidorAssembler extends BaseAssembler {

    private InserirSolicitacaoServidorAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(InserirSolicitacaoServidor inserirSolicitacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, inserirSolicitacaoServidor.getSenha());
        parametros.put(RSE_MATRICULA, inserirSolicitacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(inserirSolicitacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(inserirSolicitacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(SERVICO_CODIGO, inserirSolicitacaoServidor.getServicoCodigo());
        final double adeVlr = inserirSolicitacaoServidor.getValorParcela();
        if (adeVlr == Double.NaN) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((inserirSolicitacaoServidor.getPrazo() == Integer.MIN_VALUE) || (inserirSolicitacaoServidor.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, inserirSolicitacaoServidor.getPrazo());
        }
        final double vlrLib = inserirSolicitacaoServidor.getValorLiberado();
        if (vlrLib == Double.NaN) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(SER_END, getValue(inserirSolicitacaoServidor.getEndereco()));
        parametros.put(SER_BAIRRO, getValue(inserirSolicitacaoServidor.getBairro()));
        parametros.put(SER_CIDADE, getValue(inserirSolicitacaoServidor.getCidade()));
        parametros.put(SER_UF, getValue(inserirSolicitacaoServidor.getUf()));
        parametros.put(SER_CEP, getValue(inserirSolicitacaoServidor.getCep()));
        parametros.put(SER_TEL, getValue(inserirSolicitacaoServidor.getTelefone()));
        parametros.put(SER_LOGIN, getValue(inserirSolicitacaoServidor.getLoginServidor()));
        parametros.put(SER_NRO, getValue(inserirSolicitacaoServidor.getNumero()));
        parametros.put(SER_COMPL, getValue(inserirSolicitacaoServidor.getComplemento()));
        parametros.put(RSE_MUNICIPIO_LOTACAO, getValue(inserirSolicitacaoServidor.getMunicipioLotacao()));
        parametros.put(SER_EMAIL, getValue(inserirSolicitacaoServidor.getEmail()));
        parametros.put(SER_CELULAR, getValue(inserirSolicitacaoServidor.getCelular()));
        parametros.put(CSA_IDENTIFICADOR, inserirSolicitacaoServidor.getConsignatariaCodigo());

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.servidor.v2.InserirSolicitacaoServidor inserirSolicitacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, inserirSolicitacaoServidor.getSenha());
        parametros.put(RSE_MATRICULA, inserirSolicitacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(inserirSolicitacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(inserirSolicitacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(SERVICO_CODIGO, inserirSolicitacaoServidor.getServicoCodigo());
        final double adeVlr = inserirSolicitacaoServidor.getValorParcela();
        if (adeVlr == Double.NaN) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((inserirSolicitacaoServidor.getPrazo() == Integer.MIN_VALUE) || (inserirSolicitacaoServidor.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, inserirSolicitacaoServidor.getPrazo());
        }
        final double vlrLib = inserirSolicitacaoServidor.getValorLiberado();
        if (vlrLib == Double.NaN) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(SER_END, getValue(inserirSolicitacaoServidor.getEndereco()));
        parametros.put(SER_BAIRRO, getValue(inserirSolicitacaoServidor.getBairro()));
        parametros.put(SER_CIDADE, getValue(inserirSolicitacaoServidor.getCidade()));
        parametros.put(SER_UF, getValue(inserirSolicitacaoServidor.getUf()));
        parametros.put(SER_CEP, getValue(inserirSolicitacaoServidor.getCep()));
        parametros.put(SER_TEL, getValue(inserirSolicitacaoServidor.getTelefone()));
        parametros.put(SER_LOGIN, getValue(inserirSolicitacaoServidor.getLoginServidor()));
        parametros.put(SER_NRO, getValue(inserirSolicitacaoServidor.getNumero()));
        parametros.put(SER_COMPL, getValue(inserirSolicitacaoServidor.getComplemento()));
        parametros.put(RSE_MUNICIPIO_LOTACAO, getValue(inserirSolicitacaoServidor.getMunicipioLotacao()));
        parametros.put(SER_EMAIL, getValue(inserirSolicitacaoServidor.getEmail()));
        parametros.put(SER_CELULAR, getValue(inserirSolicitacaoServidor.getCelular()));
        parametros.put(CSA_IDENTIFICADOR, inserirSolicitacaoServidor.getConsignatariaCodigo());
        parametros.put(TERMO_ACEITE, getValue(inserirSolicitacaoServidor.getTermoAceite()));

        return parametros;
    }
}
