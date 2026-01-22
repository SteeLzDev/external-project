-- DESENV-22011
INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16997', '1', '27', '/v3/deferirConsignacao', 'acao', 'listarConsignacaoSer', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_acesso_recurso (ACR_CODIGO, PAP_CODIGO, FUN_CODIGO, ACR_RECURSO, ACR_PARAMETRO, ACR_OPERACAO, ACR_ATIVO, ACR_BLOQUEIO, ACR_SESSAO, ITM_CODIGO, ACR_FIM_FLUXO, ACR_METODO_HTTP)
VALUES ('16998', '1', '28', '/v3/indeferirConsignacao', 'acao', 'listarConsignacaoSer', 1, 'S', 'S', NULL, 'N', '2');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_consignataria', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_responsavel', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_numero', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_identificador', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_servico', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_dataReserva', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_valorParcela', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_valorFolha', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_prazo', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_parcelasPagas', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_capitalDevido', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_carencia', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('listaConsignacaoSer_status', 'S');

