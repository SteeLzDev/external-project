-- DESENV-16729
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('867', 'Executar ações por tipo de desconto na importação do retorno', 'SN', 'N', 'N', 'N', 'N', 'N', '2');

INSERT tb_param_sist_consignante (TPC_CODIGO, CSE_CODIGO, PSI_VLR)
VALUES ('867', '1', 'N');


INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO)
VALUES ('8', 'Bloquear Servidor');

INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO)
VALUES ('9', 'Excluir Servidor');

INSERT INTO tb_acao (ACA_CODIGO, ACA_DESCRICAO)
VALUES ('10', 'Registrar Falecimento do Servidor');

