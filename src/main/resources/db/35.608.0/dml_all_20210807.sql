-- DESENV-16299
-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS:

update tb_tipo_param_sist_consignante set gps_codigo = '3' where tpc_codigo in ('819','820','822','823','824','825','826','827');
update tb_tipo_param_sist_consignante set gps_codigo = '1' where tpc_codigo in ('830','831','832','833','834','835','836','837','838');
 
-- 2) CONCEDER A PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP:

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera ='S' where tpc_codigo in ('819','820','822','823','824','825','826','827','830','831','832','833','834','835','836','837','838');
 
-- 3) VINCULAR OS PARAMETROS AOS PERFIS:

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('819','820','822','823','824','825','826','827','830','831','832','833','834','835','836','837','838');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('819','820','822','823','824','825','826','827','830','831','832','833','834','835','836','837','838');

insert ignore into tb_perfil_param_sist_cse (tpc_codigo, per_codigo)
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-SEGURANCA_BASICO')
where tpc.tpc_codigo in ('830','831','832','833','834','835','836','837','838');
 
-- 4) CONFIGURAÇÃO DA TB_FUNCAO PARA SEGUNDA SENHA E EXIGE MOTIVO OPERACAO:

update tb_funcao
set FUN_EXIGE_TMO = 'S',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'P',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'P',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'P'
where FUN_CODIGO = '510';

update tb_funcao
set FUN_EXIGE_TMO = 'N',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'P',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'P',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'P'
where FUN_CODIGO = '511';

update tb_funcao
set FUN_EXIGE_TMO = 'S',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'P',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'P',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'P'
where FUN_CODIGO = '513';

update tb_funcao
set FUN_EXIGE_TMO = 'N',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'P',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'P',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'P'
where FUN_CODIGO = '514';

update tb_funcao
set FUN_EXIGE_TMO = 'N',
FUN_EXIGE_SEGUNDA_SENHA_CSE = 'P',
FUN_EXIGE_SEGUNDA_SENHA_SUP = 'S',
FUN_EXIGE_SEGUNDA_SENHA_ORG = 'P',
FUN_EXIGE_SEGUNDA_SENHA_CSA = 'P',
FUN_EXIGE_SEGUNDA_SENHA_COR = 'P'
where FUN_CODIGO = '516';
