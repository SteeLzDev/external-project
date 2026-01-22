package com.zetra.econsig.persistence.query.comunicacao;

import java.text.ParseException;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarComunicacoesQuery</p>
 * <p>Description: lista de registros de Comunicacao de acordo com filtros.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarComunicacoesQuery extends HQuery {

    private static final String CMN_LIDAS_E_NAO_LIDAS    = "2";
    private static final String CMN_COM_E_SEM_PENDENCIAS = "2";

    private static final String CMN_SEM_RELACAO_ADE = "0";

    private static final String CMN_COM_RELACAO_ADE = "1";

    private static final String CMN_COM_SEM_RELACAO_ADE = "2";

    private String rseMatricula;
    private String cpf;
    private String periodoIni;
    private String periodoFim;

    private String cmnCodigoPai;
    private String cmnCodigo;
    private String ascCodigo;
    private String csaCodigo;
    private String estCodigo;
    private String orgCodigo;
    private String serCodigo;

    private Boolean comPendencia;
    private Boolean exibeSomenteCse;
    private Boolean cmnLida;
    private Long cmnNumero;
    private boolean soCmnPai = false;
    private boolean listarBloqueioCsa = false;

    public AcessoSistema responsavel;
    public boolean count = false;

    private String adeCodigo;

    private String cmnRelacaoAde;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        boolean temClausulaUsuario = false;

        if (count) {
            corpo = "SELECT DISTINCT COUNT(*) AS TOTAL ";
        } else {
            corpo = "select " +
                    "cmn.cmnCodigo, " +
                    "cmn.cmnNumero, " +
                    "cmn.cmnData, " +
                    "cmn.cmnTexto, " +
                    "cmn.cmnPendencia, " +
                    "cmn.comunicacaoPai.cmnCodigo, " +
                    "cmn.cmnIpAcesso, " +
                    "cmn.usuario.usuLogin, " +
                    "cmn.usuario.usuNome, " +
                    "cmn.usuario.usuCodigo, " +
                    "consignante.cseCodigo, " +
                    "consignataria.csaCodigo, " +
                    "ser.serCodigo, " +
                    "rse.rseCodigo, " +
                    "rse.rseMatricula, " +
                    "usuarioCsa.csaCodigo, " +
                    "usuarioCse.cseCodigo, " +
                    "usuarioCor.corCodigo, " +
                    "usuarioOrg.orgCodigo, " +
                    "usuarioSer.serCodigo, " +
                    "usuarioSup.cseCodigo, " +
                    "cmnCse.cmeDestinatario, " +
                    "cmnCsa.cmcDestinatario, " +
                    "cmnOrg.cmoDestinatario, " +
                    "cmnSer.cmsDestinatario,";

            final StringBuilder auxBuilder = new StringBuilder(corpo);

            // Destinatário
            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignante.cseCodigo");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignataria.csaCodigo");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_SIM).append("' then orgao.orgCodigo");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_SIM).append("' then ser.serCodigo end AS CODIGO_ENTIDADE_DESTINATARIO,");

            auxBuilder.append(" case");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'CSE'");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'CSA'");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'ORG'");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_SIM).append("' then 'SER' end AS TIPO_ENTIDADE_DESTINATARIO,");

            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignante.cseNome");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_SIM).append("' then consignataria.csaNome");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_SIM).append("' then orgao.orgNome");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_SIM).append("' then ser.serNome end AS NOME_ENTIDADE_DESTINATARIO,");

            // Remetente
            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignante.cseCodigo");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignataria.csaCodigo");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_NAO).append("' then orgao.orgCodigo");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_NAO).append("' then ser.serCodigo end AS CODIGO_ENTIDADE_REMETENTE,");

            auxBuilder.append(" case");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'CSE'");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'CSA'");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'ORG'");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_NAO).append("' then 'SER' end AS TIPO_ENTIDADE_REMETENTE,");

            auxBuilder.append(" case ");
            auxBuilder.append(" when cmnCse.cmeDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignante.cseNome");
            auxBuilder.append(" when cmnCsa.cmcDestinatario = '").append(CodedValues.TPC_NAO).append("' then consignataria.csaNome");
            auxBuilder.append(" when cmnOrg.cmoDestinatario = '").append(CodedValues.TPC_NAO).append("' then orgao.orgNome");
            auxBuilder.append(" when cmnSer.cmsDestinatario = '").append(CodedValues.TPC_NAO).append("' then ser.serNome end AS NOME_ENTIDADE_REMETENTE,");

            auxBuilder.append("(select count (lcu.cmnCodigo) from LeituraComunicacaoUsuario lcu where  lcu.cmnCodigo = cmn.cmnCodigo and lcu.usuCodigo = :usuCodigo) as COUNT_LEITURAS,");
            auxBuilder.append("cmn.adeCodigo ");
            corpo = auxBuilder.toString();
            temClausulaUsuario = true;
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM Comunicacao cmn");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoCseSet cmnCse");
        corpoBuilder.append(" LEFT OUTER JOIN cmnCse.consignante consignante");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoCsaSet cmnCsa");
        corpoBuilder.append(" LEFT OUTER JOIN cmnCsa.consignataria consignataria");

        corpoBuilder.append(" LEFT OUTER JOIN cmn.comunicacaoOrgSet cmnOrg");
        corpoBuilder.append(" LEFT OUTER JOIN cmnOrg.orgao orgao");

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

        if (!TextHelper.isNull(estCodigo)) {
            if (!TextHelper.isNull(serCodigo)) {
                corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
            } else {
                corpoBuilder.append(" AND orgao.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
            }
        }

        if (!TextHelper.isNull(orgCodigo)) {
            if (!TextHelper.isNull(serCodigo)) {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            } else {
                corpoBuilder.append(" AND cmnOrg.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            }
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

        if (comPendencia != null) {
            corpoBuilder.append(" AND cmn.cmnPendencia ").append(criaClausulaNomeada("comPendencia", comPendencia));
        }

        if ((exibeSomenteCse != null) && exibeSomenteCse) {
            corpoBuilder.append(" AND (consignante.cseCodigo IS NOT NULL) ");
        }

        if (cmnLida != null) {
            corpoBuilder.append(" AND ").append(!cmnLida ? "NOT" : "");
            corpoBuilder.append(" EXISTS (SELECT 1 FROM LeituraComunicacaoUsuario lcuWhere WHERE lcuWhere.cmnCodigo = cmn.cmnCodigo AND lcuWhere.usuCodigo = :usuCodigo)");
            temClausulaUsuario = true;
        }

        if (!TextHelper.isNull(cmnRelacaoAde) && !CMN_COM_SEM_RELACAO_ADE.equals(cmnRelacaoAde)) {
            if (CMN_COM_RELACAO_ADE.equals(cmnRelacaoAde)) {
                corpoBuilder.append(" AND cmn.adeCodigo IS NOT NULL");
            } else if (CMN_SEM_RELACAO_ADE.equals(cmnRelacaoAde)) {
                corpoBuilder.append(" AND cmn.adeCodigo IS NULL");
            }
        }

        if (!TextHelper.isNull(periodoIni)) {
            try {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd") + " 00:00:00";
                corpoBuilder.append(" AND cmn.cmnData >= :periodoIni");
            } catch (final ParseException ex) {
                throw new HQueryException("mensagem.erro.data.inicio.parse.invalido", (AcessoSistema) null);
            }
        }
        if (!TextHelper.isNull(periodoFim)) {
            try {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd") + " 23:59:59";
                corpoBuilder.append(" AND cmn.cmnData <= :periodoFim");
            } catch (final ParseException ex) {
                throw new HQueryException("mensagem.erro.data.fim.parse.invalido", (AcessoSistema) null);
            }
        }

        if (listarBloqueioCsa) {
            final boolean usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CMN_PENDENTE, CodedValues.TPC_SIM, responsavel);

            int diasParaBloqueioCmnSer = 0;

            try {
                final Object paramValue = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_SER, responsavel);
                diasParaBloqueioCmnSer = TextHelper.isNum(paramValue) ? Integer.parseInt(paramValue.toString()) : 0;
            } catch (final NumberFormatException nex) {
                diasParaBloqueioCmnSer = 0;
            }

            int diasParaBloqueioCmnCseOrg = 0;

            try {
                final Object paramValue = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_CSE_ORG, responsavel);
                diasParaBloqueioCmnCseOrg = TextHelper.isNum(paramValue) ? Integer.parseInt(paramValue.toString()) : 0;
            } catch (final NumberFormatException nex) {
                diasParaBloqueioCmnCseOrg = 0;
            }

            // ATENÇÃO: É A MESMA CLÁUSULA DA ListaConsignatariaCmnPendenteAlemPrazoQuery
            // Comunicações originais geradas pelo usuário que ainda estão pendentes sem nenhuma resposta com prazo maior
            // que a data limite de bloqueio da CSA, ou aqueles que tem resposta pendente do usuário que criou a comunicação
            // original com prazo maior que a data limite de bloqueio da CSA
            if (!usaDiasUteis) {
                corpoBuilder.append(" AND to_days(current_date) - to_days(COALESCE((");
                corpoBuilder.append(" SELECT MAX(cmnFilho.cmnData) FROM Comunicacao cmnFilho ");
                corpoBuilder.append(" WHERE cmnFilho.comunicacaoPai.cmnCodigo = cmn.cmnCodigo and cmnFilho.usuario.usuCodigo = cmn.usuario.usuCodigo ");
                corpoBuilder.append("), cmn.cmnData)) >= ");
            } else {
                // se usa dias úteis, conta quantos tem entre a data da última comunicação pendente do usuário e a data corrente
                corpoBuilder.append(" AND (SELECT COUNT(*) FROM Calendario cal");
                corpoBuilder.append(" WHERE cal.calDiaUtil = '").append(CodedValues.TPC_SIM).append("'");
                corpoBuilder.append(" AND cal.calData BETWEEN to_date(COALESCE((");
                corpoBuilder.append(" SELECT MAX(cmnFilho.cmnData) FROM Comunicacao cmnFilho ");
                corpoBuilder.append(" WHERE cmnFilho.comunicacaoPai.cmnCodigo = cmn.cmnCodigo and cmnFilho.usuario.usuCodigo = cmn.usuario.usuCodigo ");
                corpoBuilder.append("), cmn.cmnData)) and data_corrente()) > ");
            }

            // Dias para bloqueio depende de qual parâmetro está habilitado
            corpoBuilder.append(" (CASE");
            corpoBuilder.append(" WHEN ser.serCodigo IS NOT NULL THEN ").append(diasParaBloqueioCmnSer);
            corpoBuilder.append(" WHEN consignante.cseCodigo IS NOT NULL THEN ").append(diasParaBloqueioCmnCseOrg);
            corpoBuilder.append(" WHEN orgao.orgCodigo IS NOT NULL THEN ").append(diasParaBloqueioCmnCseOrg);
            corpoBuilder.append(" ELSE 99999 END)");
        }

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND cmn.adeCodigo = :adeCodigo ");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY cmn.cmnData DESC ");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, cpf, false, query);

        if (temClausulaUsuario) {
            String usuCodigo = responsavel != null ? responsavel.getUsuCodigo() : null;
            if (TextHelper.isNull(usuCodigo)) {
                // Se por algum motivo usuário é nulo, define um valor inválido
                // para que a consulta não retorne valores.
                usuCodigo = UUID.randomUUID().toString();
            }
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(cmnCodigo)) {
            defineValorClausulaNomeada("cmnCodigo", cmnCodigo, query);
        }

        if (!TextHelper.isNull(cmnNumero)) {
            defineValorClausulaNomeada("cmnNumero", cmnNumero, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
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

        if (comPendencia != null) {
            defineValorClausulaNomeada("comPendencia", comPendencia, query);
        }

        if (!TextHelper.isNull(periodoIni)) {
            defineValorClausulaNomeada("periodoIni", parseDateTimeString(periodoIni), query);
        }

        if (!TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoFim", parseDateTimeString(periodoFim), query);
        }

        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        return query;
    }

    public void setCriterios(TransferObject criterio) {
        if (criterio != null) {
            csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            cpf = (String) criterio.getAttribute(Columns.SER_CPF);
            rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
            comPendencia = ((criterio.getAttribute(Columns.CMN_PENDENCIA) != null) && !CMN_COM_E_SEM_PENDENCIAS.equals(criterio.getAttribute(Columns.CMN_PENDENCIA))) ? "1".equals(criterio.getAttribute(Columns.CMN_PENDENCIA)) || criterio.getAttribute(Columns.CMN_PENDENCIA).equals(Boolean.TRUE) : null;
            exibeSomenteCse = (criterio.getAttribute("exibeSomenteCse") != null) ? "1".equals(criterio.getAttribute("exibeSomenteCse").toString()) : Boolean.FALSE;
            periodoIni = (String) criterio.getAttribute("periodoIni");
            periodoFim = (String) criterio.getAttribute("periodoFim");
            orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
            estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
            ascCodigo = (String) criterio.getAttribute(Columns.CMN_ASC_CODIGO);
            serCodigo = (String) criterio.getAttribute(Columns.SER_CODIGO);
            soCmnPai = (criterio.getAttribute("APENAS_CMN_PAI") != null) ? ((Boolean) criterio.getAttribute("APENAS_CMN_PAI")) : false;
            cmnCodigoPai = (String) criterio.getAttribute(Columns.CMN_CODIGO_PAI);
            cmnCodigo = (String) criterio.getAttribute(Columns.CMN_CODIGO);
            cmnLida = ((criterio.getAttribute("CMN_LIDA") != null) && !CMN_LIDAS_E_NAO_LIDAS.equals(criterio.getAttribute("CMN_LIDA"))) ? "1".equals(criterio.getAttribute("CMN_LIDA")) : null;
            cmnNumero = (criterio.getAttribute(Columns.CMN_NUMERO) != null) ? Long.valueOf((String) criterio.getAttribute(Columns.CMN_NUMERO)) : null;
            listarBloqueioCsa = (criterio.getAttribute("BLOQUEIO_CSA") != null) ? ((Boolean) criterio.getAttribute("BLOQUEIO_CSA")) : false;
            adeCodigo = (String) criterio.getAttribute(Columns.CMN_ADE_CODIGO);
            cmnRelacaoAde = (String) criterio.getAttribute("CMN_RELACAO_ADE");
        }
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
                Columns.CMC_DESTINATARIO,
                Columns.CMO_DESTINATARIO,
                Columns.CMS_DESTINATARIO,
                "CODIGO_ENTIDADE_DESTINATARIO",
                "TIPO_ENTIDADE_DESTINATARIO",
                "NOME_ENTIDADE_DESTINATARIO",
                "CODIGO_ENTIDADE_REMETENTE",
                "TIPO_ENTIDADE_REMETENTE",
                "NOME_ENTIDADE_REMETENTE",
                "COUNT_LEITURAS",
                Columns.CMN_ADE_CODIGO
        };
    }
}
