package com.zetra.econsig.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.PerfilCorDao;
import com.zetra.econsig.dao.PerfilCsaDao;
import com.zetra.econsig.dao.PerfilCseDao;
import com.zetra.econsig.dao.PerfilDao;
import com.zetra.econsig.dao.PerfilSupDao;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.persistence.entity.PerfilCor;
import com.zetra.econsig.persistence.entity.PerfilCsa;
import com.zetra.econsig.persistence.entity.PerfilCse;
import com.zetra.econsig.persistence.entity.PerfilSup;

@Service
public class ManutencaoPerfilService {

	@Autowired
	private PerfilDao perfilDao;
	
	@Autowired
	private PerfilCseDao perfilCseDao;
	
	@Autowired
	private PerfilCsaDao perfilCsaDao;
	
	@Autowired
	private PerfilCorDao perfilCorDao;
	
	@Autowired
	private PerfilSupDao perfilSupDao;
	
    /**
     * Retorna lista de perfil
     * @param perfilDescricao
     */
	public List<Perfil> getPerfil(String perfilDescricao) {
		return perfilDao.findByPerDescricao(perfilDescricao);
    }

    /**
     * Confere o status de um perfil, no banco de dados.
     * @param perfilDescricao
     * @return 
     */
    public int getStatusPerfilCse(String perfilDescricao) {
		List<Perfil> perfil = perfilDao.findByPerDescricao(perfilDescricao);
		PerfilCse perfilCse = perfilCseDao.findByPerCodigo(perfil.get(0).getPerCodigo());
    	
    	return perfilCse.getPceAtivo().intValue();
    }
    
    public int getStatusPerfilCsa(String perfilDescricao) {
		List<Perfil> perfil = perfilDao.findByPerDescricao(perfilDescricao);
		PerfilCsa perfilCsa = perfilCsaDao.findByPerCodigo(perfil.get(0).getPerCodigo());
    	
    	return perfilCsa.getPcaAtivo().intValue();
    }  
    
    public int getStatusPerfilCor(String perfilDescricao) {
		List<Perfil> perfil = perfilDao.findByPerDescricao(perfilDescricao);
		PerfilCor perfilCor = perfilCorDao.findByPerCodigo(perfil.get(0).getPerCodigo());
    	
    	return perfilCor.getPcoAtivo().intValue();
    }
    
    public int getStatusPerfilSup(String perfilDescricao) {
		List<Perfil> perfil = perfilDao.findByPerDescricao(perfilDescricao);
		PerfilSup perfilSup = perfilSupDao.findByPerCodigo(perfil.get(0).getPerCodigo());
    	
    	return perfilSup.getPsuAtivo().intValue();
    } 
}
