package com.zetra.econsig.persistence.query.relatorio;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioTermoAdesaoNaoAutorizadoQuery</p>
 * <p> Description: Relatório de termo de adesão não autorizado
 * pelo parâmetro de serviço 135.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26263 $
 * $Date: 2019-02-19 18:35:15 -0300 (ter, 19 fev 2019) $
 */
public class RelatorioTermoAdesaoNaoAutorizadoQuery extends ReportHQuery {

    public boolean aceiteWeb;
    public boolean aceiteMobile;
    public Date periodoIni;
    public Date periodoFim;
    public boolean cse;
    public boolean org;
    public boolean csa;
    public boolean cor;
    public boolean ser;
    public boolean sup;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {

        if (criterio != null) {
            periodoIni = (Date) criterio.getAttribute("periodoIni");
            periodoFim = (Date) criterio.getAttribute("periodoFim");
            aceiteWeb = (boolean) criterio.getAttribute("aceiteWeb");
            aceiteMobile = (boolean) criterio.getAttribute("aceiteMobile");
            cse = (boolean) criterio.getAttribute("cse");
            org = (boolean) criterio.getAttribute("org");
            csa = (boolean) criterio.getAttribute("csa");
            cor = (boolean) criterio.getAttribute("cor");
            ser = (boolean) criterio.getAttribute("ser");
            sup = (boolean) criterio.getAttribute("sup");
        }

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select");
        corpoBuilder.append(" usu.usuNome as usu_nome,");
        corpoBuilder.append(" usu.usuLogin as usu_login,");
        corpoBuilder.append(" usu.usuEmail as usu_email,");
        corpoBuilder.append(" usu.usuTel as usu_tel,");
        corpoBuilder.append(" usu.usuCpf as usu_cpf,");
        corpoBuilder.append(" stu.stuDescricao as stu_descricao,");
        corpoBuilder.append(" case");
        corpoBuilder.append(" when usuarioCse.cseCodigo is not null then cse.cseNome");
        corpoBuilder.append(" when usuarioOrg.orgCodigo is not null then org.orgNome");
        corpoBuilder.append(" when usuarioCsa.csaCodigo is not null then csa.csaNome");
        corpoBuilder.append(" when usuarioCor.corCodigo is not null then cor.corNome");
        corpoBuilder.append(" when usuarioSer.serCodigo is not null then 'Servidor'");
        corpoBuilder.append(" when usuarioSup.cseCodigo is not null then 'Suporte'");
        corpoBuilder.append(" end AS ENTIDADE,");
        corpoBuilder.append(" to_locale_datetime(ltu.ltuData) as ltu_data,");
        corpoBuilder.append(" ltu.ltuIpAcesso as ltu_ip_acesso,");
        corpoBuilder.append(" tad.tadTitulo AS DOCUMENTO_ACEITO,");
        corpoBuilder.append(" case");
        if (aceiteMobile && aceiteWeb) {
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.WEB.getCodigo()).append("' then 'WEB'");
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.REST.getCodigo()).append("' then 'MOBILE'");
        } else if (!aceiteMobile && aceiteWeb) {
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.WEB.getCodigo()).append("' then 'WEB'");
        } else {
            corpoBuilder.append(" when ltu.ltuCanal = '").append(CanalEnum.REST.getCodigo()).append("' then 'MOBILE'");
        }
        corpoBuilder.append(" end AS ACEITACAO_VIA");
        corpoBuilder.append(" from Usuario usu");
        corpoBuilder.append(" inner join usu.statusLogin stu");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup");

        corpoBuilder.append(" LEFT JOIN usuarioCsa.consignataria csa");
        corpoBuilder.append(" LEFT JOIN usuarioCse.consignante cse");
        corpoBuilder.append(" LEFT JOIN usuarioCor.correspondente cor");
        corpoBuilder.append(" LEFT JOIN usuarioOrg.orgao org");
        
        corpoBuilder.append(" INNER JOIN usu.termoAdesaoSet tad");
        corpoBuilder.append(" INNER JOIN tad.leituraTermoUsuarioSet ltu");

        corpoBuilder.append(" where 1=1");
        corpoBuilder.append(" and ltu.ltuTermoAceito = '").append(CodedValues.CAS_NAO).append("' ");
        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" and ltu.ltuData between :periodoIni and :periodoFim");
        }

        if (!cse || !org || !csa || !cor || !ser || !sup) {
            corpoBuilder.append(" and ( 1 = 2 ");
            if (csa) {
                corpoBuilder.append(" or usuarioCsa.csaCodigo is not null");
            }
            if (cor) {
                corpoBuilder.append(" or usuarioCor.corCodigo is not null");
            }
            if (ser) {
                corpoBuilder.append(" or usuarioSer.serCodigo is not null");
            }
            if (cse) {
                corpoBuilder.append(" or usuarioCse.cseCodigo is not null");
            }
            if (sup) {
                corpoBuilder.append(" or usuarioSup.cseCodigo is not null");
            }
            if (org) {
                corpoBuilder.append(" or usuarioOrg.orgCodigo is not null");
            }
            corpoBuilder.append(" )");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoIni", periodoIni, query);
            defineValorClausulaNomeada("periodoFim", periodoFim, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.USU_NOME,
                Columns.USU_LOGIN,
                Columns.USU_EMAIL,
                Columns.USU_TEL,
                Columns.USU_CPF,
                Columns.STU_DESCRICAO,
                "ENTIDADE",
                Columns.LTU_DATA,
                Columns.LTU_IP_ACESSO,
                "ACEITACAO_VIA",
                "DOCUMENTO_ACEITO"
        };
    }
}