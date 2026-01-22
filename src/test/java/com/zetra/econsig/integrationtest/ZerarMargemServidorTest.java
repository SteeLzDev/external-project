package com.zetra.econsig.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.component.ConfiguradorPermissoesUsuario;
import com.zetra.econsig.config.ContextSpringConfiguration;
import com.zetra.econsig.dao.HistoricoMargemFolhaDao;
import com.zetra.econsig.dao.HistoricoMargemRseDao;
import com.zetra.econsig.dao.MargemDao;
import com.zetra.econsig.dao.MargemRegistroServidorDao;
import com.zetra.econsig.dao.ParamSvcConsignanteDao;
import com.zetra.econsig.dao.RegistroServidorDao;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Margem;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.service.MargemService;
import com.zetra.econsig.service.RegistroServidorService;
import com.zetra.econsig.service.ServicoService;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.util.AcessoSistemaBuilder;
import com.zetra.econsig.values.CodedValues;

public class ZerarMargemServidorTest extends ContextSpringConfiguration {

    private static final String RSE_CODIGO = "83808080808080808080808080804CAB";

	@Autowired
	private ServidorController servidorController;

	@Autowired
	private ConfiguradorPermissoesUsuario configuradorPermissoesUsuario;

	@Autowired
	private RegistroServidorDao registroServidorDao;

	@Autowired
	private ParamSvcConsignanteDao paramSvcConsignanteDao;

	@Autowired
	private ServicoService servicoService;

	@Autowired
	private MargemService margemService;

	@Autowired
	private MargemDao margemDao;

	@Autowired
	private MargemRegistroServidorDao margemRegistroServidorDao;

	@Autowired
	private HistoricoMargemFolhaDao historicoMargemDao;

	@Autowired
	private HistoricoMargemRseDao historicoMargemRseDao;

	@Autowired
	private RegistroServidorService registroServidorService;

	private AcessoSistema usuSup;

	@BeforeEach
	public void beforeClass() throws UsuarioControllerException {
		usuSup = new AcessoSistemaBuilder("AA808080808080808080808080809E80")
				.setTipoEntidade(AcessoSistema.ENTIDADE_CSE)
				.setCodigoEntidade("1").build();

		configuradorPermissoesUsuario.carregarPermissoes(usuSup, "AA808080808080808080808080809E80", AcessoSistema.ENTIDADE_SUP, "1");

		usuSup.getPermissoes().put(CodedValues.FUN_CONF_MARGEM_FOLHA_SER, new EnderecoFuncaoTransferObject(CodedValues.FUN_CONF_MARGEM_FOLHA_SER, "Confirmar margem folha do servidor"));

		RegistroServidor rse = registroServidorDao.findById(RSE_CODIGO).get();

		rse.setRseMargem(BigDecimal.valueOf(5321.65d));
		rse.setRseMargem2(BigDecimal.valueOf(191.56d));
		rse.setRseMargem3(BigDecimal.valueOf(174.12d));

		registroServidorDao.save(rse);
	}

	@Test
	public void zera_margens_com_servico_ativo() throws ServidorControllerException {
	    String rseCodigo = "481780808080808080809090";
		RegistroServidor rse = registroServidorService.alterarStatusRegistroServidor(rseCodigo, CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM);

		servidorController.rejeitarMargemFolha(List.of(rseCodigo), usuSup);

		rse = registroServidorDao.findById(rseCodigo).get();

		assertEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem()));
		assertEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem2()));
		assertEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem3()));

		assertEquals(BigDecimal.ZERO.doubleValue(), rse.getRseMargemUsada2().doubleValue());
		assertEquals(BigDecimal.ZERO.doubleValue(), rse.getRseMargemUsada3().doubleValue());

		assertEquals(CodedValues.SRS_ATIVO, rse.getStatusRegistroServidor().getSrsCodigo());

		rse.setRseMargem(BigDecimal.valueOf(2000.00d));
		rse.setRseMargem2(BigDecimal.valueOf(5000.00d));
		rse.setRseMargem3(BigDecimal.valueOf(3000.00));

		rse.setRseMargemUsada(BigDecimal.ZERO);
		rse.setRseMargemUsada2(BigDecimal.ZERO);
		rse.setRseMargemUsada3(BigDecimal.ZERO);

		registroServidorDao.save(rse);
	}

	@Test
	public void nao_zera_margem_1_sem_servico_ativo() throws ServidorControllerException {
		List<ParamSvcConsignante> listParamsIncMargem = paramSvcConsignanteDao.findByTpsCodigo(CodedValues.TPS_INCIDE_MARGEM);

		listParamsIncMargem.stream().filter(param -> param.getPseVlr().equals("1")).map(ParamSvcConsignante::getSvcCodigo).toList().forEach(svcCodigo -> servicoService.toggleSvcAtivo(svcCodigo, false));

		servidorController.rejeitarMargemFolha(List.of(RSE_CODIGO), usuSup);

		RegistroServidor rse = registroServidorDao.findById(RSE_CODIGO).get();

		assertNotEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem()));
		assertEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem2()));
		assertEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem3()));

		rse.setRseMargem(BigDecimal.valueOf(5321.65d));
		rse.setRseMargem2(BigDecimal.valueOf(191.56d));
		rse.setRseMargem3(BigDecimal.valueOf(174.12d));

		registroServidorDao.save(rse);

		listParamsIncMargem.stream().filter(param -> param.getPseVlr().equals("1")).map(ParamSvcConsignante::getSvcCodigo).toList().forEach(svcCodigo -> servicoService.toggleSvcAtivo(svcCodigo, true));

	}

	@Test
	public void zera_margem_extra_com_servico_ativo() throws ServidorControllerException {
		Margem mar = margemService.incluirMargem("202", "F");

		MargemRegistroServidor mrs = margemService.incluirMargemRegistroServidor("202", RSE_CODIGO, BigDecimal.valueOf(2000d), BigDecimal.ZERO, BigDecimal.valueOf(2000d));

		paramSvcConsignanteDao.findByTpsCodigo(CodedValues.TPS_INCIDE_MARGEM).stream().filter(paramSvc -> paramSvc.getPseVlr().equals(CodedValues.INCIDE_MARGEM_SIM.toString()))
		.findFirst()
		.ifPresent(param -> {
			param.setPseVlr("202");
			paramSvcConsignanteDao.save(param);
		});

		servidorController.rejeitarMargemFolha(List.of(RSE_CODIGO), usuSup);

		mrs = margemRegistroServidorDao.findByMarCodigoAndRseCodigo(Short.parseShort("202"), RSE_CODIGO);

		assertEquals(0, BigDecimal.ZERO.compareTo(mrs.getMrsMargem()));

		paramSvcConsignanteDao.findByTpsCodigo(CodedValues.TPS_INCIDE_MARGEM).stream().filter(paramSvc -> paramSvc.getPseVlr().equals("202"))
		.findFirst()
		.ifPresent(param -> {
			param.setPseVlr(CodedValues.INCIDE_MARGEM_SIM.toString());
			paramSvcConsignanteDao.save(param);
		});

		RegistroServidor rse = registroServidorDao.findById(RSE_CODIGO).get();

		rse.setRseMargem(BigDecimal.valueOf(5321.65d));
		rse.setRseMargem2(BigDecimal.valueOf(191.56d));
		rse.setRseMargem3(BigDecimal.valueOf(174.12d));

		registroServidorDao.save(rse);

		margemRegistroServidorDao.delete(mrs);
		historicoMargemDao.findByMarCodigo("202").forEach(hist -> historicoMargemDao.delete(hist));
		historicoMargemRseDao.findByMarCodigo("202").forEach(hist -> historicoMargemRseDao.delete(hist));
		margemDao.delete(mar);

	}

	@Test
	public void nao_zera_margem_extra_sem_servico_ativo() throws ServidorControllerException {
		RegistroServidor rse = registroServidorService.alterarStatusRegistroServidor(RSE_CODIGO, CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM);

		Margem mar = margemService.incluirMargem("303", "F");

		MargemRegistroServidor mrs = margemService.incluirMargemRegistroServidor("303", RSE_CODIGO, BigDecimal.valueOf(2000d), BigDecimal.ZERO, BigDecimal.valueOf(2000d));

		servidorController.rejeitarMargemFolha(List.of(RSE_CODIGO), usuSup);

		mrs = margemRegistroServidorDao.findByMarCodigoAndRseCodigo(Short.parseShort("303"), RSE_CODIGO);

		assertEquals(0, BigDecimal.valueOf(2000d).compareTo(mrs.getMrsMargem()));

		rse = registroServidorDao.findById(RSE_CODIGO).get();

		assertEquals(CodedValues.SRS_ATIVO, rse.getStatusRegistroServidor().getSrsCodigo());

		rse.setRseMargem(BigDecimal.valueOf(5321.65d));
		rse.setRseMargem2(BigDecimal.valueOf(191.56d));
		rse.setRseMargem3(BigDecimal.valueOf(174.12d));
		registroServidorDao.save(rse);

		registroServidorService.alterarStatusRegistroServidor(RSE_CODIGO, CodedValues.SRS_ATIVO);

		margemRegistroServidorDao.delete(mrs);
		historicoMargemDao.findByMarCodigo("303").forEach(hist -> historicoMargemDao.delete(hist));
		historicoMargemRseDao.findByMarCodigo("303").forEach(hist -> historicoMargemRseDao.delete(hist));
		margemDao.delete(mar);

	}

	@Test
	public void nao_zera_nenhuma_margem_pois_nao_ha_servico_ativo() throws ServidorControllerException {
		RegistroServidor rse = registroServidorService.alterarStatusRegistroServidor(RSE_CODIGO, CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM);

		List<ParamSvcConsignante> listParamsIncMargem = paramSvcConsignanteDao.findByTpsCodigo(CodedValues.TPS_INCIDE_MARGEM);

		listParamsIncMargem.stream().map(ParamSvcConsignante::getSvcCodigo).toList().forEach(svcCodigo -> servicoService.toggleSvcAtivo(svcCodigo, false));

		servidorController.rejeitarMargemFolha(List.of(RSE_CODIGO), usuSup);

		rse = registroServidorDao.findById(RSE_CODIGO).get();

		assertEquals(CodedValues.SRS_ATIVO, rse.getStatusRegistroServidor().getSrsCodigo());

		assertNotEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem()));
		assertNotEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem2()));
		assertNotEquals(0, BigDecimal.ZERO.compareTo(rse.getRseMargem3()));

		listParamsIncMargem.stream().map(ParamSvcConsignante::getSvcCodigo).toList().forEach(svcCodigo -> servicoService.toggleSvcAtivo(svcCodigo, true));
	}

}
