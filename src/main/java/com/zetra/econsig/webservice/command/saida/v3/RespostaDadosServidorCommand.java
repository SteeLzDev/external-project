package com.zetra.econsig.webservice.command.saida.v3;

import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.DADOS_SERVIDOR_V3_0;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO_IDT;
import static com.zetra.econsig.webservice.CamposAPI.SER_NACIONALIDADE;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_BAIRRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CART_PROF;
import static com.zetra.econsig.webservice.CamposAPI.SER_CELULAR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CEP;
import static com.zetra.econsig.webservice.CamposAPI.SER_CIDADE;
import static com.zetra.econsig.webservice.CamposAPI.SER_COMPL;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_END;
import static com.zetra.econsig.webservice.CamposAPI.SER_EST_CIVIL;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_MAE;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME_PAI;
import static com.zetra.econsig.webservice.CamposAPI.SER_NRO;
import static com.zetra.econsig.webservice.CamposAPI.SER_PIS;
import static com.zetra.econsig.webservice.CamposAPI.SER_SEXO;
import static com.zetra.econsig.webservice.CamposAPI.SER_TEL;
import static com.zetra.econsig.webservice.CamposAPI.SER_UF;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_REGISTRO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaBoletoCommand</p>
 * <p>Description: classe command que gera um boleto em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaDadosServidorCommand extends RespostaRequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaDadosServidorCommand.class);

    public RespostaDadosServidorCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        // Exibe os atributos do boleto
        CustomTransferObject dadosServidor = (CustomTransferObject) parametros.get(DADOS_SERVIDOR_V3_0);

        if (dadosServidor != null) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(DADOS_SERVIDOR_V3_0);


            Object ser_sexo = null;
            try {
                ser_sexo = dadosServidor.getAttribute(Columns.SER_SEXO).toString().equalsIgnoreCase("M") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.masculino", responsavel).toUpperCase() : dadosServidor.getAttribute(Columns.SER_SEXO).toString().equalsIgnoreCase("F") ? ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.feminino", responsavel).toUpperCase() : "";
            } catch (Exception ex) {
                ser_sexo = dadosServidor.getAttribute(Columns.SER_SEXO);
            }

            Object rse_data_admissao = dadosServidor.getAttribute(Columns.RSE_DATA_ADMISSAO) != null ? DateHelper.toDateString((java.util.Date) dadosServidor.getAttribute(Columns.RSE_DATA_ADMISSAO)) : null;

            reg.addAtributo(SERVIDOR, dadosServidor.getAttribute(Columns.SER_NOME));
            reg.addAtributo(SER_CPF, dadosServidor.getAttribute(Columns.SER_CPF));
            reg.addAtributo(SER_SEXO, ser_sexo);
            reg.addAtributo(SER_EST_CIVIL, dadosServidor.getAttribute(Columns.SER_EST_CIVIL));
            reg.addAtributo(SER_NRO_IDT, dadosServidor.getAttribute(Columns.SER_NRO_IDT));
            reg.addAtributo(SER_NOME_PAI, dadosServidor.getAttribute(Columns.SER_NOME_PAI));
            reg.addAtributo(SER_NOME_MAE, dadosServidor.getAttribute(Columns.SER_NOME_MAE));
            reg.addAtributo(SER_END, dadosServidor.getAttribute(Columns.SER_END));
            reg.addAtributo(SER_NRO, dadosServidor.getAttribute(Columns.SER_NRO));
            reg.addAtributo(SER_COMPL, dadosServidor.getAttribute(Columns.SER_COMPL));
            reg.addAtributo(SER_BAIRRO, dadosServidor.getAttribute(Columns.SER_BAIRRO));
            reg.addAtributo(SER_CIDADE, dadosServidor.getAttribute(Columns.SER_CIDADE));
            reg.addAtributo(SER_UF, dadosServidor.getAttribute(Columns.SER_UF));
            reg.addAtributo(SER_CEP, dadosServidor.getAttribute(Columns.SER_CEP));
            reg.addAtributo(SER_TEL, dadosServidor.getAttribute(Columns.SER_TEL));
            reg.addAtributo(SER_CELULAR, dadosServidor.getAttribute(Columns.SER_CELULAR));
            reg.addAtributo(SER_NACIONALIDADE, dadosServidor.getAttribute(Columns.SER_NACIONALIDADE));
            reg.addAtributo(SER_CART_PROF, dadosServidor.getAttribute(Columns.SER_CART_PROF));
            reg.addAtributo(SER_PIS, dadosServidor.getAttribute(Columns.SER_PIS));
            reg.addAtributo(RSE_MATRICULA, dadosServidor.getAttribute(Columns.RSE_MATRICULA));
            reg.addAtributo(RSE_DATA_ADMISSAO, rse_data_admissao);
            reg.addAtributo(RSE_PRAZO, dadosServidor.getAttribute(Columns.RSE_PRAZO));
            reg.addAtributo(RSE_TIPO, dadosServidor.getAttribute(Columns.RSE_TIPO));
            reg.addAtributo(ORGAO, dadosServidor.getAttribute(Columns.ORG_NOME));
            reg.addAtributo(ORG_IDENTIFICADOR, dadosServidor.getAttribute(Columns.ORG_IDENTIFICADOR));
            reg.addAtributo(ESTABELECIMENTO, dadosServidor.getAttribute(Columns.EST_NOME));
            reg.addAtributo(EST_IDENTIFICADOR, dadosServidor.getAttribute(Columns.EST_IDENTIFICADOR));
            reg.addAtributo(SER_EMAIL, dadosServidor.getAttribute(Columns.SER_EMAIL));
            reg.addAtributo(TIPO_REGISTRO, dadosServidor.getAttribute(Columns.RSE_TRS_CODIGO));

            try {
                ParametroDelegate parDelegate = new ParametroDelegate();
                if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                    Object ser_data_nasc = dadosServidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.toDateString((java.util.Date) dadosServidor.getAttribute(Columns.SER_DATA_NASC)) : null;
                    reg.addAtributo(DATA_NASCIMENTO, ser_data_nasc);
                }
            } catch (ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            respostas.add(reg);
        }
        return respostas;
    }
}
