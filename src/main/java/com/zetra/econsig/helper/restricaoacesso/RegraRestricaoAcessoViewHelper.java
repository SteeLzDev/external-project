package com.zetra.econsig.helper.restricaoacesso;

import java.sql.Time;
import java.text.ParseException;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RegraRestricaoAcessoViewHelper</p>
 * <p>Description: Helper Class para criação e remoção de regra de restrição de acesso</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraRestricaoAcessoViewHelper {

	public static void createRegraRestricaoAcesso(HttpServletRequest request, AcessoSistema responsavel) throws ZetraException {
		String horaIni = request.getParameter("horaIni");
		String horaFim = request.getParameter("horaFim");

		Date rraHoraIni = null;
		Date rraHoraFim = null;
		Date rraData = null;

		if (TextHelper.isNull(horaIni) || TextHelper.isNull(horaFim)) {
			throw new ZetraException("mensagem.erro.restricao.acesso.hora.inicial.final.invalida", responsavel);
		}

		String [] horaIniArray = horaIni.split(":");
		String [] horaFimArray = horaFim.split(":");

		horaIni = (horaIniArray[1].equals("59")) ? horaIni + ":59" : horaIni + ":00";
		horaFim = (horaFimArray[1].equals("59")) ? horaFim + ":59" : horaFim + ":00";

		try {
			if (!TextHelper.isNull(horaIni)) {
				rraHoraIni = DateHelper.parse(horaIni, "HH:mm:ss");
			}

			if (!TextHelper.isNull(horaFim)) {
				rraHoraFim = DateHelper.parse(horaFim, "HH:mm:ss");
			}

			String data = request.getParameter("data");
			if (!TextHelper.isNull(data)) {
				rraData = DateHelper.parse(data, LocaleHelper.getDatePattern());
			}
		} catch (ParseException e) {
			throw new ZetraException("mensagem.erro.restricao.acesso.hora.inicial.final.invalida", responsavel);
		}

		String rraDescricao = request.getParameter("rraDescricao");
		String funCodigo = request.getParameter("funcao");

		String[] papCodigos = request.getParameterValues("chkPapel");
		String[] diasSemana = request.getParameterValues("chkDiaSemana");

		String diasUteis = request.getParameter("diaUtil");

		String csaCodigo = null;

		if (responsavel.isCsa()) {
			csaCodigo = responsavel.getCodigoEntidade();
		} else if (responsavel.isCor()) {
			csaCodigo = responsavel.getCodigoEntidadePai();
		}

		ParametroDelegate paramDelegate = new ParametroDelegate();

		TransferObject restricaoTO = new CustomTransferObject();
		restricaoTO.setAttribute(Columns.RRA_HORA_INICIO, new Time(rraHoraIni.getTime()));
		restricaoTO.setAttribute(Columns.RRA_HORA_FIM, new Time(rraHoraFim.getTime()));
		restricaoTO.setAttribute(Columns.RRA_DESCRICAO, rraDescricao);
		restricaoTO.setAttribute(Columns.RRA_DATA, rraData);
		restricaoTO.setAttribute(Columns.RRA_FUN_CODIGO, funCodigo);
		restricaoTO.setAttribute(Columns.RRA_DIAS_UTEIS, diasUteis);
		restricaoTO.setAttribute(Columns.RCA_CSA_CODIGO, csaCodigo);

		if (papCodigos != null) {
			for (String papCodigo : papCodigos) {
				if (diasSemana != null) {
					for (String element : diasSemana) {
						restricaoTO.setAttribute(Columns.RRA_DIA_SEMANA, Short.parseShort(element));
						restricaoTO.setAttribute(Columns.RRA_PAP_CODIGO, papCodigo);

						paramDelegate.createRestricaoAcesso(restricaoTO, responsavel);
					}
				} else {
					restricaoTO.setAttribute(Columns.RRA_PAP_CODIGO, papCodigo);

					paramDelegate.createRestricaoAcesso(restricaoTO, responsavel);
				}
			}
		} else if (diasSemana != null) {
			for (String element : diasSemana) {
				restricaoTO.setAttribute(Columns.RRA_DIA_SEMANA, Short.parseShort(element));
				paramDelegate.createRestricaoAcesso(restricaoTO, responsavel);
			}
		} else {
			paramDelegate.createRestricaoAcesso(restricaoTO, responsavel);
		}
	}

	public static void excluirRegraRestricaoAcesso(String rraCodigo, AcessoSistema responsavel) throws ZetraException {
		ParametroDelegate paramDelegate = new ParametroDelegate();

		paramDelegate.excluirRegraRestricaoAcesso(rraCodigo, responsavel);
	}
}
