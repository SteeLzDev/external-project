/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     05/06/2024 14:02:24                          */
/*==============================================================*/


alter table tb_consignataria
   add CSA_CONSULTA_MARGEM_SEM_SENHA char(1) not null default 'N';

/*==============================================================*/
/* Table: tb_consulta_margem_sem_senha                          */
/*==============================================================*/
create table tb_consulta_margem_sem_senha
(
   CSS_CODIGO           varchar(32) not null,
   RSE_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   CSS_DATA_INI         datetime not null,
   CSS_DATA_FIM         datetime not null,
   CSS_DATA_REVOGACAO_SUP datetime,
   CSS_DATA_REVOGACAO_SER datetime,
   CSS_DATA_ALERTA      datetime,
   primary key (CSS_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_consulta_margem_sem_senha add constraint FK_R_959 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

alter table tb_consulta_margem_sem_senha add constraint FK_R_960 foreign key (RSE_CODIGO)
      references tb_registro_servidor (RSE_CODIGO) on delete restrict on update restrict;

