package net.dontdrinkandroot.gitki.service;

import net.dontdrinkandroot.gitki.model.DirectoryListing;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Washington Sorst <philip@sorst.net>
 */
public class DefaultGitService implements GitService
{
    private Path basePath;

    private Git git;

    protected DefaultGitService()
    {
        /* RI */
    }

    public DefaultGitService(String gitDir) throws IOException
    {
        if (!StringUtils.endsWith(gitDir, File.separator)) {
            throw new RuntimeException("Git Dir must end with " + File.separator);
        }

        Path gitPath = Paths.get(gitDir);
        if (!gitPath.isAbsolute()) {
            throw new RuntimeException("Repository path must be absolute");
        }

        this.basePath = gitPath;

        Repository repository = FileRepositoryBuilder.create(this.basePath.resolve(".git").toFile());
        if (repository.getRef("HEAD") == null) {
            repository.create();
        }

        this.git = new Git(repository);
    }

    @Override
    public DirectoryListing listDirectory(Path path) throws IOException
    {
        if (path.isAbsolute()) {
            throw new RuntimeException("Path must not be absolute");
        }
        Path directoryPath = this.basePath.resolve(path);

        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath);

        List<Path> subDirectories = new ArrayList<>();
        List<Path> files = new ArrayList<>();
        for (Path subPath : directoryStream) {
            if (Files.isDirectory(subPath)) {
                subDirectories.add(this.basePath.relativize(subPath));
            } else {
                files.add(this.basePath.relativize(subPath));
            }
        }

        return new DirectoryListing(subDirectories, files);
    }

    @Override
    public byte[] getContent(Path path) throws IOException
    {
        Path fullPath = this.basePath.resolve(path);
        if (!Files.exists(fullPath)) {
            return null;
        }

        return Files.readAllBytes(fullPath);
    }

    @Override
    public String getContentAsString(Path path) throws IOException
    {
        byte[] bytes = this.getContent(path);
        if (null == bytes) {
            return null;
        }

        return new String(bytes);
    }

    @Override
    public void save(Path path, String content, String commitMessage) throws IOException, GitAPIException
    {
        Path fullPath = this.basePath.resolve(path);
        Files.write(fullPath, content.getBytes());
        //TODO: Set committer
        this.git.add().addFilepattern(path.toString()).call();
        this.git.commit().setMessage(commitMessage).call();
    }
}
