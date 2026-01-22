package com.zetra.econsig.persistence.query.mensagem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMensagemCsaBloqueioQuery</p>
 * <p>Description: Lista as consignatárias que devem ser bloqueadas pela não confirmação de leitura da mensagem.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMensagemCsaBloqueioQuery extends HNativeQuery {

    private final AcessoSistema responsavel;

    public String csaCodigo;

    public ListaMensagemCsaBloqueioQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        Object objQtdeDiasBloqCsaNaoConfLeituraMsg = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQ_CSA_MENSAGEM_NAO_LIDA, responsavel);
        Integer qtdeDiasBloqCsaNaoConfLeituraMsg = !TextHelper.isNull(objQtdeDiasBloqCsaNaoConfLeituraMsg) ? Integer.parseInt(objQtdeDiasBloqCsaNaoConfLeituraMsg.toString()) : 0;

        String tpcSim = CodedValues.TPC_SIM;

        StringBuilder corpoBuilder = new StringBuilder();

        // Seleciona as mensagens que estão associadas à alguma consignatária
        corpoBuilder.append("select men.men_codigo, csa.csa_codigo, csa.csa_ativo, csa.nca_codigo ");
        corpoBuilder.append("from tb_mensagem men ");
        corpoBuilder.append("inner join tb_mensagem_csa mca on (men.men_codigo = mca.men_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csa on (csa.csa_codigo = mca.csa_codigo) ");
        corpoBuilder.append("left outer join tb_leitura_mensagem_usuario lmu on (lmu.men_codigo = men.men_codigo) ");
        corpoBuilder.append("left outer join tb_usuario_csa uca on (uca.csa_codigo = csa.csa_codigo and lmu.usu_codigo = uca.usu_codigo) ");
        corpoBuilder.append("where men_exibe_csa ").append(criaClausulaNomeada("tpcSim", tpcSim)).append(" ");
        corpoBuilder.append("and men_bloq_csa_sem_leitura ").append(criaClausulaNomeada("tpcSim", tpcSim)).append(" ");
        corpoBuilder.append("and lmu.men_codigo is null ");
        corpoBuilder.append("and (to_days(data_corrente()) - to_days(men.men_data)) >= :qtdeDiasBloqCsaNaoConfLeituraMsg ");

        // Verifica se existe alguma função associada à mensagem e se a consignatária possui algum usuário ou perfil relacionado a função
        corpoBuilder.append("and ( ");
        corpoBuilder.append("exists ( ");
        corpoBuilder.append("select * from tb_funcao_perfil_csa fca ");
        corpoBuilder.append("where fca.csa_codigo = csa.csa_codigo ");
        corpoBuilder.append("and (fca.fun_codigo = men.fun_codigo) ");
        corpoBuilder.append(") ");
        corpoBuilder.append("or exists ( ");
        corpoBuilder.append("select * ");
        corpoBuilder.append("from tb_perfil_csa pca ");
        corpoBuilder.append("inner join tb_funcao_perfil fca on (fca.per_codigo = pca.per_codigo) ");
        corpoBuilder.append("where pca.csa_codigo = csa.csa_codigo ");
        corpoBuilder.append("and (fca.fun_codigo = men.fun_codigo) ");
        corpoBuilder.append(") ");
        corpoBuilder.append("or men.fun_codigo is null ");
        corpoBuilder.append(") ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("and csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo)).append(" ");
        }

        corpoBuilder.append("union all ");

        /**
         * Seleciona as mensagens que não estão associadas à nenhuma consignatária mas exibe para o perfil consignatária,
         * sendo assim a mensagem está associada a todas as consignatárias.
         */
        corpoBuilder.append("select mensagem.men_codigo, consignataria.csa_codigo, consignataria.csa_ativo, consignataria.nca_codigo ");
        corpoBuilder.append("from tb_mensagem mensagem ");
        corpoBuilder.append("left outer join tb_mensagem_csa mca on (mensagem.men_codigo = mca.men_codigo) ");
        corpoBuilder.append("cross join tb_consignataria consignataria ");
        corpoBuilder.append("where men_exibe_csa ").append(criaClausulaNomeada("tpcSim", tpcSim)).append(" ");
        corpoBuilder.append("and men_bloq_csa_sem_leitura ").append(criaClausulaNomeada("tpcSim", tpcSim)).append(" ");
        corpoBuilder.append("and mca.men_codigo is null ");
        corpoBuilder.append("and (to_days(data_corrente()) - to_days(mensagem.men_data)) >= :qtdeDiasBloqCsaNaoConfLeituraMsg ");

        // Verifica se existe alguma função associada à mensagem e se a consignatária possui algum usuário ou perfil relacionado a função
        corpoBuilder.append("and ( ");
        corpoBuilder.append("exists ( ");
        corpoBuilder.append("select * from tb_funcao_perfil_csa fca ");
        corpoBuilder.append("where fca.csa_codigo = consignataria.csa_codigo ");
        corpoBuilder.append("and (fca.fun_codigo = mensagem.fun_codigo) ");
        corpoBuilder.append(") ");
        corpoBuilder.append("or exists ( ");
        corpoBuilder.append("select * ");
        corpoBuilder.append("from tb_perfil_csa pca ");
        corpoBuilder.append("inner join tb_funcao_perfil fca on (fca.per_codigo = pca.per_codigo) ");
        corpoBuilder.append("where pca.csa_codigo = consignataria.csa_codigo ");
        corpoBuilder.append("and (fca.fun_codigo = mensagem.fun_codigo) ");
        corpoBuilder.append(") ");
        corpoBuilder.append("or mensagem.fun_codigo is null ");
        corpoBuilder.append(") ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("and consignataria.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo)).append(" ");
        }

        // Certifica que somente mensagem não confirmada por nenhum usuário da consignatária será selecionada
        corpoBuilder.append("and not exists (select * ");
        corpoBuilder.append("from tb_mensagem men ");
        corpoBuilder.append("left outer join tb_mensagem_csa mca on (men.men_codigo = mca.men_codigo) ");
        corpoBuilder.append("inner join tb_leitura_mensagem_usuario lmu on (lmu.men_codigo = men.men_codigo) ");
        corpoBuilder.append("inner join tb_usuario_csa uca on (lmu.usu_codigo = uca.usu_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csa on (csa.csa_codigo = uca.csa_codigo) ");
        corpoBuilder.append("where men_exibe_csa ").append(criaClausulaNomeada("tpcSim", tpcSim)).append(" ");
        corpoBuilder.append("and men_bloq_csa_sem_leitura ").append(criaClausulaNomeada("tpcSim", tpcSim)).append(" ");
        corpoBuilder.append("and men.men_codigo = mensagem.men_codigo and consignataria.csa_codigo = csa.csa_codigo ");
        corpoBuilder.append(") ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("tpcSim", tpcSim, query);
        defineValorClausulaNomeada("qtdeDiasBloqCsaNaoConfLeituraMsg", qtdeDiasBloqCsaNaoConfLeituraMsg, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.MEN_CODIGO,
                Columns.CSA_CODIGO,
                Columns.CSA_ATIVO,
                Columns.NCA_CODIGO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
