package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.EstabelecimentoDao;
import com.zetra.econsig.persistence.entity.Estabelecimento;

@Service
public class EstabelecimentoService {

    @Autowired
    private EstabelecimentoDao estabelecimentoDao;

    public Estabelecimento obterEstabelecimentoPorIdentificador(String estIdentificador) {
        return estabelecimentoDao.findByEstIdentificador(estIdentificador);
    }

}
