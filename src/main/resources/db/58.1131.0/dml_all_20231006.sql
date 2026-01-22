-- DESENV-20496
UPDATE tb_campo_sistema SET CAS_CHAVE = 'ssoap_consultarMargem_thaHabitacaoCodigo' WHERE CAS_CHAVE = 'soap_consultarMargem_thaHabilitacaoCodigo';
UPDATE tb_campo_sistema SET CAS_CHAVE = 'soap_consultarMargem_thaHabitacaoDescricao' WHERE CAS_CHAVE = 'soap_consultarMargem_thaHabilitacaoDescricao';
UPDATE tb_campo_sistema SET CAS_CHAVE = 'soap_pesquisarServidor_thaHabitacaoCodigo' WHERE CAS_CHAVE = 'soap_pesquisarServidor_thaHabilitacaoCodigo';
UPDATE tb_campo_sistema SET CAS_CHAVE = 'soap_pesquisarServidor_thaHabitacaoDescricao' WHERE CAS_CHAVE = 'soap_pesquisarServidor_thaHabilitacaoDescricao';

