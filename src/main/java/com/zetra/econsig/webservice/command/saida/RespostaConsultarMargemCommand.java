package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.INFO_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_MARGEM_3;
import static com.zetra.econsig.webservice.CamposAPI.VALORES_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_1;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_2;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_3;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConsultarMargemCommand</p>
 * <p>Description: classe command que gera info margem em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarMargemCommand extends RespostaRequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RespostaConsultarMargemCommand.class);

    public RespostaConsultarMargemCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        ParametroDelegate parDelegate = new ParametroDelegate();

        RegistroRespostaRequisicaoExterna registro = respostas.get(0);

        registro.addAtributo(EST_IDENTIFICADOR, parametros.get(ESTABELECIMENTO));
        registro.addAtributo(ESTABELECIMENTO, parametros.get(EST_NOME));
        registro.addAtributo(ORG_IDENTIFICADOR, parametros.get(ORGAO));
        registro.addAtributo(ORGAO, parametros.get(ORG_NOME));
        registro.addAtributo(RSE_TIPO, parametros.get(RSE_TIPO));
        registro.addAtributo(SERVIDOR, parametros.get(SERVIDOR));
        registro.addAtributo(SER_CPF, parametros.get(SER_CPF));
        registro.addAtributo(RSE_MATRICULA, parametros.get(RSE_MATRICULA));

        try {
            if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                String ser_data_nasc = "";
                if (parametros.get(DATA_NASCIMENTO) != null) {
                    ser_data_nasc = parametros.get(DATA_NASCIMENTO).toString();
                    if (!ser_data_nasc.equals("0000-00-00") && !ser_data_nasc.equals("0001-01-01") && !ser_data_nasc.equals("1753-01-01")) {
                        ser_data_nasc = DateHelper.reformat(ser_data_nasc, LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
                    } else {
                        ser_data_nasc = "";
                    }
                }
                registro.addAtributo(DATA_NASCIMENTO, ser_data_nasc);
            }
        } catch (ParseException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        Object rse_data_admissao = null;
        try {
            if (parametros.get(RSE_DATA_ADMISSAO) != null) {
                rse_data_admissao = DateHelper.reformat(parametros.get(RSE_DATA_ADMISSAO).toString(), LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
            }
        } catch (ParseException ex) {
            rse_data_admissao = parametros.get(RSE_DATA_ADMISSAO);
        }
        registro.addAtributo(RSE_DATA_ADMISSAO, rse_data_admissao);
        registro.addAtributo(RSE_PRAZO, parametros.get(RSE_PRAZO));

        Map<String, Object> valoresMargem = (Map<String, Object>) parametros.get(VALORES_MARGEM);
        if (valoresMargem != null) {
            for (String chave : valoresMargem.keySet()) {
                CamposAPI campo = switch (chave) {
                    case "VALOR_MARGEM_1" -> VALOR_MARGEM_1;
                    case "VALOR_MARGEM_2" -> VALOR_MARGEM_2;
                    case "VALOR_MARGEM_3" -> VALOR_MARGEM_3;
                    case "TEXTO_MARGEM_1" -> TEXTO_MARGEM_1;
                    case "TEXTO_MARGEM_2" -> TEXTO_MARGEM_2;
                    case "TEXTO_MARGEM_3" -> TEXTO_MARGEM_3;
                    default -> null;
                };
                if (campo != null) {
                    registro.addAtributo(campo, valoresMargem.get(chave));
                }
            }
        } else {
            registro.addAtributo(MENSAGEM, parametros.get(MENSAGEM));
            registro.addAtributo(VALOR_MARGEM, parametros.get(VALOR_MARGEM));
        }

        // Caso seja consulta de margem múltipla passa a lista de margens para frente
        List<Map<CamposAPI, Object>> lstInfoMargem = (List<Map<CamposAPI, Object>>) parametros.get(INFO_MARGEM);
        if (lstInfoMargem != null && !lstInfoMargem.isEmpty()) {
            for (Map<CamposAPI, Object> map : lstInfoMargem) {
                try {
                    if (!parDelegate.hasValidacaoDataNasc(responsavel)) {
                        String ser_data_nasc = "";
                        if (map.get(DATA_NASCIMENTO) != null) {
                            ser_data_nasc = map.get(DATA_NASCIMENTO).toString();
                            if (!ser_data_nasc.equals("0000-00-00") && !ser_data_nasc.equals("0001-01-01") && !ser_data_nasc.equals("1753-01-01")) {
                                ser_data_nasc = DateHelper.reformat(ser_data_nasc, LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
                            } else {
                                ser_data_nasc = "";
                            }
                        }
                        map.put(DATA_NASCIMENTO, ser_data_nasc);
                    } else {
                        map.remove(DATA_NASCIMENTO);
                    }
                } catch (ParseException | ParametroControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                rse_data_admissao = null;
                try {
                    if (map.get(RSE_DATA_ADMISSAO) != null) {
                        rse_data_admissao = DateHelper.reformat(map.get(RSE_DATA_ADMISSAO).toString(), LocaleHelper.FORMATO_DATA_INGLES, LocaleHelper.getDatePattern());
                    }
                } catch (ParseException ex) {
                    rse_data_admissao = map.get(RSE_DATA_ADMISSAO);
                }
                map.put(RSE_DATA_ADMISSAO, rse_data_admissao);

                Map<String, Object> margens = (Map<String, Object>) map.get(VALORES_MARGEM);
                if (margens != null) {
                    for (String chave : margens.keySet()) {
                        CamposAPI campo = switch (chave) {
                            case "VALOR_MARGEM_1" -> VALOR_MARGEM_1;
                            case "VALOR_MARGEM_2" -> VALOR_MARGEM_2;
                            case "VALOR_MARGEM_3" -> VALOR_MARGEM_3;
                            case "TEXTO_MARGEM_1" -> TEXTO_MARGEM_1;
                            case "TEXTO_MARGEM_2" -> TEXTO_MARGEM_2;
                            case "TEXTO_MARGEM_3" -> TEXTO_MARGEM_3;
                            default -> null;
                        };
                        if (campo != null) {
                            map.put(campo, margens.get(chave));
                        }
                    }
                }
            }

            // Inclui a informação de margem formatada
            registro.addAtributo(INFO_MARGEM, lstInfoMargem);
        }

        return respostas;
    }
}
