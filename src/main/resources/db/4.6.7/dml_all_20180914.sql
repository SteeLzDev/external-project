-- DESENV-9391

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA) 
VALUES ('603', NULL, 'Nome do Arquivo de configuração da entrada na exportação de contratos beneficia operadora', 'ALFA', 'N', 'N', 'exp_integracao_operadora_entrada.xml', 'N', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA) 
VALUES ('604', NULL, 'Nome do Arquivo de configuração da saida na exportação de contratos beneficia operadora', 'ALFA', 'N', 'N', 'exp_integracao_operadora_saida.xml', 'N', 'N');

INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, GPS_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_VLR_DEFAULT, TPC_SUP_ALTERA, TPC_SUP_CONSULTA) 
VALUES ('605', NULL, 'Nome do Arquivo de configuração da tradutor na exportação de contratos beneficia operadora', 'ALFA', 'N', 'N', 'exp_integracao_operadora_tradutor.xml', 'N', 'N');

INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('158', 'Alteração do Status do Contrato Beneficio'); 
