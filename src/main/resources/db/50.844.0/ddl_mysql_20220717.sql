/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/04/2022 14:11:49                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_recurso_sistema                                    */
/*==============================================================*/
create table tb_recurso_sistema
(
   RES_CHAVE            varchar(200) not null,
   RES_CONTEUDO         longtext not null,
   primary key (RES_CHAVE)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

