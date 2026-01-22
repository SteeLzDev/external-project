package com.zetra.econsig.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.UsuarioChaveSessaoDao;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.persistence.entity.UsuarioChaveSessao;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioChaveSessaoService {

    @Autowired
    private UsuarioChaveSessaoDao usuarioChaveSessaoDao;

    public UsuarioChaveSessao criarUsuarioChaveSessao(String usuCodigo, String ucsToken, Date ucsDataCriacao) throws CreateException {
        UsuarioChaveSessao ucs = new UsuarioChaveSessao();
        ucs.setUsuCodigo(usuCodigo);
        ucs.setUcsToken(ucsToken);
        ucs.setUcsDataCriacao(ucsDataCriacao);

        return usuarioChaveSessaoDao.save(ucs);
    }

}
