package net.fabricmc.loom.util;

import org.gradle.api.Project;

public class ProjectHolder {
    private static Project project;

    public static Project getProject() {
        return project;
    }

    public static void setProject(Project project) {
        ProjectHolder.project = project;
    }
}
