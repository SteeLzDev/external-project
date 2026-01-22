package com.zetra.econsig.job.process;

import java.math.BigDecimal;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.consignacao.ReajustaADEHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ProcessaReajusteAde</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaReajusteAde extends Processo {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaReajusteAde.class);

    private final String csaCodigo;

    private final Map<String, String[]> parameterMap;

    private final AcessoSistema responsavel;

    public ProcessaReajusteAde(String csaCodigo, Map<String, String[]> parameterMap, AcessoSistema responsavel) {
        this.csaCodigo = csaCodigo;
        this.parameterMap = parameterMap;
        this.responsavel = responsavel;
    }

    @Override
    protected void executar() {

        String vlrIgual      = getParametro("vlr_igual");
        String vlrMaiorIgual = getParametro("vlr_maior_igual");
        String vlrMenorIgual = getParametro("vlr_menor_igual");
        String verba         = getParametro("verba");
        String padraoVerba   = getParametro("padrao_verba");
        String padraoIndice  = getParametro("padrao_indice");
        String limitadoTipo  = getParametro("limitado_tipo");
        String limitadoVlr   = getParametro("limitado_vlr");
        boolean validar      = getParametro("acao").equalsIgnoreCase("validar");

        CustomTransferObject regras = new CustomTransferObject();

        try {
            if (!TextHelper.isNull(getParametro("chkvlr_igual"))) {
                regras.setAttribute("vlr_igual", NumberHelper.reformat(vlrIgual, NumberHelper.getLang(), "en"));
            }

            if (!TextHelper.isNull(getParametro("chkvlr_maior_igual"))) {
                regras.setAttribute("vlr_maior_igual", NumberHelper.reformat(vlrMaiorIgual,NumberHelper.getLang(),"en"));
            }

            if (!TextHelper.isNull(getParametro("chkvlr_menor_igual"))) {
                regras.setAttribute("vlr_menor_igual", NumberHelper.reformat(vlrMenorIgual,NumberHelper.getLang(),"en"));
            }

            if ((!TextHelper.isNull(getParametro("chkverba"))) && !TextHelper.isNull(verba.trim())) {
                regras.setAttribute("verba", verba.split(";")[0]);
                regras.setAttribute("servico", verba.split(";")[1]);
            }

            if (!TextHelper.isNull(getParametro("chkpadrao_verba"))) {
                regras.setAttribute("padrao_indice", padraoIndice);
            }

            if (!TextHelper.isNull(getParametro("chkpadrao_verba"))) {
                regras.setAttribute("padrao_verba", padraoVerba);
            }

            if (!TextHelper.isNull(getParametro("chklimitado_vlr"))) {
                if(limitadoTipo!= null) {
                    regras.setAttribute("limitado_tipo", limitadoTipo);
                }
                if(limitadoVlr!= null) {
                    regras.setAttribute("limitado_vlr", NumberHelper.reformat(limitadoVlr, NumberHelper.getLang(), "en"));
                }
            }

            BigDecimal vlrReajuste = new BigDecimal(NumberHelper.reformat(getParametro("vlr_reajuste").toString(), NumberHelper.getLang(), "en"));
            String tipoReajuste = getParametro("tipo_reajuste");

            ReajustaADEHelper.reajustaAdes(csaCodigo, CodedValues.CSE_CODIGO_SISTEMA, vlrReajuste, tipoReajuste, regras, validar, responsavel);

            // Seta mensagem de sucesso na sessão do usuário
            if(validar){
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.reajuste.contrato.validado.sucesso", responsavel);
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.reajuste.contrato.efetuado.sucesso", responsavel);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            codigoRetorno = ERRO;
            if(validar){
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.reajuste.contrato", responsavel) + "<br>"
                              + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
            } else {
                    mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.aplicacao.reajuste.contrato", responsavel) + "<br>"
                              + ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ex.getMessage());
             }
        }
    }

    private String getParametro(String parametro){
        try {
            return parameterMap.get(parametro)[0];
        } catch (NullPointerException ex){
            return null;
        }
    }
}
