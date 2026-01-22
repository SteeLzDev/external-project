package com.zetra.econsig.config;

/**
 * <p> Title: CustomClassPathResourceManager</p>
 * <p> Description: Customização ClassPathResourceManager do Undertow para recuperar
 * os arquivos de tags jsp (ex.: empty_v4.tag) através do classpath, quando executado
 * em modo WAR pela linha de comando.</p>
 * <p> Copyright: Copyright (c) 2022</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CustomClassPathResourceManager { //extends ClassPathResourceManager {
//    private static final String JSP_TAG_FILES_LOCATION = "WEB-INF/tags";
//
//    private final Map<String, JarEntry> jarEntries;
//    private final ClassLoader classLoader;
//    private FileSystem fs;
//
//    public CustomClassPathResourceManager(final Class<?> clazz) {
//        super(clazz.getClassLoader());
//        classLoader = clazz.getClassLoader();
//        jarEntries = new HashMap<>();
//        try {
//            URL urlBase = clazz.getClassLoader().getResource("/");
//
//            final Map<String, String> env = new HashMap<>();
//            final String[] array = urlBase.toURI().toString().split("!");
//            fs = FileSystems.newFileSystem(URI.create(array[0]), env);
//
//            String urlString = urlBase.toString();
//            String filePath = urlString.substring(urlString.indexOf(File.separatorChar), urlString.indexOf('!'));
//            JarFile jarFile = new JarFile(filePath);
//            final Enumeration<JarEntry> entries = jarFile.entries();
//            while (entries.hasMoreElements()) {
//                JarEntry entry = entries.nextElement();
//                String entryName = entry.getName();
//                if (entryName.indexOf(JSP_TAG_FILES_LOCATION) >= 0) {
//                    jarEntries.put(entryName, entry);
//                }
//            }
//            jarFile.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public Resource getResource(final String path) throws IOException {
//        Resource resource = super.getResource(path);
//        if (resource != null && path.indexOf(JSP_TAG_FILES_LOCATION) >= 0) {
//            return new CustomURLResource(resource.getUrl(), path);
//        }
//        return resource;
//    }
//
//    public class CustomURLResource extends URLResource {
//        public CustomURLResource(URL url, String path) {
//            super(url, path);
//        }
//
//        private JarEntry getEntry() {
//            String path = getPath();
//            JarEntry entry = jarEntries.get(path);
//            if (entry == null) {
//                if (path.startsWith("/")) {
//                    path = path.substring(1);
//                } else {
//                    path = "/" + path;
//                }
//                entry = jarEntries.get(path);
//            }
//            if (entry == null) {
//                if (path.endsWith("/")) {
//                    path = path.substring(0, path.length() - 1);
//                } else {
//                    path += "/";
//                }
//                entry = jarEntries.get(path);
//            }
//            return entry;
//        }
//
//        @Override
//        public boolean isDirectory() {
//            JarEntry entry = getEntry();
//            return entry == null || entry.isDirectory();
//        }
//
//        @Override
//        public Path getFilePath() {
//            JarEntry entry = getEntry();
//            if (entry != null && !entry.isDirectory()) {
//                try {
//                    return fs.getPath("/", entry.getName());
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    return null;
//                }
//            }
//            return null;
//        }
//
//        @Override
//        public File getFile() {
//            return null;
//        }
//
//        @Override
//        public List<Resource> list() {
//            final String path = getPath().charAt(0) == '/' ? getPath().substring(1) : getPath();
//
//            List<Resource> result = new LinkedList<>();
//            jarEntries.keySet().stream()
//                      .filter(n -> n.startsWith(path))
//                      .forEach(n -> result.add(new CustomURLResource(classLoader.getResource(n), n)))
//                      ;
//
//            return result;
//        }
//    }
}
