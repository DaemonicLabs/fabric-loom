package net.fabricmc.loom.util;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;

public class ArtifactRemover {
    public static final void removeArtifacts(Project project, final Task task, final boolean excludeDefaultConfiguration) {
        project.getConfigurations().stream().filter(
                (obj) -> !excludeDefaultConfiguration || !obj.getName().equals(Dependency.DEFAULT_CONFIGURATION)
        ).forEach((conf) -> {
            conf.getArtifacts().removeIf((artifact) -> {
//                project.getLogger().lifecycle("configuration: " + conf);
//                project.getLogger().lifecycle("artifact.file: " + artifact.getFile());
//                project.getLogger().lifecycle("artifact.classifier: " + artifact.getClassifier());
//                project.getLogger().lifecycle("files: " + task.getOutputs().getFiles().getFiles());
//                project.getLogger().lifecycle("remove: " + task.getOutputs().getFiles().getFiles().contains(artifact.getFile()));
                return task.getOutputs().getFiles().getFiles().contains(artifact.getFile());
            });
        });
    }
}
