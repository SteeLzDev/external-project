package com.zetra.econsig.test;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.helper.arquivo.FileHelper;

@SuppressWarnings("all")
public class Auditor {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Auditor.class);

    // SRC Path
    private static final String JAVA_PATH = "/home/igor/WorkDir/Zetra/eConsig/src/main/java/";
    private static final String JAVA_SUFFIX = ".java";

    // JSP Path
    private static final String JSP_PATH = "/home/igor/WorkDir/Zetra/eConsig/src/main/webapp/";
    private static final String JSP_SUFFIX = ".jsp";

    // DAO's
    private static final String DAO_PATH = "/home/igor/WorkDir/Zetra/eConsig/src/main/java/com/zetra/econsig/persistence/dao";
    private static final String DAO_PACKAGE = "com.zetra.econsig.persistence.dao";
    private static final String DAO_PREFIX = "MySql";
    private static final String DAO_SUFFIX = "DAO.java";

    // Session Façades
    private static final String FACADE_PATH = "/home/igor/WorkDir/Zetra/eConsig/src/main/java/com/zetra/econsig/service";
    private static final String FACADE_PREFIX = null;
    private static final String FACADE_SUFFIX = "ControllerBean.java";

    // Delegates
    private static final String DELEGATE_PATH = "/home/igor/WorkDir/Zetra/eConsig/src/main/java/com/zetra/econsig/delegate";
    private static final String DELEGATE_PREFIX = null;
    private static final String DELEGATE_SUFFIX = "Delegate.java";

    private static List<File> listarArquivos(final String path, final String prefix, final String suffix) {
        FileFilter filter = file -> file.isDirectory() || (
               (prefix == null || file.getName().startsWith(prefix)) &&
               (suffix == null || file.getName().endsWith(suffix)));
        List<File> files = new ArrayList<>();

        File[] classes = new File(path).listFiles(filter);
        for (File classe : classes) {
            if (classe.isDirectory()) {
                files.addAll(listarArquivos(classe.getAbsolutePath(), prefix, suffix));
            } else {
                files.add(classe);
            }
        }

        return files;
    }

    private static void procurarMetodosNaoUtilizadosDAO() {
        List<File> classes = listarArquivos(DAO_PATH, DAO_PREFIX, DAO_SUFFIX);

        for (File classe : classes) {
            try {
                String nomeClasse = classe.getAbsolutePath().substring(classe.getAbsolutePath().indexOf("com/")).replace('/', '.').replaceFirst(".java", "");
                Class mysqldao = Class.forName(nomeClasse);

                if (!Modifier.isAbstract(mysqldao.getModifiers())) {
                    Class[] interfaces = mysqldao.getInterfaces();
                    Class dao = null;

                    for (Class interface1 : interfaces) {
                        if (interface1.getPackage().getName().equals(DAO_PACKAGE)) {
                            dao = interface1;
                        }
                    }

                    if (dao != null) {
                        //LOG.debug("Comparando: [" + dao.getName() + "] [" + mysqldao.getName() + "]");

                        // Pega os métodos do mysqldao
                        Method[] metodos = mysqldao.getDeclaredMethods();

                        for (Method metodo : metodos) {
                            if (Modifier.isPublic(metodo.getModifiers()) &&
                                    !Modifier.isStatic(metodo.getModifiers())) {
                                // O método é público, pesquisa na interface
                                try {
                                    dao.getMethod(metodo.getName(), metodo.getParameterTypes());
                                } catch (SecurityException e) {
                                    LOG.error(e.getMessage(), e);
                                } catch (NoSuchMethodException e) {
                                    LOG.error("Método público não encontratdo na interface: [" + metodo + "]");
                                }

                                // Pesquisa nos demais arquivos para saber se é usado
                                if (!procurarConteudoArquivos(metodo.getName(), false, "DAO.java")) {
                                    LOG.error("Método público não utilizado: [" + metodo + "]");
                                }
                            }
                        }
                    } else {
                        //LOG.error("Não encontrou a interface para o dao [" + mysqldao.getName() + "]");
                    }
                } else {
                    //LOG.error("O dao [" + mysqldao.getName() + "] é uma classe abstrata");
                }

            } catch (ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static void procurarMetodosNaoUtilizadosFacade() {
        List<File> classes = listarArquivos(FACADE_PATH, FACADE_PREFIX, FACADE_SUFFIX);

        for (File classe : classes) {
            try {
                String nomeClasse = classe.getAbsolutePath().substring(classe.getAbsolutePath().indexOf("com/")).replace('/', '.').replaceFirst(".java", "");
                Class facadeBean = Class.forName(nomeClasse);

                if (!Modifier.isAbstract(facadeBean.getModifiers())) {
                    String nomeInterface = nomeClasse.replaceFirst("Bean", "");
                    Class facade = Class.forName(nomeInterface);

                    //LOG.debug("Comparando: [" + facade.getName() + "] [" + facadeBean.getName() + "]");

                    // Pega os métodos do facadeBean
                    Method[] metodos = facadeBean.getDeclaredMethods();

                    for (Method metodo : metodos) {
                        if (Modifier.isPublic(metodo.getModifiers()) &&
                                !Modifier.isStatic(metodo.getModifiers())) {
                            // O método não é público nem estático
                            if (!metodo.getName().startsWith("ejb") &&
                                    !metodo.getName().equals("setSessionContext")) {
                                try {
                                    facade.getMethod(metodo.getName(), metodo.getParameterTypes());
                                } catch (SecurityException e) {
                                    LOG.error(e.getMessage(), e);
                                } catch (NoSuchMethodException e) {
                                    LOG.error("Método público não encontratdo na interface: [" + metodo + "]");
                                }

                                // Pesquisa nos demais arquivos para saber se é usado
                                if (!procurarConteudoArquivos(metodo.getName(), true, classe.getName().replaceFirst("Bean.java", ""), "Delegate.java")) {
                                    LOG.error("Método público não utilizado: [" + metodo + "]");
                                }

                            }
                        }
                    }
                } else {
                    //LOG.error("O façade [" + facadeBean.getName() + "] é uma classe abstrata");
                }

            } catch (ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static void procurarMetodosNaoUtilizadosDelegate() {
        List<File> classes = listarArquivos(DELEGATE_PATH, DELEGATE_PREFIX, DELEGATE_SUFFIX);

        for (File classe : classes) {
            try {
                String nomeClasse = classe.getAbsolutePath().substring(classe.getAbsolutePath().indexOf("com/")).replace('/', '.').replaceFirst(".java", "");
                if (!nomeClasse.contains("LogDelegate")) {
                    Class delegate = Class.forName(nomeClasse);

                    if (!Modifier.isAbstract(delegate.getModifiers())) {
                        Method[] metodos = delegate.getDeclaredMethods();

                        for (Method metodo : metodos) {
                            if (Modifier.isPublic(metodo.getModifiers()) &&
                                    !Modifier.isStatic(metodo.getModifiers())) {
                                // Pesquisa nos demais arquivos para saber se é usado
                                if (!procurarConteudoArquivos(metodo.getName(), true, "Delegate.java", "Controller.java", "ControllerBean.java")) {
                                    LOG.error("Método público não utilizado: [" + metodo + "]");
                                }
                            }
                        }
                    } else {
                        //LOG.error("O façade [" + facadeBean.getName() + "] é uma classe abstrata");
                    }
                }
            } catch (ClassNotFoundException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    private static boolean procurarConteudoArquivos(String nomeMetodo, boolean procurarPaginas, String ...ignorar) {
        List<File> classes = listarArquivos(JAVA_PATH, null, JAVA_SUFFIX);

        boolean encontrou = false;
        for (File classe : classes) {
            boolean pular = false;
            for (String chave : ignorar) {
                if (classe.getName().indexOf(chave) != -1) {
                    pular = true;
                }
            }
            if (!pular) {
                String conteudo = FileHelper.readAll(classe.getAbsolutePath());
                if (conteudo.indexOf(nomeMetodo) != -1) {
                    encontrou = true;
                    break;
                }
            }
        }

        if (!encontrou && procurarPaginas) {
            List<File> paginas = listarArquivos(JSP_PATH, null, JSP_SUFFIX);

            for (File pagina : paginas) {
                String conteudo = FileHelper.readAll(pagina.getAbsolutePath());
                if (conteudo.indexOf(nomeMetodo) != -1) {
                    encontrou = true;
                    break;
                }
            }
        }

        return encontrou;
    }

    public static void main(String[] args) {
        LOG.debug("INICIO");
        procurarMetodosNaoUtilizadosDAO();
        procurarMetodosNaoUtilizadosFacade();
        procurarMetodosNaoUtilizadosDelegate();
        LOG.debug("FIM");
    }
}
