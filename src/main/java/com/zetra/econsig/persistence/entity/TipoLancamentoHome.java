package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoLancamentoHome</p>
 * <p>Description: Classe Home para a entidade TipoLancamento</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoLancamentoHome extends AbstractEntityHome {

    public static TipoLancamento findByPrimaryKey(String tlaCodigo) throws FindException {
        TipoLancamento tipoLancamento = new TipoLancamento();
        tipoLancamento.setTlaCodigo(tlaCodigo);
        return find(tipoLancamento, tlaCodigo);
    }

    public static TipoLancamento findByTntCodigo(String tntCodigo) throws FindException {
        String query = "FROM TipoLancamento tla WHERE tla.tipoNatureza.tntCodigo = :tntCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tntCodigo", tntCodigo);
        List<TipoLancamento> result = findByQuery(query.toString(), parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static TipoLancamento create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static List<TipoLancamento> findByTntCodigoAndNseCodigo(List<String> tntCodigos, String nseCodigo) throws FindException {
        String query = "FROM TipoLancamento tla WHERE tla.tipoNatureza.tntCodigo IN (:tntCodigos) and tla.naturezaServico.nseCodigo = :nseCodigo";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tntCodigos", tntCodigos);
        parameters.put("nseCodigo", nseCodigo);

        return findByQuery(query.toString(), parameters);

    }

	public static List<TipoLancamento> listar() throws FindException {
		String query = "from TipoLancamento";
		return findByQuery(query, null);
	}
}

