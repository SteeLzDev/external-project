package com.zetra.econsig.persistence.query.relatorio;

import java.awt.Color;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.coeficiente.ListaServicoPrazoAtivoQuery;
import com.zetra.econsig.report.reports.ReportColumn;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.Columns;

import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.base.JRBaseLineBox;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.type.HorizontalTextAlignEnum;
import net.sf.jasperreports.engine.type.VerticalTextAlignEnum;

/**
 * <p>Title: RelatorioTaxasQuery</p>
 * <p>Description: Consulta de relatório de conferência de ranking de taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioTaxasQuery extends ReportHNativeQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioTaxasQuery.class);

    public Date dataTaxa;
    public String order;
    public String svcCodigo;
    public String prazoInicial;
    public String prazoFinal;
    public String prazoOrdenacao;
    public String prazosInformados;
    public String[] prazosInformadosList;
    public boolean prazoMultiploDoze = false;
    public int filtro;
    private boolean apenasTaxasAtivas = false;
    public String csaAtivo;

    private final ArrayList<String> fieldList = new ArrayList<>();

    public RelatorioTaxasQuery(int filtro) {
        this.filtro = filtro;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        order = (String) criterio.getAttribute("ordenacao");
        svcCodigo = (String) criterio.getAttribute("svcCodigo");
        dataTaxa = (Date) criterio.getAttribute("dataTaxa");
        prazoInicial = (String) criterio.getAttribute("prazoInicial");
        prazoFinal   = (String) criterio.getAttribute("prazoFinal");
        prazoOrdenacao = (order != null) && !"CSA".equals(order) ? order : null;
        prazoMultiploDoze = (Boolean) criterio.getAttribute("prazoMultiploDoze");
        prazosInformados = (String) criterio.getAttribute("prazosInformados");

        final String[] csaAtivoParam = (String[]) criterio.getAttribute("CSA_ATIVO");
        if ((csaAtivoParam != null) && (csaAtivoParam.length == 1)) {
            final String ativo = csaAtivoParam[0];
            csaAtivo = "1".equalsIgnoreCase(ativo) ? "1" : "0";
        }

        // Verifica se a data da taxa é maior ou igual à data atual.
        // Em caso positivo, trata-se de consulta apenas às taxas ativas.
        // Utilizado para otimizar a consulta, já que o union não é necessário nesse caso.
        if (dataTaxa != null) {
            Date dataAtual = null;
            final String strDataAtual = DateHelper.toDateString(DateHelper.getSystemDatetime());
            try {
                dataAtual = DateHelper.parse(strDataAtual, LocaleHelper.getDatePattern());
            } catch (final ParseException ex) {
                LOG.debug("Erro no formato da data da taxa.", ex);
            }

            if (dataTaxa.equals(dataAtual) || dataTaxa.after(dataAtual)) {
                apenasTaxasAtivas = true;
            }
        } else {
            apenasTaxasAtivas = true;
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Define os campos básicos do relatório
        fieldList.clear();
        fieldList.add("CONSIGNATARIA");

        // Pega os prazos do serviço
        final ListaServicoPrazoAtivoQuery queryPrz = new ListaServicoPrazoAtivoQuery();
        queryPrz.svcCodigo = svcCodigo;
        queryPrz.prazo = true;
        queryPrz.prazoInicial = prazoInicial;
        queryPrz.prazoFinal = prazoFinal;
        queryPrz.prazoOrdenacao = prazoOrdenacao;
        queryPrz.prazoMultiploDoze = prazoMultiploDoze;
        queryPrz.prazosInformados = prazosInformados;

        final List<TransferObject> prazos = queryPrz.executarDTO(session, null);

        // Campos das colunas com as taxas.
        final List<ReportColumn> reportColumns = new ArrayList<>();
        String fieldName;
        final Class<?> fieldClass = BigDecimal.class;
        int x = 221;
        final int width = 33;
        final int widthTaxaPraticada = 60;

        final float lineWidth = 0.5f;

        final JRLineBox box = new JRBaseLineBox(null);
        box.getTopPen().setLineWidth(lineWidth);
        box.getBottomPen().setLineWidth(lineWidth);
        box.getLeftPen().setLineWidth(lineWidth);
        box.getRightPen().setLineWidth(lineWidth);
        final JRDesignStyle styleCelula = new JRDesignStyle();
        styleCelula.setName(ReportManager.STYLE_CELULA);

        final JRLineBox titleBox = new JRBaseLineBox(null);
        titleBox.getTopPen().setLineWidth(lineWidth);
        titleBox.getBottomPen().setLineWidth(lineWidth);
        titleBox.getLeftPen().setLineWidth(lineWidth);
        titleBox.getRightPen().setLineWidth(lineWidth);
        final Color titleBackcolor = new Color(204, 204, 204);

        String fieldEfetivo;

        String consignataria = null;

        switch (filtro) {
            case 1:
                consignataria = " concatenar(concatenar(" + Columns.getColumnName(Columns.CSA_IDENTIFICADOR) + ", ' - '), CASE WHEN nullif(trim(" + Columns.getColumnName(Columns.CSA_NOME_ABREV) + "), '') is null THEN " + Columns.getColumnName(Columns.CSA_NOME) + " ELSE " + Columns.getColumnName(Columns.CSA_NOME_ABREV) + " END)";
                break;
            case 2:
                consignataria = "'" + ApplicationResourcesHelper.getMessage("rotulo.relatorio.ranking.taxas.media", null).toUpperCase() + "'";
                break;
            case 3:
                consignataria = "'" + ApplicationResourcesHelper.getMessage("rotulo.relatorio.ranking.taxas.minimo", null).toUpperCase() + "'";
                break;
            case 4:
                consignataria = "'" + ApplicationResourcesHelper.getMessage("rotulo.relatorio.ranking.taxas.maximo", null).toUpperCase() + "'";
                break;
            default:
                break;
        }

        final StringBuilder fields = new StringBuilder();
        fields.append(consignataria).append(" AS CONSIGNATARIA");

        final Iterator<TransferObject> ite = prazos.iterator();
        while (ite.hasNext()) {
            final String prazo = ite.next().getAttribute(Columns.PRZ_VLR).toString();
            fieldName = "cft_vlr" + prazo;
            fieldList.add(fieldName);

            fieldEfetivo = "cft_vlr_efetivo" + prazo;
            fieldList.add(fieldEfetivo);

            switch (filtro) {
                case 1:
                    fields.append(", COALESCE(SUM(").append(Columns.getColumnName(Columns.CFT_VLR)).append(" * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append(")))), 0.00) AS ").append(fieldName);

                    fields.append(", COALESCE(to_decimal(AVG(NULLIF((select AVG(cft1.cft_vlr * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append(")))) ");
                    fields.append(" from ").append(Columns.TB_COEFICIENTE).append(" cft1 ");
                    fields.append(" inner join ").append(Columns.TB_COEFICIENTE_DESCONTO).append(" cde on (cft1.cft_codigo = cde.cft_codigo) ");
                    fields.append(" where cft1.prz_csa_codigo = FROM_VIRTUAL.prz_csa_codigo ");
                    fields.append("   and cft1.cft_data_ini_vig >= FROM_VIRTUAL.cft_data_ini_vig ");
                    fields.append("   and (cft1.cft_data_ini_vig < FROM_VIRTUAL.cft_data_fim_vig or FROM_VIRTUAL.cft_data_fim_vig is null) ");
                    fields.append(" ), 0.00)), 13, 2), 0.00) AS ").append(fieldEfetivo);

                    break;
                case 2:
                    fields.append(", COALESCE(to_decimal(AVG(NULLIF(").append(Columns.getColumnName(Columns.CFT_VLR)).append(" * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append("))), 0.00)), 13, 2), 0.00) AS ").append(fieldName);

                    fields.append(", COALESCE(to_decimal(AVG(NULLIF((select AVG(cft1.cft_vlr * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append(")))) ");
                    fields.append(" from ").append(Columns.TB_COEFICIENTE).append(" cft1 ");
                    fields.append(" inner join ").append(Columns.TB_COEFICIENTE_DESCONTO).append(" cde on (cft1.cft_codigo = cde.cft_codigo) ");
                    fields.append(" where cft1.prz_csa_codigo = FROM_VIRTUAL.prz_csa_codigo ");
                    fields.append("   and cft1.cft_data_ini_vig >= FROM_VIRTUAL.cft_data_ini_vig ");
                    fields.append("   and (cft1.cft_data_ini_vig < FROM_VIRTUAL.cft_data_fim_vig or FROM_VIRTUAL.cft_data_fim_vig is null) ");
                    fields.append(" ), 0.00)), 13, 2), 0.00) AS ").append(fieldEfetivo);

                    break;
                case 3:
                    fields.append(", COALESCE(MIN(NULLIF(").append(Columns.getColumnName(Columns.CFT_VLR)).append(" * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append("))), 0.00)), 0.00) AS ").append(fieldName);

                    fields.append(", COALESCE(MIN(NULLIF((select MIN(cft1.cft_vlr * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append(")))) ");
                    fields.append(" from ").append(Columns.TB_COEFICIENTE).append(" cft1 ");
                    fields.append(" inner join ").append(Columns.TB_COEFICIENTE_DESCONTO).append(" cde on (cft1.cft_codigo = cde.cft_codigo) ");
                    fields.append(" where cft1.prz_csa_codigo = FROM_VIRTUAL.prz_csa_codigo ");
                    fields.append("   and cft1.cft_data_ini_vig >= FROM_VIRTUAL.cft_data_ini_vig ");
                    fields.append("   and (cft1.cft_data_ini_vig < FROM_VIRTUAL.cft_data_fim_vig or FROM_VIRTUAL.cft_data_fim_vig is null) ");
                    fields.append(" ), 0.00)), 0.00) AS ").append(fieldEfetivo);

                    break;
                case 4:
                    fields.append(", COALESCE(MAX(NULLIF(").append(Columns.getColumnName(Columns.CFT_VLR)).append(" * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append("))), 0.00)), 0.00) AS ").append(fieldName);

                    fields.append(", COALESCE(MAX(NULLIF((select MAX(cft1.cft_vlr * (1-abs(sign(").append(Columns.getColumnName(Columns.PRZ_VLR)).append(" - ").append(prazo).append(")))) ");
                    fields.append(" from ").append(Columns.TB_COEFICIENTE).append(" cft1 ");
                    fields.append(" inner join ").append(Columns.TB_COEFICIENTE_DESCONTO).append(" cde on (cft1.cft_codigo = cde.cft_codigo) ");
                    fields.append(" where cft1.prz_csa_codigo = FROM_VIRTUAL.prz_csa_codigo ");
                    fields.append("   and cft1.cft_data_ini_vig >= FROM_VIRTUAL.cft_data_ini_vig ");
                    fields.append("   and (cft1.cft_data_ini_vig < FROM_VIRTUAL.cft_data_fim_vig or FROM_VIRTUAL.cft_data_fim_vig is null) ");
                    fields.append(" ), 0.00)), 0.00) AS ").append(fieldEfetivo);

                    break;
                default:
                    break;
            }

            ReportColumn column = new ReportColumn();
            column.setBandName(ReportManager.BAND_DETAIL);
            column.setFieldName(fieldName);
            column.setFieldClass(fieldClass);
            column.setPattern("0.00");
            column.setStyle(styleCelula);
            column.setBackcolor(prazo.equals(prazoOrdenacao) ? new Color(230, 230, 230) : null);
            column.setHorizontalAlignment(HorizontalTextAlignEnum.RIGHT);
            column.setVerticalAlignment(VerticalTextAlignEnum.MIDDLE);
            column.setBox(box);
            column.setX(x);
            column.setY(0);
            column.setWidth(width);
            column.setHeigth(17);
            column.setTitleElementKey(prazo);
            column.setTitleBandName(ReportManager.BAND_COLUMNHEADER);
            column.setTitle(prazo);
            column.setTitleBackcolor(titleBackcolor);
            column.setTitleHorizontalAlignment(HorizontalTextAlignEnum.CENTER);
            column.setTitleVerticalAlignment(VerticalTextAlignEnum.MIDDLE);
            column.setTitleBox(titleBox);
            column.setTitleX(x);
            column.setTitleY(0);
            column.setTitleWidth(width);
            column.setTitleHeigth(17);
            reportColumns.add(column);

            x += width;

            column = new ReportColumn();
            column.setBandName(ReportManager.BAND_DETAIL);
            column.setFieldName(fieldEfetivo);
            column.setFieldClass(fieldClass);
            column.setPattern("0.00");
            column.setStyle(styleCelula);
            column.setBackcolor(prazo.equals(prazoOrdenacao) ? new Color(230, 230, 230) : null);
            column.setHorizontalAlignment(HorizontalTextAlignEnum.RIGHT);
            column.setVerticalAlignment(VerticalTextAlignEnum.MIDDLE);
            column.setBox(box);
            column.setX(x);
            column.setY(0);
            column.setWidth(widthTaxaPraticada);
            column.setHeigth(17);
            column.setTitleElementKey(fieldEfetivo);
            column.setTitleBandName(ReportManager.BAND_COLUMNHEADER);
            column.setTitle(ApplicationResourcesHelper.getMessage("rotulo.relatorio.ranking.taxas.media.praticada", null));
            column.setTitleBackcolor(titleBackcolor);
            column.setTitleHorizontalAlignment(HorizontalTextAlignEnum.CENTER);
            column.setTitleVerticalAlignment(VerticalTextAlignEnum.MIDDLE);
            column.setTitleBox(titleBox);
            column.setTitleX(x);
            column.setTitleY(0);
            column.setTitleWidth(widthTaxaPraticada);
            column.setTitleHeigth(17);
            reportColumns.add(column);

            x += widthTaxaPraticada;
        }
        getReportTemplate().getParameters().put(ReportManager.KEY_NAME_COLUMNS, reportColumns);
        getReportTemplate().getParameters().put(ReportManager.KEY_NAME_COLUMNS_EXPAND_WIDTH, false);

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ").append(fields.toString()).append(" FROM ( ");

        if (apenasTaxasAtivas) {
            corpoBuilder.append(getCorpoQueryRelatorio(fields.toString(), Columns.TB_COEFICIENTE_ATIVO));
        } else {
            corpoBuilder.append(getCorpoQueryRelatorio(fields.toString(), Columns.TB_COEFICIENTE_ATIVO));
            corpoBuilder.append(" UNION ");
            corpoBuilder.append(getCorpoQueryRelatorio(fields.toString(), Columns.TB_COEFICIENTE));
        }
        corpoBuilder.append(") FROM_VIRTUAL ");
        if (filtro == 1) {
            corpoBuilder.append("GROUP BY ").append(consignataria);
        }

        corpoBuilder.append(" ORDER BY ");
        final int index = fieldList.indexOf("cft_vlr" + order);
        if (index >= 0) {
            corpoBuilder.append(index + 1).append(", ");
        }
        corpoBuilder.append("1");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (dataTaxa != null) {
            defineValorClausulaNomeada("cftDataIniVig", dataTaxa, query);
            defineValorClausulaNomeada("cftDataFimVig", dataTaxa, query);
        }
        if (!TextHelper.isNull(prazosInformados)) {
            defineValorClausulaNomeada("prazosInformados", prazosInformados.split(","), query);
        }
        if (!TextHelper.isNull(prazoInicial) && !TextHelper.isNull(prazoFinal)) {
            defineValorClausulaNomeada("prazoInicial", prazoInicial, query);
            defineValorClausulaNomeada("prazoFinal", prazoFinal, query);

            if (!TextHelper.isNull(prazoOrdenacao)) {
                defineValorClausulaNomeada("prazoOrdenacao", prazoOrdenacao, query);
            }
        }
        if (!TextHelper.isNull(csaAtivo)) {
            defineValorClausulaNomeada("csaAtivo", csaAtivo, query);
        }

        return query;
    }

    /**
     * Monta o corpo da query de acordo com qual tabela de taxas utilizar.
     * @param campos
     * @param tabelaTaxas
     * @return
     */
    private String getCorpoQueryRelatorio(String campos, String tabelaTaxas) {
        String colunaCftPrzCsaCodigo = Columns.CFT_PRZ_CSA_CODIGO;
        String campoCftDataIniVig = Columns.CFT_DATA_INI_VIG;
        String campoCftDataFimVig = Columns.CFT_DATA_FIM_VIG;
        String campoCftVlr = Columns.CFT_VLR;

        if (Columns.TB_COEFICIENTE_ATIVO.equals(tabelaTaxas)) {
            colunaCftPrzCsaCodigo = Columns.CFA_PRZ_CSA_CODIGO;
            campoCftDataIniVig = Columns.CFA_DATA_INI_VIG;
            campoCftDataFimVig = Columns.CFA_DATA_FIM_VIG;
            campoCftVlr = Columns.CFA_VLR;
        }

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        if (filtro == 1) {
            corpoBuilder.append(Columns.CSA_IDENTIFICADOR).append(", ");
            corpoBuilder.append(Columns.CSA_NOME).append(", ");
            corpoBuilder.append(Columns.CSA_NOME_ABREV).append(", ");
            corpoBuilder.append(Columns.CSA_CODIGO).append(", ");
        }

        corpoBuilder.append(campoCftVlr).append(", ").append(Columns.PRZ_VLR);

        corpoBuilder.append(", ").append(colunaCftPrzCsaCodigo);
        corpoBuilder.append(", ").append(campoCftDataIniVig);
        corpoBuilder.append(", ").append(campoCftDataFimVig);

        corpoBuilder.append(" FROM ").append(Columns.TB_PRAZO);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_PRAZO_CONSIGNATARIA);
        corpoBuilder.append("   ON ").append(Columns.PZC_PRZ_CODIGO).append(" = ").append(Columns.PRZ_CODIGO);
        corpoBuilder.append(" INNER JOIN ").append(tabelaTaxas);
        corpoBuilder.append("   ON ").append(colunaCftPrzCsaCodigo).append(" = ").append(Columns.PZC_CODIGO);
        corpoBuilder.append(" INNER JOIN ").append(Columns.TB_CONSIGNATARIA);
        corpoBuilder.append("   ON ").append(Columns.CSA_CODIGO).append(" = ").append(Columns.PZC_CSA_CODIGO);

        corpoBuilder.append(" WHERE ").append(Columns.PRZ_SVC_CODIGO).append(criaClausulaNomeada("svcCodigo", svcCodigo));
        if (!TextHelper.isNull(csaAtivo)) {
            corpoBuilder.append(" AND  ").append(Columns.TB_CONSIGNATARIA).append(".csa_ativo ").append(criaClausulaNomeada("csaAtivo", csaAtivo));
        }

        if (dataTaxa != null) {
            // Buscando as taxas de juros que estavam ativas ou que estarão ativas em um intevalo de tempo
            corpoBuilder.append(" AND ").append(campoCftDataIniVig).append(" <= :cftDataIniVig");
            corpoBuilder.append(" AND (").append(campoCftDataFimVig).append(" >= :cftDataFimVig OR ").append(campoCftDataFimVig).append(" IS NULL)");
        } else {
            // Buscando as taxas de juros que estão ativas no momento
            corpoBuilder.append(" AND to_days(").append(campoCftDataIniVig).append(") <= to_days(data_corrente())");
            corpoBuilder.append(" AND (to_days(").append(campoCftDataFimVig).append(") >= to_days(data_corrente()) OR ").append(campoCftDataFimVig).append(" IS NULL)");
        }

        if (!TextHelper.isNull(prazoInicial) && !TextHelper.isNull(prazoFinal)) {
            corpoBuilder.append(" AND ((").append(Columns.PRZ_VLR).append(" >= :prazoInicial");
            corpoBuilder.append("        AND ").append(Columns.PRZ_VLR).append(" <= :prazoFinal)");

            if (!TextHelper.isNull(prazoOrdenacao)) {
                corpoBuilder.append(" OR ").append(Columns.PRZ_VLR).append(" = :prazoOrdenacao");
            }

            corpoBuilder.append("     )");
        }

        if (prazoMultiploDoze) {
            corpoBuilder.append(" AND MOD(").append(Columns.PRZ_VLR).append(", 12) = 0 ");
        }

        if (!TextHelper.isNull(prazosInformados)) {
            corpoBuilder.append(" AND ").append(Columns.PRZ_VLR).append(criaClausulaNomeada("prazosInformados", prazosInformados.split(",")));
        }


        corpoBuilder.append(" GROUP BY ");
        if (filtro > 1) {
            corpoBuilder.append(Columns.PRZ_SVC_CODIGO).append(", ");
        } else {
            corpoBuilder.append(Columns.CSA_IDENTIFICADOR).append(", ");
            corpoBuilder.append(Columns.CSA_NOME).append(", ");
            corpoBuilder.append(Columns.CSA_NOME_ABREV).append(", ");
            corpoBuilder.append(Columns.CSA_CODIGO).append(", ");
        }
        corpoBuilder.append(campoCftVlr).append(", ").append(Columns.PRZ_VLR);
        corpoBuilder.append(", ").append(colunaCftPrzCsaCodigo);
        corpoBuilder.append(", ").append(campoCftDataIniVig);
        corpoBuilder.append(", ").append(campoCftDataFimVig);

        return corpoBuilder.toString();
    }

    @Override
    protected String[] getFields() {
        return fieldList.toArray(new String[]{});
    }
}
