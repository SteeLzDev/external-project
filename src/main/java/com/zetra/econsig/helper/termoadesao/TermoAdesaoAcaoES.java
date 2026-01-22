package com.zetra.econsig.helper.termoadesao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.TermoAdesaoAcaoException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: TermoAdesaoAcaoES</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 */
public class TermoAdesaoAcaoES extends TermoAdesaoAcaoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TermoAdesaoAcaoES.class);
	private static final long serialVersionUID = 1L;

	// Regra de Margem
	// Existem duas Margens, cartão e Empréstimo. O limite deve ser sempre 70%, pois a folha envia compulsórios do registro servidor na RSE_DESCONTOS_COMP
	// E estes compulsórios o percentual dele é de 30% quando o servidor não tem Cartão e 20% quando tem cartão e assim vai 10% para o cartão
	// Porém quando o valor do compulsórios passa do limite que ele deveria ter 20 ou 30, essa diferente de valor é tirado da margem de Empréstimo
	// Sendo assim, ao aceitar ou recusar o termo, devemos fazer a distruição destes percentuais e valores.
	// Agora, quando o servidor recusa o termo, só podemos refazer os cálculos se ele não tiver reserva de cartão.
	// Margem Empréstimo (mar_codigo 1)
	// Margem Cartão (mar_codigo 3)

	@Override
    public void posAceiteTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
		calculaMargem(responsavel.getRseCodigo(), new BigDecimal("0.10"), new BigDecimal("0.20"), responsavel);
    }

	@Override
    public void posRecusaTermo (String tadCodigo, AcessoSistema responsavel) throws TermoAdesaoAcaoException {

		final String rseCodigo = responsavel.getRseCodigo();

		try {
			final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
	        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
	        final List<String> cnvCodigosReserva = new ArrayList<>();

	        final List<TransferObject> lstCnvCodigosCartaoReserva = convenioController.ListaConveniosIncMargemCartaoReservaLancamento(Short.valueOf("3"), true, responsavel);

	        for (final TransferObject cnv : lstCnvCodigosCartaoReserva) {
	            cnvCodigosReserva.add((String) cnv.getAttribute(Columns.CNV_CODIGO));
	        }

	        final BigDecimal totalReservasCartao = pesquisarConsignacaoController.ObtemTotalValorConsignacaoPorRseCnv(rseCodigo, cnvCodigosReserva, null, responsavel);

	        if (totalReservasCartao.compareTo(BigDecimal.ZERO) > 0) {
	        	throw new TermoAdesaoAcaoException("mensagem.erro.termo.adesao.classe.acao.es.reserva", responsavel);
	        }

		} catch (AutorizacaoControllerException | NumberFormatException | ConvenioControllerException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new TermoAdesaoAcaoException("mensagem.erro.termo.adesao.classe.acao", responsavel);
		}

		calculaMargem(rseCodigo, BigDecimal.ZERO, new BigDecimal("0.30"), responsavel);
    }

	private void calculaMargem(String rseCodigo, BigDecimal percentualCartao, BigDecimal percentualCompulsorio, AcessoSistema responsavel) throws TermoAdesaoAcaoException {
		try {
            final MargemController margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

			final RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
			final BigDecimal salario = registroServidor.getRseSalario();
			final BigDecimal valorCompulsorio = registroServidor.getRseDescontosComp();

			BigDecimal valorMargemEmprestimo = salario.multiply(new BigDecimal("0.40"));
			final BigDecimal valorMargemCartao = salario.multiply(percentualCartao);
			final BigDecimal limiteValorCompulsorio = salario.multiply(percentualCompulsorio);

			final BigDecimal diferenca = limiteValorCompulsorio.subtract(valorCompulsorio);

			if (diferenca.compareTo(BigDecimal.ZERO) < 0) {
				valorMargemEmprestimo = valorMargemEmprestimo.add(diferenca);
			}
			registroServidor.setRseMargem(valorMargemEmprestimo);
			registroServidor.setRseMargem3(valorMargemCartao);

			servidorController.updateRegistroServidor(registroServidor, false, true, false, responsavel);
			margemController.recalculaMargem("RSE", List.of(rseCodigo), responsavel);
		} catch (ServidorControllerException | MargemControllerException ex ) {
			LOG.error(ex.getMessage(), ex);
			throw new TermoAdesaoAcaoException("mensagem.erro.termo.adesao.classe.acao", responsavel);
		}
	}

}
