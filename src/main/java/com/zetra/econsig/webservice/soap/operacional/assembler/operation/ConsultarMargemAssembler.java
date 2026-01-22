package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.MATRICULA_MULTIPLA;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;


/**
 * <p>Title: ConsultarMargemAssembler</p>
 * <p>Description: Assembler para ConsultarMargem.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarMargemAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarMargemAssembler.class);

    private ConsultarMargemAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.ConsultarMargem consultarMargem) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, consultarMargem.getMatricula());
        parametros.put(SER_CPF, getValue(consultarMargem.getCpf()));
        final Double adeVlr = consultarMargem.getValorParcela();
        if (adeVlr.equals(Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        parametros.put(SER_SENHA, getValue(consultarMargem.getSenhaServidor()));
        parametros.put(TOKEN, consultarMargem.getTokenAutServidor());
        parametros.put(USUARIO, consultarMargem.getUsuario());
        parametros.put(SENHA, consultarMargem.getSenha());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarMargem.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarMargem.getEstabelecimentoCodigo()));
        parametros.put(CONVENIO, getValue(consultarMargem.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarMargem.getCliente()));
        parametros.put(SER_LOGIN, consultarMargem.getLoginServidor());
        parametros.put(SERVICO_CODIGO, getValue(consultarMargem.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(consultarMargem.getCodVerba()));
        parametros.put(MATRICULA_MULTIPLA, getValue(consultarMargem.getMatriculaMultipla()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v3.ConsultarMargem consultarMargem) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, consultarMargem.getMatricula());
        parametros.put(SER_CPF, getValue(consultarMargem.getCpf()));
        final Double adeVlr = consultarMargem.getValorParcela();
        if (adeVlr.equals(Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        parametros.put(SER_SENHA, getValue(consultarMargem.getSenhaServidor()));
        parametros.put(TOKEN, consultarMargem.getTokenAutServidor());
        parametros.put(USUARIO, consultarMargem.getUsuario());
        parametros.put(SENHA, consultarMargem.getSenha());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarMargem.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarMargem.getEstabelecimentoCodigo()));
        parametros.put(CONVENIO, getValue(consultarMargem.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarMargem.getCliente()));
        parametros.put(SER_LOGIN, consultarMargem.getLoginServidor());
        parametros.put(SERVICO_CODIGO, getValue(consultarMargem.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(consultarMargem.getCodVerba()));
        parametros.put(MATRICULA_MULTIPLA, getValue(consultarMargem.getMatriculaMultipla()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.ConsultarMargem consultarMargem) {
        final com.zetra.econsig.webservice.soap.operacional.v3.ConsultarMargem consultarMargemV3 = new com.zetra.econsig.webservice.soap.operacional.v3.ConsultarMargem();
        try {
            BeanUtils.copyProperties(consultarMargemV3, consultarMargem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
            throw new  java.lang.UnsupportedOperationException(ApplicationResourcesHelper.getMessage("mensagem.erro.soap.executar.consultar.margem.arg0", null, e.getMessage()));
        }
        return toMap(consultarMargemV3);
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v7.ConsultarMargem consultarMargem) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, consultarMargem.getMatricula());
        parametros.put(SER_CPF, getValue(consultarMargem.getCpf()));
        final Double adeVlr = consultarMargem.getValorParcela();
        if (adeVlr.equals(Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        parametros.put(SER_SENHA, getValue(consultarMargem.getSenhaServidor()));
        parametros.put(TOKEN, consultarMargem.getTokenAutServidor());
        parametros.put(USUARIO, consultarMargem.getUsuario());
        parametros.put(SENHA, consultarMargem.getSenha());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarMargem.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarMargem.getEstabelecimentoCodigo()));
        parametros.put(CONVENIO, getValue(consultarMargem.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarMargem.getCliente()));
        parametros.put(SER_LOGIN, consultarMargem.getLoginServidor());
        parametros.put(SERVICO_CODIGO, getValue(consultarMargem.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(consultarMargem.getCodVerba()));
        parametros.put(MATRICULA_MULTIPLA, getValue(consultarMargem.getMatriculaMultipla()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v8.ConsultarMargem consultarMargem) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(RSE_MATRICULA, consultarMargem.getMatricula());
        parametros.put(SER_CPF, getValue(consultarMargem.getCpf()));
        final Double adeVlr = consultarMargem.getValorParcela();
        if (adeVlr.equals(Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        parametros.put(SER_SENHA, getValue(consultarMargem.getSenhaServidor()));
        parametros.put(TOKEN, consultarMargem.getTokenAutServidor());
        parametros.put(USUARIO, consultarMargem.getUsuario());
        parametros.put(SENHA, consultarMargem.getSenha());
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarMargem.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarMargem.getEstabelecimentoCodigo()));
        parametros.put(CONVENIO, getValue(consultarMargem.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarMargem.getCliente()));
        parametros.put(SER_LOGIN, consultarMargem.getLoginServidor());
        parametros.put(SERVICO_CODIGO, getValue(consultarMargem.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(consultarMargem.getCodVerba()));
        parametros.put(MATRICULA_MULTIPLA, getValue(consultarMargem.getMatriculaMultipla()));

        return parametros;
    }
}
