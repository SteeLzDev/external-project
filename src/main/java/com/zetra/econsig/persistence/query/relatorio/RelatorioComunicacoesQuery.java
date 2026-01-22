package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioComunicacoesQuery</p>
 * <p>Description: Query para relatório de comunicacoes.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioComunicacoesQuery extends ReportHQuery {
    private static final short CMN_LIDAS_E_NAO_LIDAS = 2;

    private static final short CMN_COM_PENDENCIAS = 1;
    private static final short CMN_COM_E_SEM_PENDENCIAS = 2;

    public AcessoSistema responsavel;
    private String cmnCodigo;
    private String csaCodigo;
    private String rseMatricula;
    private String cpf;
    private String cmnCodigoPai;
    private String periodoIni;
    private String periodoFim;
    private List<String> orgCodigos;
    private String serCodigo;
    private Boolean pendente;
    private Boolean exibeSomenteCse;
    private Short cmnLida;
    private Long cmnNumero;
    private String ascCodigo;

    public boolean count = false;
    public boolean soCmnPai = false;

    @Override
    public void setCriterios (TransferObject criterio) {
        if (criterio != null) {
            final Boolean soCmnPai = (Boolean) criterio.getAttribute("APENAS_CMN_PAI");

            responsavel = (AcessoSistema) criterio.getAttribute("responsavel");
            cpf = (String) criterio.getAttribute(Columns.SER_CPF);
            rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
            csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            pendente = ((criterio.getAttribute(Columns.CMN_PENDENCIA) != null) && (Short.valueOf(criterio.getAttribute(Columns.CMN_PENDENCIA).toString()) != CMN_COM_E_SEM_PENDENCIAS)) ? Short.valueOf(criterio.getAttribute(Columns.CMN_PENDENCIA).toString()) == CMN_COM_PENDENCIAS : null;
            exibeSomenteCse = (criterio.getAttribute("exibeSomenteCse") != null) ? (Boolean) criterio.getAttribute("exibeSomenteCse") : Boolean.FALSE;
            periodoIni = (String) criterio.getAttribute("periodoIni");
            periodoFim = (String) criterio.getAttribute("periodoFim");
            orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
            ascCodigo = (String) criterio.getAttribute(Columns.CMN_ASC_CODIGO);
            serCodigo = (String) criterio.getAttribute(Columns.SER_CODIGO);
            this.soCmnPai = (soCmnPai != null) ? soCmnPai : false;
            cmnCodigoPai = (String) criterio.getAttribute(Columns.CMN_CODIGO_PAI);
            cmnCodigo = (String) criterio.getAttribute(Columns.CMN_CODIGO);
            cmnLida = (criterio.getAttribute("CMN_LIDA") != null) ? Short.valueOf((String) criterio.getAttribute("CMN_LIDA")):null;
            cmnNumero = (criterio.getAttribute(Columns.CMN_NUMERO) != null) ? Long.valueOf((String) criterio.getAttribute(Columns.CMN_NUMERO)):null;
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String usuCodigo = responsavel != null ? responsavel.getUsuCodigo() : "";
        String corpo = "";

        if (count) {
            corpo = "SELECT DISTINCT COUNT(*) AS TOTAL ";
        } else {
            corpo = "select " +
                        "cmn.cmnCodigo AS CMN_CODIGO, " +
                        "cmn.cmnNumero AS CMN_NUMERO, " +
                        "cmn.cmnData AS CMN_DATA, " +
                        "cmn.cmnTexto AS CMN_TEXTO, " +
                        "cmn.cmnPendencia AS CMN_PENDENCIA, " +
                        "cmn.comunicacaoPai.cmnCodigo AS CMN_CODIGO_PAI, " +
                        "cmn.cmnIpAcesso AS CMN_IP_ACESSO, " +
                        "cmn.usuario.usuLogin AS USU_LOGIN, " +
                        "cmn.usuario.usuNome AS USU_NOME, " +
                        "cmn.usuario.usuCodigo AS USU_CODIGO, " +
                        "consignante.cseCodigo AS CSE_CODIGO, " +
                        "consignataria.csaCodigo AS CSA_CODIGO, " +
                        "ser.serCodigo AS SER_CODIGO, " +
                        "rse.rseCodigo AS RSE_CODIGO, " +
                        "rse.rseMatricula AS RSE_MATRICULA, " +
                        "usuarioCsa.csaCodigo AS UCA_CSA_CODIGO, " +
                        "usuarioCse.cseCodigo AS UCE_CSE_CODIGO, " +
                        "usuarioCor.corCodigo AS UCO_COR_CODIGO, " +
                        "usuarioOrg.orgCodigo AS UOR_ORG_CODIGO, " +
                        "usuarioSer.serCodigo AS USE_SER_CODIGO, " +
                        "usuarioSup.cseCodigo AS USP_CSE_CODIGO, " +
                        "cmnCse.cmeDestinatario AS CME_DESTINATARIO, " +
                        "cmnOrg.cmoDestinatario AS CMO_DESTINATARIO, " +
                        "cmnCsa.cmcDestinatario AS CMC_DESTINATARIO, " +
                        "cmnSer.cmsDestinatario AS CMS_DESTINATARIO, ";

            final StringBuilder auxBuilder = new StringBuilder(corpo);

            // Destinatário

            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignante.cseCodigo");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_SIM).append("' then orgao.orgCodigo");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignataria.csaCodigo");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_SIM).append("' then ser.serCodigo end AS CODIGO_ENTIDADE_DESTINATARIO,");

            auxBuilder.append(" case");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'CSE'");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'ORG'");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'CSA'");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'SER' end AS TIPO_ENTIDADE_DESTINATARIO,");

            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignante.cseNome");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_SIM).append("' then orgao.orgNome");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignataria.csaNome");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_SIM).append("' then ser.serNome end AS NOME_ENTIDADE_DESTINATARIO,");

            // Remetente
            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignante.cseCodigo");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_NAO).append("' then orgao.orgCodigo");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignataria.csaCodigo");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_NAO).append("' then ser.serCodigo end AS CODIGO_ENTIDADE_REMETENTE,");

            auxBuilder.append(" case");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'CSE'");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'ORG'");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'CSA'");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'SER' end AS TIPO_ENTIDADE_REMETENTE,");

            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignante.cseNome");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_NAO).append("' then orgao.orgNome");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignataria.csaNome");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_NAO).append("' then ser.serNome end AS NOME_ENTIDADE_REMETENTE,");

            auxBuilder.append("(select count (lcu.cmnCodigo) from LeituraComunicacaoUsuario lcu where  lcu.cmnCodigo = cmn.cmnCodigo");
            auxBuilder.append(" and lcu.usuCodigo = '").append(usuCodigo).append("') as COUNT_LEITURAS ");
            corpo = auxBuilder.toString();
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM Comunicacao cmn");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoCseSet cmnCse");
        corpoBuilder.append(" LEFT OUTER JOIN cmnCse.consignante consignante");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoOrgSet cmnOrg");
        corpoBuilder.append(" LEFT OUTER JOIN cmnOrg.orgao orgao");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoCsaSet cmnCsa");
        corpoBuilder.append(" LEFT OUTER JOIN cmnCsa.consignataria consignataria");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoSerSet cmnSer");
        corpoBuilder.append(" LEFT OUTER JOIN cmnSer.servidor ser");
        corpoBuilder.append(" LEFT OUTER JOIN cmnSer.registroServidor rse");
        corpoBuilder.append(" LEFT OUTER JOIN rse.orgao org ");
        corpoBuilder.append(" LEFT OUTER JOIN org.estabelecimento est ");
        corpoBuilder.append(" LEFT OUTER JOIN rse.statusRegistroServidor srs");

        corpoBuilder.append(" INNER JOIN cmn.usuario usu");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");

        corpoBuilder.append(" WHERE 1 = 1");
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, cpf, false));

        if (!TextHelper.isNull(cmnCodigo)) {
            corpoBuilder.append(" AND cmn.cmnCodigo ").append(criaClausulaNomeada("cmnCodigo", cmnCodigo));
        }

        if (!TextHelper.isNull(cmnNumero)) {
            corpoBuilder.append(" AND cmn.cmnNumero ").append(criaClausulaNomeada("cmnNumero", cmnNumero));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cmnCsa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND rse.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(ascCodigo)) {
            corpoBuilder.append(" AND cmn.assuntoComunicacao.ascCodigo ").append(criaClausulaNomeada("ascCodigo", ascCodigo));
        }

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" AND ser.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (!TextHelper.isNull(cmnCodigoPai)) {
            corpoBuilder.append(" AND cmn.comunicacaoPai.cmnCodigo ").append(criaClausulaNomeada("cmnCodigoPai", cmnCodigoPai));
        } else if (soCmnPai) {
            corpoBuilder.append(" AND cmn.comunicacaoPai.cmnCodigo IS NULL ");
        }

        if (pendente != null) {
            corpoBuilder.append(" AND cmn.cmnPendencia ").append(criaClausulaNomeada("cmnPendencia", pendente));
        }

        if ((exibeSomenteCse != null) && exibeSomenteCse) {
            corpoBuilder.append(" AND (consignante.cseCodigo IS NOT NULL) ");
        }

        if (!TextHelper.isNull(cmnLida) && (cmnLida.shortValue() != CMN_LIDAS_E_NAO_LIDAS)) {
            corpoBuilder.append(" AND ");
            if (cmnLida.shortValue() == 0) {
                corpoBuilder.append(" NOT ");
            }
            corpoBuilder.append(" EXISTS (SELECT 1 FROM LeituraComunicacaoUsuario").append(" lcuWhere WHERE lcuWhere.cmnCodigo = cmn.cmnCodigo");
            corpoBuilder.append(" AND lcuWhere.usuCodigo = '").append(usuCodigo).append("')");
        }

        if (!TextHelper.isNull(periodoIni)) {
            try {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd") + " 00:00:00";
                corpoBuilder.append(" AND cmn.cmnData >= :periodoIni");
            } catch (final ParseException ex) {
                throw new HQueryException("mensagem.erro.data.inicio.informada.invalida.arg0", responsavel, periodoIni);
            }
        }
        if (!TextHelper.isNull(periodoFim)) {
            try {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd") + " 23:59:59";
                corpoBuilder.append(" AND cmn.cmnData <= :periodoFim");
            } catch (final ParseException ex) {
                throw new HQueryException("mensagem.erro.data.fim.informada.invalida.arg0", responsavel, periodoFim);
            }
        }

        if(!count) {
            corpoBuilder.append(" ORDER BY cmn.cmnData DESC ");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, cpf, false, query);

        if (!TextHelper.isNull(cmnCodigo)) {
            defineValorClausulaNomeada("cmnCodigo", cmnCodigo, query);
        }

        if (!TextHelper.isNull(cmnNumero)) {
            defineValorClausulaNomeada("cmnNumero", cmnNumero, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigos)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(ascCodigo)) {
            defineValorClausulaNomeada("ascCodigo", ascCodigo, query);
        }

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (!TextHelper.isNull(cmnCodigoPai)) {
            defineValorClausulaNomeada("cmnCodigoPai", cmnCodigoPai, query);
        }

        if (pendente != null) {
            defineValorClausulaNomeada("cmnPendencia", pendente, query);
        }

        if (!TextHelper.isNull(periodoIni)) {
            defineValorClausulaNomeada("periodoIni", parseDateTimeString(periodoIni), query);
        }

        if (!TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoFim", parseDateTimeString(periodoFim), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CMN_CODIGO,
                Columns.CMN_NUMERO,
                Columns.CMN_DATA,
                Columns.CMN_TEXTO,
                Columns.CMN_PENDENCIA,
                Columns.CMN_CODIGO_PAI,
                Columns.CMN_IP_ACESSO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.USU_CODIGO,
                Columns.CSE_CODIGO,
                Columns.CSA_CODIGO,
                Columns.SER_CODIGO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.CME_DESTINATARIO,
                Columns.CMO_DESTINATARIO,
                Columns.CMC_DESTINATARIO,
                Columns.CMS_DESTINATARIO,
                "CODIGO_ENTIDADE_DESTINATARIO",
                "TIPO_ENTIDADE_DESTINATARIO",
                "NOME_ENTIDADE_DESTINATARIO",
                "CODIGO_ENTIDADE_REMETENTE",
                "TIPO_ENTIDADE_REMETENTE",
                "NOME_ENTIDADE_REMETENTE",
                "COUNT_LEITURAS"
        };
    }
}
