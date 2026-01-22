package com.zetra.econsig.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.ConvenioDao;
import com.zetra.econsig.dao.OrgaoDao;
import com.zetra.econsig.dao.VerbaConvenioDao;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.values.CodedValues;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class OrgaoService {

    @Autowired
    private OrgaoDao orgaoDao;

    @Autowired
    private ConvenioDao convenioDao;

    @Autowired
    private VerbaConvenioDao verbaConvenioDao;

    public Orgao obterOrgaoPorIdentificador(String orgIdentificador) {
        return orgaoDao.findByOrgIdentificador(orgIdentificador);
    }

    public Orgao incluirOrgaoAtivo(String estCodigo, String orgNome, String orgIdentificador) {
        try {
            Orgao orgao = new Orgao();
            orgao.setOrgCodigo(DBHelper.getNextId());
            orgao.setEstCodigo(estCodigo);
            orgao.setOrgNome(orgNome);
            orgao.setOrgIdentificador(orgIdentificador);
            orgao.setOrgAtivo(CodedValues.STS_ATIVO);
            return orgaoDao.save(orgao);
        } catch (MissingPrimaryKeyException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public void excluirOrgao(String orgCodigo) {
        List<Convenio> convenios = convenioDao.findByOrgCodigo(orgCodigo);
        if (convenios != null && !convenios.isEmpty()) {
            for (Convenio convenio : convenios) {
                verbaConvenioDao.removeByCnvCodigo(convenio.getCnvCodigo());
            }
            convenioDao.removeByOrgCodigo(orgCodigo);
        }

        orgaoDao.deleteById(orgCodigo);
    }
}
