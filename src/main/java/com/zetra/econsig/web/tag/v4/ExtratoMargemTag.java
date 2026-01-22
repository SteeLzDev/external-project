package com.zetra.econsig.web.tag.v4;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.margem.CasamentoMargem;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: ExtratoMargemTag</p>
 * <p>Description: Tag para exibição do cálculo do extrato de margem.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 24240 $
 * $Date: 2018-05-15 10:45:30 -0300 (Ter, 15 mai 2018) $
 */
public class ExtratoMargemTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExtratoMargemTag.class);

    // Escopo do atributo que contém os dados necessários para a tag: Default PAGE
    private String scope;
    // Nome do atributo que contém a lista de margens a ser exibida
    private String margem;
    // Nome do atributo que contém os dados do servidor
    private String extrato;

    private AcessoSistema responsavel;

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setMargem(String margem) {
        this.margem = margem;
    }

    public void setExtrato(String extrato) {
        this.extrato = extrato;
    }

    @Override
    public int doEndTag() throws JspException {
        try {
            final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

            responsavel = JspHelper.getAcessoSistema(request);

            @SuppressWarnings("unchecked")
            final
            // Obtem os dados do extrato de margem
            List<TransferObject> lstExtrato = (List<TransferObject>) pageContext.getAttribute(extrato, getScopeAsInt(scope));

            @SuppressWarnings("unchecked")
            final
            // Obtem os dados das margens
            List<MargemTO> lstMargem = (List<MargemTO>) pageContext.getAttribute(margem, getScopeAsInt(scope));

            // Gera o resultado
            pageContext.getOut().print(geraDetalheServidor(lstExtrato, lstMargem));

            return EVAL_PAGE;

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new JspException(ex.getMessage(), ex);
        }
    }

    protected String geraDetalheServidor(List<TransferObject> lstExtrato, List<MargemTO> lstMargem) throws ParseException {
        // Inicia geração do código HTML
        final StringBuilder code = new StringBuilder();

        // Parâmetros de sistema para margem casada
        final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
        final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
        final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
        final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

        // Grupos de casamento das margens extras
        final List<Short> gruposCasamento = CasamentoMargem.getInstance().getGrupos();

        // Cria estruturas de objetos necessárias:
        // Configuração de exibição de margem
        final Map<Short, ExibeMargem> exibeMargemMap = new HashMap<>();
        // Sub-total de contratos por tipo de resultado e margem
        final Map<String, Map<Short, BigDecimal>> subTotalUsadoMap = new HashMap<>();
        // Total de margem usada para cada margem
        final Map<Short, BigDecimal> totalUsadoMap = new HashMap<>();
        // Total de margem usada pós casamento para cada margem
        final Map<Short, BigDecimal> totalUsadoFinalMap = new HashMap<>();
        // Total de margem restante para cada margem
        final Map<Short, BigDecimal> totalRestanteMap = new HashMap<>();
        // Cálculo da margem restante pós casamento para cada margem
        final Map<Short, BigDecimal> totalRestanteFinalMap = new HashMap<>();

        // Navega na lista de margens inicializando os objetos necessários
        Iterator<MargemTO> itMargens = lstMargem.iterator();
        while (itMargens.hasNext()) {
            final MargemTO margemTO = itMargens.next();
            exibeMargemMap.put(margemTO.getMarCodigo(), new ExibeMargem(margemTO, responsavel));

            for (int i = 1; i <= 8; i++) {
                Map<Short, BigDecimal> subTotalTipoMap = subTotalUsadoMap.get(String.valueOf(i));
                if (subTotalTipoMap == null) {
                    subTotalTipoMap = new HashMap<>();
                    subTotalUsadoMap.put(String.valueOf(i), subTotalTipoMap);
                }
                subTotalTipoMap.put(margemTO.getMarCodigo(), new BigDecimal("0.00"));
            }
            totalUsadoMap.put(margemTO.getMarCodigo(), new BigDecimal("0.00"));
            totalUsadoFinalMap.put(margemTO.getMarCodigo(), new BigDecimal("0.00"));
            totalRestanteMap.put(margemTO.getMarCodigo(), new BigDecimal("0.00"));
            totalRestanteFinalMap.put(margemTO.getMarCodigo(), new BigDecimal("0.00"));
        }

        String rseCodigo = null;
        boolean exibeContratosNaoIncideMargem = false;
        for (final TransferObject item : lstExtrato) {
            final String tipo = item.getAttribute("TIPO").toString();
            if ("0".equals(tipo)) {
                rseCodigo = (String) item.getAttribute(Columns.RSE_CODIGO);
                exibeContratosNaoIncideMargem = true;
                continue;
            }

            final Short adeIncMargem = (Short) item.getAttribute(Columns.ADE_INC_MARGEM);
            final BigDecimal vlrUsado = (BigDecimal) item.getAttribute("MARGEM_USADA");

            BigDecimal subTotalUsado = subTotalUsadoMap.get(tipo).get(adeIncMargem);
            subTotalUsado = !TextHelper.isNull(subTotalUsado) ? subTotalUsado.add(vlrUsado) : BigDecimal.ZERO;
            subTotalUsadoMap.get(tipo).put(adeIncMargem, subTotalUsado);

            BigDecimal totalUsado = totalUsadoMap.get(adeIncMargem);
            totalUsado = !TextHelper.isNull(totalUsado) ? totalUsado.add(vlrUsado) : BigDecimal.ZERO;
            totalUsadoMap.put(adeIncMargem, totalUsado);

            BigDecimal totalUsadoFinal = totalUsadoFinalMap.get(adeIncMargem);
            totalUsadoFinal = !TextHelper.isNull(totalUsadoFinal) ? totalUsadoFinal.add(vlrUsado) : BigDecimal.ZERO;
            totalUsadoFinalMap.put(adeIncMargem, totalUsadoFinal);
        }

        // Soma as margens usadas quando forem casadas
        if (margem1CasadaMargem3) {
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
        } else if (margem123Casadas) {
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2)).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_2, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
        } else if (margem1CasadaMargem3Esq) {
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_3, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
        } else if (margem123CasadasEsq) {
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2)).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_2, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2)).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
            totalUsadoFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_3, totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2)).add(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3)));
        }

        final Map<Short, Map<Short, BigDecimal>> intermediarioUsada = new HashMap<>();

        // Soma as margens usadas quando forem casadas das margens extras
        for (final Short grupo : gruposCasamento) {
            // Cálculo da margem restante pós casamento para cada margem
            // Se faz necessário criar uma varivael hash de valor intermediário, pois durante os casamentos os valores não são imutáveis, ou seja,
            // eles vão sofrendo alteração e para calcular o valor usado corretamente para ser mostrado, cada passo da alteração precisa ficar registrado e por isso
            // utilizamos da totalIntermediarioMap, que é a variável entre o hash imutável (totalUsadoMap) e o valor agrupado. (totalUsadoFinalMap)
            final Map<Short, BigDecimal> totalIntermediarioMap = new HashMap<>(totalUsadoFinalMap);
            intermediarioUsada.put(grupo, totalIntermediarioMap);

            final String tipoCasamento = CasamentoMargem.getInstance().getTipoGrupo(grupo);
            final List<Short> marCodigos = CasamentoMargem.getInstance().getMargensCasadas(grupo);
            if (CasamentoMargem.DIREITA.equals(tipoCasamento)) {
                for (int i = 0; i < marCodigos.size(); i++) {
                    for (int j = i+1; j < marCodigos.size(); j++) {
                        // Soma o i com o valor de i + j, onde i vem do agregado (totalUsadoFinalMap) e o j do simples (totalUsadoMap)
                        if (totalUsadoFinalMap.containsKey(marCodigos.get(i)) && totalUsadoMap.containsKey(marCodigos.get(j))) {
                            totalUsadoFinalMap.put(marCodigos.get(i), totalUsadoFinalMap.get(marCodigos.get(i)).add(totalIntermediarioMap.get(marCodigos.get(j))));
                        }
                    }
                }
            } else if (CasamentoMargem.ESQUERDA.equals(tipoCasamento)) {
                for (int i = 0; i < marCodigos.size(); i++) {
                    for (int j = 0; j < marCodigos.size(); j++) {
                        // Soma o i com o valor de i + j, onde i vem do agregado (totalUsadoFinalMap) e o j do simples (totalUsadoMap)
                        if ((i != j) && (totalUsadoFinalMap.containsKey(marCodigos.get(i)) && totalUsadoMap.containsKey(marCodigos.get(j)))) {
                            totalUsadoFinalMap.put(marCodigos.get(i), totalUsadoFinalMap.get(marCodigos.get(i)).add(totalIntermediarioMap.get(marCodigos.get(j))));
                        }
                    }
                }
            }
        }

        // Realiza o cálculo das margens restantes
        itMargens = lstMargem.iterator();
        while (itMargens.hasNext()) {
            final MargemTO margemTO = itMargens.next();
            final BigDecimal totalMargemUsada = totalUsadoFinalMap.get(margemTO.getMarCodigo());
            final BigDecimal margemFolha = margemTO.getMrsMargem();
            if (margemFolha != null) {
                totalRestanteMap.put(margemTO.getMarCodigo(), margemFolha.subtract(totalMargemUsada));
                totalRestanteFinalMap.put(margemTO.getMarCodigo(), margemFolha.subtract(totalMargemUsada));
            }
        }
        BigDecimal rseSalario = BigDecimal.ZERO;
        if (exibeContratosNaoIncideMargem) {
            final PesquisarServidorController pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
            try {
                if (!TextHelper.isNull(rseCodigo)) {
                    final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, false, responsavel);
                    rseSalario = (BigDecimal) servidor.getAttribute(Columns.RSE_SALARIO);
                }
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Limite as margens restantes quando forem casadas. As margens
        // casadas pela esquerda não precisam ser limitadas, pois já são abatidas
        // pelos valores das demais margens.
        if (margem1CasadaMargem3) {
            totalRestanteFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_3, totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_3).compareTo(totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM)) == 1 ?
                    totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM) : totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_3));
        } else if (margem123Casadas) {
            totalRestanteFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_2, totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_2).compareTo(totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM)) == 1 ?
                    totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM) : totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_2));

            totalRestanteFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_3, totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_3).compareTo(totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_2)) == 1 ?
                    totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_2) : totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_3));

        } else if (margem1CasadaMargem3Lateral) {
            totalRestanteFinalMap.put(CodedValues.INCIDE_MARGEM_SIM_3, totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM_3).
                    add(new BigDecimal(Math.min(0.00, totalRestanteFinalMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue()))));
        }

        // Limita as margens extras restantes quando forem casadas. As margens
        // casadas pela esquerda não precisam ser limitadas, pois já são abatidas
        // pelos valores das demais margens.
        for (final Short grupo : gruposCasamento) {
            final String tipoCasamento = CasamentoMargem.getInstance().getTipoGrupo(grupo);
            final List<Short> marCodigos = CasamentoMargem.getInstance().getMargensCasadas(grupo);
            for (int i = 0; i < marCodigos.size(); i++) {
                for (int j = i+1; j < marCodigos.size(); j++) {
                    if (CasamentoMargem.DIREITA.equals(tipoCasamento) || CasamentoMargem.MINIMO.equals(tipoCasamento)) {
                        if (totalRestanteFinalMap.containsKey(marCodigos.get(i)) && totalRestanteFinalMap.containsKey(marCodigos.get(j))) {
                            totalRestanteFinalMap.put(marCodigos.get(j), totalRestanteFinalMap.get(marCodigos.get(j)).compareTo(totalRestanteFinalMap.get(marCodigos.get(i))) == 1 ?
                                    totalRestanteFinalMap.get(marCodigos.get(i)) : totalRestanteFinalMap.get(marCodigos.get(j)));
                        }
                    } else if (CasamentoMargem.LATERAL.equals(tipoCasamento) && (totalRestanteFinalMap.containsKey(marCodigos.get(i)) && totalRestanteFinalMap.containsKey(marCodigos.get(j)))) {
                        totalRestanteFinalMap.put(marCodigos.get(j), totalRestanteFinalMap.get(marCodigos.get(j)).
                                add(new BigDecimal(Math.min(0.00, totalRestanteFinalMap.get(marCodigos.get(i)).doubleValue()))));
                    }
                }
            }
        }

        TransferObject item = null;
        String tipoAnterior = null;
        String tipo = null;

        String adeCodigo, adeData, adeNumero, sadDescricao, csaNome;
        String adeVlr, adeVlrFolha, adeTipoVlr;
        String verbaServico = null;
        Short adeIncMargem;
        int contaColuna = 0;
        String usada;
        double vlrUsada;

//primeira tabela
        if (!lstExtrato.isEmpty() && !responsavel.isCsaCor()) {
            final Iterator<TransferObject> it = lstExtrato.iterator();
            while (it.hasNext()) {
                item = it.next();
                tipo = item.getAttribute("TIPO").toString();

                if ((tipoAnterior == null) || !tipo.equals(tipoAnterior)) {
                    if (tipoAnterior != null) {
//ultima linha da primeira tabela
                        code.append("        <tr>");
                        code.append("          <td colspan=\"6\"><b>" + ApplicationResourcesHelper.getMessage("rotulo.campo.sub.total", responsavel).toUpperCase() + ":</b></td>");

                        itMargens = lstMargem.iterator();
                        while (itMargens.hasNext()) {
                            final MargemTO margemTO = itMargens.next();
                            final boolean exibeMargem = exibeMargemMap.get(margemTO.getMarCodigo()).isExibeValor();
                            if (exibeMargem) {
                                code.append("          <td><b>").append(NumberHelper.format(subTotalUsadoMap.get(tipoAnterior).get(margemTO.getMarCodigo()).doubleValue(), NumberHelper.getLang())).append("</b></td>");
                            }
                        }
                        code.append("<td> </td>");
                        code.append("</tr>");
                        code.append("      </tbody>");
                        code.append("      <tfoot>");
                        code.append("        <tr><td colspan='" + (contaColuna+1) + "'>" + ApplicationResourcesHelper.getMessage("rotulo.listagem.de", responsavel)+ " ");
                        switch (tipoAnterior.charAt(0)) {
                            case '1' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.abertos", responsavel).toLowerCase()); break;
                            case '2' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.relativo.diferenca.contratos.renegociados.comprados", responsavel).toLowerCase()); break;
                            case '3' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.pagamento.parcial", responsavel).toLowerCase()); break;
                            case '4' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.percentuais", responsavel).toLowerCase()); break;
                            case '5' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.concluidos.nao.liberados", responsavel).toLowerCase()); break;
                            case '6' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.cancelados.liquidados.concluidos.no.periodo", responsavel).toLowerCase()); break;
                            case '7' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.valor.reduzido", responsavel).toLowerCase()); break;
                            case '8' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.indeterminados.liquidados", responsavel)); break;
                            case '0' : if (exibeContratosNaoIncideMargem) {code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.contatos.nao.incide", responsavel));} break;
                        }
                        code.append("        </tr>");
                        code.append("      </tfoot>");
                        code.append("    </table>");
                        code.append("  </div>");
                        code.append("</div>");
                    }
//inicio da primeira tabela
                    code.append("<div class='card'>");
                    code.append("  <div class='card-header'>");
                    code.append("    <h2 class='card-header-title'>");
                    switch (tipo.charAt(0)) {
                        case '1' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.abertos", responsavel)); break;
                        case '2' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.relativo.diferenca.contratos.renegociados.comprados", responsavel)); break;
                        case '3' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.pagamento.parcial", responsavel)); break;
                        case '4' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.percentuais", responsavel)); break;
                        case '5' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.concluidos.nao.liberados", responsavel)); break;
                        case '6' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.cancelados.liquidados.concluidos.no.periodo", responsavel)); break;
                        case '7' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.valor.reduzido", responsavel)); break;
                        case '8' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.indeterminados.liquidados", responsavel)); break;
                        case '0' : if (exibeContratosNaoIncideMargem) {code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.contatos.nao.incide", responsavel));} break;
                    }
                    code.append("    </h2>");
                    code.append("  </div>");
                    code.append("  <div class='card-body table-responsive p-0'>");
                    code.append("    <table class='table table-striped table-hover'>");
                    code.append("      <thead>");
                    code.append("        <tr>");
                    code.append("          <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel) + "</td>");
                    code.append("          <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel) + "</td>");
                    code.append("          <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel) + "</td>");
                    if (exibeContratosNaoIncideMargem) {
                        code.append("          <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel) + "</td>");
                    }
                    code.append("          <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel) + "</td>");
                    code.append("          <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado2", responsavel) + "</td>");
                    code.append("          <th scope=\"col\" width=\"10%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.folha.abreviado.moeda", responsavel) + "</td>");

                    itMargens = lstMargem.iterator();
                    while (itMargens.hasNext()) {
                        contaColuna = 6;
                        final MargemTO margemTO = itMargens.next();
                        final boolean exibeMargem = exibeMargemMap.get(margemTO.getMarCodigo()).isExibeValor();
                        final String rotuloMargem = margemTO.getMarDescricao();
                        final String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(margemTO.getMarTipoVlr() != null ? margemTO.getMarTipoVlr().toString() : null);
                        if (exibeMargem) {
                            code.append("          <th scope=\"col\" width=\"15%\">" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.utilizado", responsavel) + "<br>").append(TextHelper.forHtmlContent(rotuloMargem)).append(" (").append(TextHelper.forHtmlContent(labelTipoVlr)).append(")</td>");
                            contaColuna += 1;
                        }
                    }
                    code.append("          <th scope=\"col\" width=\"15%\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel) + "</td>");
                    code.append("        </tr>");
                    code.append("      </thead>");
                    code.append("      <tbody>");
                }
                tipoAnterior = tipo;

                adeCodigo = item.getAttribute(Columns.ADE_CODIGO).toString();
                adeNumero = item.getAttribute(Columns.ADE_NUMERO).toString();
                sadDescricao = item.getAttribute(Columns.SAD_DESCRICAO).toString();
                adeData = DateHelper.toDateTimeString((Date) item.getAttribute(Columns.ADE_DATA));

                vlrUsada = Double.parseDouble(item.getAttribute("MARGEM_USADA").toString());
                usada = NumberHelper.format(vlrUsada, NumberHelper.getLang());

                adeVlr = item.getAttribute(Columns.ADE_VLR) != null ? NumberHelper.reformat(item.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()) : "";
                adeVlrFolha  = item.getAttribute(Columns.ADE_VLR_FOLHA) != null ? NumberHelper.reformat(item.getAttribute(Columns.ADE_VLR_FOLHA).toString(), "en", NumberHelper.getLang()) : "";
                adeTipoVlr = (String) item.getAttribute(Columns.ADE_TIPO_VLR);
                adeIncMargem = (Short) item.getAttribute(Columns.ADE_INC_MARGEM);

                csaNome = (String) item.getAttribute(Columns.CSA_NOME_ABREV);
                if (TextHelper.isNull(csaNome)) {
                    csaNome = item.getAttribute(Columns.CSA_NOME).toString();
                }
                csaNome = item.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + csaNome;
                if (csaNome.length() > 50) {
                    csaNome = csaNome.substring(0, 47) + "...";
                }

                if (exibeContratosNaoIncideMargem) {
                    verbaServico = (String) item.getAttribute(Columns.CNV_COD_VERBA) + " - " + (String) item.getAttribute(Columns.SVC_DESCRICAO);
                }

                code.append("        <tr>");

                code.append("          <td>").append(TextHelper.forHtmlContent(adeNumero)).append("</td>");
                code.append("          <td>").append(TextHelper.forHtmlContent(adeData)).append("</td>");
                code.append("          <td>").append(TextHelper.forHtmlContent(csaNome)).append("</td>");
                if (exibeContratosNaoIncideMargem) {
                    code.append("          <td>").append(TextHelper.forHtmlContent(verbaServico)).append("</td>");
                }
                code.append("          <td>").append(TextHelper.forHtmlContent(sadDescricao)).append("</td>");
                code.append("          <td>").append(TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))).append(" ").append(TextHelper.forHtmlContent(adeVlr)).append("</td>");
                code.append("          <td>").append(TextHelper.forHtmlContent(adeVlrFolha)).append("</td>");

                itMargens = lstMargem.iterator();
                while (itMargens.hasNext()) {
                    final MargemTO margemTO = itMargens.next();
                    final boolean exibeMargem = exibeMargemMap.get(margemTO.getMarCodigo()).isExibeValor();
                    if (exibeMargem) {
                        if (adeIncMargem.equals(margemTO.getMarCodigo())) {
                            code.append("          <td>").append(TextHelper.forHtmlContent(usada)).append("</td>");
                        } else {
                            code.append("          <td>").append(NumberHelper.format(0.00, NumberHelper.getLang())).append("</td>");
                        }
                    }
                }
                code.append("          <td><a href=\"javascript:doIt('hi', '").append(TextHelper.forJavaScriptAttribute(adeCodigo)).append("');\">" + ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel) + "</a></td>");
                code.append("        </tr>");
            }

            code.append("<tr>");
            code.append("<td colspan=\"6\"><b>" + ApplicationResourcesHelper.getMessage("rotulo.campo.sub.total", responsavel).toUpperCase() + ":</b></td>");
            itMargens = lstMargem.iterator();
            while (itMargens.hasNext()) {
                final MargemTO margemTO = itMargens.next();
                final boolean exibeMargem = exibeMargemMap.get(margemTO.getMarCodigo()).isExibeValor();
                if (exibeMargem && (!exibeContratosNaoIncideMargem && !"0".equals(tipo))) {
                    code.append("<td><b>").append(NumberHelper.format(subTotalUsadoMap.get(tipo).get(margemTO.getMarCodigo()).doubleValue(), NumberHelper.getLang())).append("</b></td>");
                }
            }
            code.append("<td> </td>");
            code.append("</tr>");
            code.append("      </tbody>");
            code.append("      <tfoot>");
            code.append("        <tr><td colspan='" + (contaColuna+1) + "'>" + ApplicationResourcesHelper.getMessage("rotulo.listagem.de", responsavel)+ " ");
            switch (tipo.charAt(0)) {
                case '1' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.abertos", responsavel).toLowerCase()); break;
                case '2' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.relativo.diferenca.contratos.renegociados.comprados", responsavel).toLowerCase()); break;
                case '3' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.pagamento.parcial", responsavel).toLowerCase()); break;
                case '4' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.percentuais", responsavel).toLowerCase()); break;
                case '5' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.concluidos.nao.liberados", responsavel).toLowerCase()); break;
                case '6' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.cancelados.liquidados.concluidos.no.periodo", responsavel).toLowerCase()); break;
                case '7' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.valor.reduzido", responsavel).toLowerCase()); break;
                case '8' : code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.debito.contratos.indeterminados.liquidados", responsavel)); break;
                case '0' : if (exibeContratosNaoIncideMargem) {code.append(ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.contatos.nao.incide", responsavel));} break;
            }
            code.append("        </tr>");
            code.append("      </tfoot>");
            code.append("    </table>");
            code.append("  </div>");
            code.append("</div>");
        }
//final da primeira tabela

//segunda tabela
      code.append("      <div class='card'>");
      code.append("        <div class='card-header'>");
      code.append("          <h2 class='card-header-title'>" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.contratos.resultado", responsavel) + "</h2>");
      code.append("        </div>");
      code.append("        <div class='card-body table-responsive p-0'>");
      code.append("          <table class='table table-striped table-hover'>");
      code.append("            <thead>");
      code.append("              <tr>");
      code.append("                <th scope='col' width='10%'>" + ApplicationResourcesHelper.getMessage("rotulo.extrato.calculo.margem.folha", responsavel) + "</th>");
      if (exibeContratosNaoIncideMargem) {
          code.append("                <th scope='col' width='10%'>" + ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.salario", responsavel) + "</th>");
          code.append("                <th scope='col' width='10%'>" + ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.porcentagem", responsavel) + "</th>");
      }
      code.append("                <th scope='col' width='10%'>" + ApplicationResourcesHelper.getMessage("rotulo.extrato.valor.margem.folha", responsavel) + "</th>");
      code.append("                <th scope='col' width='10%'>" + ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.total.usada", responsavel) + "</th>");
      code.append("                <th scope='col' width='10%'>" + ApplicationResourcesHelper.getMessage("rotulo.extrato.margem.restante", responsavel) + "</th>");
      code.append("              </tr>");
      code.append("            </thead>");
      code.append("            <tbody>");

      itMargens = lstMargem.iterator();
      while (itMargens.hasNext()) {
          final MargemTO margemTO = itMargens.next();
          final boolean exibeMargem = exibeMargemMap.get(margemTO.getMarCodigo()).isExibeValor();
          //coluna 01
          final String rotuloMargem = margemTO.getMarDescricao();
          //coluna 02
          final Short marCodigo = margemTO.getMarCodigo();

          //coluna 03
          // Gera texto que representa o cálculo de margem usada.
          String margemUsada = "";
          // Verifica casamento das margens fixas 1, 2 e 3
          if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM) || marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2) || marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
              if (margem1CasadaMargem3) {
                  if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                      margemUsada = "(" + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + " + " + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + ")";
                  }
              } else if (margem123Casadas) {
                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                    margemUsada = "(" + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + " + " + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2).doubleValue(), NumberHelper.getLang()) + " + " + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + ")";
                } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                    margemUsada = "(" + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2).doubleValue(), NumberHelper.getLang()) + " + " + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + ")";
                }
            } else if (margem1CasadaMargem3Esq) {
                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM) || marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    margemUsada = "(" + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + " + " + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + ")";
                }
            } else if (margem123CasadasEsq) {
                if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM) || marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2) || marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                    margemUsada = "(" + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + " + " + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_2).doubleValue(), NumberHelper.getLang()) + " + " + NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + ")";
                }
            } else if (margem1CasadaMargem3Lateral && marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                margemUsada = NumberHelper.format(totalUsadoMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + " + " + ApplicationResourcesHelper.getMessage("rotulo.campo.minimo.abreviado", responsavel).toUpperCase() + "(0, " + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + ")";
            }
          } else {
              // Verifica casamento das demais margens
              // Se faz necessário criar uma variável de margensJaAdicionadas pois a lógica que monta os valores utilizados para chegar no valor restante é feito por por margem
              // sendo assim, precisamos registrar as margens já utilizadas para que não seja montado o combado com os valores já utilizados, ou seja, que se repita indicidência de margem.
              final List<Short> margensJaAdicionadas = new ArrayList<>();
              for (final Short grupo : gruposCasamento) {
                  final Map<Short, BigDecimal> totalIntermediarioMap = intermediarioUsada.get(grupo);

                  final String tipoCasamento = CasamentoMargem.getInstance().getTipoGrupo(grupo);
                  final List<Short> marCodigos = CasamentoMargem.getInstance().getMargensCasadas(grupo);

                  if (CasamentoMargem.DIREITA.equals(tipoCasamento)) {
                      for (int i = 0; i < marCodigos.size(); i++) {
                          if (marCodigos.get(i).equals(marCodigo) && totalUsadoMap.containsKey(marCodigos.get(i))) {
                              margemUsada += (!TextHelper.isNull(margemUsada) ? " - " : "");
                              margemUsada += "(";

                              for (int j = i; j < marCodigos.size(); j++) {
                                  if (totalUsadoMap.containsKey(marCodigos.get(j)) && !margensJaAdicionadas.contains(marCodigos.get(j))) {
                                      margemUsada += " + " + NumberHelper.format(totalIntermediarioMap.get(marCodigos.get(j)).doubleValue(), NumberHelper.getLang());
                                      margensJaAdicionadas.add(marCodigos.get(j));
                                  }
                              }

                              margemUsada += " )";
                          }
                      }
                  } else if (CasamentoMargem.ESQUERDA.equals(tipoCasamento)) {
                      for (int i = 0; i < marCodigos.size(); i++) {
                          if (marCodigos.get(i).equals(marCodigo) && totalUsadoMap.containsKey(marCodigos.get(i))) {
                              margemUsada += (!TextHelper.isNull(margemUsada) ? " - " : "");
                              margemUsada += "(";

                              for (int j = 0; j < marCodigos.size(); j++) {
                                  if (totalUsadoMap.containsKey(marCodigos.get(j)) && !margensJaAdicionadas.contains(marCodigos.get(j))) {
                                      margemUsada += " + " + NumberHelper.format(totalIntermediarioMap.get(marCodigos.get(j)).doubleValue(), NumberHelper.getLang());
                                      margensJaAdicionadas.add(marCodigos.get(j));
                                  }
                              }

                              margemUsada += " )";
                          }
                      }
                  } else if (CasamentoMargem.LATERAL.equals(tipoCasamento)) {
                      for (int i = 1; i < marCodigos.size(); i++) {
                          if (marCodigos.get(i).equals(marCodigo) && totalUsadoMap.containsKey(marCodigos.get(i)) && totalUsadoMap.containsKey(marCodigos.get(i-1))) {
                              margemUsada += (!TextHelper.isNull(margemUsada) ? " - " : "");
                              margemUsada += NumberHelper.format(totalUsadoMap.get(marCodigos.get(i)).doubleValue(), NumberHelper.getLang())
                                      + " + " + ApplicationResourcesHelper.getMessage("rotulo.campo.minimo.abreviado", responsavel).toUpperCase() + "(0, " + NumberHelper.format(totalRestanteMap.get(marCodigos.get(i-1)).doubleValue(), NumberHelper.getLang()) + ")";
                          }
                      }
                  }
              }
          }

          if (TextHelper.isNull(margemUsada)) {
              // Se a margem não for casada, a margem usada será apenar o valor total utilizado.
              margemUsada = NumberHelper.format(totalUsadoMap.get(marCodigo).doubleValue(), NumberHelper.getLang());
          }

          //coluna 04
          // Gera texto que representa o cálculo de margem restante. Se não for casada será apenar o valor folha subtraído do total utilizado
          final BigDecimal vlrMargemRestCalc = totalRestanteFinalMap.get(marCodigo);
          String descritivoMargemRest = "";

          // Verifica limitação das margens casadas menores com o valor das maiores
          if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM) || marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2) || marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
              // Limite as margens restantes quando forem casadas. As margens
              // casadas pela esquerda não precisam ser limitadas, pois já são abatidas
              // pelos valores das demais margens.
              if (margem1CasadaMargem3) {
                  if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                      descritivoMargemRest = " = " + ApplicationResourcesHelper.getMessage("rotulo.campo.minimo", responsavel).toUpperCase() + "(" + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + " , " + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + ")";
                  }
              } else if (margem123Casadas) {
                  if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                      descritivoMargemRest = " = " + ApplicationResourcesHelper.getMessage("rotulo.campo.minimo", responsavel).toUpperCase() + "(" + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + " , " + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM_2).doubleValue(), NumberHelper.getLang()) + ")";
                  } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                      descritivoMargemRest = " = " + ApplicationResourcesHelper.getMessage("rotulo.campo.minimo", responsavel).toUpperCase() + "(" + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM).doubleValue(), NumberHelper.getLang()) + " , " + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM_2).doubleValue(), NumberHelper.getLang()) + " , " + NumberHelper.format(totalRestanteMap.get(CodedValues.INCIDE_MARGEM_SIM_3).doubleValue(), NumberHelper.getLang()) + ")";
                  }
              }
          } else {
              for (final Short grupo : gruposCasamento) {
                  final String tipoCasamento = CasamentoMargem.getInstance().getTipoGrupo(grupo);
                  if (CasamentoMargem.DIREITA.equals(tipoCasamento) || CasamentoMargem.MINIMO.equals(tipoCasamento)) {
                      final List<Short> marCodigos = CasamentoMargem.getInstance().getMargensCasadas(grupo);
                      for (int i = 1; i < marCodigos.size(); i++) {
                          if (marCodigos.get(i).equals(marCodigo) && totalRestanteMap.containsKey(marCodigos.get(i))) {
                              descritivoMargemRest = " = " + ApplicationResourcesHelper.getMessage("rotulo.campo.minimo", responsavel).toUpperCase() + "(";
                              for (int j = 0; j < i; j++) {
                                  if (totalRestanteMap.containsKey(marCodigos.get(j))) {
                                      descritivoMargemRest += NumberHelper.format(totalRestanteMap.get(marCodigos.get(j)).doubleValue(), NumberHelper.getLang()) + " , ";
                                  }
                              }
                              descritivoMargemRest += NumberHelper.format(totalRestanteMap.get(marCodigos.get(i)).doubleValue(), NumberHelper.getLang()) + ")";
                          }
                      }
                  }
              }
          }
          final String marTipoVlr = (margemTO != null) && !TextHelper.isNull(margemTO.getMarTipoVlr()) ? margemTO.getMarTipoVlr().toString() : "";

          code.append("              <tr>");
          if (exibeMargem) {
              code.append("                <td>").append(TextHelper.forHtmlContent(margemTO.getMarCodigo())).append(" - ").append(TextHelper.forHtmlContent(rotuloMargem)).append("</td>");
              if (exibeContratosNaoIncideMargem) {
                  code.append("                <td>").append(TextHelper.forHtmlContent(NumberHelper.format(rseSalario.doubleValue(), NumberHelper.getLang()))).append("</td>");
                  code.append("                <td>").append(TextHelper.forHtmlContent(NumberHelper.format(margemTO.getMarPorcentagem().doubleValue(), NumberHelper.getLang()))).append("</td>");
              }
              code.append("                <td>").append(NumberHelper.format(margemTO.getMrsMargem().doubleValue(), NumberHelper.getLang())).append("</td>");
              code.append("                <td>- ").append(TextHelper.forHtmlContent(margemUsada)).append("</td>");
              code.append("                <td class='font-weight-bold'>").append(TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(marTipoVlr))).append(" ").append(NumberHelper.format(vlrMargemRestCalc.doubleValue(), NumberHelper.getLang())).append(TextHelper.forHtmlContent(descritivoMargemRest)).append("</td>");
          }
          code.append("              </tr>");
      }

      code.append("            </tbody>");
      code.append("            <tfoot>");
      code.append("              <tr>");
      code.append("                <td colspan='4'>" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.calculo.de.margem", responsavel) + "</td>");
      code.append("              </tr>");
      code.append("            </tfoot>");
      code.append("          </table>");
      code.append("        </div>");
      code.append("      </div>");

      return code.toString();
    }
}