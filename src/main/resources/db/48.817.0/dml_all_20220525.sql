-- DESENV-17678
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('879', 'Exibe quadro de configuração do sistema na tela inicial do portal do servidor', 'SN', 'N', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR) VALUES ('879', '1', 'N');

INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_cancelamentoAutomatico', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_configCompraContrato', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_configRenegociacao', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_diaCorte', 'S');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_diaCorteCsa', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_diaCorteOrgaos', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_diaRepasse', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_diaRepasseOrgaos', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_nivelSeguranca', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_periodoAtual', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_periodoOrgaos', 'N');
INSERT INTO tb_campo_sistema (CAS_CHAVE, CAS_VALOR) VALUES ('ser.configSistema_taxasServicos', 'N');