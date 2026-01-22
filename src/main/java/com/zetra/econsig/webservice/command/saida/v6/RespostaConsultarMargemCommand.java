package com.zetra.econsig.webservice.command.saida.v6;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.CARGO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ESCOLARIDADE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.HABITACAO_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_SAIDA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_SALARIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_CID_NASC;
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

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;

/**
 * <p>Title: RespostaConsultarMargemCommand</p>
 * <p>Description: classe command que gera info margem em resposta à requisição externa ao eConsig versão 6.0.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarMargemCommand extends com.zetra.econsig.webservice.command.saida.v3.RespostaConsultarMargemCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(com.zetra.econsig.webservice.command.saida.v6.RespostaConsultarMargemCommand.class);

    public RespostaConsultarMargemCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        RegistroRespostaRequisicaoExterna registro = respostas.get(0);

        try {
            registro.addAtributo(SER_NRO_IDT, parametros.get(SER_NRO_IDT));
            String serDataIdtn = null;
            if (parametros.get(SER_DATA_IDT) != null) {
                serDataIdtn = parametros.get(SER_DATA_IDT).toString();

                serDataIdtn = DateHelper.reformat(serDataIdtn, LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());

            }
            registro.addAtributo(SER_DATA_IDT, serDataIdtn);
            registro.addAtributo(SER_UF_IDT, parametros.get(SER_UF_IDT));
            registro.addAtributo(SER_EMISSOR_IDT, parametros.get(SER_EMISSOR_IDT));
            registro.addAtributo(SER_CID_NASC, parametros.get(SER_CID_NASC));
            registro.addAtributo(SER_NACIONALIDADE, parametros.get(SER_NACIONALIDADE));
            registro.addAtributo(SER_SEXO, parametros.get(SER_SEXO));
            registro.addAtributo(SER_EST_CIVIL, parametros.get(SER_EST_CIVIL));
            registro.addAtributo(SER_END, parametros.get(SER_END));
            registro.addAtributo(SER_NRO, parametros.get(SER_NRO));
            registro.addAtributo(SER_COMPL, parametros.get(SER_COMPL));
            registro.addAtributo(SER_BAIRRO, parametros.get(SER_BAIRRO));
            registro.addAtributo(SER_CIDADE, parametros.get(SER_CIDADE));
            registro.addAtributo(SER_UF, parametros.get(SER_UF));
            registro.addAtributo(SER_CEP, parametros.get(SER_CEP));
            registro.addAtributo(SER_TEL, parametros.get(SER_TEL));
            registro.addAtributo(SER_CELULAR, parametros.get(SER_CELULAR));
            registro.addAtributo(RSE_SALARIO,(parametros.get(RSE_SALARIO) != null) ? ((BigDecimal) parametros.get(RSE_SALARIO)).toString() : null);
            String serDataSaida = null;
            if (parametros.get(RSE_DATA_SAIDA) != null) {
                serDataSaida = parametros.get(RSE_DATA_SAIDA).toString();
                serDataSaida = DateHelper.reformat(serDataSaida, LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
            }
            registro.addAtributo(RSE_DATA_SAIDA, serDataSaida);
            registro.addAtributo(RSE_BANCO, parametros.get(RSE_BANCO));
            registro.addAtributo(RSE_AGENCIA, parametros.get(RSE_AGENCIA));
            registro.addAtributo(RSE_CONTA, parametros.get(RSE_CONTA));
            registro.addAtributo(CARGO_CODIGO, parametros.get(CARGO_CODIGO));
            registro.addAtributo(CARGO_DESCRICAO, parametros.get(CARGO_DESCRICAO));
            registro.addAtributo(HABITACAO_CODIGO, parametros.get(HABITACAO_CODIGO));
            registro.addAtributo(HABITACAO_DESCRICAO, parametros.get(HABITACAO_DESCRICAO));
            registro.addAtributo(ESCOLARIDADE_CODIGO, parametros.get(ESCOLARIDADE_CODIGO));
            registro.addAtributo(ESCOLARIDADE_DESCRICAO, parametros.get(ESCOLARIDADE_DESCRICAO));
            registro.addAtributo(SER_QTD_FILHOS, parametros.get(SER_QTD_FILHOS));
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return respostas;
    }

}
