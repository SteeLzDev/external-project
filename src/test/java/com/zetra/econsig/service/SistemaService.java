package com.zetra.econsig.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
@SuppressWarnings("all")
public class SistemaService {

    public void executarConsulta(HQuery query) throws HQueryException {
        try {
            query.executarDTO();
        } catch (HQueryException hex) {
            if ("mensagem.erro.query.metodo.get.fields.nao.implementado".equals(hex.getMessageKey())) {
                query.executarContador();
            } else {
                throw hex;
            }
        }
    }
}
