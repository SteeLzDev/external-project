-- DESENV-13238
update tb_tipo_param_sist_consignante set TPC_DOMINIO = 'INT' where TPC_CODIGO = '652';
update tb_param_sist_consignante set PSI_VLR = '1' where TPC_CODIGO = '652' and (PSI_VLR REGEXP '^[0-9]+$') = 0;
