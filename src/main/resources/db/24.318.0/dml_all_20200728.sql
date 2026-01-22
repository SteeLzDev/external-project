-- DESENV-13833
INSERT INTO tb_tipo_param_sist_consignante (TPC_CODIGO, TPC_DESCRICAO, TPC_DOMINIO, TPC_VLR_DEFAULT, TPC_CSE_ALTERA, TPC_CSE_CONSULTA, TPC_SUP_ALTERA, TPC_SUP_CONSULTA, GPS_CODIGO)
VALUES ('760', 'Dia da semana considerado para a contagem das semanas do período', CONCAT('ESCOLHA[1=Domingo', 0x3b, '2=Segunda', 0x3b, '3=Terça', 0x3b, '4=Quarta', 0x3b, '5=Quinta', 0x3b, '6=Sexta', 0x3b, '7=Sábado]'), '6', 'N', 'N', 'N', 'N', NULL); 

INSERT INTO tb_tipo_param_orgao (TAO_CODIGO, TAO_DESCRICAO, TAO_DOMINIO, TAO_VLR_DEFAULT, TAO_SUP_ALTERA, TAO_SUP_CONSULTA, TAO_CSE_ALTERA, TAO_CSE_CONSULTA, TAO_ORG_ALTERA, TAO_ORG_CONSULTA) 
VALUES ('4', 'Dia da semana considerado para a contagem das semanas do período', CONCAT('ESCOLHA[1=Domingo', 0x3b, '2=Segunda', 0x3b, '3=Terça', 0x3b, '4=Quarta', 0x3b, '5=Quinta', 0x3b, '6=Sexta', 0x3b, '7=Sábado]'), '6', 'N', 'N', 'N', 'N', 'N', 'N');
