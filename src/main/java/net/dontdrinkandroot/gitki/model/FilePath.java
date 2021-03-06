package net.dontdrinkandroot.gitki.model;

import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class FilePath extends AbstractPath
{
    FilePath(String name, DirectoryPath directoryPath)
    {
        this.setName(name);
        this.setParent(directoryPath);
    }

    public FilePath(String name)
    {
        this.setName(name);
        this.setParent(new DirectoryPath());
    }

    public static FilePath parse(String pathString)
    {
        if (pathString.endsWith(SEPARATOR)) {
            throw new IllegalArgumentException("pathString must not end with " + SEPARATOR);
        }

        String[] parts = pathString.split(SEPARATOR);

        FilePath filePath = new FilePath(parts[parts.length - 1]);

        if (parts.length > 1) {
            filePath.setParent(DirectoryPath.from(Arrays.copyOfRange(parts, 0, parts.length - 1)));
        }

        return filePath;
    }

    @Override
    public String toString()
    {
        return this.getParent().toString() + this.getName();
    }

    @Override
    public Path toPath()
    {
        Path parentPath = this.getParent().toPath();
        return parentPath.resolve(this.getName());
    }

    @Override
    public boolean isRoot()
    {
        return false;
    }

    @Override
    public boolean isDirectoryPath()
    {
        return false;
    }

    public static FilePath from(Path path)
    {
        DirectoryPath directoryPath = new DirectoryPath();
        Path parent = path.getParent();
        if (null != parent) {
            directoryPath = DirectoryPath.from(path.getParent());
        }

        return directoryPath.appendFileName(path.getFileName().toString());
    }

    public String getExtension()
    {
        return FilenameUtils.getExtension(this.getName());
    }

    public String getNameWithoutExtension()
    {
        return FilenameUtils.removeExtension(this.getName());
    }
}
