package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.InserirSolicitacao;

/**
 * <p>Title: InserirSolicitacaoAssembler</p>
 * <p>Description: Assembler para InserirSolicitacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class InserirSolicitacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InserirSolicitacaoAssembler.class);

    private InserirSolicitacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(InserirSolicitacao inserirSolicitacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, inserirSolicitacao.getUsuario());
        parametros.put(SENHA, inserirSolicitacao.getSenha());
        parametros.put(RSE_MATRICULA, inserirSolicitacao.getMatricula());
        parametros.put(SER_CPF, getValue(inserirSolicitacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(inserirSolicitacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(inserirSolicitacao.getEstabelecimentoCodigo()));
        parametros.put(SER_SENHA, getValue(inserirSolicitacao.getSenhaServidor()));
        parametros.put(TOKEN, inserirSolicitacao.getTokenAutServidor());
        parametros.put(SERVICO_CODIGO, getValue(inserirSolicitacao.getServicoCodigo()));
        parametros.put(DATA_NASC, getValueAsDate(inserirSolicitacao.getDataNascimento()));
        final Double adeVlr = inserirSolicitacao.getValorParcela();
        if (adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        final Integer prazo = getValue(inserirSolicitacao.getPrazo());
        if ((prazo != null) && (prazo != Integer.MAX_VALUE) && (prazo > 0)) {
            parametros.put(PRAZO, prazo);
        }
        final Double vlrLib = getValue(inserirSolicitacao.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(CNV_COD_VERBA, getValue(inserirSolicitacao.getCodVerba()));
        parametros.put(SER_END, inserirSolicitacao.getEndereco());
        parametros.put(SER_BAIRRO, inserirSolicitacao.getBairro());
        parametros.put(SER_CIDADE, inserirSolicitacao.getCidade());
        parametros.put(SER_UF, inserirSolicitacao.getUf());
        parametros.put(SER_CEP, inserirSolicitacao.getCep());
        parametros.put(SER_TEL, inserirSolicitacao.getTelefone());
        parametros.put(CONVENIO, getValue(inserirSolicitacao.getConvenio()));
        parametros.put(CLIENTE, getValue(inserirSolicitacao.getCliente()));
        parametros.put(SER_LOGIN, inserirSolicitacao.getLoginServidor());

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.InserirSolicitacao inserirSolicitacao) {
       final com.zetra.econsig.webservice.soap.operacional.v1.InserirSolicitacao inserirSolicitacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.InserirSolicitacao();
        try {
            BeanUtils.copyProperties(inserirSolicitacaoV1, inserirSolicitacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return toMap(inserirSolicitacaoV1);
    }
}