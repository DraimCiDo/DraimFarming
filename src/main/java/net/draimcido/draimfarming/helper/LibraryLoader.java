package net.draimcido.draimfarming.helper;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import net.draimcido.draimfarming.Main;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Objects;

public final class LibraryLoader {

    @SuppressWarnings("Guava")
    private static final Supplier<URLClassLoaderAccess> URL_INJECTOR = Suppliers.memoize(() -> URLClassLoaderAccess.create((URLClassLoader) Main.plugin.getClass().getClassLoader()));

    public static void loadAll(Object object) {
        loadAll(object.getClass());
    }

    public static void loadAll(Class<?> clazz) {
        MavenLibrary[] libs = clazz.getDeclaredAnnotationsByType(MavenLibrary.class);
        if (libs == null) {
            return;
        }

        for (MavenLibrary lib : libs) {
            load(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().url());
        }
    }

    public static void load(String groupId, String artifactId, String version, String repoUrl) {
        load(new Dependency(groupId, artifactId, version, repoUrl));
    }

    public static void load(Dependency d) {
        String name = d.getArtifactId() + "-" + d.getVersion();

        File saveLocation = new File(getLibFolder(d), name + ".jar");
        if (!saveLocation.exists()) {

            try {
                Log.info("Dependency '" + name + "' is not already in the libraries folder. Attempting to download...");
                URL url = d.getUrl();

                try (InputStream is = url.openStream()) {
                    Files.copy(is, saveLocation.toPath());
                    Log.info("Dependency '" + name + "' successfully downloaded.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!saveLocation.exists()) {
            throw new RuntimeException("Unable to download dependency: " + d.toString());
        }

        try {
            URL_INJECTOR.get().addURL(saveLocation.toURI().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dependency: " + saveLocation.toString(), e);
        }
    }

    private static File getLibFolder(Dependency dependency) {
        File pluginDataFolder = Main.plugin.getDataFolder();
        File serverDir = pluginDataFolder.getParentFile().getParentFile();

        File helperDir = new File(serverDir, "libraries");
        String[] split = StringUtils.split(dependency.getGroupId(), ".");
        File jarDir;
        if (split.length > 1){
            jarDir = new File(helperDir, split[0] + File.separator + split[1] + File.separator + dependency.artifactId + File.separator + dependency.version );
        }else {
            jarDir = new File(helperDir, dependency.getGroupId() + File.separator + dependency.artifactId + File.separator + dependency.version );
        }
        jarDir.mkdirs();
        return jarDir;
    }

    public static final class Dependency {
        private final String groupId;
        private final String artifactId;
        private final String version;
        private final String repoUrl;

        public Dependency(String groupId, String artifactId, String version, String repoUrl) {
            this.groupId = Objects.requireNonNull(groupId, "groupId");
            this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
            this.version = Objects.requireNonNull(version, "version");
            this.repoUrl = Objects.requireNonNull(repoUrl, "repoUrl");
        }

        public String getGroupId() {
            return this.groupId;
        }

        public String getArtifactId() {
            return this.artifactId;
        }

        public String getVersion() {
            return this.version;
        }

        public String getRepoUrl() {
            return this.repoUrl;
        }

        public URL getUrl() throws MalformedURLException {
            String repo = this.repoUrl;
            if (!repo.endsWith("/")) {
                repo += "/";
            }
            repo += "%s/%s/%s/%s-%s.jar";

            String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
            return new URL(url);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Dependency)) return false;
            final Dependency other = (Dependency) o;
            return this.getGroupId().equals(other.getGroupId()) &&
                    this.getArtifactId().equals(other.getArtifactId()) &&
                    this.getVersion().equals(other.getVersion()) &&
                    this.getRepoUrl().equals(other.getRepoUrl());
        }

        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + this.getGroupId().hashCode();
            result = result * PRIME + this.getArtifactId().hashCode();
            result = result * PRIME + this.getVersion().hashCode();
            result = result * PRIME + this.getRepoUrl().hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "LibraryLoader.Dependency(" +
                    "groupId=" + this.getGroupId() + ", " +
                    "artifactId=" + this.getArtifactId() + ", " +
                    "version=" + this.getVersion() + ", " +
                    "repoUrl=" + this.getRepoUrl() + ")";
        }
    }
}
