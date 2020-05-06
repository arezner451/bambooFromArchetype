package hu.arezner;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.repository.VcsRepository;
import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;
import com.atlassian.bamboo.specs.builders.task.CleanWorkingDirectoryTask;
import com.atlassian.bamboo.specs.builders.task.MavenTask;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.util.BambooServer;
import java.util.logging.Logger;

/**
 * Plan configuration for Bamboo.
 *
 * @see
 * <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">Bamboo
 * Specs</a>
 */
@BambooSpec
public class ReziBambooPlanSpec {
    
    private static final Logger LOG = 
        Logger.getLogger(ReziBambooPlanSpec.class.getName());

    /**
     * Run 'main' to publish your plan.
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        // by default credentials are read from the '.credentials' file
        BambooServer bambooServer = new BambooServer("http://localhost:8085");

        Plan plan = new ReziBambooPlanSpec().createPlan();
        bambooServer.publish(plan);

        PlanPermissions planPermission = 
            new ReziBambooPlanSpec().createPlanPermission(plan.getIdentifier());
        
        bambooServer.publish(planPermission);
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        
        Permissions permissions = new Permissions()
            .userPermissions("arezner451", PermissionType.ADMIN)
            .groupPermissions("bamboo-admin", PermissionType.ADMIN)
            .loggedInUserPermissions(PermissionType.BUILD)
            .anonymousUserPermissionView();

        return new PlanPermissions(planIdentifier).permissions(permissions);
    }

    Project project() {
        
        return new Project()
            .name("rezi test Project")
            .key("RTP");
    }

    Plan createPlan() {
        
        return new Plan(project(), "rezi Plan", "PLAN")
            .description("Plan created from Java Based on Bamboo API Specs")
            .planRepositories(gitRepository())
            .stages(
                new Stage("Stage 1")
                    .jobs(
                        new Job("Job 1", "JOB1")
                            .tasks(
                                cleanWorkingDirectoryTask(),
                                gitRepositoryCheckoutTask(),
                                mavenCompileTask(),
                                mavenTestTask(),
                                createArtifactTargetDirScriptTask()
                            )
                            .artifacts(putBuildResults())
                    )
            );
    }

    VcsRepository gitRepository() {
        return new GitRepository()
            //.name("bamboo-specs")
            .name("mavensoapproject-1.0-SNAPSHOT")
            //.url("git@bitbucket.org:atlassian/bamboo-specs.git")
            .url("https://github.com/arezner451/mavensoapproject-1.0-SNAPSHOT.git")
            .branch("master");
    }

    VcsCheckoutTask gitRepositoryCheckoutTask() {
        return new VcsCheckoutTask().addCheckoutOfDefaultRepository();
    }

    CleanWorkingDirectoryTask cleanWorkingDirectoryTask() {    
        return new CleanWorkingDirectoryTask()
            .description("clean working directory task")
            .enabled(true);    
    }
    
    MavenTask mavenCompileTask() {
        return new MavenTask()
            .description("compile source code")
            .goal("clean install")
            .hasTests(false)
            .version3()
            .jdk("JDK 1.8")
            .executableLabel("mvn");        
    }

    MavenTask mavenTestTask() {
        return new MavenTask()
            .description("test source code")
            .goal("test")
            .hasTests(true)
            .version3()
            .jdk("JDK 1.8")
            .executableLabel("mvn");        
    }
    
    Artifact putBuildResults() {
        return new Artifact("Build results")
            .location("target")
            .copyPattern("**/*");
    }

    ScriptTask createArtifactTargetDirScriptTask() {
        return new ScriptTask()
            .description("create target directory")
            .inlineBody("mkdir target; echo 'target dir created' > target/console.out")
            .interpreterShell();
    }
    
}
