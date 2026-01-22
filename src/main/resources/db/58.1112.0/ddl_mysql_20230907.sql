/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/08/2023 15:10:05                          */
/*==============================================================*/


drop table if exists tmp_tb_convenio_vinculo_registro;

rename table tb_convenio_vinculo_registro to tmp_tb_convenio_vinculo_registro;

/*==============================================================*/
/* Table: tb_convenio_vinculo_registro                          */
/*==============================================================*/
create table tb_convenio_vinculo_registro
(
   VRS_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) not null,
   primary key (VRS_CODIGO, CSA_CODIGO, SVC_CODIGO)
) ENGINE = InnoDB;

insert into tb_convenio_vinculo_registro (VRS_CODIGO, CSA_CODIGO, SVC_CODIGO)
select distinct cvr.vrs_codigo, cnv.csa_codigo, cnv.svc_codigo
from tmp_tb_convenio_vinculo_registro cvr
inner join tb_convenio cnv on (cnv.cnv_codigo = cvr.cnv_codigo);

drop table if exists tmp_tb_convenio_vinculo_registro;

alter table tb_convenio_vinculo_registro add constraint FK_TB_CONVE_R_150_TB_VINCU foreign key (VRS_CODIGO)
      references tb_vinculo_registro_servidor (VRS_CODIGO) on delete restrict on update restrict;

alter table tb_convenio_vinculo_registro add constraint FK_R_931 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_convenio_vinculo_registro add constraint FK_R_932 foreign key (SVC_CODIGO)
      references tb_servico (SVC_CODIGO) on delete restrict on update restrict;

