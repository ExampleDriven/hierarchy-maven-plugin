package org.exampledriven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.RepositorySystem;
import java.util.Locale;
import java.util.Properties;

@Mojo( name = "tree")
public class HierarchyMojo extends AbstractMojo {

    private static final int DEPTH_INCREMENT = 2;

    private static final String LEVEL_FULL = "full";
    private static final String PROPERTY_PROJECT_VERSION = "project.version";
    private static final String PROPERTY_PARENT_PREFIX = "project.parent.";

    private enum MessageType {PARENT, IMPORT, DEP_MANAGEMENT}

    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;

    @Parameter( defaultValue = "${session}", readonly = true )
    private MavenSession session;

    @Parameter( property = "level")
    private String level;

    @Component
    private RepositorySystem repositorySystem;

    @Component
    private ProjectBuilder projectBuilder;


    public void execute() throws MojoExecutionException, MojoFailureException {

        String message = "Displaying hierarchy. ";
        if (!isLevelFull()) {
            message += "Set level="+LEVEL_FULL+" to display dependencies in dependencyManagement";
        }
        getLog().info(message);

        if (!processParentAndImportsRecursive(project, 1)) {
            getLog().info("Project has no parent and imported poms either.");
        }


    }

    private boolean processParentAndImportsRecursive(MavenProject currentProject, int depth) {

        boolean hasParent = processParentRecursive(currentProject.getParent(), depth);

        if (isLevelFull()) {
            displayManagedDependencies(currentProject, depth);
        }

        boolean hasImports = processImportsRecursive(currentProject, depth);

        return hasImports || hasParent;
    }

    private boolean isLevelFull() {
        return LEVEL_FULL.equals(level);
    }

    private void displayManagedDependencies(MavenProject currentProject, int depth) {
        DependencyManagement dependencyManagement = currentProject.getDependencyManagement();
        if (dependencyManagement != null) {
            for (Dependency dependency : dependencyManagement.getDependencies()) {
                logDependency(MessageType.DEP_MANAGEMENT, dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), depth + DEPTH_INCREMENT * 2);
            }
        }
    }

    private boolean processParentRecursive(MavenProject parent, int depth) {
        if (parent != null) {
            logDependency(MessageType.PARENT, parent.getGroupId(), parent.getArtifactId(), parent.getVersion(), depth);
            processParentAndImportsRecursive(parent, depth + DEPTH_INCREMENT);
        }

        return parent != null;
    }

    private boolean processImportsRecursive(MavenProject currentProject, int depth) {

        DependencyManagement dependencyManagement = currentProject.getOriginalModel().getDependencyManagement();
        if (dependencyManagement != null) {

            for (Dependency dependency : dependencyManagement.getDependencies()) {

                if ("import".equals(dependency.getScope()) && "pom".equals(dependency.getType())) {

                    MavenProject mavenProject;
                    try {
                        mavenProject = getMavenProject(currentProject, dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
                    } catch (ProjectBuildingException e) {
                        throw new RuntimeException(e);
                    }
                    logDependency(MessageType.IMPORT, mavenProject.getGroupId(), mavenProject.getArtifactId(), mavenProject.getVersion(), depth);
                    processParentAndImportsRecursive(mavenProject, depth + DEPTH_INCREMENT);

                }
            }
        }

        return dependencyManagement != null;
    }

    private MavenProject getMavenProject(MavenProject currentProject, String groupId, String artifactId, String version) throws ProjectBuildingException {
        String resolvedVersion = resolveVersionProperty(currentProject, version);

        Artifact pomArtifact = repositorySystem.createProjectArtifact(groupId, artifactId, resolvedVersion);
        return projectBuilder.build(pomArtifact, session.getProjectBuildingRequest()).getProject();

    }

    private String resolveVersionProperty(MavenProject currentProject, String version) {
        if (isVersionSetAsProperty(version)) {
            String propertyName = version.replace("$", "").replace("{", "").replace("}", "");
            return findPropertyRecursively(currentProject, propertyName);
        }

        return version;
    }

    private String findPropertyRecursively(MavenProject mavenProject, String propertyName) {
        if (PROPERTY_PROJECT_VERSION.equals(propertyName)) {
            return mavenProject.getVersion();
        }

        Properties properties = mavenProject.getProperties();
        if (properties != null) {
            String propertyValue = (String) properties.get(propertyName);
            if (propertyValue != null) {
                return propertyValue;
            }
        }

        MavenProject parent = mavenProject.getParent();
        if (parent != null) {
            String fixedPropertyName = propertyName;
            if (propertyName.startsWith(PROPERTY_PARENT_PREFIX)) {
                fixedPropertyName = fixedPropertyName.replaceAll("project\\.parent\\.(?<prop>.*)", "project.${prop}");
            }
            return findPropertyRecursively(parent, fixedPropertyName);
        }

        return propertyName;
    }

    private boolean isVersionSetAsProperty(String version) {
        return version.contains("${");
    }

    private void logDependency(MessageType messageType, String groupId, String artifactId, String version, int indentation) {
        String projectString = String.format(Locale.US, "%s:%s:%s", groupId, artifactId, version);

        String shift = String.format("%0" + indentation + "d", 0).replace("0", " ");

        getLog().info(shift + messageType.name() + " " + projectString);
    }

}
