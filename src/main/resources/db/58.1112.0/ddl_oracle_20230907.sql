/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     07/08/2023 15:41:28                          */
/*==============================================================*/


alter table tb_convenio_vinculo_registro
   drop constraint fk_tb_conve_r_149_tb_conve;

alter table tb_convenio_vinculo_registro
   drop constraint fk_tb_conve_r_150_tb_vincu;

drop index r_149_fk;

drop index r_150_fk;

alter table tb_convenio_vinculo_registro
   drop primary key cascade;

CALL dropTableIfExists('tmp_tb_convenio_vinculo_reg');

rename tb_convenio_vinculo_registro to tmp_tb_convenio_vinculo_reg;

/*==============================================================*/
/* Table: tb_convenio_vinculo_registro                        */
/*==============================================================*/
create table tb_convenio_vinculo_registro  (
   vrs_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   svc_codigo           varchar2(32)                    not null,
   constraint pk_tb_convenio_vinculo_reg primary key (vrs_codigo, csa_codigo, svc_codigo)
);

insert into tb_convenio_vinculo_registro (vrs_codigo, csa_codigo, svc_codigo)
select distinct cvr.vrs_codigo, cnv.csa_codigo, cnv.svc_codigo
from tmp_tb_convenio_vinculo_reg cvr
inner join tb_convenio cnv on (cnv.cnv_codigo = cvr.cnv_codigo);

CALL dropTableIfExists('tmp_tb_convenio_vinculo_reg');

/*==============================================================*/
/* Index: r_150_fk                                              */
/*==============================================================*/
create index r_150_fk on tb_convenio_vinculo_registro (
   vrs_codigo asc
);

/*==============================================================*/
/* Index: r_931_fk                                              */
/*==============================================================*/
create index r_931_fk on tb_convenio_vinculo_registro (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_932_fk                                              */
/*==============================================================*/
create index r_932_fk on tb_convenio_vinculo_registro (
   svc_codigo asc
);

alter table tb_convenio_vinculo_registro
   add constraint fk_tb_conve_r_150_tb_vincu foreign key (vrs_codigo)
      references tb_vinculo_registro_servidor (vrs_codigo);

alter table tb_convenio_vinculo_registro
   add constraint fk_tb_conve_r_931_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_convenio_vinculo_registro
   add constraint fk_tb_conve_r_932_tb_servi foreign key (svc_codigo)
      references tb_servico (svc_codigo);

