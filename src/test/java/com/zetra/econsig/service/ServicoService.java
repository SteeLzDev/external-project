package com.zetra.econsig.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.CoeficienteAtivoDao;
import com.zetra.econsig.dao.CoeficienteDao;
import com.zetra.econsig.dao.ConvenioDao;
import com.zetra.econsig.dao.NaturezaServicoDao;
import com.zetra.econsig.dao.ParamSvcConsignanteDao;
import com.zetra.econsig.dao.ParamSvcConsignatariaDao;
import com.zetra.econsig.dao.PrazoConsignatariaDao;
import com.zetra.econsig.dao.PrazoDao;
import com.zetra.econsig.dao.RelacionamentoServicoDao;
import com.zetra.econsig.dao.ServicoDao;
import com.zetra.econsig.dao.TipoGrupoSvcDao;
import com.zetra.econsig.dao.VerbaConvenioDao;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.Prazo;
import com.zetra.econsig.persistence.entity.PrazoConsignataria;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.TipoGrupoSvc;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ServicoService {

	@Autowired
	private ServicoDao servicoDao;

	@Autowired
	private ConvenioController convenioController;

	@Autowired
	private NaturezaServicoDao naturezaServicoDao;

    @Autowired
    private TipoGrupoSvcDao tipoGrupoSvcDao;

    @Autowired
    private ParamSvcConsignanteDao paramSvcConsignanteDao;

    @Autowired
    private ParamSvcConsignatariaDao paramSvcConsignatariaDao;

    @Autowired
    private PrazoDao prazoDao;

    @Autowired
    private PrazoConsignatariaDao prazoConsignatariaDao;

    @Autowired
    private ConvenioDao convenioDao;

    @Autowired
    private VerbaConvenioDao verbaConvenioDao;

    @Autowired
    private CoeficienteDao coeficienteDao;

    @Autowired
    private CoeficienteAtivoDao coeficienteAtivoDao;

    @Autowired
    private RelacionamentoServicoDao relacionamentoServicoDao;

	public String retornaSvcCodigo(String identificador) {
		return obterServicoPorIdentificador(identificador).getSvcCodigo();
	}

	public NaturezaServico obterNaturezaServicoPorCodigo(String nseCodigo) {
	    Optional<NaturezaServico> nse = naturezaServicoDao.findById(nseCodigo);
	    return nse.isPresent() ? nse.get() : null;
	}

	public Servico obterServicoPorIdentificador(String svcIdentificador) {
	    return servicoDao.findBySvcIdentificador(svcIdentificador);
	}

	public Servico incluirServicoAtivo(String svcIdentificador, String svcDescricao, String nseCodigo) {
	    try {
	        Servico servico = new Servico();
	        servico.setSvcCodigo(DBHelper.getNextId());
	        servico.setSvcIdentificador(svcIdentificador);
	        servico.setSvcDescricao(svcDescricao);
	        servico.setNseCodigo(nseCodigo);
	        servico.setSvcAtivo(CodedValues.STS_ATIVO);

	        return servicoDao.save(servico);
	    } catch (MissingPrimaryKeyException ex) {
	        log.error(ex.getMessage(), ex);
	        return null;
	    }
	}

	@Transactional
	public void excluirServico(String svcCodigo) {
	    relacionamentoServicoDao.removeBySvcCodigoOrigem(svcCodigo);
	    relacionamentoServicoDao.removeBySvcCodigoDestino(svcCodigo);
	    paramSvcConsignanteDao.removeBySvcCodigo(svcCodigo);
	    paramSvcConsignatariaDao.removeBySvcCodigo(svcCodigo);
	    List<Prazo> prazos = prazoDao.findBySvcCodigo(svcCodigo);
	    if (prazos != null && !prazos.isEmpty()) {
	        for (Prazo prazo : prazos) {
	            List<PrazoConsignataria> prazosCsa = prazoConsignatariaDao.findByPrzCodigo(prazo.getPrzCodigo());
	            if (prazosCsa != null && !prazosCsa.isEmpty()) {
	                for (PrazoConsignataria prazoCsa : prazosCsa) {
	                    coeficienteAtivoDao.removeByPrzCsaCodigo(prazoCsa.getPrzCsaCodigo());
                        coeficienteDao.removeByPrzCsaCodigo(prazoCsa.getPrzCsaCodigo());
	                }
	                prazoConsignatariaDao.removeByPrzCodigo(prazo.getPrzCodigo());
	            }
	        }
	        prazoDao.removeBySvcCodigo(svcCodigo);
	    }
	    List<Convenio> convenios = convenioDao.findBySvcCodigo(svcCodigo);
	    if (convenios != null && !convenios.isEmpty()) {
	        for (Convenio convenio : convenios) {
	            verbaConvenioDao.removeByCnvCodigo(convenio.getCnvCodigo());
	        }
	        convenioDao.removeBySvcCodigo(svcCodigo);
	    }
	    servicoDao.deleteById(svcCodigo);
	}

	public String copiaServico(String svcCodigoNov, String svcIdnNovo, String svcCodigoAntigo, AcessoSistema responsavel) throws ConvenioControllerException {
		return copiaServico(svcCodigoNov, svcIdnNovo, svcCodigoAntigo, true, responsavel);
	}

	public String copiaServico(String svcCodigoNov, String svcIdnNovo, String svcCodigoAntigo, boolean criarNovoServico, AcessoSistema responsavel) throws ConvenioControllerException {

		if (criarNovoServico) {
			Servico newService = new Servico();
			newService.setSvcCodigo(svcCodigoNov);
			newService.setSvcIdentificador(svcIdnNovo);
			newService.setNseCodigo(CodedValues.NSE_EMPRESTIMO);
			newService.setSvcAtivo(Short.valueOf("1"));

			servicoDao.save(newService);
		}

		ServicoTransferObject servico = new ServicoTransferObject(svcCodigoNov);
		servico.setSvcIdentificador(svcIdnNovo);


		convenioController.copiaServico(svcCodigoAntigo, servico, responsavel);

		return svcCodigoNov;
	}

	public void toggleSvcAtivo(String svcCodigo, boolean ativo) {
		Optional<Servico> svcOpt = servicoDao.findById(svcCodigo);

		svcOpt.ifPresent(svc -> {
			svc.setSvcAtivo(ativo ? CodedValues.STS_ATIVO : CodedValues.STS_INATIVO);
			servicoDao.save(svc);
		});
	}

	public TipoGrupoSvc createTipoGrupoSvc(String tgsCodigo, String tgsGrupo, String tgsIdn, Integer tgsQuantidade, Integer tgsQuantidadeCsa) {
		TipoGrupoSvc retorno = new TipoGrupoSvc();

		retorno.setTgsCodigo(tgsCodigo);
		retorno.setTgsCodigo(tgsGrupo);
		retorno.setTgsIdentificador(tgsIdn);
		retorno.setTgsQuantidade(tgsQuantidadeCsa);
		retorno.setTgsQuantidadePorCsa(tgsQuantidadeCsa);

		tipoGrupoSvcDao.save(retorno);

		return retorno;

	}

	@Transactional
	public void setNovoGrupoSvcAServico(String svcCodigo, String tgsCodigo, String tgsGrupo, String tgsIdn, Integer tgsQuantidade, Integer tgsQuantidadeCsa) {
		TipoGrupoSvc retorno = new TipoGrupoSvc();

		retorno.setTgsCodigo(tgsCodigo);
		retorno.setTgsCodigo(tgsGrupo);
		retorno.setTgsIdentificador(tgsIdn);
		retorno.setTgsQuantidade(tgsQuantidadeCsa);
		retorno.setTgsQuantidadePorCsa(tgsQuantidadeCsa);

		tipoGrupoSvcDao.save(retorno);

		servicoDao.findById("010B8080808080808080808080809480").ifPresent(svc -> {
			TipoGrupoSvc register = tipoGrupoSvcDao.findByTgsIdentificador(tgsIdn);
			svc.setTgsCodigo(register.getTgsCodigo());
			servicoDao.save(svc);
		});

	}

	public void deleteTipoGrupoSvc(String tgsCodigo) {
		tipoGrupoSvcDao.findById(tgsCodigo).ifPresent(toDelete -> tipoGrupoSvcDao.delete(toDelete));
	}
}
