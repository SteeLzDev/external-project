-- DESENV-14907
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO) 
VALUES ('797', 'Chave Spring Vault usada na integração entre Salary Pay e Cielo', 'ALFA', NULL, 'N', 'N', 'N', 'N', NULL);

INSERT INTO tb_tipo_dado_adicional (TDA_CODIGO, TEN_CODIGO, TDA_DESCRICAO, TDA_DOMINIO, TDA_EXPORTA, TDA_SUP_CONSULTA, TDA_CSE_CONSULTA, TDA_CSA_CONSULTA, TDA_SER_CONSULTA, TDA_SUP_ALTERA, TDA_CSE_ALTERA, TDA_CSA_ALTERA, TDA_SER_ALTERA)
VALUES ('48', '19', 'Nome do estabelecimento da transação realizada via SalaryPay/Cielo', 'ALFA', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');

INSERT INTO tb_tipo_dado_adicional (TDA_CODIGO, TEN_CODIGO, TDA_DESCRICAO, TDA_DOMINIO, TDA_EXPORTA, TDA_SUP_CONSULTA, TDA_CSE_CONSULTA, TDA_CSA_CONSULTA, TDA_SER_CONSULTA, TDA_SUP_ALTERA, TDA_CSE_ALTERA, TDA_CSA_ALTERA, TDA_SER_ALTERA)
VALUES ('49', '19', 'Informações do estabelecimento da transação realizada via SalaryPay/Cielo', 'ALFA', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'N');
