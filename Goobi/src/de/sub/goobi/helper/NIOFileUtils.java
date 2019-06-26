package de.sub.goobi.helper;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information.
 *     		- https://goobi.io
 * 			- https://www.intranda.com
 * 			- https://github.com/intranda/goobi
 * 			- http://digiverso.com
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Linking this library statically or dynamically with other modules is making a combined work based on this library. Thus, the terms and conditions
 * of the GNU General Public License cover the whole combination. As a special exception, the copyright holders of this library give you permission to
 * link this library with independent modules to produce an executable, regardless of the license terms of these independent modules, and to copy and
 * distribute the resulting executable under terms of your choice, provided that you also meet, for each linked independent module, the terms and
 * conditions of the license of that module. An independent module is a module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but you are not obliged to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.CRC32;

import org.apache.commons.io.FilenameUtils;

import de.sub.goobi.config.ConfigurationHelper;
import lombok.extern.log4j.Log4j;

/**
 * File Utils collection
 * 
 * @author Steffen Hankiewicz
 */
@Log4j
public class NIOFileUtils implements StorageProviderInterface {

    public static final CopyOption[] STANDARD_COPY_OPTIONS =
            new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES };

    /**
     * calculate all files with given file extension at specified directory recursivly
     * 
     * @param inDir the directory to run through
     * @param ext the file extension to use for counting, not case sensitive
     * @return number of files as Integer
     */
    @Override
    public Integer getNumberOfFiles(Path inDir) {
        int anzahl = 0;
        if (Files.isDirectory(inDir)) {
            /* --------------------------------
             * die Images zählen
             * --------------------------------*/
            anzahl = list(inDir.toString(), DATA_FILTER).size();

            /* --------------------------------
             * die Unterverzeichnisse durchlaufen
             * --------------------------------*/
            List<String> children = this.list(inDir.toString());
            for (String child : children) {
                anzahl += getNumberOfPaths(Paths.get(inDir.toString(), child));
            }
        }
        return anzahl;
    }

    @Override
    public Integer getNumberOfPaths(Path inDir) {
        int anzahl = 0;
        if (Files.isDirectory(inDir)) {
            /* --------------------------------
             * die Images zählen
             * --------------------------------*/
            anzahl = list(inDir.toString(), DATA_FILTER).size();

            /* --------------------------------
             * die Unterverzeichnisse durchlaufen
             * --------------------------------*/
            List<String> children = this.list(inDir.toString());
            for (String child : children) {
                anzahl += getNumberOfPaths(Paths.get(inDir.toString(), child));
            }
        }
        return anzahl;
    }

    @Override
    public Integer getNumberOfFiles(String inDir) {
        return getNumberOfFiles(Paths.get(inDir));
    }

    @Override
    public Integer getNumberOfFiles(Path dir, final String suffix) {
        int anzahl = 0;
        if (Files.isDirectory(dir)) {
            /* --------------------------------
             * die Images zählen
             * --------------------------------*/
            anzahl = list(dir.toString(), new DirectoryStream.Filter<Path>() {
                @Override
                public boolean accept(Path path) {
                    return path.getFileName()
                            .endsWith(suffix);
                }
            }

                    ).size();

            /* --------------------------------
             * die Unterverzeichnisse durchlaufen
             * --------------------------------*/
            List<String> children = this.list(dir.toString());
            for (String child : children) {
                anzahl += getNumberOfFiles(Paths.get(dir.toString(), child), suffix);
            }
        }
        return anzahl;

    }

    // replace listFiles
    @Override
    public List<Path> listFiles(String folder) {
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folder))) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException ex) {
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    // replace listFiles(FilenameFilter)
    @Override
    public List<Path> listFiles(String folder, DirectoryStream.Filter<Path> filter) {
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folder), filter)) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException ex) {
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    // replace list
    @Override
    public List<String> list(String folder) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folder))) {
            for (Path path : directoryStream) {
                fileNames.add(path.getFileName()
                        .toString());
            }
        } catch (IOException ex) {
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    // replace list(FilenameFilter)
    @Override
    public List<String> list(String folder, DirectoryStream.Filter<Path> filter) {
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(folder), filter)) {
            for (Path path : directoryStream) {
                fileNames.add(path.getFileName()
                        .toString());
            }
        } catch (IOException ex) {
        }
        Collections.sort(fileNames);
        return fileNames;
    }

    public static boolean checkImageType(String name) {
        boolean fileOk = false;
        String prefix = ConfigurationHelper.getInstance().getImagePrefix();
        if (name.matches(prefix + "\\.[Tt][Ii][Ff][Ff]?")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[jJ][pP][eE]?[gG]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[jJ][pP][2]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[pP][nN][gG]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[gG][iI][fF]")) {
            fileOk = true;
        }
        return fileOk;
    }

    public static boolean check3DType(String name) {
        boolean fileOk = false;
        String prefix = ConfigurationHelper.getInstance().getImagePrefix();
        if (name.matches(prefix + "\\.[Oo][bB][jJ]?")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[pP][lL][yY]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[sS][tT][lL]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[fF][bB][xX]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.3[dD][sS]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[xX]3[dD]")) {
            fileOk = true;
        } else if (name.matches(prefix + "\\.[Gg][Ll][Tt][Ff]")) {
            fileOk = true;
        }
        return fileOk;
    }

    @Override
    public List<String> listDirNames(String folder) {
        return this.list(folder, folderFilter);
    }

    public static final DirectoryStream.Filter<Path> imageNameFilter = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path path) {
            return checkImageType(path.getFileName().toString());
        }
    };

    public static final DirectoryStream.Filter<Path> objectNameFilter = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path path) {
            String prefix = ConfigurationHelper.getInstance()
                    .getImagePrefix();
            String name = path.getFileName()
                    .toString();
            boolean fileOk = false;
            if (name.matches(prefix + "\\.[Oo][bB][jJ]?")) {
                fileOk = true;
            } else if (name.matches(prefix + "\\.[pP][lL][yY]")) {
                fileOk = true;
            } else if (name.matches(prefix + "\\.[sS][tT][lL]")) {
                fileOk = true;
            } else if (name.matches(prefix + "\\.[fF][bB][xX]")) {
                fileOk = true;
            } else if (name.matches(prefix + "\\.3[dD][sS]")) {
                fileOk = true;
            } else if (name.matches(prefix + "\\.[xX]3[dD]")) {
                fileOk = true;
            } else if (name.matches(prefix + "\\.[Gg][Ll][Tt][Ff]")) {
                fileOk = true;
            }
            return fileOk;
        }
    };

    public static final class ObjectHelperNameFilter implements DirectoryStream.Filter<Path> {

        private String mainFileBaseName;

        public ObjectHelperNameFilter(Path objFilePath) {
            this.mainFileBaseName = FilenameUtils.getBaseName(objFilePath.getFileName()
                    .toString());
        }

        @Override
        public boolean accept(Path path) {
            String baseName = FilenameUtils.getBaseName(path.getFileName()
                    .toString());
            boolean fileOk = false;
            if (baseName.equals(mainFileBaseName)) {
                String prefix = ConfigurationHelper.getInstance()
                        .getImagePrefix();
                String name = path.getFileName()
                        .toString();
                if (name.matches(prefix + "\\.[mM][tT][lL]?")) {
                    fileOk = true;
                } else if (name.matches(prefix + "\\.[jJ][pP][eE]?[gG]")) {
                    fileOk = true;
                } else if (name.matches(prefix + "\\.[pP][nN][gG]")) {
                    fileOk = true;
                } else if (name.matches(prefix + "\\.[xX]3[dD]")) {
                    fileOk = true;
                } else if (name.matches(prefix + "\\.[Bb][Ii][Nn]")) {
                    fileOk = true;
                }
            }
            return fileOk;
        }
    };

    public static final DirectoryStream.Filter<Path> imageOrObjectNameFilter = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path path) throws IOException {
            return imageNameFilter.accept(path) || objectNameFilter.accept(path);
        }
    };

    public static final DirectoryStream.Filter<Path> folderFilter = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path path) {
            return path.toFile()
                    .isDirectory();
        }
    };

    public static final DirectoryStream.Filter<Path> fileFilter = new DirectoryStream.Filter<Path>() {
        @Override
        public boolean accept(Path path) {
            return path.toFile()
                    .isFile();
        }
    };

    public static final DirectoryStream.Filter<Path> DATA_FILTER = new DirectoryStream.Filter<Path>() {

        @Override
        public boolean accept(Path path) {
            String name = path.getFileName().toString();
            return StorageProvider.dataFilterString(name);
        }

    };

    @Override
    public void copyDirectory(final Path source, final Path target) throws IOException {
        Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes sourceBasic) throws IOException {
                Path targetDir = Files.createDirectories(target.resolve(source.relativize(dir)));
                FileStore fileStore = Files.getFileStore(targetDir);
                AclFileAttributeView acl = Files.getFileAttributeView(dir, AclFileAttributeView.class);
                if (acl != null) {
                    if (fileStore.supportsFileAttributeView(AclFileAttributeView.class)) {
                        AclFileAttributeView aclFileAttributeView = Files.getFileAttributeView(targetDir, AclFileAttributeView.class);
                        aclFileAttributeView.setAcl(acl.getAcl());
                    }
                }

                DosFileAttributeView dosAttrs = Files.getFileAttributeView(dir, DosFileAttributeView.class);
                if (dosAttrs != null) {
                    if (fileStore.supportsFileAttributeView(DosFileAttributeView.class)) {
                        DosFileAttributes sourceDosAttrs = dosAttrs.readAttributes();
                        DosFileAttributeView targetDosAttrs = Files.getFileAttributeView(targetDir, DosFileAttributeView.class);
                        targetDosAttrs.setArchive(sourceDosAttrs.isArchive());
                        targetDosAttrs.setHidden(sourceDosAttrs.isHidden());
                        targetDosAttrs.setReadOnly(sourceDosAttrs.isReadOnly());
                        targetDosAttrs.setSystem(sourceDosAttrs.isSystem());
                    }
                }
                FileOwnerAttributeView ownerAttrs = Files.getFileAttributeView(dir, FileOwnerAttributeView.class);
                if (ownerAttrs != null) {
                    if (fileStore.supportsFileAttributeView(FileOwnerAttributeView.class)) {
                        FileOwnerAttributeView targetOwner = Files.getFileAttributeView(targetDir, FileOwnerAttributeView.class);
                        targetOwner.setOwner(ownerAttrs.getOwner());
                    }
                }
                PosixFileAttributeView posixAttrs = Files.getFileAttributeView(dir, PosixFileAttributeView.class);
                if (posixAttrs != null) {
                    if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
                        PosixFileAttributes sourcePosix = posixAttrs.readAttributes();
                        PosixFileAttributeView targetPosix = Files.getFileAttributeView(targetDir, PosixFileAttributeView.class);
                        targetPosix.setPermissions(sourcePosix.permissions());
                        targetPosix.setGroup(sourcePosix.group());
                    }
                }
                UserDefinedFileAttributeView userAttrs = Files.getFileAttributeView(dir, UserDefinedFileAttributeView.class);
                if (userAttrs != null) {
                    if (fileStore.supportsFileAttributeView(UserDefinedFileAttributeView.class)) {
                        UserDefinedFileAttributeView targetUser = Files.getFileAttributeView(targetDir, UserDefinedFileAttributeView.class);
                        for (String key : userAttrs.list()) {
                            ByteBuffer buffer = ByteBuffer.allocate(userAttrs.size(key));
                            userAttrs.read(key, buffer);
                            buffer.flip();
                            targetUser.write(key, buffer);
                        }
                    }
                }
                // Must be done last, otherwise last-modified time may be
                // wrong
                BasicFileAttributeView targetBasic = Files.getFileAttributeView(targetDir, BasicFileAttributeView.class);
                if (targetBasic != null) {
                    targetBasic.setTimes(sourceBasic.lastModifiedTime(), sourceBasic.lastAccessTime(), sourceBasic.creationTime());
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)), STANDARD_COPY_OPTIONS);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
                throw e;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                if (e != null) {
                    throw e;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public void uploadDirectory(final Path source, final Path target) throws IOException {
        copyDirectory(source, target);
    }

    @Override
    public void downloadDirectory(final Path source, final Path target) throws IOException {
        copyDirectory(source, target);
    }

    @Override
    public Path renameTo(Path oldName, String newNameString) throws IOException {
        if (newNameString == null || newNameString.isEmpty() || oldName == null) {
            return null;
        }
        return Files.move(oldName, oldName.resolveSibling(newNameString));
    }

    // program options initialized to default values
    private int bufferSize = 4 * 1024;

    @Override
    public void copyFile(Path srcFile, Path destFile) throws IOException {
        Files.copy(srcFile, destFile, STANDARD_COPY_OPTIONS);
    }

    @Override
    public Long createChecksum(Path file) throws IOException {
        InputStream in = new FileInputStream(file.toString());
        CRC32 checksum = new CRC32();
        checksum.reset();
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) >= 0) {
            checksum.update(buffer, 0, bytesRead);
        }
        in.close();
        return Long.valueOf(checksum.getValue());
    }

    @Override
    public Long start(Path srcFile, Path destFile) throws IOException {
        // make sure the source file is indeed a readable file
        if (!Files.isRegularFile(srcFile) || !Files.isReadable(srcFile)) {
            log.error("Not a readable file: " + srcFile.getFileName()
            .toString());
        }

        // copy file, optionally creating a checksum
        copyFile(srcFile, destFile);

        // copy timestamp of last modification
        Files.setLastModifiedTime(destFile, Files.readAttributes(srcFile, BasicFileAttributes.class)
                .lastModifiedTime());

        // verify file
        long checksumSrc = checksumMappedFile(srcFile.toString());
        long checksumDest = checksumMappedFile(destFile.toString());

        if (checksumSrc == checksumDest) {
            return checksumDest;
        } else {
            return Long.valueOf(0);
        }

    }

    @Override
    public long checksumMappedFile(String filepath) throws IOException {

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filepath);

            FileChannel fileChannel = inputStream.getChannel();

            int len = (int) fileChannel.size();

            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, len);

            CRC32 crc = new CRC32();

            for (int cnt = 0; cnt < len; cnt++) {

                int i = buffer.get(cnt);

                crc.update(i);

            }

            return crc.getValue();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public boolean deleteDir(Path dir) {
        if (!Files.exists(dir)) {
            return true;
        }
        if (Files.isDirectory(dir)) {
            List<Path> children = this.listFiles(dir.toString());
            for (Path child : children) {
                if (Files.isDirectory(child)) {
                    boolean success = deleteDir(child);
                    if (!success) {
                        return false;
                    }
                } else if (Files.isRegularFile(child)) {
                    try {
                        Files.delete(child);
                    } catch (IOException e) {
                    }

                }
            }
        } else if (Files.isRegularFile(dir)) {
            try {
                Files.delete(dir);
            } catch (IOException e) {
            }
        }
        // The directory is now empty so delete it
        try {
            return Files.deleteIfExists(dir);
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * Deletes all files and subdirectories under dir. But not the dir itself
     */
    @Override
    public boolean deleteInDir(Path dir) {
        if (Files.exists(dir) && Files.isDirectory(dir)) {
            List<String> children = this.list(dir.toString());
            for (String child : children) {
                boolean success = deleteDir(Paths.get(dir.toString(), child));
                if (!success) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Deletes all files and subdirectories under dir. But not the dir itself and no metadata files
     */
    @Override
    public boolean deleteDataInDir(Path dir) {
        if (Files.exists(dir) && Files.isDirectory(dir)) {
            List<String> children = this.list(dir.toString());
            for (String child : children) {
                if (!child.endsWith(".xml")) {
                    boolean success = deleteDir(Paths.get(dir.toString(), child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean isFileExists(Path path) {
        return Files.exists(path);
    }

    @Override
    public boolean isDirectory(Path path) {
        return Files.isDirectory(path);
    }

    @Override
    public void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    @Override
    public long getLastModifiedDate(Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class).lastModifiedTime().toMillis();
    }

    @Override
    public long getCreationDate(Path path) throws IOException {
        return Files.readAttributes(path, BasicFileAttributes.class).creationTime().toMillis();
    }

    @Override
    public Path createTemporaryFile(String prefix, String suffix) throws IOException {
        return Files.createTempFile(prefix, suffix);
    }

    @Override
    public void deleteFile(Path path) throws IOException {
        Files.delete(path);
    }

    @Override
    public void move(Path oldPath, Path newPath) throws IOException {
        Files.move(oldPath, newPath);
    }

    @Override
    public boolean isWritable(Path path) {
        return Files.isWritable(path);
    }

    @Override
    public boolean isReadable(Path path) {
        return Files.isReadable(path);
    }

    @Override
    public long getFileSize(Path path) throws IOException {
        return Files.size(path);
    }

    @Override
    public long getDirectorySize(Path path) throws IOException {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException e) {
                    log.debug("skipped: " + file, e);
                    // Skip folders that can't be traversed
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e) {
                    if (e != null) {
                        log.debug("had trouble traversing: " + dir, e);
                    }
                    // Ignore errors traversing a folder
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
        }

        return size.get();
    }

    @Override
    public void createFile(Path path) throws IOException {
        Files.createFile(path);

    }

    @Override
    public void uploadFile(InputStream in, Path dest, Long contentLength) throws IOException {
        // identical to uploadFile(InputStream, Path) because contentLength is irrelevant for local copy
        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
    }
    
    @Override
    public void uploadFile(InputStream in, Path dest) throws IOException {
        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public InputStream newInputStream(Path src) throws IOException {
        return Files.newInputStream(src);
    }

    @Override
    public OutputStream newOutputStream(Path dest) throws IOException {
        return Files.newOutputStream(dest);
    }

    @Override
    public URI getURI(Path path) {
        return path.toUri();
    }
}
