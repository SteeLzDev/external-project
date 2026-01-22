-- DESENV-10658

-- 1) ATUALIZAR O GRUPO DE PARAMETROS NOS NOVOS PARAMETROS:

update tb_tipo_param_sist_consignante set gps_codigo = '1' where tpc_codigo in ('616','617','618');
update tb_tipo_param_sist_consignante set gps_codigo = '2' where tpc_codigo in ('621','622','623','624');
update tb_tipo_param_sist_consignante set gps_codigo = '3' where tpc_codigo in ('495','619','625','626','627');

-- 2) CONCEDER A PERMISSAO DE VISUALIZAR E ALTERAR PARA USUÁRIOS SUP:

update tb_tipo_param_sist_consignante set tpc_sup_consulta = 'S', tpc_sup_altera ='S' where tpc_codigo in ('495','616','617','618','619','621','622','623','624','625','626','627');

-- 3) VINCULAR OS PARAMETROS AOS PERFIS:

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-SEGURANCA_BASICO')
where tpc.tpc_codigo in ('616','617','618');

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-IMP_AVANCADO')
where tpc.tpc_codigo in ('495','619','621','622','623','624','625','626','627');

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-MANUTENCAO')
where tpc.tpc_codigo in ('495','619','621','622','623','624','625','626','627');

insert ignore into tb_perfil_param_sist_cse
select tpc.tpc_codigo, per.per_codigo
from tb_tipo_param_sist_consignante tpc
inner join tb_perfil per on (per.per_codigo = 'ZETRA-OPERACAO')
where tpc.tpc_codigo in ('621','622','623','624');
