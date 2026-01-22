package com.zetra.econsig.service.regraconvenio;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.RegraConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.RegraConvenioDAO;
import com.zetra.econsig.persistence.query.regraconvenio.BuscaRegrasConvenioByCsaQuery;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.values.Columns;


@Service
@Transactional
public class RegraConvenioControllerBean implements RegraConvenioController {
	/** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RegraConvenioControllerBean.class);
	
    @Override
	public void salvarRegrasConvenio(List<RegrasConvenioParametrosBean> listParametros, String csaCodigo, AcessoSistema responsavel) throws RegraConvenioControllerException {		
		try {
			List<CustomTransferObject> listParams = listParametros.stream()
				    .map(param -> {
				        CustomTransferObject rco = new CustomTransferObject();
				        rco.setAttribute(Columns.RCO_CAMPO_CODIGO, param.getCodigo());
				        rco.setAttribute(Columns.RCO_CAMPO_NOME, param.getChave());
				        rco.setAttribute(Columns.RCO_CAMPO_VALOR, param.getValor());
				        rco.setAttribute(Columns.RCO_CSA_CODIGO, csaCodigo);
				        rco.setAttribute(Columns.RCO_SVC_CODIGO, param.getSvcCodigo());
				        rco.setAttribute(Columns.RCO_ORG_CODIGO, param.getOrgCodigo());
				        rco.setAttribute(Columns.RCO_MAR_CODIGO, param.getMarCodigo());
				        return rco;
				    }).collect(Collectors.toList());
			final RegraConvenioDAO regraConvenioDAO = DAOFactory.getDAOFactory().getRegraConvenioDAO();
			regraConvenioDAO.insereRegrasConvenio(listParams);
		} catch (DAOException ex) {
			LOG.error(ex.getMessage(), ex);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			throw new RegraConvenioControllerException(ex);
		}
	}
    
	@Override
	public List<TransferObject> listaRegrasConvenioByCsa(String csaCodigo, AcessoSistema responsavel) throws RegraConvenioControllerException {
		try {
			BuscaRegrasConvenioByCsaQuery query = new BuscaRegrasConvenioByCsaQuery();
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RegraConvenioControllerException(ex);
        }		
	}

	@Override
	public void removeRegrasConvenioByCsa(String csaCodigo, AcessoSistema responsavel) throws RegraConvenioControllerException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM RegraConvenio rc WHERE rc.consignataria.csaCodigo = :csaCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("csaCodigo", csaCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
        	LOG.error(ex.getMessage(), ex);
            throw new RegraConvenioControllerException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
		
	}
}
