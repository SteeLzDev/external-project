package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ServidorHome</p>
 * <p>Description: Classe Home para a entidade HtServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HtServidorHome extends AbstractEntityHome {

    public static HtServidor findByPrimaryKey(String serCodigo) throws FindException {
        HtServidor servidor = new HtServidor();
        servidor.setSerCodigo(serCodigo);
        return find(servidor, serCodigo);
    }

    public static HtServidor findByRseCodigo(String rseCodigo) throws FindException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ser FROM HtRegistroServidor rse ");
        query.append("INNER JOIN rse.servidor ser ");
        query.append("WHERE rse.rseCodigo = :rseCodigo ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        List<HtServidor> result = findByQuery(query.toString(), parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<HtServidor> findByCPF(String serCpf) throws FindException {
        String query = "FROM HtServidor ser WHERE ser.serCpf = :serCpf";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCpf", serCpf);

        return findByQuery(query, parameters);
    }

    public static HtServidor create(Servidor servidor) throws CreateException {
        Session session = SessionUtil.getSession();
        HtServidor bean = new HtServidor();

        try {
            bean.setSerCodigo(servidor.getSerCodigo());
            bean.setSerCpf(servidor.getSerCpf());
            bean.setSerDataNasc(servidor.getSerDataNasc());
            bean.setSerNomeMae(servidor.getSerNomeMae());
            bean.setSerNomePai(servidor.getSerNomePai());
            bean.setSerNome(servidor.getSerNome());
            bean.setSerPrimeiroNome(servidor.getSerPrimeiroNome());
            bean.setSerNomeMeio(servidor.getSerNomeMeio());
            bean.setSerUltimoNome(servidor.getSerUltimoNome());
            bean.setSerTitulacao(servidor.getSerTitulacao());
            bean.setSerSexo(servidor.getSerSexo());
            bean.setSerEstCivil(servidor.getSerEstCivil());
            bean.setSerQtdFilhos(servidor.getSerQtdFilhos());
            bean.setSerNacionalidade(servidor.getSerNacionalidade());
            bean.setSerNroIdt(servidor.getSerNroIdt());
            bean.setSerCartProf(servidor.getSerCartProf());
            bean.setSerPis(servidor.getSerPis());
            bean.setSerEnd(servidor.getSerEnd());
            bean.setSerBairro(servidor.getSerBairro());
            bean.setSerCidade(servidor.getSerCidade());
            bean.setSerCompl(servidor.getSerCompl());
            bean.setSerNro(servidor.getSerNro());
            bean.setSerCep(servidor.getSerCep());
            bean.setSerUf(servidor.getSerUf());
            bean.setSerTel(servidor.getSerTel());
            bean.setSerEmail(servidor.getSerEmail());
            bean.setSerEmissorIdt(servidor.getSerEmissorIdt());
            bean.setSerUfIdt(servidor.getSerUfIdt());
            bean.setSerDataIdt(servidor.getSerDataIdt());
            bean.setSerCidNasc(servidor.getSerCidNasc());
            bean.setSerUfNasc(servidor.getSerUfNasc());
            bean.setSerNomeConjuge(servidor.getSerNomeConjuge());
            bean.setSerDeficienteVisual(servidor.getSerDeficienteVisual());
            bean.setSerDataAlteracao(servidor.getSerDataAlteracao());
            bean.setSerCelular(servidor.getSerCelular());
            bean.setSerAcessaHostAHost(servidor.getSerAcessaHostAHost());
            bean.setSerPermiteAlterarEmail(servidor.getSerPermiteAlterarEmail());

            if (!TextHelper.isNull(servidor.getNivelEscolaridade())) {
                bean.setNivelEscolaridade(!TextHelper.isNull(servidor.getNivelEscolaridade().getNesCodigo()) ? (NivelEscolaridade) session.getReference(NivelEscolaridade.class, servidor.getNivelEscolaridade().getNesCodigo()) : null);
            }
            if (!TextHelper.isNull(servidor.getTipoHabitacao())) {
                bean.setTipoHabitacao(!TextHelper.isNull(servidor.getTipoHabitacao().getThaCodigo()) ? (TipoHabitacao) session.getReference(TipoHabitacao.class, servidor.getTipoHabitacao().getThaCodigo()) : null);
            }
            if (!TextHelper.isNull(servidor.getStatusServidor())) {
                bean.setStatusServidor(!TextHelper.isNull(servidor.getStatusServidor().getSseCodigo()) ? (StatusServidor) session.getReference(StatusServidor.class, servidor.getStatusServidor().getSseCodigo()) : null);
            }

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

}
